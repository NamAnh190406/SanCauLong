package Controller;

import DAO.DatSanDAO;
import DAO.HoaDonDAO;
import Model.HOADON;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;

public class TaoHoaDonController implements Initializable {

    @FXML private TextField tfMaHD;
    @FXML private ComboBox<String> cbMaDS;
    @FXML private TextField tfTongTien;
    @FXML private TextField tfTienGiam;
    @FXML private TextField tfThanhTien;
    @FXML private TextArea taGhiChu;

    private HoaDonDAO hoaDonDAO;
    private DatSanDAO datSanDAO;
    private NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    // Callback để refresh bảng hóa đơn sau khi lưu
    private Runnable onSaveCallback;

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            hoaDonDAO = new HoaDonDAO();
            datSanDAO = new DatSanDAO();
            loadDanhSachDS();
            setupListeners();
        } catch (SQLException e) {
            showAlert("Lỗi kết nối: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadDanhSachDS() {
        if (datSanDAO == null) return;
        datSanDAO.getallDatsans().forEach(ds -> cbMaDS.getItems().add(ds.getMaDS()));
    }

    private void setupListeners() {
        // Khi chọn mã đặt sân → tự động tính tổng tiền
        cbMaDS.setOnAction(e -> {
            String maDS = cbMaDS.getValue();
            if (maDS != null && datSanDAO != null) {
                long tongTien = datSanDAO.TinhThanhTien(maDS);
                tfTongTien.setText(formatter.format(tongTien) + " ₫");
                tinhThanhTien(tongTien);
            }
        });

        // Khi nhập tiền giảm → tự động tính thành tiền
        tfTienGiam.textProperty().addListener((obs, oldVal, newVal) -> {
            String maDS = cbMaDS.getValue();
            if (maDS != null && datSanDAO != null) {
                long tongTien = datSanDAO.TinhThanhTien(maDS);
                tinhThanhTien(tongTien);
            }
        });
    }

    private void tinhThanhTien(long tongTien) {
        try {
            String giam = tfTienGiam.getText().trim();
            long tienGiam = giam.isEmpty() ? 0 : Long.parseLong(giam);
            long thanhTien = tongTien - tienGiam;
            tfThanhTien.setText(formatter.format(thanhTien) + " ₫");
        } catch (NumberFormatException e) {
            tfThanhTien.setText("Nhập số hợp lệ!");
        }
    }

    @FXML
    private void onLuu() {
        // Kiểm tra dữ liệu
        if (tfMaHD.getText().trim().isEmpty()) {
            showAlert("Vui lòng nhập mã hóa đơn!", Alert.AlertType.WARNING);
            return;
        }
        if (cbMaDS.getValue() == null) {
            showAlert("Vui lòng chọn mã đặt sân!", Alert.AlertType.WARNING);
            return;
        }

        try {
            String maHD = tfMaHD.getText().trim();
            String maDS = cbMaDS.getValue();
            long tongTien = datSanDAO.TinhThanhTien(maDS);
            String giamStr = tfTienGiam.getText().trim();
            long tienGiam = giamStr.isEmpty() ? 0 : Long.parseLong(giamStr);
            String ghiChu = taGhiChu.getText().trim();

            HOADON hd = new HOADON(maHD, tongTien, tienGiam, ghiChu, LocalDate.now(), maDS);

            boolean ok = hoaDonDAO.addHoaDon(hd);
            if (ok) {
                showAlert("Tạo hóa đơn thành công!", Alert.AlertType.INFORMATION);
                if (onSaveCallback != null) onSaveCallback.run();
                onHuy();
            } else {
                showAlert("Tạo hóa đơn thất bại!", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            showAlert("Tiền giảm phải là số!", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void onHuy() {
        ((Stage) tfMaHD.getScene().getWindow()).close();
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}