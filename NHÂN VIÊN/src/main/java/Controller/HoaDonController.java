package Controller;

import DAO.HoaDonDAO;
import Model.HOADON;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;

public class HoaDonController implements Initializable {

    @FXML private TableView<HOADON> tableHoaDon;
    @FXML private TableColumn<HOADON, String> colMaHD;
    @FXML private TableColumn<HOADON, String> colMaDS;
    @FXML private TableColumn<HOADON, LocalDate> colNgayXuat;
    @FXML private TableColumn<HOADON, Long> colTongTien;
    @FXML private TableColumn<HOADON, Long> colTienGiam;
    @FXML private TableColumn<HOADON, Long> colThanhTien;
    @FXML private TableColumn<HOADON, String> colGhiChu;
    @FXML private TableColumn<HOADON, Void> colThaoTac;
    @FXML private TextField tfSearch;
    @FXML private DatePicker dpNgay;
    @FXML private Label lblTongDoanhThu;

    private HoaDonDAO hoaDonDAO;
    private ObservableList<HOADON> danhSachHoaDon;
    private FilteredList<HOADON> filteredList;
    private NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumns();
        try {
            hoaDonDAO = new HoaDonDAO();
            loadDuLieu();
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối database: " + e.getMessage());
        }
    }

    private void setupColumns() {
        colMaHD.setCellValueFactory(new PropertyValueFactory<>("maHD"));
        colMaDS.setCellValueFactory(new PropertyValueFactory<>("maDS"));
        colNgayXuat.setCellValueFactory(new PropertyValueFactory<>("ngayXuat"));
        colTongTien.setCellValueFactory(new PropertyValueFactory<>("tongTienDV"));
        colTienGiam.setCellValueFactory(new PropertyValueFactory<>("soTienGiam"));
        colThanhTien.setCellValueFactory(new PropertyValueFactory<>("thanhTien"));
        colGhiChu.setCellValueFactory(new PropertyValueFactory<>("ghiChu"));

        colTongTien.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatter.format(item) + " ₫");
            }
        });
        colTienGiam.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatter.format(item) + " ₫");
            }
        });
        colThanhTien.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatter.format(item) + " ₫");
            }
        });

        colThaoTac.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Xem");
            {
                btn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 4; -fx-cursor: hand;");
                btn.setOnAction(e -> {
                    HOADON hd = getTableView().getItems().get(getIndex());
                    showChiTiet(hd);
                });
            }
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    public void loadDuLieu() {
        if (hoaDonDAO == null) return;
        danhSachHoaDon = hoaDonDAO.getAllHoaDon();
        filteredList = new FilteredList<>(danhSachHoaDon, p -> true);
        tableHoaDon.setItems(filteredList);
        capNhatTongDoanhThu(danhSachHoaDon);
    }

    @FXML
    private void onSearch() {
        String keyword = tfSearch.getText().toLowerCase().trim();
        filteredList.setPredicate(hd -> {
            if (keyword.isEmpty()) return true;
            return hd.getMaHD().toLowerCase().contains(keyword)
                || hd.getMaDS().toLowerCase().contains(keyword);
        });
        capNhatTongDoanhThu(filteredList);
    }

    @FXML
    private void onLocNgay() {
        LocalDate ngay = dpNgay.getValue();
        if (ngay == null) { showAlert("Vui lòng chọn ngày!"); return; }
        ObservableList<HOADON> list = hoaDonDAO.getHoaDonByNgay(ngay);
        tableHoaDon.setItems(list);
        capNhatTongDoanhThu(list);
    }

    @FXML
    private void onXemTat() {
        dpNgay.setValue(null);
        tfSearch.clear();
        loadDuLieu();
    }

    @FXML
    private void onTaoHoaDon() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/TaoHoaDonUI.fxml"));
            Parent root = loader.load();

            TaoHoaDonController ctrl = loader.getController();
            ctrl.setOnSaveCallback(this::loadDuLieu);

            Stage stage = new Stage();
            stage.setTitle("Tạo Hóa Đơn Mới");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            showAlert("Lỗi mở form: " + e.getMessage());
        }
    }

    private void showChiTiet(HOADON hd) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi Tiết Hóa Đơn");
        alert.setHeaderText("Mã HĐ: " + hd.getMaHD());
        alert.setContentText(
            "Mã đặt sân: " + hd.getMaDS() + "\n" +
            "Ngày xuất: " + hd.getNgayXuat() + "\n" +
            "Tổng tiền DV: " + formatter.format(hd.getTongTienDV()) + " ₫\n" +
            "Tiền giảm: " + formatter.format(hd.getSoTienGiam()) + " ₫\n" +
            "Thành tiền: " + formatter.format(hd.getThanhTien()) + " ₫\n" +
            "Ghi chú: " + (hd.getGhiChu() != null ? hd.getGhiChu() : "")
        );
        alert.showAndWait();
    }

    private void capNhatTongDoanhThu(Iterable<HOADON> list) {
        long tong = 0;
        for (HOADON hd : list) tong += hd.getThanhTien();
        lblTongDoanhThu.setText("Tổng: " + formatter.format(tong) + " ₫");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}