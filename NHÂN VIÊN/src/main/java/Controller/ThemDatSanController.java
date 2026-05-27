package Controller;

import DAO.DatSanDAO;
import DAO.HoaDonDAO;
import Model.DATSAN;
import Model.SAN; 
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class ThemDatSanController implements Initializable {

    @FXML private TextField txtTenKhachHang;
    @FXML private TextField txtSoDienThoai;
    @FXML private ComboBox<SAN> cbxChonSan; 
    @FXML private ComboBox<String> cbxGioBatDau;
    @FXML private ComboBox<Integer> cbxThoiLuong;
    @FXML private DatePicker dpNgayDat;
    
    @FXML private Label lblPreviewSan;
    @FXML private Label lblPreviewGio;
    @FXML private Label lblPreviewGia;
    // Bổ sung thêm Label này trong file FXML (SceneBuilder) nếu bạn muốn hiển thị Tên khách ở khung preview xanh lá
    @FXML private Label lblPreviewKhachHang; 

    @FXML private Button btnHuy;
    @FXML private Button btnXacNhan;

    private final NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private Runnable callback;
    
    // Callback đặc biệt: nhận MaDS sau khi tạo đặt sân thành công
    // Để HoaDonController có thể tra hóa đơn và mở trang thanh toán
    private java.util.function.Consumer<String> onSuccessCallback;
    
    // Biến lưu trữ Mã Khách Hàng được chọn từ danh sách gợi ý
    private String selectedMaKH = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupCustomerAutocomplete(); // Khởi tạo tính năng gợi ý khách hàng
        setupComboBoxes();
        setupRealTimePreview();
        setupActionButtons();
        
        dpNgayDat.setValue(LocalDate.now());
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    /**
     * Đặt callback sẽ được gọi khi tạo đặt sân thành công, truyền vào MaDS vừa tạo.
     * Dùng để HoaDonController tra hóa đơn và tự động mở màn hình thanh toán.
     */
    public void setOnSuccessCallback(java.util.function.Consumer<String> onSuccessCallback) {
        this.onSuccessCallback = onSuccessCallback;
    }

    // ================= TÍNH NĂNG GỢI Ý KHÁCH HÀNG =================
    private void setupCustomerAutocomplete() {
        ContextMenu suggestionMenu = new ContextMenu();

        // Lắng nghe khi người dùng gõ vào ô Tên Khách Hàng
        txtTenKhachHang.textProperty().addListener((obs, oldVal, newVal) -> {
            // Nếu người dùng đang tự gõ (không phải do click chọn) -> Xóa mã KH cũ
            if (txtTenKhachHang.isFocused()) {
                selectedMaKH = null; 
                cappNhatPreviewKhachHang();
            }

            suggestionMenu.getItems().clear();

            if (newVal == null || newVal.trim().isEmpty()) {
                suggestionMenu.hide();
                return;
            }

            // Lấy danh sách gợi ý từ Mock Data (Hãy thay bằng hàm gọi Database của bạn!)
            List<KhachHangDTO> suggestions = timKiemKhachHang(newVal);

            if (suggestions.isEmpty()) {
                suggestionMenu.hide();
            } else {
                for (KhachHangDTO kh : suggestions) {
                    MenuItem item = new MenuItem(kh.getTenKH() + " - " + kh.getSdt());
                    
                    // Xử lý sự kiện khi click vào một gợi ý
                    item.setOnAction(e -> {
                        txtTenKhachHang.setText(kh.getTenKH());
                        txtSoDienThoai.setText(kh.getSdt());
                        selectedMaKH = kh.getMaKH(); // Lưu lại mã KH để LƯU DATABASE
                        
                        txtTenKhachHang.positionCaret(kh.getTenKH().length()); // Đưa con trỏ về cuối chữ
                        cappNhatPreviewKhachHang();
                    });
                    
                    suggestionMenu.getItems().add(item);
                }
                
                // Hiển thị menu bên dưới TextField
                if (!suggestionMenu.isShowing() && txtTenKhachHang.getScene() != null) {
                    suggestionMenu.show(txtTenKhachHang, Side.BOTTOM, 0, 0);
                }
            }
        });
        
        // Ẩn menu khi click ra ngoài
        txtTenKhachHang.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                suggestionMenu.hide();
            }
        });
    }

    // Class tạm để chứa dữ liệu Khách Hàng (Thay thế bằng Model KHACHHANG của bạn nếu có)
    private static class KhachHangDTO {
        private String maKH, tenKH, sdt;
        public KhachHangDTO(String maKH, String tenKH, String sdt) {
            this.maKH = maKH; this.tenKH = tenKH; this.sdt = sdt;
        }
        public String getMaKH() { return maKH; }
        public String getTenKH() { return tenKH; }
        public String getSdt() { return sdt; }
    }

    private List<KhachHangDTO> timKiemKhachHang(String keyword) {
        List<KhachHangDTO> result = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        
        try {
            javafx.collections.ObservableList<Model.KHACHHANG> dbData = DAO.KhachHangDAO.getDanhSachKhachHang();
            if (dbData != null) {
                for (Model.KHACHHANG kh : dbData) {
                    if (kh.getHoTen().toLowerCase().contains(lowerKeyword) || 
                        (kh.getSDT() != null && kh.getSDT().contains(keyword))) {
                        result.add(new KhachHangDTO(kh.getMaKH(), kh.getHoTen(), kh.getSDT()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }
    // ===============================================================

    private void setupComboBoxes() {
        ObservableList<SAN> danhSachSan = SAN.getDanhSachSanTuDB();
        cbxChonSan.setItems(danhSachSan);
        
        cbxChonSan.setCellFactory(param -> new ListCell<SAN>() {
            @Override
            protected void updateItem(SAN item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.TenSan + " - " + currencyFormat.format(item.GiaThueTheoGio) + "đ/h");
                }
            }
        });
        cbxChonSan.setButtonCell(cbxChonSan.getCellFactory().call(null));
        
        ObservableList<String> listGio = FXCollections.observableArrayList(
                "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", 
                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00"
        );
        cbxGioBatDau.setItems(listGio);

        cbxThoiLuong.setItems(FXCollections.observableArrayList(1, 2, 3));
        cbxThoiLuong.getSelectionModel().selectFirst(); 
    }

    private void setupRealTimePreview() {
        cbxChonSan.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lblPreviewSan.setText("Sân: " + newVal.TenSan);
                tinhToanGiaTien();
            }
        });

        cbxGioBatDau.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> cappNhatPreviewThoiGian());
        dpNgayDat.valueProperty().addListener((obs, oldVal, newVal) -> cappNhatPreviewThoiGian());
        cbxThoiLuong.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> tinhToanGiaTien());
    }

    private void cappNhatPreviewThoiGian() {
        String gio = cbxGioBatDau.getSelectionModel().getSelectedItem();
        LocalDate ngay = dpNgayDat.getValue();

        if (gio != null && ngay != null) {
            lblPreviewGio.setText("Thời gian: " + gio + " - " + ngay.format(dateFormatter));
        } else if (gio != null) {
            lblPreviewGio.setText("Giờ: " + gio);
        } else if (ngay != null) {
            lblPreviewGio.setText("Ngày: " + ngay.format(dateFormatter));
        }
    }
    
    private void cappNhatPreviewKhachHang() {
        if (lblPreviewKhachHang != null) {
            String ten = txtTenKhachHang.getText();
            if (ten == null || ten.trim().isEmpty()) {
                lblPreviewKhachHang.setText("Khách hàng: (Chưa nhập)");
            } else {
                lblPreviewKhachHang.setText("Khách hàng: " + ten);
            }
        }
    }

    private void tinhToanGiaTien() {
        SAN sanDuocChon = cbxChonSan.getSelectionModel().getSelectedItem();
        Integer thoiLuong = cbxThoiLuong.getSelectionModel().getSelectedItem();

        if (sanDuocChon != null && thoiLuong != null) {
            long tongTien = sanDuocChon.GiaThueTheoGio * thoiLuong;
            lblPreviewGia.setText("Giá: " + currencyFormat.format(tongTien) + "đ");
        }
    }

    private void setupActionButtons() {
        btnHuy.setOnAction(event -> dongCuaSo());
        btnXacNhan.setOnAction(event -> handleXacNhan());
    }

    private boolean isValidInput(String tenKhach, String sdt, SAN san, String gio, LocalDate ngayDat) {
        if (tenKhach.isEmpty() || sdt.isEmpty() || san == null || gio == null || ngayDat == null) {
            showAlert(Alert.AlertType.WARNING, "Vui lòng nhập đầy đủ thông tin!");
            return false;
        }
        if (!sdt.matches("(84|0[3|5|7|8|9])+([0-9]{8})")) {
            showAlert(Alert.AlertType.WARNING, "Số điện thoại không hợp lệ!");
            return false;
        }
        if (ngayDat.isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING, "Không thể đặt sân cho ngày trong quá khứ!");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void handleXacNhan() {
        btnXacNhan.setDisable(true); // Khóa nút chống spam click
        
        String tenKhach = txtTenKhachHang.getText().trim();
        String sdt = txtSoDienThoai.getText().trim();
        SAN san = cbxChonSan.getSelectionModel().getSelectedItem();
        String gio = cbxGioBatDau.getSelectionModel().getSelectedItem().trim(); // Lấy và gọt khoảng trắng ngay từ đầu
        Integer thoiLuong = cbxThoiLuong.getSelectionModel().getSelectedItem();
        LocalDate ngayDat = dpNgayDat.getValue();

        if (!isValidInput(tenKhach, sdt, san, gio, ngayDat)) {
            btnXacNhan.setDisable(false); // Mở khóa nút nếu nhập sai
            return;
        }

        // Bước 1: Đảm bảo MaKH hợp lệ trong DB (tìm theo SĐT, nếu chưa có thì tạo mới)
        DatSanDAO datSanDAO = new DatSanDAO();
        String maKHHopLe = datSanDAO.upsertKhachVangLai(tenKhach, sdt);
        if (maKHHopLe == null) {
            btnXacNhan.setDisable(false);
            showAlert(Alert.AlertType.ERROR, "Không thể xác định khách hàng. Kiểm tra kết nối CSDL!");
            return;
        }
        selectedMaKH = maKHHopLe;

        long tongTien = san.GiaThueTheoGio * thoiLuong;

        try {
            String maDatSan = "DS" + (System.currentTimeMillis() % 100000);

            DATSAN ds = new DATSAN();
            ds.setMaDS(maDatSan);
            ds.setMaKH(selectedMaKH);
            ds.setTenKH(tenKhach);
            ds.setSdtKH(sdt);
            ds.setMaHD(null);
            ds.setMaSan(san.MaSan);

            // Map CHUẨN XÁC theo dữ liệu bảng KHUNGGIO
            String maKhungGio = "";
            switch (gio) {
                case "06:00": maKhungGio = "KG001"; break; // 06:00 - 07:30
                case "07:00":
                case "07:30": maKhungGio = "KG002"; break; // 07:30 - 09:00
                case "08:00":
                case "09:00": maKhungGio = "KG003"; break; // 09:00 - 10:30
                case "10:00":
                case "10:30": maKhungGio = "KG004"; break; // 10:30 - 12:00
                case "11:00": maKhungGio = "KG004"; break;
                case "13:00":
                case "14:00": maKhungGio = "KG005"; break; // 13:00 - 14:30
                case "14:30":
                case "15:00": maKhungGio = "KG006"; break; // 14:30 - 16:00
                case "16:00": maKhungGio = "KG007"; break; // 16:00 - 17:30
                case "17:00":
                case "17:30": maKhungGio = "KG008"; break; // 17:30 - 19:00
                case "18:00":
                case "19:00": maKhungGio = "KG009"; break; // 19:00 - 20:30
                case "20:00":
                case "20:30": maKhungGio = "KG010"; break; // 20:30 - 22:00
                default:
                    System.out.println("Cảnh báo: Giờ không khớp DB, mặc định gán KG001");
                    maKhungGio = "KG001";
            }
            ds.setMaKG(maKhungGio);
            ds.setNgayDat(ngayDat);
            ds.setTrangThai("ChoDuyet");
            ds.setTongTienTamTinh(tongTien);

            // Bước 2: Lưu đặt sân trực tiếp vào DB (INSERT đủ trường)
            boolean isDatSanSaved = datSanDAO.insertDatSanDirect(ds);

            if (isDatSanSaved) {
                // Bước 3: Tự động tạo hóa đơn với trạng thái "Chưa Thanh Toán"
                HoaDonDAO hoaDonDAO = new HoaDonDAO();
                boolean isHoaDonCreated = hoaDonDAO.addHoaDon(maDatSan, tongTien);

                if (!isHoaDonCreated) {
                    throw new Exception("Lưu đặt sân thành công nhưng không thể tạo hóa đơn!");
                }

                System.out.println("✓ Đã tạo đặt sân: " + maDatSan + " và hóa đơn tương ứng");

                // Ghi thông báo hệ thống: đặt sân thành công
                Utils.NotificationHelper.datSanThanhCong(
                    maDatSan,
                    tenKhach,
                    san.TenSan != null ? san.TenSan : san.MaSan,
                    ngayDat.toString()
                );

                // Gọi callback cơ bản (reload danh sách hóa đơn)
                if (callback != null) {
                    callback.run();
                }
                dongCuaSo();
                showAlert(Alert.AlertType.INFORMATION, "Đã đặt sân và tạo hóa đơn thành công!");
                // Gọi callback đặc biệt với MaDS để mở trang thanh toán
                if (onSuccessCallback != null) {
                    onSuccessCallback.accept(maDatSan);
                }
            } else {
                throw new Exception("Không thể lưu thông tin đặt sân!");
            }
            
        } catch (Exception e) {
            btnXacNhan.setDisable(false); // Mở khóa nút nếu có lỗi DB để người dùng thử lại
            String errorMsg = e.getMessage() != null ? e.getMessage() : "";
            
            if (errorMsg.contains("ORA-20001")) {
                showAlert(Alert.AlertType.WARNING, 
                    "❌ Rất tiếc!\n\nSân này đã được đặt trong khung giờ này rồi.\n\n" +
                    "Vui lòng chọn:\n• Khung giờ khác\n• Hoặc sân khác");
            } else if (errorMsg.contains("ORA-")) {
                showAlert(Alert.AlertType.ERROR, "Lỗi cơ sở dữ liệu:\n" + errorMsg);
                e.printStackTrace();
            } else {
                showAlert(Alert.AlertType.ERROR, "Có lỗi xảy ra: " + errorMsg);
                e.printStackTrace();
            }
        }
    }
    
    private void dongCuaSo() {
        Stage stage = (Stage) btnHuy.getScene().getWindow();
        stage.close();
    }
}