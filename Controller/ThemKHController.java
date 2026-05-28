package com.mycompany.mavenproject1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ThemKHController {

    @FXML private TextField txtMaKH;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtSDT;
    @FXML private TextField txtEmail;

    @FXML
    private void handleLuuKhachHang() {
        String ma = txtMaKH.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();

        if (ma.isEmpty() || hoTen.isEmpty() || sdt.isEmpty()) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Vui lòng nhập đầy đủ các thông tin bắt buộc (*)");
            return;
        }

        if (!sdt.matches("^[0-9]{9,11}$")) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi định dạng", "Số điện thoại phải từ 9 đến 11 chữ số!");
            return;
        }

        if (!email.isEmpty() && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi định dạng", "Email nhập vào không đúng định dạng (phải chứa @ và .)");
            return;
        }

        String sql = "INSERT INTO KHACHHANG (MaKH, HoTen, SDT, Email, HangThanhVien, NgayDK, DiemTichLuy) VALUES (?, ?, ?, ?, ?, SYSDATE, 0)";
        try (Connection conn = DBContext.KetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ma);
            pstmt.setString(2, hoTen);
            pstmt.setString(3, sdt);
            pstmt.setString(4, email.isEmpty() ? null : email);
            pstmt.setString(5, "Đồng");

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm khách hàng mới thành công!");
                lamMoiForm();
            } else {
                hienThongBao(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm khách hàng!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            hienThongBao(Alert.AlertType.ERROR, "Lỗi Database", "Không thể thêm khách hàng. Chi tiết: " + e.getMessage());
        }
    }

    @FXML
    private void handleThoat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("QLyKhachHang.fxml"));
            Parent view = loader.load();
            txtMaKH.getScene().setRoot(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void lamMoiForm() {
        txtMaKH.clear();
        txtHoTen.clear();
        txtSDT.clear();
        txtEmail.clear();
    }

    private void hienThongBao(Alert.AlertType type, String tieuDe, String noiDung) {
        Alert alert = new Alert(type);
        alert.setTitle(tieuDe);
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }
}