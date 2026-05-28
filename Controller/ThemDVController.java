package com.mycompany.mavenproject1;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class ThemDVController {
    @FXML private TextField txtMaDV;
    @FXML private TextField txtTenDV;
    @FXML private ComboBox<String> cbDonViTinh;
    @FXML private TextField txtGiaBan;
    @FXML private TextField txtSoLuong;
    @FXML
    public void initialize() {
        cbDonViTinh.setItems(FXCollections.observableArrayList(
                "Chai", "Hộp","Cái"
        ));
    }
    @FXML
    private void handleLuuDichVu() {
        String ma = txtMaDV.getText().trim();
        String ten = txtTenDV.getText().trim();
        String dvt = cbDonViTinh.getValue();
        String giaStr = txtGiaBan.getText().trim();
        String slStr = txtSoLuong.getText().trim();
        if (ma.isEmpty() || ten.isEmpty() || dvt == null || giaStr.isEmpty() || slStr.isEmpty()) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        try {
            long gia = Long.parseLong(giaStr);
            int sl = Integer.parseInt(slStr);
            if (gia <= 0 || sl < 0) {
                hienThongBao(Alert.AlertType.ERROR, "Lỗi giá trị", "Giá bán > 0 và Số lượng >= 0!");
                return;
            }
            String sql = "INSERT INTO DICHVU (MaDV, TenDV, DonViTinh, GiaBan, SLTonkho) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = DBContext.KetNoi();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, ma);
                pstmt.setString(2, ten);
                pstmt.setString(3, dvt);
                pstmt.setLong(4, gia);
                pstmt.setInt(5, sl);
                pstmt.executeUpdate();
                hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm dịch vụ mới!");
                lamMoiForm();
            }
        } catch (NumberFormatException e) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi định dạng", "Giá bán và Số lượng phải là số!");
        } catch (SQLException e) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi Database", "Mã dịch vụ đã tồn tại hoặc lỗi kết nối!");
        }
    }
    @FXML
    private void handleThoat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("QLyDichVu.fxml"));
            Parent view = loader.load();
            // ✅ Sửa: setCenter -> setRoot
            txtMaDV.getScene().setRoot(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void lamMoiForm() {
        txtMaDV.clear();
        txtTenDV.clear();
        cbDonViTinh.setValue(null);
        txtGiaBan.clear();
        txtSoLuong.clear();
    }
    private void hienThongBao(Alert.AlertType type, String tieuDe, String noiDung) {
        Alert alert = new Alert(type);
        alert.setTitle(tieuDe);
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }
}