package com.mycompany.mavenproject1;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.MouseEvent;

public class ManHinhChinhNVController implements Initializable {

    @FXML private Label     lblSanTrong;
    @FXML private Label     lblDangSuDung;
    @FXML private Label     lblLichHomNay;
    @FXML private Label     lblDoanhThu;
    @FXML private Label     lblNhanVien;
    @FXML private Label     lblNotifBadge;
    @FXML private TextField tfSearch;

    private static final String DB_TRANG_THAI_TRONG     = "HoatDong";
    private static final String DB_TRANG_THAI_DANG_DUNG = "DangDung";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblNhanVien.setText(LuuThongTinDangNhap.hoTen != null ? LuuThongTinDangNhap.hoTen : "Nhân viên");
        taiThongKe();
    }

    // ================== TẢI DỮ LIỆU ==================

    private void taiThongKe() {
        try (Connection conn = DBContext.KetNoi()) {
            int tong = tongSoSan(conn);

            String sqlTrong = "SELECT COUNT(*) FROM SAN WHERE TrangThai = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlTrong)) {
                ps.setString(1, DB_TRANG_THAI_TRONG);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) lblSanTrong.setText(rs.getInt(1) + "/" + tong);
                }
            }

            String sqlDung = "SELECT COUNT(*) FROM SAN WHERE TrangThai = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlDung)) {
                ps.setString(1, DB_TRANG_THAI_DANG_DUNG);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) lblDangSuDung.setText(rs.getInt(1) + "/" + tong);
                }
            }

            String sqlLich = "SELECT COUNT(*) FROM DATSAN WHERE TRUNC(NgayDat) = TRUNC(SYSDATE)";
            try (PreparedStatement ps = conn.prepareStatement(sqlLich);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) lblLichHomNay.setText(String.valueOf(rs.getInt(1)));
            }

            String sqlDT = "SELECT NVL(SUM(ThanhTien), 0) FROM HOADON hd " +
                           "JOIN DATSAN ds ON hd.MaDS = ds.MaDS " +
                           "WHERE TRUNC(ds.NgayDat) = TRUNC(SYSDATE)";
            try (PreparedStatement ps = conn.prepareStatement(sqlDT);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) lblDoanhThu.setText(formatTien(rs.getLong(1)));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi tải thống kê: " + e.getMessage());
        }
    }

    private int tongSoSan(Connection conn) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM SAN");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ignored) { }
        return 0;
    }

    // ================== TOPBAR ==================

    @FXML private void onSearchKeyReleased() { }
    @FXML private void onBellClick() { }
    @FXML private void onProfileClick() { }
    @FXML private void onTitleBarPressed(MouseEvent e) { }
    @FXML private void onTitleBarDragged(MouseEvent e) { }

    // ================== QUICK ACTIONS ==================

    @FXML private void onDatSanClick()          { nav("QLySanVaKG"); }
    @FXML private void onQuanLyKhachHangClick() { nav("QLyKhachHang"); }

    // ================== SIDEBAR ==================

    @FXML private void onSanCauLongClick() { nav("SanCauLong"); }
    @FXML private void onKhachHangClick()  { nav("KhachHangNV"); }
    @FXML private void onCaiDatClick()     { nav("CaiDatNV"); }

    // ================== ĐĂNG XUẤT ==================

    @FXML
    private void onDangXuatClick() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Đăng xuất");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn đăng xuất không?");
        ButtonType btnXacNhan = new ButtonType("Đăng xuất", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnHuy     = new ButtonType("Hủy",       ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(btnXacNhan, btnHuy);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == btnXacNhan) {
                LuuThongTinDangNhap.hoTen = null;
                nav("primary");
            }
        });
    }

    // ================== HELPER ==================

    private void nav(String fxml) {
        try { App.setRoot(fxml); } catch (Exception e) { e.printStackTrace(); }
    }

    private String formatTien(long amount) {
        if (amount >= 1_000_000) return String.format("%.1fM", amount / 1_000_000.0);
        if (amount >= 1_000)     return String.format("%.1fK", amount / 1_000.0);
        return String.valueOf(amount);
    }
}