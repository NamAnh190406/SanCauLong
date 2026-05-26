package Controller;

import Utils.Databasehelper;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import com.hoc.app_doan_scl.MainApp;
public class QuenMatKhau {

    @FXML private TextField txtEmail;
    @FXML private TextField txtSDT;
    @FXML private Label lblKiemTra;

    // Lưu tạm email và sdt để truyền sang màn 2
    public static String emailTam;
    public static String sdtTam;

    @FXML
    private void handleDatLaiMatKhau() {
        String email = txtEmail.getText().trim();
        String sdt   = txtSDT.getText().trim();

        // Kiểm tra không để trống
        if (email.isEmpty() || sdt.isEmpty()) {
            lblKiemTra.setText("Vui lòng nhập đầy đủ Email và Số điện thoại!");
            return;
        }

        // Kiểm tra email + sdt có tồn tại trong DB không
        if (kiemTraTonTai(email, sdt)) {
            // Lưu lại để màn 2 dùng
            emailTam = email;
            sdtTam   = sdt;
            // Chuyển sang màn nhập mật khẩu mới
            try {
                MainApp.setRoot("NhapLaiMK");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            lblKiemTra.setText("Email hoặc Số điện thoại không đúng!");
        }
    }

    private boolean kiemTraTonTai(String email, String sdt) {
        String sql = "SELECT COUNT(*) FROM KHACHHANG WHERE Email = ? AND SDT = ?";
        Databasehelper connectDB= new Databasehelper();
        try (Connection conn = connectDB.createCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, sdt);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    private void handleThoat(MouseEvent event) throws IOException {
        MainApp.setRoot("Primary"); // Quay về trang đăng nhập
    }
}