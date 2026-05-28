package com.mycompany.mavenproject1;

import com.mycompany.mavenproject1.model.NHANVIEN;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SuaNhanVienController {

    @FXML private TextField txtMaNV;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtSDT;
    @FXML private ComboBox<String> cbChucVu;
    @FXML private ToggleGroup groupCaLamViec;
    @FXML private RadioButton rdCa1;
    @FXML private RadioButton rdCa2;
    @FXML private RadioButton rdCa3;

    @FXML
    public void initialize() {
        cbChucVu.setItems(FXCollections.observableArrayList(
            "Quản lý", "Tiếp tân", "Bảo vệ", "Lao công"
        ));
    }

    public void setNhanVienBanDau(NHANVIEN nv) {
        txtMaNV.setText(nv.getMaNV());
        txtHoTen.setText(nv.getHoTen());
        txtSDT.setText(nv.getSDT());
        cbChucVu.setValue(nv.getChucVu());

        if (nv.getCaLamViec() != null) {
            switch (nv.getCaLamViec().trim()) {
                case "Ca1" -> rdCa1.setSelected(true);
                case "Ca2" -> rdCa2.setSelected(true);
                case "Ca3" -> rdCa3.setSelected(true);
                default    -> rdCa1.setSelected(true);
            }
        }
    }

    @FXML
    private void handleLuuCapNhat() {
        String ma     = txtMaNV.getText();
        String hoTen  = txtHoTen.getText().trim();
        String sdt    = txtSDT.getText().trim();
        String chucVu = cbChucVu.getValue();

        // Giá trị lưu DB phải khớp đúng constraint: 'Ca1', 'Ca2', 'Ca3'
        String caLamViec;
        if (rdCa1.isSelected())      caLamViec = "Ca1";
        else if (rdCa2.isSelected()) caLamViec = "Ca2";
        else if (rdCa3.isSelected()) caLamViec = "Ca3";
        else                         caLamViec = "Ca1"; // mặc định

        // Kiểm tra đầu vào
        if (hoTen.isEmpty()) {
            hienThongBao(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập họ tên nhân viên.");
            return;
        }
        if (sdt.isEmpty()) {
            hienThongBao(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập số điện thoại.");
            return;
        }
        if (!sdt.matches("\\d{10,11}")) {
            hienThongBao(Alert.AlertType.WARNING, "Sai định dạng", "Số điện thoại phải có 10-11 chữ số.");
            return;
        }
        if (chucVu == null) {
            hienThongBao(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng chọn chức vụ.");
            return;
        }

        String sql = "UPDATE NHAN_VIEN SET Hoten_nv = ?, SDT = ?, ChucVu = ?, CaLamViec = ? WHERE Ma_NV = ?";

        try (Connection conn = DBContext.KetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hoTen);
            pstmt.setString(2, sdt);
            pstmt.setString(3, chucVu);
            pstmt.setString(4, caLamViec);
            pstmt.setString(5, ma);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật thông tin nhân viên!");
                handleThoat();
            } else {
                hienThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Không tìm thấy nhân viên để cập nhật.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            String thongBao;
            if (e.getMessage().contains("CHK_NV_CALAMVIEC")) {
                thongBao = "Ca làm việc không hợp lệ! Chỉ chấp nhận Ca1, Ca2, Ca3.";
            } else if (e.getMessage().contains("ORA-00001") || e.getMessage().contains("unique")) {
                thongBao = "Số điện thoại đã tồn tại trong hệ thống!";
            } else {
                thongBao = "Lỗi cập nhật: " + e.getMessage();
            }
            hienThongBao(Alert.AlertType.ERROR, "Lỗi Database", thongBao);
        }
    }

    @FXML
    private void handleThoat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("QLyNhanVien.fxml"));
            Parent view = loader.load();
            txtMaNV.getScene().setRoot(view);
        } catch (IOException e) {
            System.err.println("Không thể nạp file QLyNhanVien.fxml:");
            e.printStackTrace();
        }
    }

    private void hienThongBao(Alert.AlertType type, String tieuDe, String noiDung) {
        Alert alert = new Alert(type);
        alert.setTitle(tieuDe);
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }
}