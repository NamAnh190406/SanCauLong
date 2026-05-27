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
        String ketQua = datLaiMatKhau( QuenMatKhau.emailTam,QuenMatKhau.sdtTam,matKhauMoi);

        lblKiemTra.setText(ketQua);

        // Nếu thành công thì tự động quay về trang đăng nhập sau 1 giây
        if (ketQua.equals("Đặt lại mật khẩu thành công!")) {
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Chờ 1 giây cho user đọc thông báo
                    javafx.application.Platform.runLater(() -> {
                        try {
                            MainApp.setRoot("Primary.fxml");
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
        String sql = "SELECT f_QuenMatKhau(?, ?, ?) FROM DUAL";
        Databasehelper connectDB = new Databasehelper();
        try (Connection conn = connectDB.createCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, sdt);
            pstmt.setString(3, matKhauMoi);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Lỗi không xác định!";
    }

    @FXML
    private void handleThoat(MouseEvent event) throws IOException {
        MainApp.setRoot("QuenMatKhau"); // Quay lại màn nhập email + sdt
    }
}