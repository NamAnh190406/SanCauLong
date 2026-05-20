package Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CustomerDetailController {

    // ================== FXML INJECTIONS ==================
    @FXML private Label lblCustomerName;
    @FXML private Label lblPhoneNumber;
    @FXML private Label lblCourt;
    @FXML private Label lblTime;
    @FXML private Label lblDate;
    @FXML private Label lblStatus;
    @FXML private Label lblTotalAmount;

    @FXML private Button btnConfirm;
    @FXML private Button btnEdit;
    @FXML private Button btnCancelBooking;
    @FXML private Button btnClose;

    @FXML private VBox waitingListContainer;

    // ================== CALLBACKS (Giao tiếp với form chính) ==================
    private Runnable onConfirmAction;
    private Runnable onEditAction;
    private Runnable onCancelAction;
    private Runnable onCloseAction;

    @FXML
    public void initialize() {
        // Gắn sự kiện cho các nút. Khi bấm sẽ gọi ngược về form chính.
        btnConfirm.setOnAction(event -> {
            if (onConfirmAction != null) onConfirmAction.run();
        });

        btnEdit.setOnAction(event -> {
            if (onEditAction != null) onEditAction.run();
        });

        btnCancelBooking.setOnAction(event -> {
            if (onCancelAction != null) onCancelAction.run();
        });

        btnClose.setOnAction(event -> {
            if (onCloseAction != null) onCloseAction.run();
        });
    }

    // ================== HÀM BƠM DỮ LIỆU & GIAO DIỆN ==================
    /**
     * Hàm dùng để truyền dữ liệu từ màn hình lịch sân sang panel này
     */
    public void setCustomerData(String name, String phone, String court, String time, String date, String status, String totalAmount) {
        lblCustomerName.setText(name);
        lblPhoneNumber.setText(phone);
        lblCourt.setText(court);
        lblTime.setText(time);
        lblDate.setText(date);
        lblTotalAmount.setText(totalAmount);

        // Đổi màu Label trạng thái và Đổi Text nút Xác nhận theo ngữ cảnh
        if (status.equalsIgnoreCase("Đã đặt") || status.equalsIgnoreCase("booked")) {
            lblStatus.setText("Đã đặt");
            lblStatus.setStyle("-fx-background-color: #fef9c3; -fx-text-fill: #a16207; -fx-font-size: 11px; -fx-padding: 4 8; -fx-background-radius: 4;");
            
            btnConfirm.setText("Bắt đầu chơi");
            btnConfirm.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand;");
            
            btnCancelBooking.setVisible(true);
            btnCancelBooking.setManaged(true);
        } else if (status.equalsIgnoreCase("Đang chơi") || status.equalsIgnoreCase("playing")) {
            lblStatus.setText("Đang chơi");
            lblStatus.setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #16a34a; -fx-font-size: 11px; -fx-padding: 4 8; -fx-background-radius: 4;");
            
            btnConfirm.setText("Chuyển về đã đặt");
            btnConfirm.setStyle("-fx-background-color: #eab308; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand;");
            
            // Đang chơi thì ẩn nút hủy
            btnCancelBooking.setVisible(false);
            btnCancelBooking.setManaged(false);
        } else {
            lblStatus.setText(status);
        }
    }

    // ================== SETTERS CHO CALLBACKS ==================
    public void setOnConfirmAction(Runnable onConfirmAction) {
        this.onConfirmAction = onConfirmAction;
    }

    public void setOnEditAction(Runnable onEditAction) {
        this.onEditAction = onEditAction;
    }

    public void setOnCancelAction(Runnable onCancelAction) {
        this.onCancelAction = onCancelAction;
    }

    public void setOnCloseAction(Runnable onCloseAction) {
        this.onCloseAction = onCloseAction;
    }
}