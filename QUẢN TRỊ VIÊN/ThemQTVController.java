package com.example.guidemo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ThemQTVController {

    @FXML private TextField txtMaQTV;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtSDT;
    @FXML private TextField txtEmail;

    @FXML
    private void handleThem() {
        String ma = txtMaQTV.getText().trim();
        String hoTen = txtHoTen.getText().trim();

        if (ma.isEmpty() || hoTen.isEmpty()) {
            hienThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ Mã QTV và Họ tên!");
            return;
        }

        // Đồng bộ quy đổi dữ liệu sang bảng TAIKHOAN duy nhất trong DB của bạn
        String maTK = ma;

        // Tạo username tự động từ họ tên viết liền không dấu
        String username = hoTen.toLowerCase().replaceAll("\\s+", "");
        if (username.length() < 4) {
            username = username + "123"; // Đảm bảo không dính lỗi CONSTRAINT độ dài của Oracle
        }

        String password = "123"; // Mật khẩu mặc định
        String vaiTro = "Admin";   // Tự động gán quyền Admin vĩnh viễn

        String sql = "INSERT INTO TAIKHOAN (Ma_TK, Username, Password, VaiTro, TrangThai) VALUES (?, ?, ?, ?, 'HoatDong')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maTK);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.setString(4, vaiTro);

            pstmt.executeUpdate();
            hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Thêm quản trị viên mới thành công!");

            handleThoat(); // Quay lại màn hình danh sách chính

        } catch (SQLException e) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi Oracle", "Mã QTV hoặc Tên đăng nhập tự động đã tồn tại!\nChi tiết: " + e.getMessage());
        }
    }

    @FXML
    private void handleThoat() {
        try {
            Parent v = FXMLLoader.load(getClass().getResource("QLyQTV.fxml"));
            if (TrangChuController.rootPane != null) {
                TrangChuController.rootPane.setCenter(v);
            } else {
                txtMaQTV.getScene().setRoot(v);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void hienThongBao(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type, content);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}