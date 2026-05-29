package Controller;

import DAO.HoaDonDAO;
import DAO.ThanhToanDAO;
import Model.HOADON;
import Model.ThanhToan;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class TaoThanhToanController implements Initializable {

    @FXML private TextField tfMaTT;
    @FXML private ComboBox<String> cbMaHD;
    @FXML private TextField tfSoTien;
    @FXML private ComboBox<String> cbPTTT;
    @FXML private ComboBox<String> cbTrangThai;

    private ThanhToanDAO thanhToanDAO;
    private HoaDonDAO hoaDonDAO;
    private NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
    private Runnable onSaveCallback;

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbPTTT.setItems(FXCollections.observableArrayList("Tiền mặt", "Chuyển khoản", "Thẻ ngân hàng", "Ví điện tử"));
        cbPTTT.setValue("Tiền mặt");
        cbTrangThai.setItems(FXCollections.observableArrayList("Chờ thanh toán", "Đã thanh toán"));
        cbTrangThai.setValue("Chờ thanh toán");

        try {
            hoaDonDAO = new HoaDonDAO();
            thanhToanDAO = new ThanhToanDAO();
            hoaDonDAO.getAllHoaDon().forEach(hd -> cbMaHD.getItems().add(hd.getMaHD()));
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối: " + e.getMessage());
        }

        // Khi chọn hóa đơn → hiện số tiền
        cbMaHD.setOnAction(e -> {
            String maHD = cbMaHD.getValue();
            if (maHD != null && hoaDonDAO != null) {
                HOADON hd = hoaDonDAO.getHoaDonByMa(maHD);
                if (hd != null) {
                    tfSoTien.setText(formatter.format(hd.getThanhTien()) + " ₫");
                }
            }
        });
    }

    @FXML
    private void onLuu() {
        if (tfMaTT.getText().trim().isEmpty()) {
            showAlert("Vui lòng nhập mã giao dịch!", Alert.AlertType.WARNING);
            return;
        }
        if (cbMaHD.getValue() == null) {
            showAlert("Vui lòng chọn mã hóa đơn!", Alert.AlertType.WARNING);
            return;
        }

        try {
            String maTT = tfMaTT.getText().trim();
            String maHD = cbMaHD.getValue();
            String pttt = cbPTTT.getValue();
            String trangThai = cbTrangThai.getValue();

            HOADON hd = hoaDonDAO.getHoaDonByMa(maHD);
            long soTien = hd != null ? hd.getThanhTien() : 0;

            ThanhToan tt = new ThanhToan(maTT, pttt, null, trangThai, maHD, soTien);
            boolean ok = thanhToanDAO.addThanhToan(tt);

            if (ok) {
                showAlert("Tạo thanh toán thành công!", Alert.AlertType.INFORMATION);
                if (onSaveCallback != null) onSaveCallback.run();
                onHuy();
            } else {
                showAlert("Tạo thanh toán thất bại!", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Lỗi: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onHuy() {
        ((Stage) tfMaTT.getScene().getWindow()).close();
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}