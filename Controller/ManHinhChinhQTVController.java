package com.mycompany.mavenproject1;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class ManHinhChinhQTVController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println(">>> Màn hình Quản Trị Viên đã mở!");
    }

    @FXML
    private void handleMoThongKe() {
        try {
            App.setRoot("ThongKeDoanhThu"); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMoQuanLyNhanVien() {
        try { App.setRoot("QLyNhanVien"); } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML
    private void handleMoQuanLyKhachHang() {
        try { App.setRoot("QLyKhachHang"); } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML
    private void handleMoQuanLyDichVu() {
        try { App.setRoot("QLyDichVu"); } catch (Exception e) { e.printStackTrace(); }
    }
    
    @FXML
    private void handleMoQuanLyDanhGia() {
        try { App.setRoot("QLyDanhGia"); } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML
    private void handleMoQuanLySanVaKhungGio() {
        try { App.setRoot("QLySanVaKG"); } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML
    private void handleMoQuanLyBangGia() {
        try { App.setRoot("QLyBangGia"); } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML
    private void handleMoQuanLyQTV() {
        try { App.setRoot("QLyQTV"); } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML
    private void handleDangXuat() {
        try { App.setRoot("primary"); } catch (Exception e) { e.printStackTrace(); }
    }
}