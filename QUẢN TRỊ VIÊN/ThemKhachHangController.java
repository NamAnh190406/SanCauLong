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

public class ThemKhachHangController {

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

        // 1. Kiểm tra các trường bắt buộc rỗng
        if (ma.isEmpty() || hoTen.isEmpty() || sdt.isEmpty()) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Vui lòng nhập đầy đủ các thông tin bắt buộc (*)");
            return;
        }

        // Kiểm tra định dạng Email sơ bộ nếu người dùng có nhập
        if (!email.isEmpty() && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi định dạng", "Email nhập vào không đúng định dạng (phải chứa @ và .)");
            return;
        }

        // 2. Thực hiện chèn dữ liệu vào bảng KHACHHANG trong Oracle
        // Các cột NgayDK, HangThanhVien, DiemTichLuy đã có DEFAULT sẵn trong DB nên không cần chèn
        String sql = "INSERT INTO KHACHHANG (MaKH, HoTen, SDT, Email) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ma);
            pstmt.setString(2, hoTen);
            pstmt.setString(3, sdt);
            // Nếu email trống thì truyền giá trị NULL vào DB
            pstmt.setString(4, email.isEmpty() ? null : email);

            pstmt.executeUpdate();

            hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm khách hàng mới thành công!");
            lamMoiForm();

        } catch (SQLException e) {
            e.printStackTrace();
            // Bắt các lỗi trùng Khóa chính hoặc UNIQUE ràng buộc SĐT/Email từ Oracle
            if (e.getMessage().contains("pk_khachhang") || e.getMessage().contains("SYS_C")) {
                hienThongBao(Alert.AlertType.ERROR, "Lỗi Database", "Mã khách hàng, Số điện thoại hoặc Email đã tồn tại trên hệ thống!");
            } else {
                hienThongBao(Alert.AlertType.ERROR, "Lỗi kết nối", "Không thể thêm khách hàng. Chi tiết: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleThoat() {
        try {
            // Quay trở lại màn hình danh sách khách hàng chính xác
            FXMLLoader loader = new FXMLLoader(getClass().getResource("QLyKhachHang.fxml"));
            Parent view = loader.load();
            if (TrangChuController.rootPane != null) {
                TrangChuController.rootPane.setCenter(view);
            }
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
