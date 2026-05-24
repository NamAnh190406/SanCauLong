package com.mycompany.mavenproject1;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class NhapLaiMKController {

    @FXML private TextField txtMatKhauMoi;
    @FXML private TextField txtNhapLaiMatKhau;
    @FXML private Label lblKiemTra;

    @FXML
    private void handleDatLaiMatKhau() {
        String matKhauMoi    = txtMatKhauMoi.getText();
        String nhapLaiMatKhau = txtNhapLaiMatKhau.getText();

        // Kiểm tra không để trống
        if (matKhauMoi.isEmpty() || nhapLaiMatKhau.isEmpty()) {
            lblKiemTra.setText("Vui lòng nhập đầy đủ mật khẩu!");
            return;
        }

        // Kiểm tra 2 mật khẩu có khớp không
        if (!matKhauMoi.equals(nhapLaiMatKhau)) {
            lblKiemTra.setText("Mật khẩu nhập lại không khớp!");
            return;
        }

        // Kiểm tra độ dài tối thiểu
        if (matKhauMoi.length() < 6) {
            lblKiemTra.setText("Mật khẩu phải có ít nhất 6 ký tự!");
            return;
        }

        // Gọi function Oracle để đặt lại mật khẩu
        String ketQua = datLaiMatKhau(
            QuenMatKhau.emailTam,
            QuenMatKhau.sdtTam,
            matKhauMoi
        );

        lblKiemTra.setText(ketQua);

        // Nếu thành công thì tự động quay về trang đăng nhập sau 1 giây
        if (ketQua.equals("Đặt lại mật khẩu thành công!")) {
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Chờ 1 giây cho user đọc thông báo
                    javafx.application.Platform.runLater(() -> {
                        try {
                            App.setRoot("Primary");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private String datLaiMatKhau(String email, String sdt, String matKhauMoi) {
    String sqlSelect = "SELECT tk.Ma_TK FROM KHACHHANG kh " +
                       "JOIN TAIKHOAN tk ON kh.Ma_TK = tk.Ma_TK " +
                       "WHERE kh.Email = ? AND kh.SDT = ?";
    
    String sqlUpdate = "UPDATE TAIKHOAN SET Password = ? WHERE Ma_TK = ?";
    
    try (Connection conn = DBContext.KetNoi()) {
        String maTK = null;
        try (PreparedStatement ps = conn.prepareStatement(sqlSelect)) {
            ps.setString(1, email);
            ps.setString(2, sdt);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                maTK = rs.getString("Ma_TK");
            } else {
                return "Email hoặc số điện thoại không đúng!";
            }
        }
        
        try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
            ps.setString(1, matKhauMoi);
            ps.setString(2, maTK);
            ps.executeUpdate();
        }
        
        return "Đặt lại mật khẩu thành công!";
        
    } catch (SQLException e) {
        e.printStackTrace();
        return "Lỗi SQL: " + e.getMessage();
    }
}
    
    
    @FXML
    private void handleThoat(MouseEvent event) throws IOException {
        App.setRoot("QuenMatKhau"); // Quay lại màn nhập email + sdt
    }
}