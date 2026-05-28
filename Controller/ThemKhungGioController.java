package com.mycompany.mavenproject1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.sql.*;
import javafx.stage.Stage;

public class ThemKhungGioController {

    @FXML private TextField txtMaKG;
    @FXML private TextField txtGioBD; // Người dùng nhập: 07:30
    @FXML private TextField txtGioKT; // Người dùng nhập: 09:00

    @FXML
    private void handleLuuThemMoi() {
        String ma = txtMaKG.getText().trim();
        String bd = txtGioBD.getText().trim();
        String kt = txtGioKT.getText().trim();

        if (ma.isEmpty() || bd.isEmpty() || kt.isEmpty()) {
            hienThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng điền đủ thông tin!");
            return;
        }

        // Tạo chuỗi hoàn chỉnh khớp định dạng TIMESTAMP để insert
        String tsBD = "2025-01-01 " + bd + ":00";
        String tsKT = "2025-01-01 " + kt + ":00";

        String sql = "INSERT INTO KHUNGGIO (MaKG, GioBD, GioKT) VALUES (?, TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS'))";

        try (Connection conn = DBContext.KetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ma);
            pstmt.setString(2, tsBD);
            pstmt.setString(3, tsKT);

            pstmt.executeUpdate();
            hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Thêm khung giờ mới thành công!");
            quayLai();
        } catch (SQLException e) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm mới. Hãy chắc chắn giờ kết thúc lớn hơn giờ bắt đầu (chk_kg_gio)!\nChi tiết: " + e.getMessage());
        }
    }

    @FXML private void handleHuyBo() { quayLai(); }

   private void quayLai() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("QLyKhungGio.fxml"));
        Parent v = loader.load();
        Stage stage = (Stage) txtMaKG.getScene().getWindow();
        stage.getScene().setRoot(v);
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    private void hienThongBao(Alert.AlertType t, String title, String c) {
        Alert a = new Alert(t, c); a.setTitle(title); a.setHeaderText(null); a.showAndWait();
    }
}