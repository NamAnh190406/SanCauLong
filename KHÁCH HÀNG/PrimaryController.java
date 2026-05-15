package com.mycompany.mavenproject1;

import com.mycompany.mavenproject1.databse.DBContext; 
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class PrimaryController implements Initializable {

    // 1. Khai báo các ID từ Scene Builder
    @FXML
    private PasswordField txtPassword; 
    @FXML
    private TextField txtPasswordShow; 
    @FXML
    private Label lblStatus;
    @FXML
    private TextField txtUsername; 
    @FXML
    private StackPane stackError; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // --- PHẦN KẾT NỐI DATABASE ---
        System.out.println("--- DANG THU KET NOI ORACLE ---");
        try {
            DBContext db = new DBContext();
            Connection con = db.KetNoi();
            if (con != null) {
                System.out.println(">>> KET NOI THANH CONG ROI DO MA!");
                con.close();
            }
        } catch (Exception e) {
            System.out.println("Loi ket noi: " + e.getMessage());
        }

        // --- PHẦN CÀI ĐẶT BAN ĐẦU CHO GIAO DIỆN ---
        if (txtPasswordShow != null) {
            txtPasswordShow.setVisible(false);
            txtPasswordShow.setManaged(false);
        }
        
        if (stackError != null) {
            stackError.setVisible(false);
            stackError.setManaged(false); 
        }
    }

    // 2. Hàm xử lý con mắt hiện/ẩn mật khẩu
    @FXML
    public void togglePassword(MouseEvent event) {
        if (txtPassword.isVisible()) {
            // Hiện mật khẩu
            txtPasswordShow.setText(txtPassword.getText());
            txtPasswordShow.setVisible(true);
            txtPasswordShow.setManaged(true);
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
            System.out.println("Dang hien mat khau");
        } else {
            // Ẩn mật khẩu
            txtPassword.setText(txtPasswordShow.getText());
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
            txtPasswordShow.setVisible(false);
            txtPasswordShow.setManaged(false);
            System.out.println("Dang an mat khau");
        }
    }

    @FXML
    private void handleLogin() {
        System.out.println("stackError là: " + stackError); // DEBUG
        
        if (checkLogin()) {
            System.out.println("Đăng nhập thành công!");
            // Đã đưa đoạn chuyển màn hình vào đúng vị trí hợp lệ
            try {
                App.setRoot("ManHinhChinh");
            } catch (IOException e) {
                System.out.println("Lỗi khi chuyển sang ManHinhChinh: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Sai rồi, đang hiện bảng lỗi...");
            if (stackError != null) {
                stackError.setVisible(true);
                stackError.setManaged(true); 
            } else {
                System.out.println("LỖI: stackError đang bị NULL!");
            }
        }
    }

    private boolean checkLogin() {
        String user = txtUsername.getText();
        String pass = txtPassword.isVisible() 
                      ? txtPassword.getText() 
                      : txtPasswordShow.getText();

        String sql = "SELECT kh.MaKH, kh.HoTen, kh.DiemTichLuy, kh.HangThanhVien " +
                     "FROM TAIKHOAN tk JOIN KHACHHANG kh ON tk.Ma_TK = kh.Ma_TK " +
                     "WHERE tk.Username = ? AND tk.Password = ?";

        try (Connection conn = DBContext.KetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user);
            pstmt.setString(2, pass);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Lưu vào Session (Sử dụng biến public trực tiếp)
                    LuuThongTinDangNhap.maKH          = rs.getString("MaKH");
                    LuuThongTinDangNhap.hoTen         = rs.getString("HoTen");
                    LuuThongTinDangNhap.diemTichLuy   = rs.getInt("DiemTichLuy");
                    LuuThongTinDangNhap.hangThanhVien = rs.getString("HangThanhVien");
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    private void closeError() {
        if (stackError != null) {
            stackError.setVisible(false); 
        }
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    private void gotoDangKyTaiKhoan(MouseEvent event) throws IOException {
        App.setRoot("DangKyTaiKhoan");
    }

    @FXML
    private void gotoQuenMatKhau(MouseEvent event) throws IOException {
        App.setRoot("QuenMatKhau");
    }
}