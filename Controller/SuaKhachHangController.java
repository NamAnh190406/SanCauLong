package com.mycompany.mavenproject1;

import com.mycompany.mavenproject1.model.KHACHHANG;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import java.sql.Connection;

public class SuaKhachHangController {

    @FXML private TextField txtMaKH;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtSDT;
    @FXML private TextField txtEmail;

    @FXML
    public void initialize() {
        // Lấy dữ liệu từ LuuThongTinDangNhap thay vì nhận qua setter
        KHACHHANG kh = LuuThongTinDangNhap.khachHangDangSua;
        if (kh != null) {
            txtMaKH.setText(kh.getMaKH());
            txtHoTen.setText(kh.getHoTen());
            txtSDT.setText(kh.getSDT());
            txtEmail.setText(kh.getEmail() != null ? kh.getEmail() : "");
        }
    }

    @FXML
    private void handleLuuCapNhat() {
        String ma = txtMaKH.getText();
        String hoTenMoi = txtHoTen.getText().trim();
        String sdtMoi = txtSDT.getText().trim();
        String emailMoi = txtEmail.getText().trim();

        if (hoTenMoi.isEmpty() || sdtMoi.isEmpty()) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi", "Họ tên và Số điện thoại không được để trống!");
            return;
        }

        try (Connection conn = DBContext.KetNoi()) {
            KHACHHANG model = new KHACHHANG();
            model.MaKH = ma;
            boolean thanhCong = model.CapNhatThongTin(hoTenMoi, sdtMoi, emailMoi.isEmpty() ? null : emailMoi);
            if (thanhCong) {
                hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật thông tin khách hàng!");
                handleThoat();
            } else {
                hienThongBao(Alert.AlertType.ERROR, "Thất bại", "Cập nhật thất bại, vui lòng kiểm tra lại!");
            }
        } catch (Exception e) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi Database", "Số điện thoại hoặc Email đã bị trùng!\nChi tiết: " + e.getMessage());
        }
    }

    @FXML
    private void handleThoat() {
        try {
            App.setRoot("QLyKhachHang");
        } catch (Exception e) {
            e.printStackTrace();
            hienThongBao(Alert.AlertType.ERROR, "Lỗi", "Không thể quay lại danh sách khách hàng!");
        }
    }

    private void hienThongBao(Alert.AlertType type, String tieuDe, String noiDung) {
        Alert alert = new Alert(type);
        alert.setTitle(tieuDe);
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }
}