package Controller;

import Controller.LuuThongTinDangNhap;
import Utils.Databasehelper;
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
import com.hoc.app_doan_scl.MainApp;

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
            Databasehelper connectDB = new Databasehelper();
            Connection con = connectDB.createCon();
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
            System.out.println("Đăng nhập thành công! Vai trò: " + LuuThongTinDangNhap.vaiTro);
            try {
                if ("KhachHang".equalsIgnoreCase(LuuThongTinDangNhap.vaiTro)) {
                    MainApp.setRoot("ManHinhChinh");
                } else {
                    MainApp.setRoot("TrangChuUI");
                }
            } catch (IOException e) {
                System.out.println("Lỗi khi chuyển màn hình: " + e.getMessage());
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

        String sqlTK = "SELECT Ma_TK, VaiTro FROM TAIKHOAN WHERE Username = ? AND Password = ?";
        Databasehelper connectDB = new Databasehelper();
        try (Connection conn = connectDB.createCon();
                PreparedStatement pstmtTK = conn.prepareStatement(sqlTK)) {

            pstmtTK.setString(1, user);
            pstmtTK.setString(2, pass);

            try (ResultSet rsTK = pstmtTK.executeQuery()) {
                if (rsTK.next()) {
                    String maTK = rsTK.getString("Ma_TK");
                    String vaiTro = rsTK.getString("VaiTro");
                    LuuThongTinDangNhap.vaiTro = vaiTro;

                    if ("KhachHang".equalsIgnoreCase(vaiTro)) {
                        String sqlKH = "SELECT MaKH, HoTen, DiemTichLuy, HangThanhVien FROM KHACHHANG WHERE Ma_TK = ?";
                        try (PreparedStatement pstmtKH = conn.prepareStatement(sqlKH)) {
                            pstmtKH.setString(1, maTK);
                            try (ResultSet rsKH = pstmtKH.executeQuery()) {
                                if (rsKH.next()) {
                                    LuuThongTinDangNhap.maKH = rsKH.getString("MaKH");
                                    LuuThongTinDangNhap.hoTen = rsKH.getString("HoTen");
                                    LuuThongTinDangNhap.diemTichLuy = rsKH.getInt("DiemTichLuy");
                                    LuuThongTinDangNhap.hangThanhVien = rsKH.getString("HangThanhVien");
                                    return true;
                                }
                            }
                        }
                    } else {
                        // NhanVien, QuanTriVien, Admin, QuanLy...
                        String sqlNV = "SELECT MA_NV, HOTEN_NV, CHUCVU FROM NHAN_VIEN WHERE Ma_TK = ?";
                        try (PreparedStatement pstmtNV = conn.prepareStatement(sqlNV)) {
                            pstmtNV.setString(1, maTK);
                            try (ResultSet rsNV = pstmtNV.executeQuery()) {
                                if (rsNV.next()) {
                                    LuuThongTinDangNhap.maNV = rsNV.getString("MA_NV");
                                    LuuThongTinDangNhap.hoTen = rsNV.getString("HOTEN_NV");
                                    LuuThongTinDangNhap.chucVu = rsNV.getString("CHUCVU");
                                    return true;
                                }
                            }
                        }
                    }
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
        MainApp.setRoot("secondary");
    }

    @FXML
    private void gotoDangKyTaiKhoan(MouseEvent event) throws IOException {
        MainApp.setRoot("DangKyTaiKhoan");
    }

    @FXML
    private void gotoQuenMatKhau(MouseEvent event) throws IOException {
        MainApp.setRoot("QuenMatKhau");
    }

    // --- XỬ LÝ CÁC NÚT ĐIỀU KHIỂN CỬA SỔ ---
    @FXML
    private void handleMinimize(MouseEvent event) {
        javafx.stage.Stage stage = (javafx.stage.Stage) txtUsername.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleRestore(MouseEvent event) {
        javafx.stage.Stage stage = (javafx.stage.Stage) txtUsername.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    @FXML
    private void handleClose(MouseEvent event) {
        javafx.application.Platform.exit();
        System.exit(0);
    }
}