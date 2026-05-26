package Controller;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SanBadmintonController implements Initializable {

    // ------------------------------------------------------------------ FXML
    @FXML private Button btnPrevDay;
    @FXML private Label  lblDate;
    @FXML private GridPane scheduleGrid;

    // Detail panel FXML refs
    @FXML private VBox   detailPanel;
    @FXML private Label  lblCustomer;
    @FXML private Label  lblPhone;
    @FXML private Label  lblCourt;
    @FXML private Label  lblTime;
    @FXML private Label  lblBookingDate;
    @FXML private Label  lblStatus;
    @FXML private Label  lblTotal;
    @FXML private Button btnConfirm;
    @FXML private Button btnEdit;
    @FXML private Button btnCancelBooking;
    @FXML private VBox   waitingListBox;

    // ------------------------------------------------------------------ Data
    private static final String[] COURTS     = {"Sân 1","Sân 2","Sân 3","Sân 4","Sân 5","Sân 6"};
    private static final String[] TIME_SLOTS = {
        "06:00","07:00","08:00","09:00","10:00","11:00",
        "12:00","13:00","14:00","15:00","16:00","17:00",
        "18:00","19:00","20:00","21:00","22:00"
    };
    private static final int PRICE_PER_HOUR = 100000; // VND

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private LocalDate currentDate;
    private final Map<String, Booking> bookingMap = new HashMap<>();
    private int nextId = 1;
    private Booking selectedBooking = null;

    // ================================================================= Init
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentDate = LocalDate.now();
        updateDateLabel();
        
        // Thêm một số dữ liệu mẫu (Mock data) vào bookingMap để test giao diện
        addMock(0, "08:00", "Nguyễn Văn A", "0901234567", "booked", 2); // Sân 1, 8h, 2 tiếng
        addMock(1, "14:00", "Trần Thị B", "0912345678", "playing", 1);  // Sân 2, 14h, 1 tiếng

        buildGrid(); // GỌI HÀM VẼ LƯỚI MỚI CHUẨN

        detailPanel.setVisible(false);
        detailPanel.setManaged(false);
    }

    private void addMock(int courtIdx, String time, String name, String phone, String status, int dur) {
        bookingMap.put(key(courtIdx, time), new Booking(nextId++, courtIdx, time, name, phone, status, dur));
    }

    // ================================================================= Grid
    private void buildGrid() {
        scheduleGrid.getChildren().clear();
        scheduleGrid.getColumnConstraints().clear();
        scheduleGrid.getRowConstraints().clear();

        // Col 0 = time, col 1..N = courts
        ColumnConstraints timeCol = new ColumnConstraints(90);
        timeCol.setHgrow(Priority.NEVER);
        scheduleGrid.getColumnConstraints().add(timeCol);
        for (int c = 0; c < COURTS.length; c++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setMinWidth(130);
            scheduleGrid.getColumnConstraints().add(cc);
        }

        // Header row
        scheduleGrid.getRowConstraints().add(new RowConstraints(44));
        addCell(makeHeaderCell("Giờ"), 0, 0);
        for (int c = 0; c < COURTS.length; c++) {
            addCell(makeHeaderCell(COURTS[c]), c + 1, 0);
        }

        // Time rows
        for (int r = 0; r < TIME_SLOTS.length; r++) {
            String time = TIME_SLOTS[r];
            int row = r + 1;
            scheduleGrid.getRowConstraints().add(new RowConstraints(56));

            Label timeLabel = new Label(time);
            timeLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            timeLabel.setAlignment(Pos.CENTER);
            timeLabel.setStyle("-fx-background-color: #f9fafb; -fx-text-fill: #374151;"
                    + " -fx-font-size: 12; -fx-font-weight: bold; -fx-padding: 0 8;");
            addCell(timeLabel, 0, row);

            for (int c = 0; c < COURTS.length; c++) {
                Booking b = bookingMap.get(key(c, time));
                Pane cell = b != null ? makeBookedCell(b) : makeEmptyCell(c, time);
                addCell(cell, c + 1, row);
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
        l.setStyle("-fx-background-color: #f3f4f6; -fx-text-fill: #111827;"
                + " -fx-font-weight: bold; -fx-font-size: 13; -fx-padding: 0 8;");
        return l;
    }

    private Pane makeEmptyCell(int courtIdx, String time) {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);
        box.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        String baseStyle = "-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-width: 0 1 1 0; -fx-cursor: hand; -fx-padding: 4;";
        box.setStyle(baseStyle);

        Label plus = new Label("+");
        plus.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 18;");
        Label lbl = new Label("Đặt sân");
        lbl.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 11;");
        box.getChildren().addAll(plus, lbl);

        box.setOnMouseClicked(e -> { hideDetail(); showAddDialog(courtIdx, time); });
        box.setOnMouseEntered(e -> box.setStyle("-fx-background-color: #f0fdf4; -fx-border-color: #e5e7eb; -fx-border-width: 0 1 1 0; -fx-cursor: hand; -fx-padding: 4;"));
        box.setOnMouseExited(e -> box.setStyle(baseStyle));
        return box;
    }

    private Pane makeBookedCell(Booking b) {
        boolean playing = b.status.equals("playing");
        String bg  = playing ? "#dcfce7" : "#fef9c3";
        String bdr = playing ? "#86efac" : "#fcd34d";
        String badgeBg  = playing ? "#bbf7d0" : "#fde68a";
        String badgeFg  = playing ? "#16a34a" : "#b45309";
        String statusTxt = playing ? "Đang chơi" : "Đã đặt";
        String baseStyle = String.format(
                "-fx-background-color: %s; -fx-border-color: %s;"
                + " -fx-border-width: 1 1 2 1; -fx-cursor: hand; -fx-padding: 6;", bg, bdr);

        VBox box = new VBox(3);
        box.setAlignment(Pos.TOP_LEFT);
        box.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        box.setStyle(baseStyle);
        box.setPadding(new Insets(6));

        HBox top = new HBox(4);
        top.setAlignment(Pos.CENTER_LEFT);
        Label name = new Label(b.customer);
        name.setStyle("-fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: #111827;");
        name.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(name, Priority.ALWAYS);

        Label badge = new Label(statusTxt);
        badge.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: %s;"
                + " -fx-font-size: 9; -fx-background-radius: 4; -fx-padding: 2 5;",
                badgeBg, badgeFg));
        top.getChildren().addAll(name, badge);

        Label phone = new Label(b.phone);
        phone.setStyle("-fx-font-size: 10; -fx-text-fill: #6b7280;");
        Label dur = new Label(b.duration + "h");
        dur.setStyle("-fx-font-size: 10; -fx-text-fill: #9ca3af;");

        box.getChildren().addAll(top, phone, dur);

        box.setOnMouseClicked(e -> showDetail(b));
        box.setOnMouseEntered(e -> box.setStyle(baseStyle + " -fx-opacity: 0.82;"));
        box.setOnMouseExited(e  -> box.setStyle(baseStyle));
        return box;
    }

    // ================================================================= Detail Panel
    private void showDetail(Booking b) {
        selectedBooking = b;

        String endTime = calcEndTime(b.timeSlot, b.duration);
        boolean playing = b.status.equals("playing");

        lblCustomer.setText(b.customer);
        lblPhone.setText(b.phone);
        lblCourt.setText(COURTS[b.courtId]);
        lblTime.setText(b.timeSlot + " - " + endTime);
        lblBookingDate.setText(currentDate.format(DATE_FMT));
        lblTotal.setText(String.format("%,dđ", b.duration * PRICE_PER_HOUR).replace(',', '.'));

        if (playing) {
            lblStatus.setText("🟢 Đang chơi");
            lblStatus.setStyle("-fx-font-size: 11; -fx-font-weight: bold;"
                    + " -fx-background-color: #dcfce7; -fx-text-fill: #16a34a;"
                    + " -fx-background-radius: 5; -fx-padding: 3 10;");
            btnConfirm.setText("↩ Chuyển về đã đặt");
            btnConfirm.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 9 0; -fx-cursor: hand; -fx-font-size: 13;");
        } else {
            lblStatus.setText("🟡 Đã đặt");
            lblStatus.setStyle("-fx-font-size: 11; -fx-font-weight: bold;"
                    + " -fx-background-color: #fef9c3; -fx-text-fill: #b45309;"
                    + " -fx-background-radius: 5; -fx-padding: 3 10;");
            btnConfirm.setText("▶ Bắt đầu chơi");
            btnConfirm.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 9 0; -fx-cursor: hand; -fx-font-size: 13;");
        }

        refreshWaitingList();

        detailPanel.setVisible(true);
        detailPanel.setManaged(true);
    }

    private void refreshWaitingList() {
        while (waitingListBox.getChildren().size() > 1) {
            waitingListBox.getChildren().remove(1);
        }

        int shown = 0;
        for (Booking b : bookingMap.values()) {
            if (selectedBooking != null && b.id == selectedBooking.id) continue;
            if (shown >= 4) break;
            waitingListBox.getChildren().add(makeWaitingItem(b));
            shown++;
        }
        if (shown == 0) {
            Label empty = new Label("Không có ai đang chờ");
            empty.setStyle("-fx-font-size: 12; -fx-text-fill: #9ca3af; -fx-padding: 4 0;");
            waitingListBox.getChildren().add(empty);
        }
    }

    private VBox makeWaitingItem(Booking b) {
        VBox item = new VBox(2);
        item.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8;"
                + " -fx-padding: 10 12; -fx-border-color: #f3f4f6;"
                + " -fx-border-width: 1; -fx-border-radius: 8;");

        Label name = new Label(b.customer);
        name.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #111827;");
        Label phone = new Label(b.phone);
        phone.setStyle("-fx-font-size: 11; -fx-text-fill: #6b7280;");
        Label slot = new Label(COURTS[b.courtId] + " - " + b.timeSlot);
        slot.setStyle("-fx-font-size: 11; -fx-text-fill: #6b7280;");
        item.getChildren().addAll(name, phone, slot);
        return item;
    }

    private void hideDetail() {
        selectedBooking = null;
        detailPanel.setVisible(false);
        detailPanel.setManaged(false);
    }

    // ================================================================= Button handlers
    @FXML private void onCloseDetail() { hideDetail(); }

    @FXML
    private void onConfirmBooking() {
        if (selectedBooking == null) return;
        String newStatus = selectedBooking.status.equals("playing") ? "booked" : "playing";
        Booking updated = new Booking(selectedBooking.id, selectedBooking.courtId,
                selectedBooking.timeSlot, selectedBooking.customer, selectedBooking.phone,
                newStatus, selectedBooking.duration);
        bookingMap.put(key(selectedBooking.courtId, selectedBooking.timeSlot), updated);
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
                bookingMap.remove(key(selectedBooking.courtId, selectedBooking.timeSlot));
                buildGrid();
                hideDetail();
            }
        });
    }

    // ================================================================= Add dialog
    @FXML private void onAddBooking() { showAddDialog(-1, ""); }

    private void showAddDialog(int preCourtIdx, String preTime) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Thêm Đặt Sân Mới");

        VBox root = new VBox(14);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: white;");
        root.setMinWidth(400);

        Label title = new Label("📋 Thêm Đặt Sân Mới");
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #111827;");

        TextField tfName  = styledField("Nhập tên khách hàng");
        TextField tfPhone = styledField("Nhập số điện thoại");

        ComboBox<String> cbCourt = new ComboBox<>();
        cbCourt.getItems().addAll(COURTS);
        cbCourt.setPromptText("Chọn sân...");
        cbCourt.setMaxWidth(Double.MAX_VALUE);
        styleCombo(cbCourt);
        if (preCourtIdx >= 0) cbCourt.getSelectionModel().select(preCourtIdx);

        ComboBox<String> cbTime = new ComboBox<>();
        cbTime.getItems().addAll(TIME_SLOTS);
        cbTime.setPromptText("Chọn giờ...");
        cbTime.setMaxWidth(Double.MAX_VALUE);
        styleCombo(cbTime);
        if (!preTime.isEmpty()) cbTime.getSelectionModel().select(preTime);

        ComboBox<String> cbDur = new ComboBox<>();
        cbDur.getItems().addAll("1 giờ", "2 giờ", "3 giờ");
        cbDur.getSelectionModel().selectFirst();
        cbDur.setMaxWidth(Double.MAX_VALUE);
        styleCombo(cbDur);

        HBox btns = new HBox(10);
        Button btnCancel  = new Button("Hủy");
        Button btnConfirmDialog = new Button("✔ Xác nhận");
        for (Button b : new Button[]{btnCancel, btnConfirmDialog}) {
            b.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(b, Priority.ALWAYS);
        }
        btnCancel.setStyle("-fx-background-color: #6b7280; -fx-text-fill: white;"
                + " -fx-font-weight: bold; -fx-background-radius: 8;"
                + " -fx-padding: 10 0; -fx-cursor: hand;");
        btnConfirmDialog.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white;"
                + " -fx-font-weight: bold; -fx-background-radius: 8;"
                + " -fx-padding: 10 0; -fx-cursor: hand;");
        btns.getChildren().addAll(btnCancel, btnConfirmDialog);

        root.getChildren().addAll(
                title,
                labeled("Tên khách hàng *", tfName),
                labeled("Số điện thoại *", tfPhone),
                labeled("Chọn sân *", cbCourt),
                labeled("Giờ bắt đầu *", cbTime),
                labeled("Thời lượng *", cbDur),
                btns);

        btnCancel.setOnAction(e -> dialog.close());
        btnConfirmDialog.setOnAction(e -> {
            String name  = tfName.getText().trim();
            String phone = tfPhone.getText().trim();
            int courtIdx = cbCourt.getSelectionModel().getSelectedIndex();
            String time  = cbTime.getSelectionModel().getSelectedItem();
            int dur      = cbDur.getSelectionModel().getSelectedIndex() + 1;

            if (name.isEmpty() || phone.isEmpty() || courtIdx < 0 || time == null) {
                showAlert("Vui lòng điền đầy đủ thông tin!"); return;
            }
            if (bookingMap.containsKey(key(courtIdx, time))
                    && (selectedBooking == null
                        || !key(courtIdx, time).equals(key(selectedBooking.courtId, selectedBooking.timeSlot)))) {
                showAlert("Sân này đã có người đặt vào khung giờ này!"); return;
            }
            Booking b = new Booking(nextId++, courtIdx, time, name, phone, "booked", dur);
            bookingMap.put(key(courtIdx, time), b);
            buildGrid();
            showDetail(b);
            dialog.close();
        });

        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }

    // ================================================================= Nav
    @FXML private void onPrevDay() { currentDate = currentDate.minusDays(1); updateDateLabel(); buildGrid(); }
    @FXML private void onNextDay() { currentDate = currentDate.plusDays(1);  updateDateLabel(); buildGrid(); }
    
    private void updateDateLabel() { 
        lblDate.setText(currentDate.format(DATE_FMT)); 
    }

    // ================================================================= Helpers
    private static String key(int court, String time) { return court + "|" + time; }

    private static String calcEndTime(String start, int hours) {
        String[] parts = start.split(":");
        int h = Integer.parseInt(parts[0]) + hours;
        return String.format("%02d:%s", h, parts[1]);
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #f9fafb; -fx-border-color: #e5e7eb;"
                + " -fx-border-radius: 8; -fx-background-radius: 8;"
                + " -fx-padding: 9 12; -fx-font-size: 13;");
        return tf;
    }

    private void styleCombo(ComboBox<?> cb) {
        cb.setStyle("-fx-background-color: #f0fdf4; -fx-border-color: #86efac;"
                + " -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 13;");
    }

    private VBox labeled(String labelText, javafx.scene.Node field) {
        VBox vb = new VBox(5);
        Label l = new Label(labelText);
        l.setStyle("-fx-font-size: 12; -fx-text-fill: #6b7280;");
        vb.getChildren().addAll(l, field);
        return vb;
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        a.setHeaderText(null); a.showAndWait();
    }

    // ================================================================= Model
    private static class Booking {
        int id, courtId, duration;
        String timeSlot, customer, phone, status;
        Booking(int id, int courtId, String timeSlot,
                String customer, String phone, String status, int duration) {
            this.id = id; this.courtId = courtId; this.timeSlot = timeSlot;
            this.customer = customer; this.phone = phone;
            this.status = status; this.duration = duration;
        }
    }
}