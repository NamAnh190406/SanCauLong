package com.mycompany.mavenproject1;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class CaiDatController implements Initializable {

    @FXML private AnchorPane panelThongTin;
    @FXML private AnchorPane panelBaoMat;
    @FXML private Label lblMaKH;
    @FXML private Label lblHang;
    @FXML private Label lblDiem;
    @FXML private Label lblNgayDK;
    @FXML private Label lblKiemTra;

    // TextField để người dùng sửa
    @FXML private TextField txtHoTen;
    @FXML private TextField txtSDT;
    @FXML private TextField txtEmail;

    private boolean thongTinDangMo = false;
    private boolean baoMatDangMo = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        panelThongTin.setVisible(false);
        panelThongTin.setManaged(false);
        panelBaoMat.setVisible(false);
        panelBaoMat.setManaged(false);

        // Hiển thị thông tin
        lblMaKH.setText(LuuThongTinDangNhap.maKH);
        lblHang.setText(LuuThongTinDangNhap.hangThanhVien);
        lblDiem.setText(String.valueOf(LuuThongTinDangNhap.diemTichLuy));

        // Ngày ĐK chỉ hiển thị, không cho sửa
        if (LuuThongTinDangNhap.ngayDK != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            lblNgayDK.setText(sdf.format(LuuThongTinDangNhap.ngayDK));
        }

        // TextField điền sẵn để sửa
        txtHoTen.setText(LuuThongTinDangNhap.hoTen);
        txtSDT.setText(LuuThongTinDangNhap.sdt);
        txtEmail.setText(LuuThongTinDangNhap.email);
    }

    private boolean kiemTraHopLe() {
    String hoTen = txtHoTen.getText().trim();
    String sdt   = txtSDT.getText().trim();
    String email = txtEmail.getText().trim();

    if (hoTen.isEmpty()) {
        lblKiemTra.setText("Họ tên không được để trống!");
        lblKiemTra.setStyle("-fx-text-fill: red;");
        return false;
    }

    if (!sdt.matches("^0[0-9]{9}$")) {
        lblKiemTra.setText("Số điện thoại không hợp lệ! VD: 0901234567");
        lblKiemTra.setStyle("-fx-text-fill: red;");
        return false;
    }

    if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
        lblKiemTra.setText("Email không hợp lệ! VD: example@gmail.com");
        lblKiemTra.setStyle("-fx-text-fill: red;");
        return false;
    }

    return true;
}
    
    
    @FXML
private void handleLuu(MouseEvent event) {
    // Kiểm tra phía Java trước (nhanh hơn)
    if (!kiemTraHopLe()) return;

    String hoTen = txtHoTen.getText().trim();
    String sdt   = txtSDT.getText().trim();
    String email = txtEmail.getText().trim();

    String sql = "{call CAP_NHAT_THONG_TIN(?, ?, ?, ?, ?)}";
    try (java.sql.Connection conn = DBContext.KetNoi();
         java.sql.CallableStatement cs = conn.prepareCall(sql)) {

        cs.setString(1, LuuThongTinDangNhap.maKH);
        cs.setString(2, hoTen);
        cs.setString(3, sdt);
        cs.setString(4, email);
        cs.registerOutParameter(5, java.sql.Types.VARCHAR); // nhận kết quả
        cs.execute();

        String ketQua = cs.getString(5);

        if ("OK".equals(ketQua)) {
            // Cập nhật biến static
            LuuThongTinDangNhap.hoTen = hoTen;
            LuuThongTinDangNhap.sdt   = sdt;
            LuuThongTinDangNhap.email = email;

            // Hiện ở label Kiểm Tra
            lblKiemTra.setText("Lưu thành công!");
            lblKiemTra.setStyle("-fx-text-fill: green;");
        } else {
            // Hiện lỗi ở label Kiểm Tra
            lblKiemTra.setText(ketQua);
            lblKiemTra.setStyle("-fx-text-fill: red;");
        }

    } catch (Exception e) {
        lblKiemTra.setText("Lỗi kết nối: " + e.getMessage());
        lblKiemTra.setStyle("-fx-text-fill: red;");
    }
}

   @FXML
private void toggleThongTin() {
    thongTinDangMo = !thongTinDangMo;
    panelThongTin.setVisible(thongTinDangMo);
    panelThongTin.setManaged(thongTinDangMo);

    // Ẩn panel kia
    if (thongTinDangMo) {
        baoMatDangMo = false;
        panelBaoMat.setVisible(false);
        panelBaoMat.setManaged(false);
    }
}

@FXML
private void toggleBaoMat() {
    baoMatDangMo = !baoMatDangMo;
    panelBaoMat.setVisible(baoMatDangMo);
    panelBaoMat.setManaged(baoMatDangMo);

    // Ẩn panel kia
    if (baoMatDangMo) {
        thongTinDangMo = false;
        panelThongTin.setVisible(false);
        panelThongTin.setManaged(false);
    }
}
    @FXML
    private void handleThoat(MouseEvent event) throws IOException {
        App.setRoot("ManHinhChinh");
    }
}