package com.mycompany.mavenproject1;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.ResourceBundle;

public class DatSanController implements Initializable {

    private static final String S = "";

    @FXML private VBox buoc1Pane, buoc2Pane, thanhCongPane;

    @FXML private VBox cardSanA1, cardSanA2, cardSanB1, cardSanC1, cardSanC2;
    @FXML private Label iconChonA1, iconChonA2, iconChonB1, iconChonC1, iconChonC2;

    @FXML private DatePicker datePicker;

    @FXML private VBox kg001, kg002, kg003, kg004, kg005;
    @FXML private VBox kg006, kg007, kg008, kg009, kg010;

    @FXML private Label lblTomTatSan, lblTomTatNgay, lblTomTatKhungGio;
    @FXML private Label lblTomTatTienSan, lblTomTatTong;

    @FXML private Label lblXacNhanTenSan, lblXacNhanDiaChi;
    @FXML private Label lblXacNhanLoai1, lblXacNhanLoai2;
    @FXML private Label lblXacNhanNgay, lblXacNhanGio;
    @FXML private TextField txtHoTen, txtSDT, txtEmail;

    @FXML private HBox ptttTienMat, ptttChuyenKhoan;
    @FXML private Label radioBtnTienMat, radioBtnChuyenKhoan;

    @FXML private Label lblTongTienSan, lblTongTienCuoi;

    @FXML private Label lblMaDS;
    @FXML private Label lblKQSan, lblKQNgay, lblKQGio;
    @FXML private Label lblKQNguoiDat, lblKQSDT, lblKQPTTT, lblKQTongTien;

    private String maSanChon = "", tenSanChon = "", diaChiChon = "";
    private String loai1Chon = "", loai2Chon = "";
    private int    giaSanChon = 0;
    private String maKGChon = "", tenKGChon = "";
    private String ptttChon = "TienMat", ptttHienThi = "Tiền mặt";
    private LocalDate ngayChon = null;

    private static final String STYLE_CARD_DEFAULT =
        "-fx-background-color: white; -fx-background-radius: 12;" +
        "-fx-border-color: #e5e7eb; -fx-border-radius: 12; -fx-border-width: 1;" +
        "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 1);";
    private static final String STYLE_CARD_SELECTED =
        "-fx-background-color: #f0fdf4; -fx-background-radius: 12;" +
        "-fx-border-color: #16a34a; -fx-border-radius: 12; -fx-border-width: 2;" +
        "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(22,163,74,0.15), 8, 0, 0, 2);";
    private static final String STYLE_KG_DEFAULT =
        "-fx-background-color: #f9fafb; -fx-background-radius: 8;" +
        "-fx-border-color: #e5e7eb; -fx-border-radius: 8; -fx-border-width: 1;" +
        "-fx-padding: 10 12 10 12; -fx-cursor: hand;";
    private static final String STYLE_KG_SELECTED =
        "-fx-background-color: #f0fdf4; -fx-background-radius: 8;" +
        "-fx-border-color: #16a34a; -fx-border-radius: 8; -fx-border-width: 2;" +
        "-fx-padding: 10 12 10 12; -fx-cursor: hand;";
    private static final String STYLE_PTTT_DEFAULT =
        "-fx-background-color: white; -fx-background-radius: 10;" +
        "-fx-border-color: #e5e7eb; -fx-border-radius: 10; -fx-border-width: 1;" +
        "-fx-padding: 14; -fx-cursor: hand;";
    private static final String STYLE_PTTT_SELECTED =
        "-fx-background-color: white; -fx-background-radius: 10;" +
        "-fx-border-color: #16a34a; -fx-border-radius: 10; -fx-border-width: 2;" +
        "-fx-padding: 14; -fx-cursor: hand;";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        datePicker.setValue(LocalDate.now());
        ngayChon = LocalDate.now();
        lblTomTatNgay.setText(ngayChon.format(DateTimeFormatter.ofPattern("d/M/yyyy")));
        capNhatPTTT(ptttTienMat, radioBtnTienMat, true);
    }

    // ── CHỌN SÂN ──
    @FXML private void handleChonSanA1() { chonSan("S01","Sân A1","123 Nguyễn Trãi, Q1, TP.HCM","Đơn","Gỗ",80000,cardSanA1,iconChonA1); }
    @FXML private void handleChonSanA2() { chonSan("S02","Sân A2","123 Nguyễn Trãi, Q1, TP.HCM","Đôi","Gỗ",120000,cardSanA2,iconChonA2); }
    @FXML private void handleChonSanB1() { chonSan("S03","Sân B1","45 Lê Lợi, Q3, TP.HCM","Đơn","Nhựa PVC",60000,cardSanB1,iconChonB1); }
    @FXML private void handleChonSanC1() { chonSan("S05","Sân C1","88 Trần Hưng Đạo, Q5, TP.HCM","Đôi","Cao Su",150000,cardSanC1,iconChonC1); }
    @FXML private void handleChonSanC2() { chonSan("S06","Sân C2","88 Trần Hưng Đạo, Q5, TP.HCM","Đơn","Cao Su",90000,cardSanC2,iconChonC2); }

    private void chonSan(String maSan, String tenSan, String diaChi,
                         String loai1, String loai2, int gia, VBox card, Label icon) {
        resetTatCaCard();
        card.setStyle(STYLE_CARD_SELECTED);
        icon.setText("✓");
        maSanChon = maSan; tenSanChon = tenSan; diaChiChon = diaChi;
        loai1Chon = loai1; loai2Chon = loai2; giaSanChon = gia;
        lblTomTatSan.setText(tenSan);
        lblTomTatTienSan.setText(formatTien(gia));
        capNhatTongTomTat();
    }

    private void resetTatCaCard() {
        for (VBox c : new VBox[]{cardSanA1,cardSanA2,cardSanB1,cardSanC1,cardSanC2})
            c.setStyle(STYLE_CARD_DEFAULT);
        for (Label l : new Label[]{iconChonA1,iconChonA2,iconChonB1,iconChonC1,iconChonC2})
            l.setText("");
    }

    // ── CHỌN NGÀY ──
    @FXML
    private void handleChonNgay() {
        if (datePicker.getValue() != null) {
            ngayChon = datePicker.getValue();
            lblTomTatNgay.setText(ngayChon.format(DateTimeFormatter.ofPattern("d/M/yyyy")));
        }
    }

    // ── CHỌN KHUNG GIỜ ──
    @FXML private void handleChonKG001() { chonKG("KG001","06:00 - 07:30",kg001); }
    @FXML private void handleChonKG002() { chonKG("KG002","07:30 - 09:00",kg002); }
    @FXML private void handleChonKG003() { chonKG("KG003","09:00 - 10:30",kg003); }
    @FXML private void handleChonKG004() { chonKG("KG004","10:30 - 12:00",kg004); }
    @FXML private void handleChonKG005() { chonKG("KG005","13:00 - 14:30",kg005); }
    @FXML private void handleChonKG006() { chonKG("KG006","14:30 - 16:00",kg006); }
    @FXML private void handleChonKG007() { chonKG("KG007","16:00 - 17:30",kg007); }
    @FXML private void handleChonKG008() { chonKG("KG008","17:30 - 19:00",kg008); }
    @FXML private void handleChonKG009() { chonKG("KG009","19:00 - 20:30",kg009); }
    @FXML private void handleChonKG010() { chonKG("KG010","20:30 - 22:00",kg010); }

    private void chonKG(String maKG, String tenKG, VBox card) {
        for (VBox k : new VBox[]{kg001,kg002,kg003,kg004,kg005,kg006,kg007,kg008,kg009,kg010})
            k.setStyle(STYLE_KG_DEFAULT);
        card.setStyle(STYLE_KG_SELECTED);
        maKGChon = maKG; tenKGChon = tenKG;
        lblTomTatKhungGio.setText(tenKG);
        capNhatTongTomTat();
    }

    private void capNhatTongTomTat() {
        lblTomTatTong.setText(giaSanChon > 0 ? formatTien((long)(giaSanChon * 1.5)) : "0 đ");
    }

    // ── TIẾP THEO ──
    @FXML
    private void handleTiepTheo() {
        if (maSanChon.isEmpty()) { showAlert("Vui lòng chọn sân!"); return; }
        if (ngayChon == null)    { showAlert("Vui lòng chọn ngày!"); return; }
        if (maKGChon.isEmpty())  { showAlert("Vui lòng chọn khung giờ!"); return; }

        // Điền tự động thông tin KH đã đăng nhập
        if (LuuThongTinDangNhap.hoTen != null) txtHoTen.setText(LuuThongTinDangNhap.hoTen);
        loadSDTEmail();

        lblXacNhanTenSan.setText(tenSanChon);
        lblXacNhanDiaChi.setText(diaChiChon);
        lblXacNhanLoai1.setText(loai1Chon);
        lblXacNhanLoai2.setText(loai2Chon);
        lblXacNhanNgay.setText(ngayChon.format(DateTimeFormatter.ofPattern("d/M/yyyy")));
        lblXacNhanGio.setText(tenKGChon);
        long tien = (long)(giaSanChon * 1.5);
        lblTongTienSan.setText(formatTien(tien));
        lblTongTienCuoi.setText(formatTien(tien));

        buoc1Pane.setVisible(false); buoc1Pane.setManaged(false);
        buoc2Pane.setVisible(true);  buoc2Pane.setManaged(true);
    }

    private void loadSDTEmail() {
        if (LuuThongTinDangNhap.maKH == null) return;
        String sql = "SELECT SDT, Email FROM " + S + "KHACHHANG WHERE MaKH = ?";
        try (Connection con = DBContext.KetNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, LuuThongTinDangNhap.maKH);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getString("SDT")   != null) txtSDT.setText(rs.getString("SDT"));
                if (rs.getString("Email") != null) txtEmail.setText(rs.getString("Email"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ── PTTT ──
    @FXML private void handleChonTienMat()     { setPTTT("TienMat",     "Tiền mặt"); }
    @FXML private void handleChonChuyenKhoan() { setPTTT("ChuyenKhoan", "Chuyển khoản"); }

    private void setPTTT(String ma, String hienThi) {
        ptttChon = ma; ptttHienThi = hienThi;
        capNhatPTTT(ptttTienMat,    radioBtnTienMat,    "TienMat".equals(ma));
        capNhatPTTT(ptttChuyenKhoan,radioBtnChuyenKhoan,"ChuyenKhoan".equals(ma));
    }

    private void capNhatPTTT(HBox box, Label radio, boolean selected) {
        box.setStyle(selected ? STYLE_PTTT_SELECTED : STYLE_PTTT_DEFAULT);
        radio.setText(selected ? "●" : "○");
        radio.setStyle(selected
            ? "-fx-font-size: 14px; -fx-text-fill: #16a34a;"
            : "-fx-font-size: 14px; -fx-text-fill: #9ca3af;");
    }

    // ── ĐẶT SÂN ──
    @FXML
    private void handleDatSan() {
        String hoTen = txtHoTen.getText().trim();
        String sdt   = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();

        if (hoTen.isEmpty()) { showAlert("Vui lòng nhập họ tên!"); return; }
        if (sdt.isEmpty())   { showAlert("Vui lòng nhập số điện thoại!"); return; }

        if (!kiemTraSanTrong()) {
            showAlert("Sân đã được đặt trong khung giờ này! Vui lòng chọn khung giờ khác.");
            return;
        }

        Connection con = null;
        try {
            con = DBContext.KetNoi();
            con.setAutoCommit(false);

            String maKH  = timHoacTaoKhachHang(con, hoTen, sdt, email);
            String maDS  = sinhMaDS(con);
            long tienSan = (long)(giaSanChon * 1.5);

            // INSERT DATSAN
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO " + S + "DATSAN (MaDS,NgayDat,TrangThai,TongTienTamTinh,MaKH,MaSan,MaKG) " +
                    "VALUES (?,?,'ChoDuyet',?,?,?,?)")) {
                ps.setString(1, maDS);
                ps.setDate(2, Date.valueOf(ngayChon));
                ps.setLong(3, tienSan);
                ps.setString(4, maKH);
                ps.setString(5, maSanChon);
                ps.setString(6, maKGChon);
                ps.executeUpdate();
            }

            // INSERT HOADON
            String maHD = "HD" + maDS.substring(2);
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO " + S + "HOADON (MaHoaDon,TongTienDV,SoTienGG,ThanhTien,MaDS) " +
                    "VALUES (?,0,0,?,?)")) {
                ps.setString(1, maHD);
                ps.setLong(2, tienSan);
                ps.setString(3, maDS);
                ps.executeUpdate();
            }

            // INSERT THANHTOAN
            String maTT = "TT" + maDS.substring(2);
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO " + S + "THANHTOAN (MaTT,PTTT,TrangThai,MaHoaDon) " +
                    "VALUES (?,?,'DangXuLy',?)")) {
                ps.setString(1, maTT);
                ps.setString(2, ptttChon);
                ps.setString(3, maHD);
                ps.executeUpdate();
            }

            con.commit();
            
            LuuThongTinDatSan.maDS = maDS;

            // Hiện màn hình thành công
            lblMaDS.setText(maDS);
            lblKQSan.setText(tenSanChon);
            lblKQNgay.setText(ngayChon.format(DateTimeFormatter.ofPattern("d/M/yyyy")));
            lblKQGio.setText(tenKGChon);
            lblKQNguoiDat.setText(hoTen);
            lblKQSDT.setText(sdt);
            lblKQPTTT.setText(ptttHienThi);
            lblKQTongTien.setText(formatTien(tienSan));

            buoc2Pane.setVisible(false); buoc2Pane.setManaged(false);
            thanhCongPane.setVisible(true); thanhCongPane.setManaged(true);

        } catch (SQLException e) {
            if (con != null) try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            showAlert("Lỗi khi đặt sân: " + e.getMessage());
        } finally {
            if (con != null) try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private boolean kiemTraSanTrong() {
        String sql = "SELECT COUNT(*) FROM " + S + "DATSAN " +
                     "WHERE MaSan=? AND TRUNC(NgayDat)=TRUNC(?) " +
                     "AND MaKG=? AND TrangThai IN ('DaDuyet','ChoDuyet')";
        try (Connection con = DBContext.KetNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanChon);
            ps.setDate(2, Date.valueOf(ngayChon));
            ps.setString(3, maKGChon);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) == 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private String timHoacTaoKhachHang(Connection con, String hoTen, String sdt, String email)
            throws SQLException {
        // Ưu tiên dùng KH đang đăng nhập
        if (LuuThongTinDangNhap.maKH != null) return LuuThongTinDangNhap.maKH;

        // Tìm theo SDT
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT MaKH FROM " + S + "KHACHHANG WHERE SDT=?")) {
            ps.setString(1, sdt);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("MaKH");
        }

        // Tạo mới
        int nextNum = 1;
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT NVL(MAX(TO_NUMBER(SUBSTR(MaKH,3))),0)+1 FROM " + S + "KHACHHANG");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) nextNum = rs.getInt(1);
        }
        String maKH = "KH" + String.format("%03d", nextNum);
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO " + S + "KHACHHANG (MaKH,HoTen,SDT,Email,HangThanhVien,DiemTichLuy) " +
                "VALUES (?,?,?,?,'Đồng',0)")) {
            ps.setString(1, maKH);
            ps.setString(2, hoTen);
            ps.setString(3, sdt);
            ps.setString(4, email.isEmpty() ? null : email);
            ps.executeUpdate();
        }
        return maKH;
    }

    private String sinhMaDS(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT NVL(MAX(TO_NUMBER(SUBSTR(MaDS,3))),0)+1 FROM " + S + "DATSAN");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return "DS" + String.format("%06d", rs.getInt(1));
        }
        return "DS" + String.format("%06d", new Random().nextInt(999999));
    }

    // ── ĐIỀU HƯỚNG ──
    @FXML private void handleThoat() {
        try { App.setRoot("ManHinhChinh"); } catch (IOException e) { e.printStackTrace(); }
    }
    @FXML private void handleQuayLaiBuoc1() {
        buoc2Pane.setVisible(false); buoc2Pane.setManaged(false);
        buoc1Pane.setVisible(true);  buoc1Pane.setManaged(true);
    }
    @FXML private void handleQuayVeTrangChu() {
        try { App.setRoot("ManHinhChinh"); } catch (IOException e) { e.printStackTrace(); }
    }

    private String formatTien(long tien) {
        return String.format("%,d đ", tien).replace(",", ".");
    }
    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Thông báo"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
}