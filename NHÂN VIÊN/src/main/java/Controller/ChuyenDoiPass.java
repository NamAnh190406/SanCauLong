package Controller; // Kiểm tra lại đúng package của bạn

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView; // Thư viện FontAwesome
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class ChuyenDoiPass implements Initializable {

    @FXML
    private PasswordField txtPassword; // Ô dấu chấm

    @FXML
    private TextField txtPasswordShow; // Ô hiện chữ

    @FXML
    private FontAwesomeIconView eyeIcon; // Icon con mắt

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Mặc định ẩn ô hiện chữ đi
        txtPasswordShow.setVisible(false);
        txtPasswordShow.setManaged(false); // Để nó không chiếm không gian khi ẩn
        
        // Đồng bộ dữ liệu giữa 2 ô: gõ bên này bên kia tự nhận
        txtPassword.textProperty().bindBidirectional(txtPasswordShow.textProperty());
    }

    @FXML
    private void togglePassword(MouseEvent event) {
        if (txtPassword.isVisible()) {
            // Chuyển sang chế độ HIỆN mật khẩu
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
            
            txtPasswordShow.setVisible(true);
            txtPasswordShow.setManaged(true);
            
            // Đổi icon sang mắt gạch chéo
            eyeIcon.setGlyphName("EYE_SLASH");
        } else {
            // Chuyển sang chế độ ẨN mật khẩu
            txtPasswordShow.setVisible(false);
            txtPasswordShow.setManaged(false);
            
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
            
            // Đổi icon về con mắt bình thường
            eyeIcon.setGlyphName("EYE");
        }
    }
    
    // Nếu bạn có nút Đăng nhập thì thêm vào đây
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = "Admin"; // Ví dụ thôi
        String password = txtPassword.getText();
        System.out.println("Đang đăng nhập với pass: " + password);
    }
}