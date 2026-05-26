
import Controller.LuuThongTinDangNhap;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class ManHinhChinhController implements Initializable {

    @FXML private Label lblHoTen;
    @FXML private Label lblDiem;
    @FXML private Label lblHang;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblHoTen.setText("Xin chào, " + LuuThongTinDangNhap.hoTen + "!");
        lblDiem.setText(String.valueOf(LuuThongTinDangNhap.diemTichLuy));
        lblHang.setText(LuuThongTinDangNhap.hangThanhVien);
    }
}