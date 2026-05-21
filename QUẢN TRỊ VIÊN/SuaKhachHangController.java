package com.example.guidemo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.sql.Connection;

public class SuaKhachHangController {

    @FXML private TextField txtMaKH;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtSDT;
    @FXML private TextField txtEmail;

    // Hàm nhận dữ liệu từ trang quản lý đổ sang
    public void setKhachHangBanDau(KHACHHANG kh) {
        txtMaKH.setText(kh.getMaKH());
        txtHoTen.setText(kh.getHoTen());
        txtSDT.setText(kh.getSDT());
        txtEmail.setText(kh.getEmail() != null ? kh.getEmail() : "");
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

        // Tận dụng chính hàm CapNhatThongTin bạn viết sẵn trong Model KHACHHANG
        try (Connection conn = DBConnection.getConnection()) {
            KHACHHANG model = new KHACHHANG();
            model.MaKH = ma; // gán khóa để đối chiếu WHERE

            boolean thanhCong = model.CapNhatThongTin(hoTenMoi, sdtMoi, emailMoi.isEmpty() ? null : emailMoi);

            if (thanhCong) {
                hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật thông tin khách hàng!");
                handleThoat(); // Tự động quay lại trang danh sách khách hàng
            } else {
                hienThongBao(Alert.AlertType.ERROR, "Thất bại", "Cập nhật thất bại, vui lòng kiểm tra lại!");
            }
        } catch (Exception e) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi Database", "Số điện thoại hoặc Email đã bị trùng! \nChi tiết: " + e.getMessage());
        }
    }

    @FXML
    private void handleThoat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("QLyKhachHang.fxml"));
            Parent view = loader.load();
            if (TrangChuController.rootPane != null) {
                TrangChuController.rootPane.setCenter(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
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