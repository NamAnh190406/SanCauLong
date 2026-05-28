package com.mycompany.mavenproject1;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * SanCauLongController
 *
 * - KHÔNG dùng mock data hardcode nữa.
 * - BookingStore static giữ data không mất khi navigate.
 * - Mỗi lần vào màn hình, sync BookingStore từ DB (load lại ngày hiện tại).
 * - Khi đổi trạng thái → UPDATE cả BookingStore lẫn DB.SAN + DB.DATSAN.
 */
public class SanCauLongController implements Initializable {

    // ================================================================= FXML
    @FXML private Label    lblDate;
    @FXML private GridPane scheduleGrid;
    @FXML private VBox     detailPanel;
    @FXML private Label    lblCustomer, lblPhone, lblCourt, lblTime, lblBookingDate, lblStatus, lblTotal;
    @FXML private Button   btnConfirm, btnEdit, btnCancelBooking;
    @FXML private VBox     waitingListBox;

    // ================================================================= Constants
    private static final String[] COURTS     = {"Sân 1","Sân 2","Sân 3","Sân 4","Sân 5","Sân 6"};
    private static final String[] TIME_SLOTS = {
        "06:00","07:00","08:00","09:00","10:00","11:00",
        "12:00","13:00","14:00","15:00","16:00","17:00",
        "18:00","19:00","20:00","21:00","22:00"
    };
    private static final int               PRICE_PER_HOUR       = 100_000;
    private static final DateTimeFormatter DATE_FMT              = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    // Phải khớp với giá trị thực trong DB.SAN.TrangThai
    private static final String            DB_SAN_TRONG         = "HoatDong";
    private static final String            DB_SAN_DANG_DUNG     = "DangDung";

    // ================================================================= Singleton store
    /**
     * Lưu booking theo key "dd/MM/yyyy|courtIdx|HH:mm".
     * Static → tồn tại suốt vòng đời app, không mất khi navigate.
     * Mỗi lần initialize() sẽ reload ngày hiện tại từ DB vào store.
     */
    private static class BookingStore {
        private static final BookingStore INSTANCE  = new BookingStore();
        private final Map<String, Booking> map      = new HashMap<>();
        // Tập các ngày đã load từ DB để không load lại nếu đã có
        private final Set<String>          loadedDates = new HashSet<>();
        private int nextId = 1;

        static BookingStore get() { return INSTANCE; }

        void put(Booking b)                        { map.put(key(b.date, b.courtId, b.timeSlot), b); }
        Booking get(LocalDate d, int c, String t)  { return map.get(key(d, c, t)); }
        void remove(LocalDate d, int c, String t)  { map.remove(key(d, c, t)); }
        Collection<Booking> all()                  { return map.values(); }
        int nextId()                               { return nextId++; }
        boolean isLoaded(LocalDate d)              { return loadedDates.contains(d.format(DATE_FMT)); }
        void markLoaded(LocalDate d)               { loadedDates.add(d.format(DATE_FMT)); }
        /** Xoá toàn bộ booking của một ngày (trước khi reload) */
        void clearDate(LocalDate d) {
            String prefix = d.format(DATE_FMT) + "|";
            map.entrySet().removeIf(e -> e.getKey().startsWith(prefix));
            loadedDates.remove(d.format(DATE_FMT));
        }

        private static String key(LocalDate date, int court, String time) {
            return date.format(DATE_FMT) + "|" + court + "|" + time;
        }
    }

    // ================================================================= Instance state
    private LocalDate currentDate;
    private Booking   selectedBooking = null;

    // ================================================================= Init
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentDate = LocalDate.now();
        // Mỗi lần vào màn hình: reload ngày hôm nay từ DB (luôn fresh)
        loadBookingsFromDB(currentDate, true);
        updateDateLabel();
        buildGrid();
        detailPanel.setVisible(false);
        detailPanel.setManaged(false);
    }

    // ================================================================= Load từ DB
    /**
     * Đọc DATSAN từ DB và đưa vào BookingStore.
     * @param forceReload true = xoá cache và load lại; false = bỏ qua nếu đã load
     *
     * Mapping cột DB → Booking:
     *   MaSan  → courtId  (index 0-based: MaSan 1 = index 0)
     *   KhungGio → timeSlot (giả sử dạng "08:00")
     *   TenKH / HoTen → customer
     *   SDT / DienThoai → phone
     *   TrangThai (DATSAN) → status ("DangChoi"→"playing", còn lại→"booked")
     *   SoGio / ThoiLuong → duration
     *   NgayDat → date
     *
     * ⚠️ Nếu tên cột DB khác thì sửa rs.getString("...") cho khớp.
     */
    private void loadBookingsFromDB(LocalDate date, boolean forceReload) {
        if (!forceReload && BookingStore.get().isLoaded(date)) return;
        BookingStore.get().clearDate(date);

        // Lấy ngày theo định dạng Oracle: TRUNC so sánh DATE
        String sql =
            "SELECT ds.MaSan - 1 AS CourtIdx, ds.KhungGio, kh.HoTen, kh.SDT, " +
            "       ds.TrangThai, ds.SoGio, ds.NgayDat, ds.MaDS " +
            "FROM DATSAN ds " +
            "JOIN KHACHHANG kh ON ds.MaKH = kh.MaKH " +
            "WHERE TRUNC(ds.NgayDat) = ? " +
            "AND ds.TrangThai NOT IN ('DaHuy', 'HoanThanh')";

        try (Connection conn = DBContext.KetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Oracle date: dùng java.sql.Date
            ps.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int     courtIdx  = rs.getInt("CourtIdx");        // 0-based
                    String  timeSlot  = rs.getString("KhungGio");     // "08:00"
                    String  hoTen     = rs.getString("HoTen");
                    String  sdt       = rs.getString("SDT");
                    String  ttDB      = rs.getString("TrangThai");    // "DangChoi", "DaDat", v.v.
                    int     soGio     = rs.getInt("SoGio");
                    Date    ngayDat   = rs.getDate("NgayDat");
                    int     maDS      = rs.getInt("MaDS");

                    // Map trạng thái DB → internal status
                    String status = "DangChoi".equalsIgnoreCase(ttDB) ? "playing" : "booked";

                    LocalDate ld = (ngayDat != null) ? ngayDat.toLocalDate() : date;
                    Booking b = new Booking(maDS, courtIdx, timeSlot, hoTen, sdt, status, soGio, ld);
                    BookingStore.get().put(b);
                }
            }
            BookingStore.get().markLoaded(date);
        } catch (SQLException e) {
            System.err.println("Lỗi load booking từ DB: " + e.getMessage());
        }
    }

    // ================================================================= Grid
    private void buildGrid() {
        scheduleGrid.getChildren().clear();
        scheduleGrid.getColumnConstraints().clear();
        scheduleGrid.getRowConstraints().clear();

        ColumnConstraints timeCol = new ColumnConstraints(90);
        timeCol.setHgrow(Priority.NEVER);
        scheduleGrid.getColumnConstraints().add(timeCol);
        for (int c = 0; c < COURTS.length; c++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setMinWidth(130);
            scheduleGrid.getColumnConstraints().add(cc);
        }

        scheduleGrid.getRowConstraints().add(new RowConstraints(44));
        addCell(makeHeaderCell("Giờ"), 0, 0);
        for (int c = 0; c < COURTS.length; c++) addCell(makeHeaderCell(COURTS[c]), c + 1, 0);

        for (int r = 0; r < TIME_SLOTS.length; r++) {
            String time = TIME_SLOTS[r];
            int    row  = r + 1;
            scheduleGrid.getRowConstraints().add(new RowConstraints(56));

            Label tl = new Label(time);
            tl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            tl.setAlignment(Pos.CENTER);
            tl.setStyle("-fx-background-color:#f9fafb; -fx-text-fill:#374151;"
                    + " -fx-font-size:12; -fx-font-weight:bold; -fx-padding:0 8;");
            addCell(tl, 0, row);

            for (int c = 0; c < COURTS.length; c++) {
                Booking b = BookingStore.get().get(currentDate, c, time);
                addCell(b != null ? makeBookedCell(b) : makeEmptyCell(c, time), c + 1, row);
            }
        }
    }

    private void addCell(javafx.scene.Node node, int col, int row) {
        GridPane.setColumnIndex(node, col);
        GridPane.setRowIndex(node, row);
        GridPane.setHgrow(node, Priority.ALWAYS);
        GridPane.setVgrow(node, Priority.ALWAYS);
        GridPane.setHalignment(node, HPos.CENTER);
        GridPane.setValignment(node, VPos.CENTER);
        scheduleGrid.getChildren().add(node);
    }

    private Label makeHeaderCell(String text) {
        Label l = new Label(text);
        l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        l.setAlignment(Pos.CENTER);
        l.setStyle("-fx-background-color:#f3f4f6; -fx-text-fill:#111827;"
                + " -fx-font-weight:bold; -fx-font-size:13; -fx-padding:0 8;");
        return l;
    }

    private Pane makeEmptyCell(int courtIdx, String time) {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);
        box.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        String base = "-fx-background-color:white; -fx-border-color:#e5e7eb;"
                + " -fx-border-width:0 1 1 0; -fx-cursor:hand; -fx-padding:4;";
        box.setStyle(base);
        Label plus = new Label("+");
        plus.setStyle("-fx-text-fill:#9ca3af; -fx-font-size:18;");
        Label lbl = new Label("Đặt sân");
        lbl.setStyle("-fx-text-fill:#9ca3af; -fx-font-size:11;");
        box.getChildren().addAll(plus, lbl);
        box.setOnMouseClicked(e -> { hideDetail(); showAddDialog(courtIdx, time); });
        box.setOnMouseEntered(e -> box.setStyle("-fx-background-color:#f0fdf4; -fx-border-color:#e5e7eb;"
                + " -fx-border-width:0 1 1 0; -fx-cursor:hand; -fx-padding:4;"));
        box.setOnMouseExited(e  -> box.setStyle(base));
        return box;
    }

    private Pane makeBookedCell(Booking b) {
        boolean playing   = b.status.equals("playing");
        String  bg        = playing ? "#dcfce7" : "#fef9c3";
        String  bdr       = playing ? "#86efac" : "#fcd34d";
        String  badgeBg   = playing ? "#bbf7d0" : "#fde68a";
        String  badgeFg   = playing ? "#16a34a" : "#b45309";
        String  statusTxt = playing ? "Đang chơi" : "Đã đặt";
        String  base      = String.format("-fx-background-color:%s; -fx-border-color:%s;"
                + " -fx-border-width:1 1 2 1; -fx-cursor:hand; -fx-padding:6;", bg, bdr);

        VBox box = new VBox(3);
        box.setAlignment(Pos.TOP_LEFT);
        box.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        box.setStyle(base);
        box.setPadding(new Insets(6));

        HBox top = new HBox(4);
        top.setAlignment(Pos.CENTER_LEFT);
        Label name = new Label(b.customer);
        name.setStyle("-fx-font-size:11; -fx-font-weight:bold; -fx-text-fill:#111827;");
        name.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(name, Priority.ALWAYS);
        Label badge = new Label(statusTxt);
        badge.setStyle(String.format("-fx-background-color:%s; -fx-text-fill:%s;"
                + " -fx-font-size:9; -fx-background-radius:4; -fx-padding:2 5;", badgeBg, badgeFg));
        top.getChildren().addAll(name, badge);

        Label phone = new Label(b.phone);
        phone.setStyle("-fx-font-size:10; -fx-text-fill:#6b7280;");
        Label dur = new Label(b.duration + "h");
        dur.setStyle("-fx-font-size:10; -fx-text-fill:#9ca3af;");

        box.getChildren().addAll(top, phone, dur);
        box.setOnMouseClicked(e -> showDetail(b));
        box.setOnMouseEntered(e -> box.setStyle(base + " -fx-opacity:0.82;"));
        box.setOnMouseExited(e  -> box.setStyle(base));
        return box;
    }

    // ================================================================= Detail
    private void showDetail(Booking b) {
        selectedBooking = b;
        boolean playing = b.status.equals("playing");
        lblCustomer.setText(b.customer);
        lblPhone.setText(b.phone);
        lblCourt.setText(COURTS[b.courtId]);
        lblTime.setText(b.timeSlot + " - " + calcEndTime(b.timeSlot, b.duration));
        lblBookingDate.setText(b.date.format(DATE_FMT));
        lblTotal.setText(String.format("%,dđ", (long) b.duration * PRICE_PER_HOUR).replace(',', '.'));

        if (playing) {
            lblStatus.setText("🟢 Đang chơi");
            lblStatus.setStyle("-fx-font-size:11; -fx-font-weight:bold; -fx-background-color:#dcfce7;"
                    + " -fx-text-fill:#16a34a; -fx-background-radius:5; -fx-padding:3 10;");
            btnConfirm.setText("↩ Chuyển về đã đặt");
            btnConfirm.setStyle("-fx-background-color:#f59e0b; -fx-text-fill:white; -fx-font-weight:bold;"
                    + " -fx-background-radius:8; -fx-padding:9 0; -fx-cursor:hand; -fx-font-size:13;");
        } else {
            lblStatus.setText("🟡 Đã đặt");
            lblStatus.setStyle("-fx-font-size:11; -fx-font-weight:bold; -fx-background-color:#fef9c3;"
                    + " -fx-text-fill:#b45309; -fx-background-radius:5; -fx-padding:3 10;");
            btnConfirm.setText("▶ Bắt đầu chơi");
            btnConfirm.setStyle("-fx-background-color:#16a34a; -fx-text-fill:white; -fx-font-weight:bold;"
                    + " -fx-background-radius:8; -fx-padding:9 0; -fx-cursor:hand; -fx-font-size:13;");
        }
        refreshWaitingList();
        detailPanel.setVisible(true);
        detailPanel.setManaged(true);
    }

    private void refreshWaitingList() {
        while (waitingListBox.getChildren().size() > 1) waitingListBox.getChildren().remove(1);
        int shown = 0;
        for (Booking b : BookingStore.get().all()) {
            if (selectedBooking != null && b.id == selectedBooking.id) continue;
            if (!b.date.equals(currentDate)) continue;
            if (shown >= 4) break;
            waitingListBox.getChildren().add(makeWaitingItem(b));
            shown++;
        }
        if (shown == 0) {
            Label e = new Label("Không có ai đang chờ");
            e.setStyle("-fx-font-size:12; -fx-text-fill:#9ca3af; -fx-padding:4 0;");
            waitingListBox.getChildren().add(e);
        }
    }

    private VBox makeWaitingItem(Booking b) {
        VBox item = new VBox(2);
        item.setStyle("-fx-background-color:#f9fafb; -fx-background-radius:8; -fx-padding:10 12;"
                + " -fx-border-color:#f3f4f6; -fx-border-width:1; -fx-border-radius:8;");
        Label n = new Label(b.customer);  n.setStyle("-fx-font-size:12; -fx-font-weight:bold; -fx-text-fill:#111827;");
        Label p = new Label(b.phone);     p.setStyle("-fx-font-size:11; -fx-text-fill:#6b7280;");
        Label s = new Label(COURTS[b.courtId] + " - " + b.timeSlot); s.setStyle("-fx-font-size:11; -fx-text-fill:#6b7280;");
        item.getChildren().addAll(n, p, s);
        return item;
    }

    private void hideDetail() {
        selectedBooking = null;
        detailPanel.setVisible(false);
        detailPanel.setManaged(false);
    }

    // ================================================================= Buttons
    @FXML private void onCloseDetail() { hideDetail(); }

    /**
     * Bắt đầu chơi / Chuyển về đã đặt.
     * Cập nhật: BookingStore + DB.DATSAN.TrangThai + DB.SAN.TrangThai
     */
    @FXML
    private void onConfirmBooking() {
        if (selectedBooking == null) return;
        boolean goingPlaying = !selectedBooking.status.equals("playing");
        String  newStatus    = goingPlaying ? "playing" : "booked";
        String  newDbDatSan  = goingPlaying ? "DangChoi" : "DaDat";   // giá trị trong DB.DATSAN
        String  newDbSan     = goingPlaying ? DB_SAN_DANG_DUNG : tinhTrangThaiSan(selectedBooking.courtId, selectedBooking.id);

        // 1. Cập nhật DB.DATSAN
        updateDatSanTrangThai(selectedBooking.id, newDbDatSan);
        // 2. Cập nhật DB.SAN
        updateSanTrangThaiDB(COURTS[selectedBooking.courtId], newDbSan);
        // 3. Cập nhật BookingStore
        Booking updated = new Booking(selectedBooking.id, selectedBooking.courtId,
                selectedBooking.timeSlot, selectedBooking.customer, selectedBooking.phone,
                newStatus, selectedBooking.duration, selectedBooking.date);
        BookingStore.get().put(updated);

        buildGrid();
        showDetail(updated);
    }

    @FXML
    private void onEditBooking() {
        if (selectedBooking == null) return;
        showAddDialog(selectedBooking.courtId, selectedBooking.timeSlot);
    }

    @FXML
    private void onCancelBooking() {
        if (selectedBooking == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Hủy đặt sân của " + selectedBooking.customer + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.setTitle("Xác nhận hủy");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                int courtId = selectedBooking.courtId;
                // 1. Update DB.DATSAN → DaHuy
                updateDatSanTrangThai(selectedBooking.id, "DaHuy");
                // 2. Tính lại trạng thái sân (bỏ booking này ra)
                String sanStatus = tinhTrangThaiSan(courtId, selectedBooking.id);
                updateSanTrangThaiDB(COURTS[courtId], sanStatus);
                // 3. Xoá khỏi store
                BookingStore.get().remove(selectedBooking.date, courtId, selectedBooking.timeSlot);
                buildGrid();
                hideDetail();
            }
        });
    }

    // ================================================================= Add/Edit dialog
    @FXML private void onAddBooking() { showAddDialog(-1, ""); }

    private void showAddDialog(int preCourtIdx, String preTime) {
        boolean isEdit = selectedBooking != null
                && preCourtIdx == selectedBooking.courtId
                && preTime.equals(selectedBooking.timeSlot);

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(isEdit ? "Chỉnh Sửa Đặt Sân" : "Thêm Đặt Sân Mới");

        VBox root = new VBox(14);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color:white;");
        root.setMinWidth(400);

        Label title = new Label(isEdit ? "✏ Chỉnh Sửa Đặt Sân" : "📋 Thêm Đặt Sân Mới");
        title.setStyle("-fx-font-size:16; -fx-font-weight:bold; -fx-text-fill:#111827;");

        TextField tfName  = styledField("Nhập tên khách hàng");
        TextField tfPhone = styledField("Nhập số điện thoại");
        if (isEdit) { tfName.setText(selectedBooking.customer); tfPhone.setText(selectedBooking.phone); }

        ComboBox<String> cbCourt = new ComboBox<>();
        cbCourt.getItems().addAll(COURTS); cbCourt.setPromptText("Chọn sân..."); cbCourt.setMaxWidth(Double.MAX_VALUE); styleCombo(cbCourt);
        if (preCourtIdx >= 0) cbCourt.getSelectionModel().select(preCourtIdx);

        ComboBox<String> cbTime = new ComboBox<>();
        cbTime.getItems().addAll(TIME_SLOTS); cbTime.setPromptText("Chọn giờ..."); cbTime.setMaxWidth(Double.MAX_VALUE); styleCombo(cbTime);
        if (!preTime.isEmpty()) cbTime.getSelectionModel().select(preTime);

        ComboBox<String> cbDur = new ComboBox<>();
        cbDur.getItems().addAll("1 giờ","2 giờ","3 giờ"); cbDur.setMaxWidth(Double.MAX_VALUE); styleCombo(cbDur);
        cbDur.getSelectionModel().select(isEdit ? selectedBooking.duration - 1 : 0);

        HBox btns = new HBox(10);
        Button btnCancel = new Button("Hủy");
        Button btnOk     = new Button("✔ Xác nhận");
        for (Button b : new Button[]{btnCancel, btnOk}) { b.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(b, Priority.ALWAYS); }
        btnCancel.setStyle("-fx-background-color:#6b7280; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:8; -fx-padding:10 0; -fx-cursor:hand;");
        btnOk.setStyle("-fx-background-color:#16a34a; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:8; -fx-padding:10 0; -fx-cursor:hand;");
        btns.getChildren().addAll(btnCancel, btnOk);

        root.getChildren().addAll(title,
                labeled("Tên khách hàng *", tfName), labeled("Số điện thoại *", tfPhone),
                labeled("Chọn sân *", cbCourt), labeled("Giờ bắt đầu *", cbTime),
                labeled("Thời lượng *", cbDur), btns);

        btnCancel.setOnAction(e -> dialog.close());
        btnOk.setOnAction(e -> {
            String name     = tfName.getText().trim();
            String phone    = tfPhone.getText().trim();
            int    courtIdx = cbCourt.getSelectionModel().getSelectedIndex();
            String time     = cbTime.getSelectionModel().getSelectedItem();
            int    dur      = cbDur.getSelectionModel().getSelectedIndex() + 1;

            if (name.isEmpty() || phone.isEmpty() || courtIdx < 0 || time == null) {
                showAlert("Vui lòng điền đầy đủ thông tin!"); return;
            }
            Booking existing = BookingStore.get().get(currentDate, courtIdx, time);
            if (existing != null && (selectedBooking == null || existing.id != selectedBooking.id)) {
                showAlert("Sân này đã có người đặt vào khung giờ này!"); return;
            }

            if (isEdit) {
                // Xoá slot cũ trong store nếu sân/giờ thay đổi
                BookingStore.get().remove(selectedBooking.date, selectedBooking.courtId, selectedBooking.timeSlot);
                // Update DB.DATSAN (chỉ tên KH, giờ, sân — tuỳ schema của bạn)
                // updateDatSanEdit(selectedBooking.id, courtIdx+1, time, dur); // mở nếu cần
            }

            // Insert hoặc update vào DB.DATSAN — TODO tuỳ schema
            // int newId = isEdit ? selectedBooking.id : insertDatSan(courtIdx, time, dur, name, phone);
            int newId = isEdit ? selectedBooking.id : BookingStore.get().nextId(); // tạm dùng nextId

            Booking b = new Booking(newId, courtIdx, time, name, phone,
                    isEdit ? selectedBooking.status : "booked", dur, currentDate);
            BookingStore.get().put(b);
            buildGrid();
            showDetail(b);
            dialog.close();
        });

        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }

    // ================================================================= Navigation
    @FXML private void onPrevDay() {
        currentDate = currentDate.minusDays(1);
        loadBookingsFromDB(currentDate, false); // load nếu chưa có
        updateDateLabel(); buildGrid(); hideDetail();
    }
    @FXML private void onNextDay() {
        currentDate = currentDate.plusDays(1);
        loadBookingsFromDB(currentDate, false);
        updateDateLabel(); buildGrid(); hideDetail();
    }
    private void updateDateLabel() { lblDate.setText(currentDate.format(DATE_FMT)); }

    // ================================================================= DB helpers

    /**
     * Tính trạng thái SAN dựa trên BookingStore,
     * bỏ qua booking có id = excludeId (booking vừa bị cancel/đổi trạng thái).
     */
    private String tinhTrangThaiSan(int courtId, int excludeId) {
        for (Booking b : BookingStore.get().all()) {
            if (b.id == excludeId) continue;
            if (b.courtId == courtId && b.date.equals(currentDate) && b.status.equals("playing")) {
                return DB_SAN_DANG_DUNG;
            }
        }
        return DB_SAN_TRONG;
    }

    /** UPDATE SAN.TrangThai theo TenSan */
    private void updateSanTrangThaiDB(String tenSan, String trangThai) {
        String sql = "UPDATE SAN SET TrangThai = ? WHERE TenSan = ?";
        try (Connection conn = DBContext.KetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setString(2, tenSan);
            int rows = ps.executeUpdate();
            System.out.println("UPDATE SAN " + tenSan + " → " + trangThai + " (" + rows + " rows)");
        } catch (SQLException ex) {
            System.err.println("Lỗi update SAN: " + ex.getMessage());
        }
    }

    /** UPDATE DATSAN.TrangThai theo MaDS */
    private void updateDatSanTrangThai(int maDS, String trangThai) {
        String sql = "UPDATE DATSAN SET TrangThai = ? WHERE MaDS = ?";
        try (Connection conn = DBContext.KetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setInt(2, maDS);
            int rows = ps.executeUpdate();
            System.out.println("UPDATE DATSAN " + maDS + " → " + trangThai + " (" + rows + " rows)");
        } catch (SQLException ex) {
            System.err.println("Lỗi update DATSAN: " + ex.getMessage());
        }
    }

    // ================================================================= UI helpers
    private static String calcEndTime(String start, int hours) {
        String[] p = start.split(":");
        return String.format("%02d:%s", Integer.parseInt(p[0]) + hours, p[1]);
    }
    private TextField styledField(String prompt) {
        TextField tf = new TextField(); tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color:#f9fafb; -fx-border-color:#e5e7eb;"
                + " -fx-border-radius:8; -fx-background-radius:8; -fx-padding:9 12; -fx-font-size:13;");
        return tf;
    }
    private void styleCombo(ComboBox<?> cb) {
        cb.setStyle("-fx-background-color:#f0fdf4; -fx-border-color:#86efac;"
                + " -fx-border-radius:8; -fx-background-radius:8; -fx-font-size:13;");
    }
    private VBox labeled(String labelText, javafx.scene.Node field) {
        VBox vb = new VBox(5);
        Label l = new Label(labelText); l.setStyle("-fx-font-size:12; -fx-text-fill:#6b7280;");
        vb.getChildren().addAll(l, field); return vb;
    }
    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        a.setHeaderText(null); a.showAndWait();
    }

    // ================================================================= Model
    private static class Booking {
        final int id, courtId, duration;
        final String timeSlot, customer, phone, status;
        final LocalDate date;
        Booking(int id, int courtId, String timeSlot, String customer, String phone,
                String status, int duration, LocalDate date) {
            this.id = id; this.courtId = courtId; this.timeSlot = timeSlot;
            this.customer = customer; this.phone = phone;
            this.status = status; this.duration = duration; this.date = date;
        }
    }

    @FXML
    private void onBackClick() {
        try { App.setRoot("ManHinhChinhNV"); } catch (Exception e) { e.printStackTrace(); }
    }
}
