package com.mycompany.mavenproject1;

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

        if (txtPasswordShow != null) {
            txtPasswordShow.setVisible(false);
            txtPasswordShow.setManaged(false);
        }

        if (stackError != null) {
            stackError.setVisible(false);
            stackError.setManaged(false);
        }
    }

    @FXML
    public void togglePassword(MouseEvent event) {
        if (txtPassword.isVisible()) {
            txtPasswordShow.setText(txtPassword.getText());
            txtPasswordShow.setVisible(true);
            txtPasswordShow.setManaged(true);
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
            System.out.println("Dang hien mat khau");
        } else {
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
        String role = checkLogin();
        try {
            if (role == null) {
                if (stackError != null) {
                    stackError.setVisible(true);
                    stackError.setManaged(true);
                }
                return;
            }

            if (role.startsWith("KH")) {
                App.setRoot("ManHinhChinh");
            } else if (role.startsWith("NV")) {
                App.setRoot("ManHinhNhanVien");
            } else if (role.startsWith("QTV")) {
                App.setRoot("ManHinhQuanTri");
            } else {
                System.out.println(">>> Ma khong xac dinh: " + role);
                if (stackError != null) {
                    stackError.setVisible(true);
                    stackError.setManaged(true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String checkLogin() {
        String user = txtUsername.getText().trim();
        String pass = txtPassword.isVisible()
                      ? txtPassword.getText().trim()
                      : txtPasswordShow.getText().trim();

        System.out.println("User: [" + user + "] Pass: [" + pass + "]");

        String sql = "SELECT COUNT(*) FROM TAIKHOAN " +
                     "WHERE Username = ? AND Password = ?";

        try (Connection conn = DBContext.KetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user);
            pstmt.setString(2, pass);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println(">>> Login dung, dang lay thong tin...");

                    String sqlKH = "SELECT kh.MAKH, kh.HOTEN, kh.DIEMTICHLUY, " +
                                   "kh.HANGTHANHVIEN, kh.SDT, kh.EMAIL, kh.NGAYDK " +
                                   "FROM TAIKHOAN tk LEFT JOIN KHACHHANG kh ON tk.MA_TK = kh.MA_TK " +
                                   "WHERE tk.Username = ?";

                    try (PreparedStatement p2 = conn.prepareStatement(sqlKH)) {
                        p2.setString(1, user);
                        try (ResultSet rs2 = p2.executeQuery()) {
                            if (rs2.next()) {
                                LuuThongTinDangNhap.maKH          = rs2.getString(1);
                                LuuThongTinDangNhap.hoTen         = rs2.getString(2);
                                LuuThongTinDangNhap.diemTichLuy   = rs2.getInt(3);
                                LuuThongTinDangNhap.hangThanhVien = rs2.getString(4);
                                LuuThongTinDangNhap.sdt           = rs2.getString(5);
                                LuuThongTinDangNhap.email         = rs2.getString(6);
                                LuuThongTinDangNhap.ngayDK        = rs2.getDate(7);

                                String ma = LuuThongTinDangNhap.maKH;
                                System.out.println(">>> Login OK: " + LuuThongTinDangNhap.hoTen + " | Ma: " + ma);
                                return ma;
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println(">>> Login THAT BAI!");
        return null;
    }

    @FXML
    private void closeError() {
        if (stackError != null) {
            stackError.setVisible(false);
            stackError.setManaged(false);
        }
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