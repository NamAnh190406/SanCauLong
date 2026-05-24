package com.mycompany.mavenproject1;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ChonDichVuController implements Initializable {

    // ===== CARDS =====
    @FXML private VBox cardVot, cardCau, cardNuocKhoang, cardNuocNgot, cardKhan;

    // ===== SỐ LƯỢNG =====
    @FXML private Label lblSoLuongVot, lblSoLuongCau;
    @FXML private Label lblSoLuongNuocKhoang, lblSoLuongNuocNgot, lblSoLuongKhan;

    // ===== TỒN KHO HIỂN THỊ TRÊN UI =====
    @FXML private Label lblConVot, lblConCau;
    @FXML private Label lblConNuocKhoang, lblConNuocNgot, lblConKhan;

    // ===== THÀNH TIỀN TỪNG MỤC =====
    @FXML private VBox  thanhTienVot, thanhTienCau, thanhTienNuocKhoang, thanhTienNuocNgot, thanhTienKhan;
    @FXML private Label lblThanhTienVot, lblThanhTienCau;
    @FXML private Label lblThanhTienNuocKhoang, lblThanhTienNuocNgot, lblThanhTienKhan;

    // ===== FOOTER =====
    @FXML private Label lblTongSoMuc, lblTongTien;

    // ===== POPUP THÀNH CÔNG =====
    @FXML private StackPane popupThanhCong;
    @FXML private Label lblMaDon;
    @FXML private Label lblChiTietVot,        lblGiaVot;
    @FXML private Label lblChiTietCau,        lblGiaCau;
    @FXML private Label lblChiTietNuocKhoang, lblGiaNuocKhoang;
    @FXML private Label lblChiTietNuocNgot,   lblGiaNuocNgot;
    @FXML private Label lblChiTietKhan,       lblGiaKhan;
    @FXML private Label lblTongCong;

    // ===== GIÁ =====
    private int GIA_VOT, GIA_CAU, GIA_NUOC_KHOANG, GIA_NUOC_NGOT, GIA_KHAN;

    // ===== TỒN KHO =====
    private int TON_VOT, TON_CAU, TON_NUOC_KHOANG, TON_NUOC_NGOT, TON_KHAN;

    // ===== STATE SỐ LƯỢNG ĐÃ CHỌN =====
    private int soLuongVot, soLuongCau, soLuongNuocKhoang, soLuongNuocNgot, soLuongKhan;

    // ===== MaDS lấy từ LuuThongTinDatSan =====
    private String maDS;

    private final CTDVDao ctdvDao = new CTDVDao();

    // =========================================================
    // INITIALIZE
    // =========================================================
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Lấy maDS từ static class
        this.maDS = LuuThongTinDatSan.maDS;
        taiThongTinDichVuTuDB();
    }

    // =========================================================
    // TẢI GIÁ + TỒN KHO TỪ DB (không dùng function)
    // =========================================================
    private void taiThongTinDichVuTuDB() {
        String sql = "SELECT MaDV, GiaBan, SLTonkho FROM DICHVU " +
                     "WHERE MaDV IN ('DV001','DV002','DV003','DV004','DV005')";

        try (Connection con = DBContext.KetNoi();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String maDV = rs.getString("MaDV");
                int gia = rs.getInt("GiaBan");
                int ton = rs.getInt("SLTonkho");
                switch (maDV) {
                    case "DV001": GIA_VOT         = gia; TON_VOT         = ton; break;
                    case "DV002": GIA_CAU         = gia; TON_CAU         = ton; break;
                    case "DV003": GIA_NUOC_KHOANG = gia; TON_NUOC_KHOANG = ton; break;
                    case "DV004": GIA_NUOC_NGOT   = gia; TON_NUOC_NGOT   = ton; break;
                    case "DV005": GIA_KHAN        = gia; TON_KHAN        = ton; break;
                }
            }
            capNhatLabelTonKho();

        } catch (SQLException e) {
            e.printStackTrace();
            // Fallback giá cứng nếu DB lỗi
            GIA_VOT = 30000; GIA_CAU = 120000; GIA_NUOC_KHOANG = 50000;
            GIA_NUOC_NGOT = 15000; GIA_KHAN = 20000;
            TON_VOT = 50; TON_CAU = 100; TON_NUOC_KHOANG = 20;
            TON_NUOC_NGOT = 200; TON_KHAN = 80;
            capNhatLabelTonKho();
        }
    }

    private void capNhatLabelTonKho() {
        if (lblConVot        != null) lblConVot.setText("Còn: "        + TON_VOT         + " cái");
        if (lblConCau        != null) lblConCau.setText("Còn: "        + TON_CAU         + " hộp");
        if (lblConNuocKhoang != null) lblConNuocKhoang.setText("Còn: " + TON_NUOC_KHOANG + " chai");
        if (lblConNuocNgot   != null) lblConNuocNgot.setText("Còn: "   + TON_NUOC_NGOT   + " chai");
        if (lblConKhan       != null) lblConKhan.setText("Còn: "       + TON_KHAN        + " cái");
    }

    // =========================================================
    // TĂNG / GIẢM
    // =========================================================
    @FXML private void handleTangVot()        { soLuongVot        = tang(soLuongVot,        TON_VOT);         capNhat(); }
    @FXML private void handleGiamVot()        { soLuongVot        = giam(soLuongVot);                         capNhat(); }
    @FXML private void handleTangCau()        { soLuongCau        = tang(soLuongCau,        TON_CAU);         capNhat(); }
    @FXML private void handleGiamCau()        { soLuongCau        = giam(soLuongCau);                         capNhat(); }
    @FXML private void handleTangNuocKhoang() { soLuongNuocKhoang = tang(soLuongNuocKhoang, TON_NUOC_KHOANG); capNhat(); }
    @FXML private void handleGiamNuocKhoang() { soLuongNuocKhoang = giam(soLuongNuocKhoang);                  capNhat(); }
    @FXML private void handleTangNuocNgot()   { soLuongNuocNgot   = tang(soLuongNuocNgot,   TON_NUOC_NGOT);  capNhat(); }
    @FXML private void handleGiamNuocNgot()   { soLuongNuocNgot   = giam(soLuongNuocNgot);                   capNhat(); }
    @FXML private void handleTangKhan()       { soLuongKhan       = tang(soLuongKhan,        TON_KHAN);       capNhat(); }
    @FXML private void handleGiamKhan()       { soLuongKhan       = giam(soLuongKhan);                        capNhat(); }

    private int tang(int sl, int max) { return Math.min(sl + 1, max); }
    private int giam(int sl)          { return Math.max(sl - 1, 0); }

    // =========================================================
    // CẬP NHẬT UI
    // =========================================================
    private void capNhat() {
        lblSoLuongVot.setText(String.valueOf(soLuongVot));
        lblSoLuongCau.setText(String.valueOf(soLuongCau));
        lblSoLuongNuocKhoang.setText(String.valueOf(soLuongNuocKhoang));
        lblSoLuongNuocNgot.setText(String.valueOf(soLuongNuocNgot));
        lblSoLuongKhan.setText(String.valueOf(soLuongKhan));

        capNhatDong(thanhTienVot,        lblThanhTienVot,        soLuongVot,        GIA_VOT);
        capNhatDong(thanhTienCau,        lblThanhTienCau,        soLuongCau,        GIA_CAU);
        capNhatDong(thanhTienNuocKhoang, lblThanhTienNuocKhoang, soLuongNuocKhoang, GIA_NUOC_KHOANG);
        capNhatDong(thanhTienNuocNgot,   lblThanhTienNuocNgot,   soLuongNuocNgot,   GIA_NUOC_NGOT);
        capNhatDong(thanhTienKhan,       lblThanhTienKhan,       soLuongKhan,       GIA_KHAN);

        lblTongSoMuc.setText(demMuc() + " mục");
        lblTongTien.setText(formatTien(tinhTongLocal()));

        capNhatMauTonKho(lblConVot,        TON_VOT         - soLuongVot);
        capNhatMauTonKho(lblConCau,        TON_CAU         - soLuongCau);
        capNhatMauTonKho(lblConNuocKhoang, TON_NUOC_KHOANG - soLuongNuocKhoang);
        capNhatMauTonKho(lblConNuocNgot,   TON_NUOC_NGOT   - soLuongNuocNgot);
        capNhatMauTonKho(lblConKhan,       TON_KHAN        - soLuongKhan);
    }

    private void capNhatDong(VBox box, Label lbl, int sl, int gia) {
        boolean hien = sl > 0;
        box.setVisible(hien);
        box.setManaged(hien);
        if (hien) lbl.setText(formatTien((long) sl * gia));
    }

    private void capNhatMauTonKho(Label lbl, int conLai) {
        if (lbl == null) return;
        if (conLai <= 0) {
            lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #dc2626; -fx-font-weight: bold;");
        } else if (conLai <= 5) {
            lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #ea580c; -fx-font-weight: bold;");
        } else {
            lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #ef4444; -fx-font-weight: bold;");
        }
    }

    // =========================================================
    // NÚT ĐẶT DỊCH VỤ
    // =========================================================
    @FXML
    private void handleDatDichVu() {
        if (tinhTongLocal() == 0) {
            showAlert("Vui lòng chọn ít nhất 1 dịch vụ!");
            return;
        }

        if (maDS == null || maDS.isEmpty()) {
            showAlert("Không tìm thấy mã đặt sân! Vui lòng đặt sân trước.");
            return;
        }

        if (soLuongVot        > TON_VOT)         { showAlert("Thuê vợt chỉ còn "      + TON_VOT         + " cái!");  return; }
        if (soLuongCau        > TON_CAU)         { showAlert("Cầu lông chỉ còn "      + TON_CAU         + " hộp!");  return; }
        if (soLuongNuocKhoang > TON_NUOC_KHOANG) { showAlert("Nước khoáng chỉ còn "   + TON_NUOC_KHOANG + " chai!"); return; }
        if (soLuongNuocNgot   > TON_NUOC_NGOT)   { showAlert("Nước ngọt chỉ còn "     + TON_NUOC_NGOT   + " chai!"); return; }
        if (soLuongKhan       > TON_KHAN)         { showAlert("Khăn thể thao chỉ còn " + TON_KHAN        + " cái!");  return; }

        Map<String, Integer> danhSach = new LinkedHashMap<>();
        danhSach.put(CTDVDao.MA_VOT,         soLuongVot);
        danhSach.put(CTDVDao.MA_CAU,         soLuongCau);
        danhSach.put(CTDVDao.MA_NUOC_KHOANG, soLuongNuocKhoang);
        danhSach.put(CTDVDao.MA_NUOC_NGOT,   soLuongNuocNgot);
        danhSach.put(CTDVDao.MA_KHAN,        soLuongKhan);

        try {
            long tongTienDB = ctdvDao.tinhTongTienDV(danhSach);
            List<String> dsMa = ctdvDao.luuCTDV(maDS, danhSach);

            giamTonKhoSauDat();

            String maCTDVHienThi = dsMa.isEmpty() ? "CTDV???" : dsMa.get(dsMa.size() - 1);
            lblMaDon.setText(maCTDVHienThi);

            dienDong(lblChiTietVot,        lblGiaVot,        "Thuê vợt",      soLuongVot,        GIA_VOT);
            dienDong(lblChiTietCau,        lblGiaCau,        "Cầu lông",      soLuongCau,        GIA_CAU);
            dienDong(lblChiTietNuocKhoang, lblGiaNuocKhoang, "Nước khoáng",   soLuongNuocKhoang, GIA_NUOC_KHOANG);
            dienDong(lblChiTietNuocNgot,   lblGiaNuocNgot,   "Nước ngọt",     soLuongNuocNgot,   GIA_NUOC_NGOT);
            dienDong(lblChiTietKhan,       lblGiaKhan,       "Khăn thể thao", soLuongKhan,       GIA_KHAN);
            lblTongCong.setText(formatTien(tongTienDB));

            popupThanhCong.setVisible(true);
            popupThanhCong.setManaged(true);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi khi lưu dịch vụ: " + e.getMessage());
        }
    }

    private void giamTonKhoSauDat() {
    String sql = "UPDATE DICHVU SET SLTonkho = SLTonkho - ? WHERE MaDV = ? AND SLTonkho >= ?";
    try (Connection con = DBContext.KetNoi();
         PreparedStatement ps = con.prepareStatement(sql)) {

        String[] maDVs    = { CTDVDao.MA_VOT, CTDVDao.MA_CAU, CTDVDao.MA_NUOC_KHOANG, CTDVDao.MA_NUOC_NGOT, CTDVDao.MA_KHAN };
        int[]    soLuongs = { soLuongVot, soLuongCau, soLuongNuocKhoang, soLuongNuocNgot, soLuongKhan };

        for (int i = 0; i < maDVs.length; i++) {
            if (soLuongs[i] > 0) {
                ps.setInt(1, soLuongs[i]);
                ps.setString(2, maDVs[i]);
                ps.setInt(3, soLuongs[i]);
                ps.addBatch();
            }
        }
        ps.executeBatch();
        // XÓA con.commit() — sysdba tự auto-commit

        TON_VOT         -= soLuongVot;
        TON_CAU         -= soLuongCau;
        TON_NUOC_KHOANG -= soLuongNuocKhoang;
        TON_NUOC_NGOT   -= soLuongNuocNgot;
        TON_KHAN        -= soLuongKhan;

        capNhatLabelTonKho();

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    private void dienDong(Label lblTen, Label lblGia, String ten, int sl, int gia) {
        if (sl > 0) {
            lblTen.setText(ten + " x" + sl);
            lblGia.setText(formatTien((long) sl * gia));
        } else {
            lblTen.setText("");
            lblGia.setText("");
        }
    }

    // =========================================================
    // THOÁT / ĐÓNG
    // =========================================================
    @FXML private void handleQuayLaiTrangChinh() { dongPopup(); }
    @FXML private void handleThoat()             { dongPopup(); }
    @FXML private void handleDong()              { dongPopup(); }

    private void dongPopup() {
        try {
            App.setRoot("ManHinhChinh");
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // HELPER
    // =========================================================
    private long tinhTongLocal() {
        return (long) soLuongVot        * GIA_VOT
             + (long) soLuongCau        * GIA_CAU
             + (long) soLuongNuocKhoang * GIA_NUOC_KHOANG
             + (long) soLuongNuocNgot   * GIA_NUOC_NGOT
             + (long) soLuongKhan       * GIA_KHAN;
    }

    private int demMuc() {
        int d = 0;
        if (soLuongVot        > 0) d++;
        if (soLuongCau        > 0) d++;
        if (soLuongNuocKhoang > 0) d++;
        if (soLuongNuocNgot   > 0) d++;
        if (soLuongKhan       > 0) d++;
        return d;
    }

    private String formatTien(long tien) {
        return String.format("%,d đ", tien).replace(",", ".");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}