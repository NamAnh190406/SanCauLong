package com.mycompany.mavenproject1;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LichSuDSController implements Initializable {

    @FXML private VBox listContainer;
    @FXML private Label lblTongDS;
    @FXML private Label lblHoanThanh;
    @FXML private Label lblDaDuyet;
    @FXML private Label lblChoDuyet;
    @FXML private Label lblDaHuy;
    @FXML private Button tabTatCa;
    @FXML private Button tabChoDuyet;
    @FXML private Button tabDaDuyet;
    @FXML private Button tabHoanThanh;
    @FXML private Button tabDaHuy;
    @FXML private StackPane popupDanhGia;
    @FXML private Label lblDanhGiaTenSan;
    @FXML private Button btnSao1, btnSao2, btnSao3, btnSao4, btnSao5;
    @FXML private Label lblMoTaSao;
    @FXML private TextArea txtNhanXet;
    @FXML private StackPane popupHuy;
    @FXML private Label lblHuyTenSan;

    private String activeTab = "Tất cả";
    private String selectedMaDS = null;
    private String selectedTenSan = null;
    private String selectedMaSan = null;
    private int currentSaoSelected = 5;
    private List<BookingHistory> fullHistoryList = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (popupDanhGia != null) { popupDanhGia.setVisible(false); popupDanhGia.setManaged(false); }
        if (popupHuy != null)     { popupHuy.setVisible(false);     popupHuy.setManaged(false); }
        refreshData();
    }

    private void goTo(String fxml) {
        try { App.setRoot(fxml); } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void handleQuayLai()    { goTo("ManHinhChinh"); }
    @FXML private void handleVeTrangChu() { goTo("ManHinhChinh"); }

    // ── Tải dữ liệu ──────────────────────────────
    private void refreshData() {
        fullHistoryList.clear();

        String maKH = LuuThongTinDangNhap.maKH;
        if (maKH == null || maKH.isEmpty()) {
            System.err.println("Chua dang nhap hoac session bi mat.");
            updateStatistics();
            renderBookingList();
            return;
        }

        System.out.println("Dang tai lich su cho MaKH: " + maKH);

        String sql = "SELECT ds.MaDS, ds.NgayDat, ds.TongTienTamTinh, ds.TrangThai, " +
                     "       s.MaSan, s.TenSan, s.LoaiSan, " +
                     "       kg.GioBD, kg.GioKT " +
                     "FROM DATSAN ds " +
                     "JOIN SAN s ON ds.MaSan = s.MaSan " +
                     "JOIN KHUNGGIO kg ON ds.MaKG = kg.MaKG " +
                     "WHERE ds.MaKH = ? " +
                     "ORDER BY ds.NgayDat DESC, ds.MaDS DESC";

        try (Connection conn = DBContext.KetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKH);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String gioBD = "", gioKT = "";
                    if (rs.getTimestamp("GioBD") != null)
                        gioBD = rs.getTimestamp("GioBD").toString().substring(11, 16);
                    if (rs.getTimestamp("GioKT") != null)
                        gioKT = rs.getTimestamp("GioKT").toString().substring(11, 16);

                    fullHistoryList.add(new BookingHistory(
                        rs.getString("MaDS"),
                        rs.getDate("NgayDat") != null ? rs.getDate("NgayDat").toString() : "",
                        rs.getDouble("TongTienTamTinh"),
                        rs.getString("TrangThai"),
                        rs.getString("MaSan"),
                        rs.getString("TenSan"),
                        rs.getString("LoaiSan"),
                        gioBD, gioKT
                    ));
                }
            }
            System.out.println("Tai thanh cong " + fullHistoryList.size() + " ban ghi.");
        } catch (SQLException e) {
            System.err.println("Loi query: " + e.getMessage());
            e.printStackTrace();
        }

        updateStatistics();
        renderBookingList();
    }

    // ── Thống kê ──────────────────────────────────
    private void updateStatistics() {
        int tong = fullHistoryList.size();
        int choDuyet = 0, daDuyet = 0, hoanThanh = 0, daHuy = 0;

        for (BookingHistory bh : fullHistoryList) {
            switch (bh.trangThai) {
                case "ChoDuyet":  choDuyet++;  break;
                case "DaDuyet":   daDuyet++;   break;
                case "HoanThanh": hoanThanh++; break;
                case "DaHuy":     daHuy++;     break;
            }
        }

        if (lblTongDS != null)    lblTongDS.setText(String.valueOf(tong));
        if (lblChoDuyet != null)  lblChoDuyet.setText(String.valueOf(choDuyet));
        if (lblDaDuyet != null)   lblDaDuyet.setText(String.valueOf(daDuyet));
        if (lblHoanThanh != null) lblHoanThanh.setText(String.valueOf(hoanThanh));
        if (lblDaHuy != null)     lblDaHuy.setText(String.valueOf(daHuy));
    }

    private String trangThaiHienThi(String trangThai) {
        switch (trangThai) {
            case "ChoDuyet":  return "Chờ duyệt";
            case "DaDuyet":   return "Đã duyệt";
            case "HoanThanh": return "Hoàn thành";
            case "DaHuy":     return "Đã hủy";
            default:          return trangThai;
        }
    }

    // ── Kiểm tra đã đánh giá chưa ────────────────
    private boolean kiemTraDaDanhGia(String maSan) {
        String maKH = LuuThongTinDangNhap.maKH;
        String sql = "SELECT COUNT(*) FROM DANHGIASAN WHERE MaKH = ? AND MaSan = ?";
        try (Connection conn = DBContext.KetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKH);
            ps.setString(2, maSan);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── Render danh sách ──────────────────────────
    private void renderBookingList() {
        if (listContainer == null) return;
        listContainer.getChildren().clear();

        DecimalFormat df = new DecimalFormat("#,###");

        for (BookingHistory bh : fullHistoryList) {
            if (!"Tất cả".equals(activeTab)) {
                String tabDB = "";
                switch (activeTab) {
                    case "Chờ duyệt":  tabDB = "ChoDuyet";  break;
                    case "Đã duyệt":   tabDB = "DaDuyet";   break;
                    case "Hoàn thành": tabDB = "HoanThanh"; break;
                    case "Đã hủy":     tabDB = "DaHuy";     break;
                }
                if (!tabDB.equals(bh.trangThai)) continue;
            }

            HBox card = new HBox();
            card.setSpacing(20);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                          "-fx-border-color: #e5e7eb; -fx-border-radius: 12; " +
                          "-fx-border-width: 1; -fx-padding: 20; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 1);");

            VBox infoBox = new VBox();
            infoBox.setSpacing(6);
            HBox.setHgrow(infoBox, javafx.scene.layout.Priority.ALWAYS);

            Label lblTenSan = new Label(bh.tenSan);
            lblTenSan.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #111827;");

            Label lblMaNgay = new Label("Mã đặt: " + bh.maDS + "  |  Ngày đặt: " + bh.ngayDat);
            lblMaNgay.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280;");

            Label lblGioLoai = new Label("🕒 " + bh.gioBD + " - " + bh.gioKT + "  |  " + bh.loaiSan);
            lblGioLoai.setStyle("-fx-font-size: 13px; -fx-text-fill: #4b5563;");

            Label lblGia = new Label("Tổng tiền: " + df.format(bh.tongTien) + " VNĐ");
            lblGia.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #16a34a;");

            infoBox.getChildren().addAll(lblTenSan, lblMaNgay, lblGioLoai, lblGia);

            VBox actionBox = new VBox();
            actionBox.setSpacing(12);
            actionBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            actionBox.setPrefWidth(180);

            Label lblStatus = new Label("● " + trangThaiHienThi(bh.trangThai));
            String pillStyle = "-fx-padding: 6 12 6 12; -fx-background-radius: 12; " +
                               "-fx-font-size: 12px; -fx-font-weight: bold;";
            switch (bh.trangThai) {
                case "ChoDuyet":  pillStyle += "-fx-background-color: #fef3c7; -fx-text-fill: #d97706;"; break;
                case "DaDuyet":   pillStyle += "-fx-background-color: #dbeafe; -fx-text-fill: #2563eb;"; break;
                case "HoanThanh": pillStyle += "-fx-background-color: #dcfce7; -fx-text-fill: #16a34a;"; break;
                case "DaHuy":     pillStyle += "-fx-background-color: #fee2e2; -fx-text-fill: #dc2626;"; break;
            }
            lblStatus.setStyle(pillStyle);
            actionBox.getChildren().add(lblStatus);

            if ("ChoDuyet".equals(bh.trangThai)) {
                Button btnHuy = new Button("🗑 Hủy đặt sân");
                btnHuy.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; " +
                                "-fx-font-size: 12px; -fx-font-weight: bold; " +
                                "-fx-background-radius: 8; -fx-padding: 8 14 8 14; " +
                                "-fx-border-color: #fca5a5; -fx-border-radius: 8; -fx-cursor: hand;");
                btnHuy.setOnAction(e -> moPopupHuy(bh.maDS, bh.tenSan));
                actionBox.getChildren().add(btnHuy);

            } else if ("HoanThanh".equals(bh.trangThai)) {
                if (kiemTraDaDanhGia(bh.maSan)) {
                    Label lblDaXong = new Label("✓ Đã đánh giá");
                    lblDaXong.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 12px; -fx-font-weight: bold;");
                    actionBox.getChildren().add(lblDaXong);
                } else {
                    Button btnDanhGia = new Button("⭐ Đánh giá");
                    btnDanhGia.setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #15803d; " +
                                        "-fx-font-size: 12px; -fx-font-weight: bold; " +
                                        "-fx-background-radius: 8; -fx-padding: 8 14 8 14; " +
                                        "-fx-border-color: #86efac; -fx-border-radius: 8; -fx-cursor: hand;");
                    btnDanhGia.setOnAction(e -> moPopupDanhGia(bh.maDS, bh.tenSan, bh.maSan));
                    actionBox.getChildren().add(btnDanhGia);
                }
            }

            card.getChildren().addAll(infoBox, actionBox);
            listContainer.getChildren().add(card);
        }

        if (listContainer.getChildren().isEmpty()) {
            VBox emptyBox = new VBox();
            emptyBox.setAlignment(javafx.geometry.Pos.CENTER);
            emptyBox.setSpacing(10);
            emptyBox.setStyle("-fx-padding: 40;");
            Label lblEmpty = new Label("Không có lịch sử đặt sân nào trong mục này.");
            lblEmpty.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280; -fx-font-style: italic;");
            emptyBox.getChildren().add(lblEmpty);
            listContainer.getChildren().add(emptyBox);
        }
    }

    // ── Tab filter ────────────────────────────────
    @FXML private void handleTabTatCa()     { activeTab = "Tất cả";    capNhatTrangThaiTab(); }
    @FXML private void handleTabChoDuyet()  { activeTab = "Chờ duyệt"; capNhatTrangThaiTab(); }
    @FXML private void handleTabDaDuyet()   { activeTab = "Đã duyệt";  capNhatTrangThaiTab(); }
    @FXML private void handleTabHoanThanh() { activeTab = "Hoàn thành"; capNhatTrangThaiTab(); }
    @FXML private void handleTabDaHuy()     { activeTab = "Đã hủy";    capNhatTrangThaiTab(); }

    private void capNhatTrangThaiTab() {
        Button[] tabs   = {tabTatCa, tabChoDuyet, tabDaDuyet, tabHoanThanh, tabDaHuy};
        String[] labels = {"Tất cả", "Chờ duyệt", "Đã duyệt", "Hoàn thành", "Đã hủy"};
        for (int i = 0; i < tabs.length; i++) {
            if (tabs[i] == null) continue;
            if (labels[i].equals(activeTab)) {
                tabs[i].setStyle("-fx-background-color: #0f172a; -fx-text-fill: white; " +
                                 "-fx-font-size: 13px; -fx-font-weight: bold; " +
                                 "-fx-padding: 10 20 10 20; -fx-cursor: hand;");
            } else {
                tabs[i].setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #374151; " +
                                 "-fx-font-size: 13px; -fx-padding: 10 20 10 20; -fx-cursor: hand;");
            }
        }
        renderBookingList();
    }

    // ── Popup Hủy ─────────────────────────────────
    private void moPopupHuy(String maDS, String tenSan) {
        this.selectedMaDS = maDS;
        this.selectedTenSan = tenSan;
        if (lblHuyTenSan != null)
            lblHuyTenSan.setText("Sân: " + tenSan + " (Mã đặt: " + maDS + ")");
        if (popupHuy != null) { popupHuy.setVisible(true); popupHuy.setManaged(true); }
    }

    @FXML
    private void handleDongHuy() {
        if (popupHuy != null) { popupHuy.setVisible(false); popupHuy.setManaged(false); }
        selectedMaDS = null; selectedTenSan = null;
    }

    @FXML
    private void handleXacNhanHuy() {
        if (selectedMaDS == null) return;
        try (Connection conn = DBContext.KetNoi();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE DATSAN SET TrangThai = 'DaHuy' WHERE MaDS = ?")) {
            ps.setString(1, selectedMaDS);
            if (ps.executeUpdate() > 0) {
                hienThongBao(AlertType.INFORMATION, "Thành công",
                        "Đã hủy đặt sân thành công!",
                        "Lịch đặt sân mã " + selectedMaDS + " đã được hủy.");
                handleDongHuy();
                refreshData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            hienThongBao(AlertType.ERROR, "Lỗi", "Không thể hủy", e.getMessage());
        }
    }

    // ── Popup Đánh Giá ────────────────────────────
    private void moPopupDanhGia(String maDS, String tenSan, String maSan) {
        this.selectedMaDS   = maDS;
        this.selectedTenSan = tenSan;
        this.selectedMaSan  = maSan;
        if (lblDanhGiaTenSan != null)
            lblDanhGiaTenSan.setText("Sân: " + tenSan + " (Mã đặt: " + maDS + ")");
        if (txtNhanXet != null) txtNhanXet.clear();
        updateStarUI(5);
        if (popupDanhGia != null) { popupDanhGia.setVisible(true); popupDanhGia.setManaged(true); }
    }

    @FXML
    private void handleDongDanhGia() {
        if (popupDanhGia != null) { popupDanhGia.setVisible(false); popupDanhGia.setManaged(false); }
        selectedMaDS = null; selectedTenSan = null; selectedMaSan = null;
    }

    @FXML private void handleSao1() { updateStarUI(1); }
    @FXML private void handleSao2() { updateStarUI(2); }
    @FXML private void handleSao3() { updateStarUI(3); }
    @FXML private void handleSao4() { updateStarUI(4); }
    @FXML private void handleSao5() { updateStarUI(5); }

    private void updateStarUI(int count) {
        this.currentSaoSelected = count;
        Button[] buttons = {btnSao1, btnSao2, btnSao3, btnSao4, btnSao5};
        for (int i = 0; i < 5; i++) {
            if (buttons[i] == null) continue;
            buttons[i].setStyle("-fx-background-color: transparent; -fx-font-size: 32px; " +
                    "-fx-cursor: hand; -fx-padding: 0 4 0 4; " +
                    "-fx-text-fill: " + (i < count ? "#fbbf24" : "#d1d5db") + ";");
        }
        if (lblMoTaSao != null) {
            switch (count) {
                case 1: lblMoTaSao.setText("Tệ ⭐️"); break;
                case 2: lblMoTaSao.setText("Không hài lòng ⭐️⭐️"); break;
                case 3: lblMoTaSao.setText("Bình thường ⭐️⭐️⭐️"); break;
                case 4: lblMoTaSao.setText("Rất tốt ⭐️⭐️⭐️⭐️"); break;
                case 5: lblMoTaSao.setText("Tuyệt vời! ⭐️⭐️⭐️⭐️⭐️"); break;
            }
        }
    }

    @FXML
    private void handleGuiDanhGia() {
        if (selectedMaDS == null || selectedMaSan == null) return;
        String nhanXet = txtNhanXet != null ? txtNhanXet.getText().trim() : "";
        String maKH = LuuThongTinDangNhap.maKH;

        // Kiểm tra đã đánh giá chưa
        String sqlCheck = "SELECT COUNT(*) FROM DANHGIASAN WHERE MaKH = ? AND MaSan = ?";
        try (Connection conn = DBContext.KetNoi();
             PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
            psCheck.setString(1, maKH);
            psCheck.setString(2, selectedMaSan);
            ResultSet rs = psCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                hienThongBao(AlertType.WARNING, "Thông báo",
                        "Bạn đã đánh giá sân này rồi!",
                        "Mỗi sân chỉ được đánh giá 1 lần.");
                handleDongDanhGia();
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // Insert đánh giá
        String sql = "INSERT INTO DANHGIASAN (MaDanhGia, DiemDG, NhanXet, ThoiDiemDanhGia, MaKH, MaSan) " +
                     "VALUES ('DG' || LPAD(SEQ_DANH_GIA.NEXTVAL, 3, '0'), ?, ?, SYSDATE, ?, ?)";
        try (Connection conn = DBContext.KetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentSaoSelected);
            ps.setString(2, nhanXet.isEmpty() ? null : nhanXet);
            ps.setString(3, maKH);
            ps.setString(4, selectedMaSan);
            ps.executeUpdate();
            hienThongBao(AlertType.INFORMATION, "Cảm ơn!",
                    "Gửi đánh giá thành công!", "Cảm ơn bạn đã đánh giá sân: " + selectedTenSan);
            handleDongDanhGia();
            refreshData();
        } catch (SQLException e) {
            e.printStackTrace();
            hienThongBao(AlertType.ERROR, "Lỗi", "Không thể gửi đánh giá", e.getMessage());
        }
        System.out.println("maKH session: [" + maKH + "]");
System.out.println("maSan: [" + selectedMaSan + "]");
    }

    private void hienThongBao(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // ── Model ─────────────────────────────────────
    public static class BookingHistory {
        public String maDS, ngayDat, trangThai, maSan, tenSan, loaiSan, gioBD, gioKT;
        public double tongTien;

        public BookingHistory(String maDS, String ngayDat, double tongTien, String trangThai,
                              String maSan, String tenSan, String loaiSan,
                              String gioBD, String gioKT) {
            this.maDS = maDS; this.ngayDat = ngayDat; this.tongTien = tongTien;
            this.trangThai = trangThai; this.maSan = maSan; this.tenSan = tenSan;
            this.loaiSan = loaiSan; this.gioBD = gioBD; this.gioKT = gioKT;
        }
    }
}