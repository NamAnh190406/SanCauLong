package com.example.guidemo;

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
        cbChucVu.setItems(FXCollections.observableArrayList("Quản lý", "Tiếp tân", "Bảo vệ", "Lao công"));
    }

    // Hàm đổ dữ liệu nhân viên cũ lên form khi vừa chuyển trang
    public void setNhanVienBanDau(NHANVIEN nv) {
        txtMaNV.setText(nv.getMaNV()); // Mã NV của bạn theo class model
        txtHoTen.setText(nv.getHoTen());
        txtSDT.setText(nv.getSDT());
        cbChucVu.setValue(nv.getChucVu());

        // Kiểm tra chuỗi ca làm việc để tích chọn đúng RadioButton
        if (nv.getCaLamViec() != null) {
            if (nv.getCaLamViec().contains("Ca 1")) rdCa1.setSelected(true);
            else if (nv.getCaLamViec().contains("Ca 2")) rdCa2.setSelected(true);
            else if (nv.getCaLamViec().contains("Ca 3")) rdCa3.setSelected(true);
        }
    }

    @FXML
    private void handleLuuCapNhat() {
        String ma = txtMaNV.getText();
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSDT.getText().trim();
        String chucVu = cbChucVu.getValue();

        // Xác định ca làm việc từ RadioButton được chọn
        String caLamViec = "Ca 1 (Sáng)";
        if (rdCa2.isSelected()) caLamViec = "Ca 2 (Chiều)";
        else if (rdCa3.isSelected()) caLamViec = "Ca 3 (Tối)";

        if (hoTen.isEmpty() || sdt.isEmpty() || chucVu == null) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Vui lòng không để trống thông tin bắt buộc (*)");
            return;
        }

        // Thực hiện cập nhật vào Database Oracle
        String sql = "UPDATE NHAN_VIEN SET Hoten_nv = ?, SDT = ?, ChucVu = ?, CaLamViec = ? WHERE Ma_NV = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hoTen);
            pstmt.setString(2, sdt);
            pstmt.setString(3, chucVu);
            pstmt.setString(4, caLamViec);
            pstmt.setString(5, ma);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật thông tin nhân viên!");
                handleThoat(); // Quay lại trang danh sách
            }
        } catch (SQLException e) {
            e.printStackTrace();
            hienThongBao(Alert.AlertType.ERROR, "Lỗi Database", "Cập nhật thất bại! Số điện thoại bị trùng hoặc lỗi kết nối.");
        }
    }

    @FXML
    private void handleThoat() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("QLyNhanVien.fxml"));
            Parent view = loader.load();

            if (TrangChuController.rootPane != null) {
                TrangChuController.rootPane.setCenter(view);
            }
        } catch (IOException e) {
            System.err.println("Không thể nạp file FXML danh sách nhân viên! Chi tiết lỗi:");
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