package com.example.guidemo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.sql.*;

public class SuaKhungGioController {

    @FXML private TextField txtMaKG;
    @FXML private TextField txtGioBD;
    @FXML private TextField txtGioKT;

    public void setKhungGioBanDau(KHUNGGIO kg) {
        txtMaKG.setText(kg.getMaKG());
        txtMaKG.setEditable(false);
        txtGioBD.setText(kg.getGioBDStr());
        txtGioKT.setText(kg.getGioKTStr());
    }

    @FXML
    private void handleLuuCapNhat() {
        String bd = txtGioBD.getText().trim();
        String kt = txtGioKT.getText().trim();

        String tsBD = "2025-01-01 " + bd + ":00";
        String tsKT = "2025-01-01 " + kt + ":00";

        String sql = "UPDATE KHUNGGIO SET GioBD = TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS'), GioKT = TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS') WHERE MaKG = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tsBD);
            pstmt.setString(2, tsKT);
            pstmt.setString(3, txtMaKG.getText());

            pstmt.executeUpdate();
            hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật khung giờ thành công!");
            quayLai();
        } catch (SQLException e) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi", "Cập nhật thất bại: " + e.getMessage());
        }
    }

    @FXML private void handleHuyBo() { quayLai(); }

    private void quayLai() {
        try {
            Parent v = FXMLLoader.load(getClass().getResource("QLyKhungGio.fxml"));
            if (TrangChuController.rootPane != null) TrangChuController.rootPane.setCenter(v);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void hienThongBao(Alert.AlertType t, String title, String c) {
        Alert a = new Alert(t, c); a.setTitle(title); a.setHeaderText(null); a.showAndWait();
    }
}