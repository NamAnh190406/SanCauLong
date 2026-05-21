package com.example.guidemo;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SuaTaiKhoanController {

    @FXML private TextField txtMaTK;
    @FXML private TextField txtUsername;
    @FXML private TextField txtPassword;
    @FXML private ChoiceBox<String> choiceVaiTro;
    @FXML private ChoiceBox<String> choiceTrangThai;

    @FXML
    public void initialize() {
        // Cấu hình các lựa chọn thả xuống khớp với CONSTRAINT trong Oracle
        choiceVaiTro.setItems(FXCollections.observableArrayList("Admin", "NhanVien", "KhachHang"));
        choiceTrangThai.setItems(FXCollections.observableArrayList("HoatDong", "KhoaAccount"));
    }

    // 🌟 ĐÂY CHÍNH LÀ HÀM GIẢI QUYẾT LỖI "Cannot resolve method" CỦA BẠN
    public void setTaiKhoanBanDau(TAIKHOANModel tk) {
        txtMaTK.setText(tk.getMaTK());
        txtMaTK.setEditable(false); // Không cho sửa khóa chính

        txtUsername.setText(tk.getUsername());
        txtUsername.setEditable(false); // Thường username cũng sẽ giữ cố định

        txtPassword.setText(tk.getPassword());
        choiceVaiTro.setValue(tk.getVaiTro());
        choiceTrangThai.setValue(tk.getTrangThai());
    }

    @FXML
    private void handleLuuCapNhat() {
        String password = txtPassword.getText().trim();
        String vaiTro = choiceVaiTro.getValue();
        String trangThai = choiceTrangThai.getValue();

        if (password.isEmpty()) {
            hienThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Mật khẩu không được để trống!");
            return;
        }

        String sql = "UPDATE TAIKHOAN SET Password = ?, VaiTro = ?, TrangThai = ? WHERE Ma_TK = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, password); // Trong thực tế nên hash MD5/BCrypt, ở đây lưu chuỗi theo DB của bạn
            pstmt.setString(2, vaiTro);
            pstmt.setString(3, trangThai);
            pstmt.setString(4, txtMaTK.getText());

            pstmt.executeUpdate();
            hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật thông tin tài khoản thành công!");
            quayLai();
        } catch (SQLException e) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi Oracle", "Cập nhật thất bại: " + e.getMessage());
        }
    }

    @FXML private void handleHuyBo() { quayLai(); }

    private void quayLai() {
        try {
            Parent v = FXMLLoader.load(getClass().getResource("QLyQTV.fxml"));
            if (TrangChuController.rootPane != null) TrangChuController.rootPane.setCenter(v);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void hienThongBao(Alert.AlertType t, String title, String c) {
        Alert a = new Alert(t, c); a.setTitle(title); a.setHeaderText(null); a.showAndWait();
    }
}