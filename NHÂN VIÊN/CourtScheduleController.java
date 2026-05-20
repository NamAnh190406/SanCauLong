package Controller;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class CourtScheduleController implements Initializable {

    // Main
    @FXML private StackPane rootPane;
    @FXML private Button btnPrevDate;
    @FXML private Label lblDate;
    @FXML private Button btnNextDate;
    @FXML private Button btnAddBooking;
    @FXML private Button btnExit;
    @FXML private GridPane scheduleGrid;

    // Add Booking Modal
    @FXML private VBox addBookingModal;
    @FXML private TextField txtCustomerName;
    @FXML private TextField txtPhoneNumber;
    @FXML private ComboBox<String> cbCourt;
    @FXML private ComboBox<String> cbTimeSlot;
    @FXML private ComboBox<Integer> cbDuration;
    @FXML private Label lblSummaryCourt;
    @FXML private Label lblSummaryTime;
    @FXML private Label lblSummaryDuration;
    @FXML private Label lblSummaryCustomer;
    @FXML private Button btnCancelAdd;
    @FXML private Button btnConfirmAdd;

    // Booking Detail Modal
    @FXML private VBox bookingDetailModal;
    @FXML private Button btnCloseDetailIcon;
    @FXML private Label lblDetailCustomer;
    @FXML private Label lblDetailPhone;
    @FXML private Label lblDetailCourt;
    @FXML private Label lblDetailTime;
    @FXML private Label lblDetailDuration;
    @FXML private Label lblDetailStatus;
    @FXML private Button btnStartPlaying;
    @FXML private Button btnRevertToBooked;
    @FXML private Button btnCloseDetail;
    @FXML private Button btnCancelBooking;

    // Models & Data
    private static class Booking {
        int id;
        int courtId;
        String timeSlot;
        String customer;
        String phone;
        String status; // "booked", "playing"
        int duration;

        public Booking(int id, int courtId, String timeSlot, String customer, String phone, String status, int duration) {
            this.id = id;
            this.courtId = courtId;
            this.timeSlot = timeSlot;
            this.customer = customer;
            this.phone = phone;
            this.status = status;
            this.duration = duration;
        }
    }

    private List<Booking> bookings = new ArrayList<>();
    private String[] courts = {"Sân 1", "Sân 2", "Sân 3", "Sân 4", "Sân 5", "Sân 6"};
    private String[] timeSlots = {
            "06:00", "07:00", "08:00", "09:00", "10:00", "11:00",
            "12:00", "13:00", "14:00", "15:00", "16:00", "17:00",
            "18:00", "19:00", "20:00", "21:00", "22:00"
    };

    private Booking selectedBooking = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initMockData();
        setupModals();
        renderScheduleGrid();
        setupEventHandlers();
    }

    private void initMockData() {
        bookings.add(new Booking(1, 0, "06:00", "Nguyễn Văn A", "0901234567", "booked", 1));
        bookings.add(new Booking(2, 0, "08:00", "Trần Thị B", "0907654321", "playing", 2));
        bookings.add(new Booking(3, 1, "07:00", "Lê Văn C", "0912345678", "booked", 1));
        bookings.add(new Booking(4, 1, "09:00", "Phạm Thị D", "0923456789", "playing", 1));
    }

    private void setupModals() {
        cbCourt.getItems().addAll(courts);
        cbTimeSlot.getItems().addAll(timeSlots);
        cbDuration.getItems().addAll(1, 2, 3);
        cbDuration.setValue(1);

        // Update nhãn Summary khi gõ
        txtCustomerName.textProperty().addListener((obs, old, neu) -> lblSummaryCustomer.setText("Khách hàng: " + (neu.isEmpty() ? "(Chưa nhập)" : neu)));
        cbCourt.valueProperty().addListener((obs, old, neu) -> lblSummaryCourt.setText("Sân: " + (neu == null ? "(Chưa chọn)" : neu)));
        cbTimeSlot.valueProperty().addListener((obs, old, neu) -> lblSummaryTime.setText("Giờ: " + (neu == null ? "(Chưa chọn)" : neu)));
        cbDuration.valueProperty().addListener((obs, old, neu) -> lblSummaryDuration.setText("Thời lượng: " + neu + " giờ"));
    }

    private void setupEventHandlers() {
        btnAddBooking.setOnAction(e -> openAddBookingModal(null, null));
        btnCancelAdd.setOnAction(e -> addBookingModal.setVisible(false));
        btnConfirmAdd.setOnAction(e -> handleAddBooking());

        btnCloseDetailIcon.setOnAction(e -> bookingDetailModal.setVisible(false));
        btnCloseDetail.setOnAction(e -> bookingDetailModal.setVisible(false));
        
        btnStartPlaying.setOnAction(e -> handleUpdateStatus("playing"));
        btnRevertToBooked.setOnAction(e -> handleUpdateStatus("booked"));
        btnCancelBooking.setOnAction(e -> handleCancelBooking());
        
        btnExit.setOnAction(e -> System.out.println("Thoát clicked"));
    }

    private void renderScheduleGrid() {
        scheduleGrid.getChildren().clear();
        scheduleGrid.getColumnConstraints().clear();
        scheduleGrid.getRowConstraints().clear();

        // Header
        Label lblHeaderTime = new Label("Giờ");
        lblHeaderTime.setStyle("-fx-font-weight: bold; -fx-padding: 10; -fx-background-color: #f9fafb;");
        lblHeaderTime.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        lblHeaderTime.setAlignment(Pos.CENTER);
        scheduleGrid.add(lblHeaderTime, 0, 0);

        ColumnConstraints timeCol = new ColumnConstraints(80);
        scheduleGrid.getColumnConstraints().add(timeCol);

        for (int i = 0; i < courts.length; i++) {
            Label lblCourt = new Label(courts[i]);
            lblCourt.setStyle("-fx-font-weight: bold; -fx-padding: 10; -fx-background-color: #f9fafb;");
            lblCourt.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            lblCourt.setAlignment(Pos.CENTER);
            scheduleGrid.add(lblCourt, i + 1, 0);

            ColumnConstraints courtCol = new ColumnConstraints();
            courtCol.setPercentWidth(100.0 / courts.length);
            scheduleGrid.getColumnConstraints().add(courtCol);
        }

        // Lưới Sân
        for (int row = 0; row < timeSlots.length; row++) {
            String time = timeSlots[row];
            Label lblTime = new Label(time);
            lblTime.setStyle("-fx-padding: 10; -fx-background-color: #f9fafb;");
            lblTime.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            lblTime.setAlignment(Pos.CENTER);
            scheduleGrid.add(lblTime, 0, row + 1);

            for (int col = 0; col < courts.length; col++) {
                final int courtIdx = col;
                final String timeSlot = time;
                Booking b = getBooking(courtIdx, time);

                VBox cell = new VBox();
                cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                cell.setPadding(new Insets(8));
                cell.setStyle("-fx-cursor: hand; -fx-background-color: white;");

                if (b != null) {
                    if ("booked".equals(b.status)) {
                        cell.setStyle("-fx-background-color: #fef9c3; -fx-cursor: hand;");
                    } else if ("playing".equals(b.status)) {
                        cell.setStyle("-fx-background-color: #dcfce7; -fx-cursor: hand;");
                    }

                    HBox header = new HBox();
                    header.setAlignment(Pos.CENTER_LEFT);
                    Label name = new Label(b.customer);
                    name.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    
                    Label status = new Label("booked".equals(b.status) ? "Đã đặt" : "Đang chơi");
                    status.setStyle("booked".equals(b.status) ? 
                        "-fx-background-color: #fde047; -fx-text-fill: #854d0e; -fx-padding: 2 6; -fx-background-radius: 4; -fx-font-size: 10px;" :
                        "-fx-background-color: #86efac; -fx-text-fill: #166534; -fx-padding: 2 6; -fx-background-radius: 4; -fx-font-size: 10px;");
                    
                    header.getChildren().addAll(name, spacer, status);

                    Label phone = new Label(b.phone);
                    phone.setStyle("-fx-font-size: 11px; -fx-text-fill: #4b5563;");
                    Label dur = new Label(b.duration + "h");
                    dur.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b7280;");

                    cell.getChildren().addAll(header, phone, dur);
                } else {
                    cell.setAlignment(Pos.CENTER);
                    Label lblAdd = new Label("+ Đặt sân");
                    lblAdd.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 12px;");
                    cell.getChildren().add(lblAdd);

                    cell.setOnMouseEntered(e -> cell.setStyle("-fx-background-color: #f0fdf4; -fx-cursor: hand;"));
                    cell.setOnMouseExited(e -> cell.setStyle("-fx-background-color: white; -fx-cursor: hand;"));
                }

                cell.setOnMouseClicked(e -> handleCellClick(courtIdx, timeSlot));
                scheduleGrid.add(cell, col + 1, row + 1);
            }
        }
    }

    private Booking getBooking(int courtId, String timeSlot) {
        return bookings.stream()
                .filter(b -> b.courtId == courtId && b.timeSlot.equals(timeSlot))
                .findFirst()
                .orElse(null);
    }

    private void handleCellClick(int courtId, String timeSlot) {
        Booking b = getBooking(courtId, timeSlot);
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

    private void handleAddBooking() {
        if (txtCustomerName.getText().isEmpty() || txtPhoneNumber.getText().isEmpty() || cbCourt.getValue() == null || cbTimeSlot.getValue() == null) {
            showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin!");
            return;
        }

        int courtId = -1;
        for (int i = 0; i < courts.length; i++) {
            if (courts[i].equals(cbCourt.getValue())) {
                courtId = i;
                break;
            }
        }

        Booking existing = getBooking(courtId, cbTimeSlot.getValue());
        if (existing != null) {
            showAlert("Lỗi", "Sân này đã có người đặt vào khung giờ này!");
            return;
        }

        int maxId = bookings.stream().mapToInt(b -> b.id).max().orElse(0);
        Booking newB = new Booking(
                maxId + 1,
                courtId,
                cbTimeSlot.getValue(),
                txtCustomerName.getText(),
                txtPhoneNumber.getText(),
                "booked",
                cbDuration.getValue()
        );
        bookings.add(newB);
        addBookingModal.setVisible(false);
        renderScheduleGrid();
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
            lblDetailStatus.setStyle("-fx-background-color: #fef9c3; -fx-text-fill: #a16207; -fx-padding: 4 8; -fx-background-radius: 4; -fx-font-size: 12px;");
            btnStartPlaying.setVisible(true);
            btnStartPlaying.setManaged(true);
            btnRevertToBooked.setVisible(false);
            btnRevertToBooked.setManaged(false);
        } else {
            lblDetailStatus.setText("Đang chơi");
            lblDetailStatus.setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #166534; -fx-padding: 4 8; -fx-background-radius: 4; -fx-font-size: 12px;");
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
        }
    }

    private void handleCancelBooking() {
        if (selectedBooking != null) {
            bookings.remove(selectedBooking);
            selectedBooking = null;
            bookingDetailModal.setVisible(false);
            renderScheduleGrid();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}