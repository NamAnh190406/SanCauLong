package com.mycompany.mavenproject1;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class DangKyTaiKhoanController implements Initializable {

    @FXML private TextField txtHoTen;
    @FXML private TextField txtSdt;
    @FXML private TextField txtEmail;
    @FXML private TextField txtUsername;
    @FXML private TextField txtPassword;
    @FXML private Label lblKiemTra;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblKiemTra.setText("Kiểm tra:");
        lblKiemTra.setStyle("-fx-text-fill: white;");
        Platform.runLater(() -> txtHoTen.getParent().requestFocus());
    }

    @FXML
    private void handleDangKy() throws IOException {
        String hoten    = txtHoTen.getText().trim();
        String sdt      = txtSdt.getText().trim();
        String email    = txtEmail.getText().trim();
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (hoten.isEmpty() || sdt.isEmpty() || email.isEmpty()
                || username.isEmpty() || password.isEmpty()) {
            setKetQua(false, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        if (!sdt.matches("^(0[3-9]\\d{8})$")) {
            setKetQua(false, "Số điện thoại không đúng định dạng!");
            return;
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            setKetQua(false, "Email không đúng định dạng!");
            return;
        }
        if (password.length() < 6) {
            setKetQua(false, "Mật khẩu phải có ít nhất 6 ký tự!");
            return;
        }

        // Kiểm tra tài khoản đã tồn tại chưa
        if (kiemTraTonTai(username, sdt, email)) {
            setKetQua(false, "Đã có tài khoản với thông tin này!");
            return;
        }

        dangKyQuaFunction(hoten, sdt, email, username, password);
    }

    private boolean kiemTraTonTai(String username, String sdt, String email) {
        String sql = "SELECT COUNT(*) FROM TAIKHOAN tk LEFT JOIN KHACHHANG kh ON tk.Ma_TK = kh.Ma_TK " +
                     "WHERE tk.Username = ? OR kh.SDT = ? OR kh.Email = ?";
        try (Connection conn = DBContext.KetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, sdt);
            pstmt.setString(3, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void dangKyQuaFunction(String hoten, String sdt, String email,
                                    String username, String password) throws IOException {
        String sql = "{ ? = call f_DangKy(?, ?, ?, ?, ?) }";
        try (Connection conn = DBContext.KetNoi();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.registerOutParameter(1, java.sql.Types.VARCHAR);
            cstmt.setString(2, hoten);
            cstmt.setString(3, sdt);
            cstmt.setString(4, email);
            cstmt.setString(5, username);
            cstmt.setString(6, password);

            cstmt.execute();
            String ketQua = cstmt.getString(1);

            if ("Đăng ký thành công!".equals(ketQua)) {
                setKetQua(true, ketQua);
                App.setRoot("primary");
            } else {
                setKetQua(false, ketQua);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            setKetQua(false, "Lỗi: " + e.getMessage());
        }
    }

    private void setKetQua(boolean hopLe, String message) {
        lblKiemTra.setText("Kiểm tra: " + message);
        lblKiemTra.setStyle(hopLe
            ? "-fx-text-fill: #7de0a8; -fx-font-weight: BOLD;"
            : "-fx-text-fill: #ff7070; -fx-font-weight: BOLD;");
    }

    @FXML
    private void thoat(MouseEvent event) throws IOException {
        App.setRoot("primary");
    }
}