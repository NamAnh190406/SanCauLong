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

public class ThemKhuyenMaiController {

    @FXML private TextField txtMaKM;
    @FXML private TextField txtTenKM;
    @FXML private TextField txtPhanTram;
    @FXML private TextField txtGiaTriToiDa;
    @FXML private DatePicker dpNgayBD;
    @FXML private DatePicker dpNgayKT;

    @FXML
    private void handleLuuKhuyenMai() {
        String ma = txtMaKM.getText().trim();
        String ten = txtTenKM.getText().trim();
        String phanTramStr = txtPhanTram.getText().trim();
        String maxStr = txtGiaTriToiDa.getText().trim();
        LocalDate ngayBD = dpNgayBD.getValue();
        LocalDate ngayKT = dpNgayKT.getValue();

        if (ma.isEmpty() || ten.isEmpty() || phanTramStr.isEmpty() || ngayBD == null || ngayKT == null) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Vui lòng điền đủ thông tin các ô có dấu (*)");
            return;
        }

        try {
            double phanTram = Double.parseDouble(phanTramStr);
            long giaTriToiDa = maxStr.isEmpty() ? 0 : Long.parseLong(maxStr);

            if (phanTram <= 0 || phanTram > 100) {
                hienThongBao(Alert.AlertType.ERROR, "Lỗi", "Phần trăm giảm giá phải nằm từ 1 đến 100%!");
                return;
            }
            if (ngayKT.isBefore(ngayBD)) {
                hienThongBao(Alert.AlertType.ERROR, "Lỗi ngày", "Ngày kết thúc không được nhỏ hơn ngày bắt đầu!");
                return;
            }

            String sql = "INSERT INTO KHUYENMAI (MaKM, TenKM, PhanTramGG, GTriToiDa, NgayBD, NgayKT) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, ma);
                pstmt.setString(2, ten);
                pstmt.setDouble(3, phanTram);
                if (giaTriToiDa == 0) pstmt.setNull(4, java.sql.Types.NUMERIC);
                else pstmt.setLong(4, giaTriToiDa);

                pstmt.setDate(5, java.sql.Date.valueOf(ngayBD));
                pstmt.setDate(6, java.sql.Date.valueOf(ngayKT));

                pstmt.executeUpdate();
                hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm mới chương trình khuyến mãi!");
                handleThoat();
            }
        } catch (NumberFormatException e) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi định dạng", "Phần trăm và Giá trị tối đa phải là dạng số!");
        } catch (SQLException e) {
            if (e.getMessage().contains("unique") || e.getMessage().contains("PRIMARY")) {
                hienThongBao(Alert.AlertType.ERROR, "Lỗi", "Mã khuyến mãi này đã tồn tại trên hệ thống!");
            } else {
                hienThongBao(Alert.AlertType.ERROR, "Lỗi DB", e.getMessage());
            }
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