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

public class ThemTaiKhoanController {

    @FXML private TextField txtMaTK;
    @FXML private TextField txtUsername;
    @FXML private TextField txtPassword;
    @FXML private ChoiceBox<String> choiceVaiTro;

    @FXML
    public void initialize() {
        choiceVaiTro.setItems(FXCollections.observableArrayList("Admin", "NhanVien", "KhachHang"));
        choiceVaiTro.setValue("NhanVien"); // Giá trị mặc định định hướng
    }

    @FXML
    private void handleLuuThemMoi() {
        String ma = txtMaTK.getText().trim();
        String user = txtUsername.getText().trim();
        String pass = txtPassword.getText().trim();
        String role = choiceVaiTro.getValue();

        if (ma.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            hienThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ thông tin bắt buộc!");
            return;
        }

        String sql = "INSERT INTO TAIKHOAN (Ma_TK, Username, Password, VaiTro, TrangThai) VALUES (?, ?, ?, ?, 'HoatDong')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ma);
            pstmt.setString(2, user);
            pstmt.setString(3, pass);
            pstmt.setString(4, role);

            pstmt.executeUpdate();
            hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Cấp tài khoản mới thành công!");
            quayLai();
        } catch (SQLException e) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi Oracle", "Không thể thêm tài khoản. Hãy kiểm tra lại độ dài username (>=4 ký tự) hoặc mã trùng!\nChi tiết: " + e.getMessage());
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