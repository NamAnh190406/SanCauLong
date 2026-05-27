package Controller;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class ThongTinCaNhanController implements Initializable {

    // ================= FXML INJECTIONS =================
    // Header
    @FXML private Label lblHeaderName;
    @FXML private Label lblHeaderRole;
    @FXML private Button btnEdit;

    // Các trường thông tin (TextFields)
    @FXML private TextField tfEmpId;
    @FXML private TextField tfJoinDate;
    @FXML private TextField tfEmail;
    @FXML private TextField tfAddress;
    @FXML private TextField tfPhone;
    @FXML private TextField tfRole;

    // Box chứa 2 nút Hủy / Lưu (Mặc định ẩn)
    @FXML private HBox boxActions;

    // Đổi mật khẩu
    @FXML private PasswordField pfOld;
    @FXML private PasswordField pfNew;
    @FXML private PasswordField pfConfirm;

    // ================= BIẾN TRẠNG THÁI =================
    private boolean isEditing = false;
    
    // Lưu lại dữ liệu cũ để phục hồi nếu bấm "Hủy"
    private String oldEmail = "";
    private String oldAddress = "";
    private String oldPhone = "";

    // Mật khẩu giả lập (Để test chức năng đổi mật khẩu)
    private String currentPasswordDB = "123456"; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Load dữ liệu cá nhân khi vừa mở form
        loadUserInfo();
        
        // Khóa tất cả các trường không cho sửa ban đầu
        setFieldsEditable(false);
    }

    // ================= LOAD DỮ LIỆU =================
    private void loadUserInfo() {
        // Lấy thông tin từ session đăng nhập
        String maNV   = Controller.LuuThongTinDangNhap.maNV;
        String hoTen  = Controller.LuuThongTinDangNhap.hoTen;
        String chucVu = Controller.LuuThongTinDangNhap.chucVu;
        String maKH   = Controller.LuuThongTinDangNhap.maKH;

        // Hiển thị header từ session (đã có sau đăng nhập)
        lblHeaderName.setText(hoTen != null && !hoTen.isEmpty() ? hoTen : "Người dùng");
        lblHeaderRole.setText(chucVu != null && !chucVu.isEmpty() ? chucVu : "Thành viên");

        if (maNV != null && !maNV.isEmpty()) {
            // Nhân viên — query bảng NHAN_VIEN
            String sql = "SELECT MA_NV, HOTEN_NV, SDT, DIACHI, EMAIL, NGAYBATDAU, CHUCVU " +
                         "FROM NHAN_VIEN WHERE MA_NV = ?";
            try (java.sql.Connection conn = new Utils.Databasehelper().createCon();
                 java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, maNV);
                try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        tfEmpId.setText(rs.getString("MA_NV"));
                        tfPhone.setText(nvl(rs.getString("SDT")));
                        tfAddress.setText(nvl(rs.getString("DIACHI")));
                        tfEmail.setText(nvl(rs.getString("EMAIL")));
                        tfRole.setText(nvl(rs.getString("CHUCVU")));
                        java.sql.Date nd = rs.getDate("NGAYBATDAU");
                        tfJoinDate.setText(nd != null ? nd.toLocalDate().toString() : "---");
                    }
                }
            } catch (java.sql.SQLException e) {
                System.err.println("Lỗi load thông tin nhân viên: " + e.getMessage());
                setDefaultFields(maNV, chucVu);
            }
        } else if (maKH != null && !maKH.isEmpty()) {
            // Khách hàng — query bảng KHACHHANG
            String sql = "SELECT MaKH, HoTen, SDT, DiaChi, HangThanhVien, DiemTichLuy " +
                         "FROM KHACHHANG WHERE MaKH = ?";
            try (java.sql.Connection conn = new Utils.Databasehelper().createCon();
                 java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, maKH);
                try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        tfEmpId.setText(rs.getString("MaKH"));
                        tfPhone.setText(nvl(rs.getString("SDT")));
                        tfAddress.setText(nvl(rs.getString("DiaChi")));
                        tfEmail.setText("---");
                        tfRole.setText(nvl(rs.getString("HangThanhVien")) +
                                       " (" + rs.getInt("DiemTichLuy") + " điểm)");
                        tfJoinDate.setText("---");
                    }
                }
            } catch (java.sql.SQLException e) {
                System.err.println("Lỗi load thông tin khách hàng: " + e.getMessage());
                setDefaultFields(maKH, "Khách hàng");
            }
        } else {
            setDefaultFields("---", "---");
        }
    }

    private void setDefaultFields(String id, String role) {
        tfEmpId.setText(id != null ? id : "---");
        tfPhone.setText("---");
        tfAddress.setText("---");
        tfEmail.setText("---");
        tfRole.setText(role != null ? role : "---");
        tfJoinDate.setText("---");
    }

    private String nvl(String s) {
        return s != null && !s.isEmpty() ? s : "---";
    }


    // ================= CHỈNH SỬA THÔNG TIN =================
    @FXML
    private void onEditToggle(ActionEvent event) {
        // Lưu lại dữ liệu trước khi sửa
        oldEmail = tfEmail.getText();
        oldAddress = tfAddress.getText();
        oldPhone = tfPhone.getText();

        isEditing = true;
        setFieldsEditable(true);

        // Ẩn nút "Chỉnh sửa" và Hiện 2 nút "Hủy" / "Lưu"
        btnEdit.setVisible(false);
        btnEdit.setManaged(false);
        
        boxActions.setVisible(true);
        boxActions.setManaged(true);
        
        // Focus ngay vào ô Email
        tfEmail.requestFocus(); 
    }

    @FXML
    private void onCancelEdit(ActionEvent event) {
        // Phục hồi lại dữ liệu cũ
        tfEmail.setText(oldEmail);
        tfAddress.setText(oldAddress);
        tfPhone.setText(oldPhone);

        endEditingMode();
    }

    @FXML
    private void onSaveEdit(ActionEvent event) {
        // Lấy dữ liệu mới
        String newEmail = tfEmail.getText().trim();
        String newAddress = tfAddress.getText().trim();
        String newPhone = tfPhone.getText().trim();

        // Validate cơ bản
        if (newEmail.isEmpty() || newPhone.isEmpty()) {
            showAlert(AlertType.WARNING, "Lỗi", "Email và Số điện thoại không được để trống!");
            return;
        }

        // TODO: Viết hàm UPDATE lên Database ở đây
        System.out.println("Đã lưu vào DB: " + newEmail + " - " + newPhone + " - " + newAddress);

        showAlert(AlertType.INFORMATION, "Thành công", "Đã cập nhật thông tin cá nhân!");
        endEditingMode();
    }

    private void endEditingMode() {
        isEditing = false;
        setFieldsEditable(false);

        // Khôi phục giao diện nút bấm
        boxActions.setVisible(false);
        boxActions.setManaged(false);
        
        btnEdit.setVisible(true);
        btnEdit.setManaged(true);
    }

    /**
     * Hàm dùng để tắt/mở khả năng gõ chữ vào TextField
     * Lưu ý: ID, Ngày vào làm và Chức vụ không bao giờ được phép sửa!
     */
    private void setFieldsEditable(boolean editable) {
        tfEmail.setEditable(editable);
        tfAddress.setEditable(editable);
        tfPhone.setEditable(editable);
        
        // Đổi màu nền để người dùng dễ nhận biết ô nào đang được sửa
        String styleEdit = "-fx-background-color: white; -fx-border-color: #16a34a; -fx-border-radius: 8; -fx-padding: 8;";
        String styleReadonly = "-fx-background-color: #f9fafb; -fx-border-color: #e5e7eb; -fx-border-radius: 8; -fx-padding: 8;";

        tfEmail.setStyle(editable ? styleEdit : styleReadonly);
        tfAddress.setStyle(editable ? styleEdit : styleReadonly);
        tfPhone.setStyle(editable ? styleEdit : styleReadonly);
    }

    // ================= ĐỔI MẬT KHẨU =================
    @FXML
    private void onChangePassword(ActionEvent event) {
        String oldPass = pfOld.getText();
        String newPass = pfNew.getText();
        String confirmPass = pfConfirm.getText();

        // 1. Kiểm tra bỏ trống
        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            showAlert(AlertType.WARNING, "Lỗi", "Vui lòng nhập đầy đủ các trường mật khẩu!");
            return;
        }

        // 2. Kiểm tra mật khẩu cũ (So với DB)
        if (!oldPass.equals(currentPasswordDB)) {
            showAlert(AlertType.ERROR, "Sai mật khẩu", "Mật khẩu hiện tại không chính xác!");
            return;
        }

        // 3. Kiểm tra độ dài mật khẩu mới
        if (newPass.length() < 6) {
            showAlert(AlertType.WARNING, "Mật khẩu yếu", "Mật khẩu mới phải có ít nhất 6 ký tự!");
            return;
        }

        // 4. Kiểm tra xác nhận mật khẩu
        if (!newPass.equals(confirmPass)) {
            showAlert(AlertType.ERROR, "Lỗi xác nhận", "Mật khẩu xác nhận không khớp với mật khẩu mới!");
            return;
        }

        // 5. TODO: UPDATE mật khẩu mới lên Database
        currentPasswordDB = newPass; // Giả lập đã lưu

        showAlert(AlertType.INFORMATION, "Thành công", "Đổi mật khẩu thành công!");
        
        pfOld.clear();
        pfNew.clear();
        pfConfirm.clear();
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}