package Controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import java.util.*;
import javafx.fxml.Initializable;

public class CourtScheduleController implements Initializable {

    // Main UI
    @FXML
    private StackPane rootPane;
    @FXML
    private Button btnPrevDate;
    @FXML
    private Label lblDate;
    @FXML
    private Button btnNextDate;
    @FXML
    private Button btnAddBooking;
    @FXML
    private Button btnExit;
    @FXML
    private GridPane scheduleGrid;

    // Add Booking Modal
    @FXML
    private VBox addBookingModal;
    @FXML
    private TextField txtCustomerName;
    @FXML
    private TextField txtPhoneNumber;
    @FXML
    private ComboBox<String> cbCourt;
    @FXML
    private ComboBox<String> cbTimeSlot;
    @FXML
    private ComboBox<Integer> cbDuration;
    @FXML
    private Label lblSummaryCourt;
    @FXML
    private Label lblSummaryTime;
    @FXML
    private Label lblSummaryDuration;
    @FXML
    private Label lblSummaryCustomer;
    @FXML
    private Button btnCancelAdd;
    @FXML
    private Button btnConfirmAdd;

    // Booking Detail Modal
    @FXML
    private VBox bookingDetailModal;
    @FXML
    private Button btnCloseDetailIcon;
    @FXML
    private Label lblDetailCustomer;
    @FXML
    private Label lblDetailPhone;
    @FXML
    private Label lblDetailCourt;
    @FXML
    private Label lblDetailTime;
    @FXML
    private Label lblDetailDuration;
    @FXML
    private Label lblDetailStatus;
    @FXML
    private Button btnStartPlaying;
    @FXML
    private Button btnRevertToBooked;
    @FXML
    private Button btnCloseDetail;
    @FXML
    private Button btnCancelBooking;

    // ========== MODELS ==========
    private static class Booking {
        int id;
        int courtId;
        String timeSlot;
        String customer;
        String phone;
        String status; // "booked", "playing"
        int duration;
        LocalDate date;

        public Booking(int id, int courtId, String timeSlot, String customer, String phone, String status, int duration,
                LocalDate date) {
            this.id = id;
            this.courtId = courtId;
            this.timeSlot = timeSlot;
            this.customer = customer;
            this.phone = phone;
            this.status = status;
            this.duration = duration;
            this.date = date;
        }

        public Booking(int id, int courtId, String timeSlot, String customer, String phone, String status,
                int duration) {
            this(id, courtId, timeSlot, customer, phone, status, duration, LocalDate.now());
        }
    }

    // ========== DATA ==========
    private List<Booking> bookings = new ArrayList<>();
    private String[] courts = {}; // Sẽ được load từ DB trong initialize
    private List<Model.SAN> listSan = new ArrayList<>();
    private String[] timeSlots = {
            "06:00", "07:30", "09:00", "10:30", "13:00", 
            "14:30", "16:00", "17:30", "19:00", "20:30"
    };

    private Booking selectedBooking = null;
    private LocalDate currentDate;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentDate = LocalDate.now();
        updateDateLabel();
        // Load courts from DB to unify names
        listSan = Model.SAN.getDanhSachSanTuDB();
        courts = new String[listSan.size()];
        for (int i = 0; i < listSan.size(); i++) {
            courts[i] = listSan.get(i).TenSan;
        }

        initMockData();
        setupModals();
        setupEventHandlers();
        renderScheduleGrid();
    }

    // ========== DATE NAVIGATION ==========
    private void updateDateLabel() {
        lblDate.setText(currentDate.format(dateFormatter));
    }

    private void previousDate() {
        currentDate = currentDate.minusDays(1);
        updateDateLabel();
        renderScheduleGrid();
    }

    private void nextDate() {
        currentDate = currentDate.plusDays(1);
        updateDateLabel();
        renderScheduleGrid();
    }

    // ========== REAL DATA FROM DB ==========
    private void initMockData() {
        bookings.clear();
        try {
            DAO.DatSanDAO datSanDAO = new DAO.DatSanDAO();
            for (Model.DATSAN ds : datSanDAO.getallDatsans()) {
                if (ds.getNgayDat() == null || ds.getMaSan() == null)
                    continue;

                // Ánh xạ Sân (từ Tên Sân trong DB sang index 0-5)
                int courtId = -1;
                for (int i = 0; i < courts.length; i++) {
                    if (ds.getTenSan() != null && ds.getTenSan().equals(courts[i])) {
                        courtId = i;
                        break;
                    }
                }
                if (courtId == -1) {
                    // Nếu tên không khớp mảng courts, thử lấy theo MaSan (ví dụ S01 -> Sân 1 ->
                    // index 0)
                    if (ds.getMaSan().startsWith("S") && ds.getMaSan().length() > 1) {
                        try {
                            courtId = Integer.parseInt(ds.getMaSan().substring(1)) - 1;
                        } catch (Exception ignored) {
                        }
                    }
                }
                if (courtId < 0 || courtId >= courts.length)
                    continue;

                // Ánh xạ Giờ Bắt Đầu (từ "06:00 - 07:30" -> "06:00")
                String timeSlot = "06:00";
                if (ds.getKhungGio() != null && ds.getKhungGio().length() >= 5) {
                    timeSlot = ds.getKhungGio().substring(0, 5);
                }

                // Ánh xạ Trạng Thái
                String status = "booked"; // Mặc định là ChoDuyet / DaDat
                if ("Đang sử dụng".equalsIgnoreCase(ds.getTrangThai())
                        || "Dang Choi".equalsIgnoreCase(ds.getTrangThai())) {
                    status = "playing";
                }

                int duration = 1; // Mặc định 1h (có thể parse từ khoảng thời gian nếu cần)
                try {
                    if (ds.getKhungGio() != null && ds.getKhungGio().contains("-")) {
                        String[] parts = ds.getKhungGio().split("-");
                        int start = Integer.parseInt(parts[0].trim().substring(0, 2));
                        int end = Integer.parseInt(parts[1].trim().substring(0, 2));
                        duration = Math.max(1, end - start);
                    }
                } catch (Exception ignored) {
                }

                bookings.add(new Booking(
                        Math.abs(ds.getMaDS().hashCode()),
                        courtId,
                        timeSlot,
                        ds.getTenKH() != null ? ds.getTenKH() : "Khách hàng",
                        "SĐT ẩn",
                        status,
                        duration,
                        ds.getNgayDat()));
            }
        } catch (Exception e) {
            System.err.println("Lỗi load dữ liệu lịch sân: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========== MODAL SETUP ==========
    private void setupModals() {
        cbCourt.getItems().addAll(courts);
        cbTimeSlot.getItems().addAll(timeSlots);
        cbDuration.getItems().addAll(1, 2, 3);
        cbDuration.setValue(1);

        // Cập nhật nhãn Summary khi thay đổi dữ liệu
        txtCustomerName.textProperty().addListener(
                (obs, old, neu) -> lblSummaryCustomer.setText("Khách hàng: " + (neu.isEmpty() ? "(Chưa nhập)" : neu)));

        cbCourt.valueProperty()
                .addListener((obs, old, neu) -> lblSummaryCourt.setText("Sân: " + (neu == null ? "(Chưa chọn)" : neu)));

        cbTimeSlot.valueProperty()
                .addListener((obs, old, neu) -> lblSummaryTime.setText("Giờ: " + (neu == null ? "(Chưa chọn)" : neu)));

        cbDuration.valueProperty()
                .addListener((obs, old, neu) -> lblSummaryDuration.setText("Thời lượng: " + neu + " giờ"));
    }

    // ========== EVENT HANDLERS ==========
    private void setupEventHandlers() {
        btnPrevDate.setOnAction(e -> previousDate());
        btnNextDate.setOnAction(e -> nextDate());
        btnAddBooking.setOnAction(e -> openAddBookingModal(null, null));
        btnCancelAdd.setOnAction(e -> addBookingModal.setVisible(false));
        btnConfirmAdd.setOnAction(e -> handleAddBooking());

        btnCloseDetailIcon.setOnAction(e -> bookingDetailModal.setVisible(false));
        btnCloseDetail.setOnAction(e -> bookingDetailModal.setVisible(false));

        btnStartPlaying.setOnAction(e -> handleUpdateStatus("playing"));
        btnRevertToBooked.setOnAction(e -> handleUpdateStatus("booked"));
        btnCancelBooking.setOnAction(e -> handleCancelBooking());

        btnExit.setOnAction(e -> System.exit(0));
    }

    // ========== RENDER GRID ==========
    private void renderScheduleGrid() {
        scheduleGrid.getChildren().clear();
        scheduleGrid.getColumnConstraints().clear();
        scheduleGrid.getRowConstraints().clear();

        // Header - Thời gian
        Label lblHeaderTime = createHeaderCell("Giờ");
        scheduleGrid.add(lblHeaderTime, 0, 0);

        ColumnConstraints timeCol = new ColumnConstraints(80);
        scheduleGrid.getColumnConstraints().add(timeCol);

        // Headers - Sân
        for (int i = 0; i < courts.length; i++) {
            Label lblCourt = createHeaderCell(courts[i]);
            scheduleGrid.add(lblCourt, i + 1, 0);

            ColumnConstraints courtCol = new ColumnConstraints();
            courtCol.setMinWidth(200);
            courtCol.setPrefWidth(250);
            scheduleGrid.getColumnConstraints().add(courtCol);
        }

        // Lưới Sân
        for (int row = 0; row < timeSlots.length; row++) {
            String time = timeSlots[row];

            // Cột thời gian
            Label lblTime = new Label(time);
            lblTime.setStyle("-fx-padding: 10; -fx-background-color: #f9fafb; -fx-font-weight: bold;");
            lblTime.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            lblTime.setAlignment(Pos.CENTER);
            scheduleGrid.add(lblTime, 0, row + 1);

            // Các ô sân
            for (int col = 0; col < courts.length; col++) {
                final int courtIdx = col;
                final String timeSlot = time;
                Booking b = getBookingForCurrentDate(courtIdx, time);

                VBox cell = createScheduleCell(b, courtIdx, timeSlot);
                scheduleGrid.add(cell, col + 1, row + 1);
            }
        }
    }

    private Label createHeaderCell(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold; -fx-padding: 10; -fx-background-color: #f9fafb;");
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        return label;
    }

    private VBox createScheduleCell(Booking b, int courtIdx, String timeSlot) {
        VBox cell = new VBox();
        cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cell.setPadding(new Insets(8));
        cell.setStyle("-fx-cursor: hand; -fx-background-color: white; -fx-border-color: #e5e7eb;");

        if (b != null) {
            // Cell có booking
            if ("booked".equals(b.status)) {
                cell.setStyle("-fx-background-color: #fef9c3; -fx-cursor: hand; -fx-border-color: #fde047;");
            } else if ("playing".equals(b.status)) {
                cell.setStyle("-fx-background-color: #dcfce7; -fx-cursor: hand; -fx-border-color: #86efac;");
            }

            // Header: Tên + Trạng thái
            HBox header = new HBox();
            header.setAlignment(Pos.CENTER_LEFT);
            Label name = new Label(b.customer);
            name.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label status = new Label("booked".equals(b.status) ? "Đã đặt" : "Đang chơi");
            status.setStyle("booked".equals(b.status)
                    ? "-fx-background-color: #fde047; -fx-text-fill: #854d0e; -fx-padding: 2 6; -fx-background-radius: 4; -fx-font-size: 10px;"
                    : "-fx-background-color: #86efac; -fx-text-fill: #166534; -fx-padding: 2 6; -fx-background-radius: 4; -fx-font-size: 10px;");

            header.getChildren().addAll(name, spacer, status);

            // Thông tin khác
            Label phone = new Label(b.phone);
            phone.setStyle("-fx-font-size: 11px; -fx-text-fill: #4b5563;");
            Label dur = new Label(b.duration + "h");
            dur.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b7280;");

            cell.getChildren().addAll(header, phone, dur);
            cell.setSpacing(4);
        } else {
            // Cell trống
            cell.setAlignment(Pos.CENTER);
            Label lblAdd = new Label("+ Đặt sân");
            lblAdd.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 12px;");
            cell.getChildren().add(lblAdd);

            cell.setOnMouseEntered(
                    e -> cell.setStyle("-fx-background-color: #f0fdf4; -fx-cursor: hand; -fx-border-color: #bbf7d0;"));
            cell.setOnMouseExited(
                    e -> cell.setStyle("-fx-background-color: white; -fx-cursor: hand; -fx-border-color: #e5e7eb;"));
        }

        cell.setOnMouseClicked(e -> handleCellClick(courtIdx, timeSlot));
        return cell;
    }

    // ========== BOOKING OPERATIONS ==========
    private Booking getBookingForCurrentDate(int courtId, String timeSlot) {
        return bookings.stream()
                .filter(b -> b.courtId == courtId && b.timeSlot.equals(timeSlot) && b.date.equals(currentDate))
                .findFirst()
                .orElse(null);
    }

    private void handleCellClick(int courtId, String timeSlot) {
        Booking b = getBookingForCurrentDate(courtId, timeSlot);
        if (b != null) {
            openBookingDetailModal(b);
        } else {
            openAddBookingModal(courts[courtId], timeSlot);
        }
    }

    private void openAddBookingModal(String courtName, String timeSlot) {
        txtCustomerName.clear();
        txtPhoneNumber.clear();
        cbCourt.setValue(courtName);
        cbTimeSlot.setValue(timeSlot);
        cbDuration.setValue(1);
        addBookingModal.setVisible(true);
    }

    private String getMaKhungGio(String gio) {
        switch (gio) {
            case "06:00": return "KG001";
            case "07:30": return "KG002";
            case "09:00": return "KG003";
            case "10:30": return "KG004";
            case "13:00": return "KG005";
            case "14:30": return "KG006";
            case "16:00": return "KG007";
            case "17:30": return "KG008";
            case "19:00": return "KG009";
            case "20:30": return "KG010";
            default: return "KG001";
        }
    }

    private void handleAddBooking() {
        String name = txtCustomerName.getText().trim();
        String phone = txtPhoneNumber.getText().trim();
        String courtName = cbCourt.getValue();
        String timeSlot = cbTimeSlot.getValue();
        Integer duration = cbDuration.getValue();

        // Kiểm tra dữ liệu
        if (name.isEmpty() || phone.isEmpty() || courtName == null || timeSlot == null) {
            showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin!");
            return;
        }

        // Kiểm tra số điện thoại
        if (!phone.matches("\\d{10}") && !phone.matches("(84|0[3|5|7|8|9])+([0-9]{8})")) {
            showAlert("Lỗi", "Số điện thoại không hợp lệ!");
            return;
        }

        // Tìm courtId và SAN object
        int courtId = -1;
        Model.SAN san = null;
        for (int i = 0; i < courts.length; i++) {
            if (courts[i].equals(courtName)) {
                courtId = i;
                if (i < listSan.size()) {
                    san = listSan.get(i);
                }
                break;
            }
        }

        if (san == null) {
            showAlert("Lỗi", "Không tìm thấy thông tin sân!");
            return;
        }

        // Đảm bảo MaKH hợp lệ
        DAO.DatSanDAO datSanDAO = new DAO.DatSanDAO();
        String maKH = datSanDAO.upsertKhachVangLai(name, phone);
        if (maKH == null) {
            showAlert("Lỗi", "Không thể xác định khách hàng. Kiểm tra kết nối DB!");
            return;
        }

        long tongTien = san.GiaThueTheoGio * duration;
        String maDatSan = "DS" + (System.currentTimeMillis() % 100000);
        String maKhungGio = getMaKhungGio(timeSlot);

        Model.DATSAN ds = new Model.DATSAN();
        ds.setMaDS(maDatSan);
        ds.setMaKH(maKH);
        ds.setTenKH(name);
        ds.setSdtKH(phone);
        ds.setMaSan(san.MaSan);
        ds.setMaKG(maKhungGio);
        ds.setNgayDat(currentDate);
        ds.setTrangThai("ChoDuyet");
        ds.setTongTienTamTinh(tongTien);

        try {
            boolean isDatSanSaved = datSanDAO.insertDatSanDirect(ds);
            if (isDatSanSaved) {
                DAO.HoaDonDAO hoaDonDAO = new DAO.HoaDonDAO();
                boolean isHoaDonCreated = hoaDonDAO.addHoaDonDatSan(maDatSan, tongTien);
                if (!isHoaDonCreated) {
                    throw new Exception("Lưu đặt sân thành công nhưng không thể tạo hóa đơn!");
                }

                Utils.NotificationHelper.datSanThanhCong(
                        maDatSan,
                        name,
                        san.TenSan != null ? san.TenSan : san.MaSan,
                        currentDate.toString());

                addBookingModal.setVisible(false);
                initMockData(); // Tải lại lịch từ DB
                showAlert("Thành công", "Đã đặt sân và tạo hóa đơn thành công!");
            } else {
                throw new Exception("Không thể lưu thông tin đặt sân!");
            }
        } catch (Exception e) {
            String errorMsg = e.getMessage() != null ? e.getMessage() : "";
            if (errorMsg.contains("ORA-20001")) {
                showAlert("Lỗi", "Sân này đã được đặt trong khung giờ này rồi.");
            } else {
                showAlert("Lỗi", "Có lỗi xảy ra: " + errorMsg);
            }
        }
    }

    private void openBookingDetailModal(Booking b) {
        selectedBooking = b;
        lblDetailCustomer.setText(b.customer);
        lblDetailPhone.setText(b.phone);
        lblDetailCourt.setText(courts[b.courtId]);
        lblDetailTime.setText(b.timeSlot);
        lblDetailDuration.setText(b.duration + " giờ");

        if ("booked".equals(b.status)) {
            lblDetailStatus.setText("Đã đặt");
            lblDetailStatus.setStyle(
                    "-fx-background-color: #fef9c3; -fx-text-fill: #a16207; -fx-padding: 4 8; -fx-background-radius: 4; -fx-font-size: 12px;");
            btnStartPlaying.setVisible(true);
            btnStartPlaying.setManaged(true);
            btnRevertToBooked.setVisible(false);
            btnRevertToBooked.setManaged(false);
        } else {
            lblDetailStatus.setText("Đang chơi");
            lblDetailStatus.setStyle(
                    "-fx-background-color: #dcfce7; -fx-text-fill: #166534; -fx-padding: 4 8; -fx-background-radius: 4; -fx-font-size: 12px;");
            btnStartPlaying.setVisible(false);
            btnStartPlaying.setManaged(false);
            btnRevertToBooked.setVisible(true);
            btnRevertToBooked.setManaged(true);
        }

        bookingDetailModal.setVisible(true);
    }

    private void handleUpdateStatus(String newStatus) {
        if (selectedBooking != null) {
            selectedBooking.status = newStatus;
            bookingDetailModal.setVisible(false);
            renderScheduleGrid();
            showAlert("Thành công", "Cập nhật trạng thái thành công!");
        }
    }

    private void handleCancelBooking() {
        if (selectedBooking != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Xác nhận");
            confirmDialog.setHeaderText("Hủy đặt sân");
            confirmDialog.setContentText("Bạn có chắc chắn muốn hủy đặt sân này?");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                bookings.remove(selectedBooking);
                selectedBooking = null;
                bookingDetailModal.setVisible(false);
                renderScheduleGrid();
                showAlert("Thành công", "Hủy đặt sân thành công!");
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}