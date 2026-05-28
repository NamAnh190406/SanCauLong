package com.mycompany.mavenproject1;
import com.mycompany.mavenproject1.model.DICHVU;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SuaDichVuController {

    @FXML private TextField txtMaDV;
    @FXML private TextField txtTenDV;
    @FXML private ComboBox<String> cbDonViTinh;
    @FXML private TextField txtGiaBan;
    @FXML private TextField txtSoLuongTon;

    @FXML
    public void initialize() {
        cbDonViTinh.setItems(FXCollections.observableArrayList("Chai", "Lon", "Gói", "Lượt", "Giờ", "Đôi", "Cái"));
    }

    // Đổ dữ liệu cũ lên form sửa sau khi đã đổi tên Getter chuẩn
    public void setDichVuBanDau(DICHVU dv) {
        txtMaDV.setText(dv.getMaDV());
        txtTenDV.setText(dv.getTenDV());
        cbDonViTinh.setValue(dv.getDonViTinh());      // Sửa từ getLoaiDV()
        txtGiaBan.setText(String.valueOf(dv.getGiaBan()));
        txtSoLuongTon.setText(String.valueOf(dv.getSlTonKho())); // Sửa từ getSoLuongTon()
    }

    @FXML
    private void handleLuuCapNhat() {
        String ma = txtMaDV.getText();
        String ten = txtTenDV.getText().trim();
        String dvt = cbDonViTinh.getValue();
        String giaStr = txtGiaBan.getText().trim();
        String tonStr = txtSoLuongTon.getText().trim();

        if (ten.isEmpty() || dvt == null || giaStr.isEmpty() || tonStr.isEmpty()) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Vui lòng nhập đầy đủ các trường bắt buộc (*)");
            return;
        }

        try {
            long giaBan = Long.parseLong(giaStr);
            int slTonKho = Integer.parseInt(tonStr);

            if (giaBan <= 0) {
                hienThongBao(Alert.AlertType.ERROR, "Lỗi", "Giá bán phải lớn hơn 0đ!");
                return;
            }
            if (slTonKho < 0) {
                hienThongBao(Alert.AlertType.ERROR, "Lỗi", "Số lượng tồn kho không được âm!");
                return;
            }

            // Thực hiện cập nhật vào đúng tên cột trong cơ sở dữ liệu: SLTonkho
            String sql = "UPDATE DICHVU SET TenDV = ?, DonViTinh = ?, GiaBan = ?, SLTonkho = ? WHERE MaDV = ?";

            try (Connection conn = DBContext.KetNoi();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, ten);
                pstmt.setString(2, dvt);
                pstmt.setLong(3, giaBan);
                pstmt.setInt(4, slTonKho);
                pstmt.setString(5, ma);

                if (pstmt.executeUpdate() > 0) {
                    hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật thông tin dịch vụ thành công!");
                    handleThoat();
                }
            }
        } catch (NumberFormatException e) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi định dạng", "Giá bán và Số lượng tồn kho bắt buộc phải là số!");
        } catch (SQLException e) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi cơ sở dữ liệu", e.getMessage());
        }
    }

    @FXML
private void handleThoat() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("QLyDichVu.fxml"));
        Parent view = loader.load();
        txtMaDV.getScene().setRoot(view);
    } catch (IOException e) {
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