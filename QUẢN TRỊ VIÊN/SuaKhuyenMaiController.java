package com.example.guidemo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class SuaKhuyenMaiController {

    @FXML private TextField txtMaKM;
    @FXML private TextField txtTenKM;
    @FXML private TextField txtPhanTram;
    @FXML private TextField txtGiaTriToiDa;
    @FXML private DatePicker dpNgayBD;
    @FXML private DatePicker dpNgayKT;

    public void setKhuyenMaiBanDau(KHUYENMAI km) {
        txtMaKM.setText(km.getMaKM());
        txtTenKM.setText(km.getTenKM());
        txtPhanTram.setText(String.valueOf(km.getPhanTramGiam()));
        txtGiaTriToiDa.setText(String.valueOf(km.getGiaTriToiDa()));
        dpNgayBD.setValue(km.getNgayBatDau());
        dpNgayKT.setValue(km.getNgayKetThuc());
    }

    @FXML
    private void handleLuuCapNhat() {
        String ma = txtMaKM.getText();
        String ten = txtTenKM.getText().trim();
        String phanTramStr = txtPhanTram.getText().trim();
        String maxStr = txtGiaTriToiDa.getText().trim();
        LocalDate ngayBD = dpNgayBD.getValue();
        LocalDate ngayKT = dpNgayKT.getValue();

        if (ten.isEmpty() || phanTramStr.isEmpty() || ngayBD == null || ngayKT == null) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Vui lòng nhập đầy đủ các trường bắt buộc (*)");
            return;
        }

        try {
            double phanTram = Double.parseDouble(phanTramStr);
            long giaTriToiDa = maxStr.isEmpty() ? 0 : Long.parseLong(maxStr);

            if (phanTram <= 0 || phanTram > 100) {
                hienThongBao(Alert.AlertType.ERROR, "Lỗi", "Phần trăm giảm giá phải nằm trong khoảng (0 - 100%]");
                return;
            }
            if (ngayKT.isBefore(ngayBD)) {
                hienThongBao(Alert.AlertType.ERROR, "Lỗi ngày", "Ngày kết thúc không được nhỏ hơn ngày bắt đầu!");
                return;
            }

            String sql = "UPDATE KHUYENMAI SET TenKM = ?, PhanTramGG = ?, GTriToiDa = ?, NgayBD = ?, NgayKT = ? WHERE MaKM = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, ten);
                pstmt.setDouble(2, phanTram);
                if (giaTriToiDa == 0) pstmt.setNull(3, java.sql.Types.NUMERIC);
                else pstmt.setLong(3, giaTriToiDa);

                pstmt.setDate(4, java.sql.Date.valueOf(ngayBD));
                pstmt.setDate(5, java.sql.Date.valueOf(ngayKT));
                pstmt.setString(6, ma);

                if (pstmt.executeUpdate() > 0) {
                    hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật chương trình khuyến mãi!");
                    handleThoat();
                }
            }
        } catch (NumberFormatException e) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi định dạng", "Phần trăm và Giá trị giảm tối đa phải là số!");
        } catch (SQLException e) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi cơ sở dữ liệu", e.getMessage());
        }
    }

    @FXML
    private void handleThoat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("QLyKhuyenMai.fxml"));
            Parent view = loader.load();
            if (TrangChuController.rootPane != null) {
                TrangChuController.rootPane.setCenter(view);
            }
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