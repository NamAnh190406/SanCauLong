package Controller;

import DAO.HoaDonDAO;
import Model.HOADON;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * PaymentPageController_Merged - Best of both worlds
 * 
 * Features from PaymentPageUI:
 * ✅ 4 payment methods (cash, transfer, card, ewallet)
 * ✅ Price breakdown detail
 * ✅ Notes field
 * ✅ Clean architecture
 * 
 * Features from ThanhToanUI:
 * ✅ Real-time cash calculation
 * ✅ QR code for transfer
 * ✅ Quick buttons (Vừa đủ, Làm tròn)
 * ✅ Database design (THANHTOAN table)
 */
public class ThanhToanController implements Initializable {

    // ============================================
    // FXML INJECTIONS
    // ============================================
    @FXML
    private Label lblInvoiceNumber, lblCustomer, lblPhone, lblCourt, lblTime;
    @FXML
    private Label lblTotalAmount, lblPaymentError;
    @FXML
    private VBox vboxPaymentDetails;
    @FXML
    private VBox vboxPaymentMethods;

    // Payment method buttons
    @FXML
    private ToggleButton btnCash, btnTransfer;

    // Cash payment section
    @FXML
    private VBox boxCashPayment;
    @FXML
    private TextField txtTienKhachDua;
    @FXML
    private Label lblTienNhan, lblTienThua;

    // Transfer payment section
    @FXML
    private VBox boxTransferPayment;

    // Notes
    @FXML
    private TextArea taNote;
    @FXML
    private Button btnCancel, btnConfirmPayment;

    // ============================================
    // STATE
    // ============================================
    private HoaDonDAO hoaDonDAO;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    private String maHoaDon;
    private long totalAmount;
    private String selectedPaymentMethod;

    // ============================================
    // INITIALIZATION
    // ============================================
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            hoaDonDAO = new HoaDonDAO();

            setupPaymentMethodToggleGroup();
            setupCashCalculation();
            setupButtonHandlers();

            System.out.println("✓ PaymentPageController_Merged initialized");
        } catch (Exception e) {
            System.err.println("✗ Error in initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============================================
    // SETUP
    // ============================================
    private void setupPaymentMethodToggleGroup() {
        ToggleGroup group = new ToggleGroup();
        btnCash.setToggleGroup(group);
        btnTransfer.setToggleGroup(group);

        group.selectedToggleProperty().addListener((obs, old, neu) -> {
            if (old != null) {
                ((ToggleButton) old).setStyle(
                        "-fx-padding: 12; -fx-font-size: 12; -fx-background-radius: 6; " +
                                "-fx-min-height: 40; -fx-background-color: #f3f4f6; -fx-text-fill: #374151;");
            }
            if (neu != null) {
                ((ToggleButton) neu).setStyle(
                        "-fx-padding: 12; -fx-font-size: 12; -fx-background-radius: 6; " +
                                "-fx-min-height: 40; -fx-background-color: #16a34a; -fx-text-fill: white; -fx-font-weight: bold;");
                updatePaymentMethodUI();
            }
        });

        btnCash.setSelected(true);
    }

    private void setupCashCalculation() {
        txtTienKhachDua.textProperty().addListener((obs, old, neu) -> {
            if (!neu.matches("\\d*")) {
                txtTienKhachDua.setText(neu.replaceAll("[^\\d]", ""));
                return;
            }

            try {
                long tienNhan = neu.isEmpty() ? 0 : Long.parseLong(neu);
                long tienThua = Math.max(0, tienNhan - totalAmount);

                lblTienNhan.setText(formatCurrency(tienNhan));
                lblTienThua.setText(formatCurrency(tienThua));

                if (tienNhan < totalAmount && tienNhan > 0) {
                    lblTienNhan.setStyle("-fx-text-fill: #dc2626;");
                } else {
                    lblTienNhan.setStyle("-fx-text-fill: #16a34a;");
                }
            } catch (NumberFormatException e) {
                lblTienNhan.setText("0 đ");
                lblTienThua.setText("0 đ");
            }
        });
    }

    private void setupButtonHandlers() {
        btnCancel.setOnAction(e -> handleCancel());
        btnConfirmPayment.setOnAction(e -> handleConfirmPayment());
    }

    // ============================================
    // PUBLIC API
    // ============================================
    public void setInvoiceData(String maHD, String customerName, String phone,
            String court, String bookingTime, long total) {
        this.maHoaDon = maHD;
        this.totalAmount = total;

        lblInvoiceNumber.setText(maHD);
        lblCustomer.setText(customerName);
        lblPhone.setText(phone);
        lblCourt.setText(court);
        lblTime.setText(bookingTime);
        lblTotalAmount.setText(formatCurrency(total));

        System.out.println("✓ Invoice data set: " + maHD + " - " + formatCurrency(total));
    }

    public void setPaymentDetails(long courtPrice, long servicePrice, long discount) {
        vboxPaymentDetails.getChildren().clear();

        addDetailRow("Tiền sân", courtPrice);

        if (servicePrice > 0) {
            addDetailRow("Tiền dịch vụ", servicePrice);
        }

        if (discount > 0) {
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);
            Label lblLabel = new Label("Giảm giá/Voucher");
            Label lblValue = createStyledLabel(
                    "-" + formatCurrency(discount),
                    "-fx-text-fill: #dc2626; -fx-font-weight: bold;");
            row.getChildren().addAll(lblLabel);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            row.getChildren().add(spacer);
            row.getChildren().add(lblValue);

            vboxPaymentDetails.getChildren().add(row);
        }
    }

    private void addDetailRow(String label, long amount) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);

        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-text-fill: #6b7280;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblAmount = new Label(formatCurrency(amount));
        lblAmount.setStyle("-fx-font-weight: bold;");

        row.getChildren().addAll(lblLabel, spacer, lblAmount);
        vboxPaymentDetails.getChildren().add(row);
    }

    // ============================================
    // PAYMENT METHOD UI MANAGEMENT
    // ============================================
    private void updatePaymentMethodUI() {
        Toggle selected = ((ToggleGroup) btnCash.getToggleGroup()).getSelectedToggle();

        // Hide all sections first
        boxCashPayment.setVisible(false);
        boxCashPayment.setManaged(false);
        boxTransferPayment.setVisible(false);
        boxTransferPayment.setManaged(false);

        // Show selected section
        if (selected == btnCash) {
            selectedPaymentMethod = "CASH";
            boxCashPayment.setVisible(true);
            boxCashPayment.setManaged(true);
            txtTienKhachDua.requestFocus();
        } else if (selected == btnTransfer) {
            selectedPaymentMethod = "TRANSFER";
            boxTransferPayment.setVisible(true);
            boxTransferPayment.setManaged(true);
        }

        // Clear error
        lblPaymentError.setVisible(false);
        lblPaymentError.setManaged(false);

        System.out.println(">>> Payment method selected: " + selectedPaymentMethod);
    }

    // ============================================
    // CASH-SPECIFIC HANDLERS (From ThanhToanUI)
    // ============================================
    @FXML
    private void btnVuaDuTienAction() {
        txtTienKhachDua.setText(String.valueOf(totalAmount));
    }

    @FXML
    private void btnLamTronAction() {
        long lamTron = (long) (Math.ceil(totalAmount / 100000.0) * 100000);
        txtTienKhachDua.setText(String.valueOf(lamTron));
    }

    // ============================================
    // PAYMENT CONFIRMATION
    // ============================================
    @FXML
    private void handleConfirmPayment() {
        if (selectedPaymentMethod == null || selectedPaymentMethod.isEmpty()) {
            lblPaymentError.setText("⚠️ Vui lòng chọn phương thức thanh toán");
            lblPaymentError.setVisible(true);
            lblPaymentError.setManaged(true);
            return;
        }

        // Validate for cash payment
        if ("CASH".equals(selectedPaymentMethod)) {
            String input = txtTienKhachDua.getText();
            long tienNhan = input.isEmpty() ? 0 : Long.parseLong(input);

            if (tienNhan < totalAmount) {
                showAlert(Alert.AlertType.WARNING, "Số tiền khách đưa không đủ thanh toán!");
                return;
            }
        }

        if (maHoaDon == null || maHoaDon.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Mã hóa đơn không hợp lệ!");
            return;
        }

        try {
            System.out.println(">>> Processing payment:");
            System.out.println("    Invoice: " + maHoaDon);
            System.out.println("    Method: " + selectedPaymentMethod);
            System.out.println("    Amount: " + formatCurrency(totalAmount));
            System.out.println("    Notes: " + taNote.getText());

            // Update invoice as paid
            boolean updateSuccess = hoaDonDAO.capNhatNgayXuatVaThanhToan(maHoaDon);

            if (!updateSuccess) {
                showAlert(Alert.AlertType.ERROR, "Không thể cập nhật trạng thái thanh toán!");
                return;
            }

            // Show success
            String methodName = getPaymentMethodName(selectedPaymentMethod);
            showAlert(Alert.AlertType.INFORMATION,
                    "✓ Thanh toán thành công!\n" +
                            "Hóa đơn " + maHoaDon + " đã thanh toán bằng " + methodName +
                            "\nSố tiền: " + formatCurrency(totalAmount));

            // Ghi thông báo hệ thống
            Utils.NotificationHelper.thanhToanThanhCong(maHoaDon, methodName, totalAmount);
            
            // Thông báo cập nhật thời gian thực
            EventBus.publish(EventBus.EVENT_INVOICE_PAID);

            System.out.println("✓ Payment confirmed for: " + maHoaDon);

            // Close dialog
            closeWindow();

        } catch (Exception e) {
            System.err.println("✗ Error processing payment: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    @FXML
    private void handleClose() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    // ============================================
    // UTILITIES
    // ============================================
    private String formatCurrency(long amount) {
        return currencyFormat.format(amount).replace("₫", "đ");
    }

    private String getPaymentMethodName(String method) {
        switch (method) {
            case "CASH":
                return "Tiền mặt";
            case "TRANSFER":
                return "Chuyển khoản";
            case "CARD":
                return "Thẻ tín dụng";
            case "EWALLET":
                return "Ví điện tử";
            default:
                return "Không xác định";
        }
    }

    private Label createStyledLabel(String text, String style) {
        Label label = new Label(text);
        label.setStyle(style);
        return label;
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}