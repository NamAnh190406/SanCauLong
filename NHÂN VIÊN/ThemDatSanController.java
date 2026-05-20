package Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ThemDatSanController implements Initializable {

    @FXML private TextField txtTenKhachHang;
    @FXML private TextField txtSoDienThoai;
    @FXML private ComboBox<String> cbxChonSan;
    @FXML private ComboBox<String> cbxGioBatDau;
    @FXML private ComboBox<String> cbxThoiLuong;

    @FXML private Label lblPreviewSan;
    @FXML private Label lblPreviewGio;
    @FXML private Label lblPreviewGia;

    @FXML private Button btnHuy;
    @FXML private Button btnXacNhan;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Init mock data
        cbxChonSan.getItems().addAll("Sân 1", "Sân 2", "Sân 3", "Sân 4", "Sân VIP 1");
        cbxGioBatDau.getItems().addAll("08:00", "09:00", "10:00", "14:00", "15:00", "18:00", "19:00");
        cbxThoiLuong.getItems().addAll("1 giờ", "2 giờ", "3 giờ");

        // Action cho nút Huỷ
        btnHuy.setOnAction(e -> {
            Stage stage = (Stage) btnHuy.getScene().getWindow();
            stage.close();
        });
        
        // Action cho nút Xác nhận
        btnXacNhan.setOnAction(e -> {
            System.out.println("Đã thêm đặt sân: " + txtTenKhachHang.getText());
            Stage stage = (Stage) btnXacNhan.getScene().getWindow();
            stage.close();
        });
    }
}
