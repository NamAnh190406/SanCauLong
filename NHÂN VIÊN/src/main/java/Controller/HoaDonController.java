package Controller;

import DAO.HoaDonDAO;
import DAO.DatSanDAO;
import DAO.ThongBaoDAO;
import Model.HOADON;
import Model.DATSAN;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import Controller.EventBus;
/**
 * HoaDonController - Refactored với Clean Architecture
 * * Cải tiến:
 * - Sử dụng ThongBaoDAO static methods
 * - ViewModel pattern cho type safety
 * - Tương tự React InvoiceManagement pattern
 * - Auto-refresh optimized với Platform.runLater
 * - Khắc phục Memory Leak (bỏ finalize, dùng shutdown)
 */
public class HoaDonController implements Initializable {

    // ============================================
    // VIEW MODEL (DTO) - Type Safe Data
    // ============================================
    public static class InvoiceViewModel {
        private HOADON hoaDon;
        private DATSAN datSan;
        private BooleanProperty selected = new SimpleBooleanProperty(false);

        public InvoiceViewModel(HOADON hoaDon, DATSAN datSan) {
            this.hoaDon = hoaDon;
            this.datSan = datSan != null ? datSan : createEmptyDatSan(hoaDon.getMaDS());
        }

        public HOADON getHoaDon() {
            return hoaDon;
        }

        public DATSAN getDatSan() {
            return datSan;
        }

        public String getInvoiceNumber() {
            return hoaDon.getMaHD();
        }

        public String getCustomer() {
            if (HOADON.LOAI_DICH_VU.equals(getLoaiHD()) && datSan.getMaDS().startsWith("HDDV")) {
                if (hoaDon.getGhiChu() != null && hoaDon.getGhiChu().contains("Khách hàng: ")) {
                    try {
                        String afterPrefix = hoaDon.getGhiChu().split("Khách hàng: ")[1];
                        return afterPrefix.split(" - ")[0].trim();
                    } catch (Exception e) {
                        // fallback
                    }
                }
            }
            return (datSan.getTenKH() != null && !datSan.getTenKH().trim().isEmpty()) ? datSan.getTenKH()
                    : "Khách hàng ẩn";
        }

        public String getPhone() {
            return (datSan.getSdtKH() != null && !datSan.getSdtKH().trim().isEmpty()) ? datSan.getSdtKH() : "N/A";
        }

        public String getCourt() {
            return (datSan.getTenSan() != null && !datSan.getTenSan().trim().isEmpty()) ? datSan.getTenSan()
                    : (datSan.getMaSan() != null ? datSan.getMaSan() : "-");
        }

        public String getBookingTime() {
            if (HOADON.LOAI_DICH_VU.equals(hoaDon.getLoaiHD())) {
                return hoaDon.getNgayXuat() != null ? hoaDon.getNgayXuat().toString() : "N/A";
            }
            if (datSan.getNgayDat() != null) {
                String kg = datSan.getKhungGio() != null ? datSan.getKhungGio() : "";
                return datSan.getNgayDat() + (kg.isEmpty() ? "" : " | " + kg);
            }
            return hoaDon.getNgayXuat() != null ? hoaDon.getNgayXuat().toString() : "N/A";
        }

        public long getCourtPrice() {
            return datSan.getTongTienTamTinh();
        }

        public long getServicePrice() {
            return hoaDon.getTongTienDV();
        }

        public long getDiscount() {
            return hoaDon.getSoTienGiam();
        }

        public long getTotal() {
            // Hóa đơn đặt sân: tiền sân + tiền DV - giảm giá
            // Hóa đơn dịch vụ: chỉ tiền DV
            if (HOADON.LOAI_DICH_VU.equals(hoaDon.getLoaiHD())) {
                return getServicePrice() - getDiscount();
            }
            return getCourtPrice() + getServicePrice() - getDiscount();
        }

        public boolean isPaid() {
            return hoaDon.getTrangThai() != null && hoaDon.getTrangThai().contains("Da Thanh Toan");
        }

        /** Loại hóa đơn: HOADON.LOAI_DAT_SAN hoặc HOADON.LOAI_DICH_VU */
        public String getLoaiHD() {
            String loai = hoaDon.getLoaiHD();
            // Tương thích ngược: nếu chưa có LoaiHD, suy ra từ MaDS
            if (loai == null || loai.isEmpty()) {
                return hoaDon.getMaDS() != null ? HOADON.LOAI_DAT_SAN : HOADON.LOAI_DICH_VU;
            }
            return loai;
        }

        /** Nhãn hiển thị trên UI */
        public String getLoaiHDLabel() {
            return HOADON.LOAI_DICH_VU.equals(getLoaiHD()) ? "🛍 Dịch vụ" : "🏘 Đặt sân";
        }

        public BooleanProperty selectedProperty() {
            return selected;
        }

        private static DATSAN createEmptyDatSan(String maDatSan) {
            DATSAN ds = new DATSAN();
            ds.setMaDS(maDatSan);
            ds.setTenKH("Khách hàng ẩn");
            ds.setSdtKH("N/A");
            ds.setMaSan("N/A");
            ds.setTongTienTamTinh(0);
            return ds;
        }
    }

    // ============================================
    // FXML INJECTIONS
    // ============================================
    @FXML
    private Label lblTotalRevenue, lblTotalUnpaid, lblTotalInvoices;
    @FXML
    private Button btnAddInvoice, btnDeleteSelected, btnCancelSelection;
    @FXML
    private TextField txtSearch;
    // SỬA #1: Dùng Button thường (khớp FXML) thay vì ToggleButton
    @FXML
    private Button btnFilterAll, btnFilterPaid, btnFilterUnpaid;
    @FXML
    private TableView<InvoiceViewModel> tableInvoices;
    @FXML
    private TableColumn<InvoiceViewModel, Boolean> colSelect;
    @FXML
    private TableColumn<InvoiceViewModel, String> colInvoiceNumber, colCustomer, colPhone, colCourt;
    @FXML
    private TableColumn<InvoiceViewModel, InvoiceViewModel> colTime, colServices, colStatus, colActions;
    @FXML
    private TableColumn<InvoiceViewModel, Long> colTotal;
    @FXML
    private HBox boxSelectedCount;
    @FXML
    private Label lblSelectedCount; // SỬA #4: label đếm đã chọn

    // SỬA #2 & #3: Modal Controls — dùng Labels riêng khớp FXML
    @FXML
    private StackPane modalOverlay;
    @FXML
    private Label lblModalTitle;
    // Thông tin cơ bản
    @FXML
    private Label lblModalCustomer, lblModalPhone, lblModalCourt, lblModalTime;
    // Chi tiết thanh toán
    @FXML
    private Label lblModalCourtDuration, lblModalCourtPrice;
    @FXML
    private VBox vboxModalServicesList;
    @FXML
    private Label lblModalTotal;
    // Thông tin thanh toán (chỉ hiện khi đã thanh toán)
    @FXML
    private VBox vboxPaymentInfo;
    @FXML
    private Label lblModalPaymentDate, lblModalPaymentMethod;
    // Buttons modal
    @FXML
    private Button btnCloseModal, btnCloseModalIcon, btnPayModal; // SỬA #3: thêm btnCloseModalIcon

    // ============================================
    // STATE & SERVICES
    // ============================================
    private HoaDonDAO hoaDonDAO;
    private DatSanDAO datSanDAO;
    private ScheduledExecutorService scheduler;

    private ObservableList<InvoiceViewModel> masterData = FXCollections.observableArrayList();
    private ObservableList<InvoiceViewModel> filteredData = FXCollections.observableArrayList();

    private String currentFilter = "ALL";
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private CheckBox cbSelectAll = new CheckBox();

    // ============================================
    // INITIALIZATION
    // ============================================
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            hoaDonDAO = new HoaDonDAO();
            datSanDAO = new DatSanDAO();

            setupTable();
            setupFiltersAndSearch();
            setupButtons();
            setupModals();

            loadDataFromDatabase();
            startAutoRefresh();
            setupEventListeners();
            System.out.println("✓ HoaDonController initialized");
        } catch (Exception e) {
            System.err.println("Error initializing HoaDonController: " + e.getMessage());
            ghiLogThongBao("Lỗi khởi tạo", "Không thể kết nối cơ sở dữ liệu: " + e.getMessage(), "error");
        }
    }

    // ============================================
    // TABLE SETUP
    // ============================================
    private void setupTable() {
        colInvoiceNumber.setCellValueFactory(new PropertyValueFactory<>("invoiceNumber"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customer"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colCourt.setCellValueFactory(new PropertyValueFactory<>("court"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        setupSelectAllCheckbox();

        colTime.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue()));
        colTime.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(InvoiceViewModel inv, boolean empty) {
                super.updateItem(inv, empty);
                if (empty || inv == null) {
                    setGraphic(null);
                } else {
                    VBox box = new VBox(4);
                    Label timeLabel = new Label(inv.getBookingTime());
                    Label durationLabel = createStyledLabel(inv.getDatSan().getThoiLuong() + "h",
                            "-fx-text-fill: #6b7280; -fx-font-size: 11px;");
                    box.getChildren().addAll(timeLabel, durationLabel);
                    setGraphic(box);
                }
            }
        });

        colServices
                .setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue()));
        colServices.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(InvoiceViewModel inv, boolean empty) {
                super.updateItem(inv, empty);
                if (empty || inv == null) {
                    setGraphic(null);
                } else {
                    long servicePrice = inv.getServicePrice();
                    Label lbl = createStyledLabel(
                            servicePrice > 0 ? formatCurrency(servicePrice) : "Không có",
                            "-fx-text-fill: #2563eb; -fx-cursor: hand;");
                    lbl.setOnMouseClicked(e -> showModal(inv));
                    setGraphic(lbl);
                }
            }
        });

        colStatus
                .setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue()));
        colStatus.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(InvoiceViewModel inv, boolean empty) {
                super.updateItem(inv, empty);
                if (empty || inv == null) {
                    setGraphic(null);
                } else {
                    Label statusLabel = new Label(inv.isPaid() ? "✓ Đã thanh toán" : "⏱ Chưa thanh toán");
                    statusLabel.setStyle(inv.isPaid()
                            ? "-fx-background-color: #dcfce7; -fx-text-fill: #15803d; -fx-padding: 4 8; -fx-background-radius: 4;"
                            : "-fx-background-color: #fee2e2; -fx-text-fill: #b91c1c; -fx-padding: 4 8; -fx-background-radius: 4;");
                    setGraphic(statusLabel);
                }
            }
        });

        colActions
                .setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue()));
        colActions.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(InvoiceViewModel inv, boolean empty) {
                super.updateItem(inv, empty);
                if (empty || inv == null) {
                    setGraphic(null);
                } else {
                    HBox actionBox = new HBox(8);
                    actionBox.setAlignment(Pos.CENTER);

                    if (!inv.isPaid()) {
                        Button btnPay = createStyledButton("Thanh toán",
                                "-fx-background-color: #16a34a; -fx-text-fill: white;");
                        btnPay.setOnAction(e -> handlePaymentClick(inv));
                        actionBox.getChildren().add(btnPay);
                    }

                    Button btnDetail = createStyledButton("Chi tiết",
                            "-fx-background-color: #2563eb; -fx-text-fill: white;");
                    btnDetail.setOnAction(e -> showModal(inv));
                    actionBox.getChildren().add(btnDetail);

                    Button btnDelete = createStyledButton("Xóa",
                            "-fx-background-color: #dc2626; -fx-text-fill: white;");
                    btnDelete.setOnAction(e -> handleDeleteInvoice(inv));
                    actionBox.getChildren().add(btnDelete);

                    setGraphic(actionBox);
                }
            }
        });

        tableInvoices.setItems(filteredData);
    }

    private void setupSelectAllCheckbox() {
        colSelect.setGraphic(cbSelectAll);
        cbSelectAll.setStyle("-fx-cursor: hand;");
        cbSelectAll.setOnAction(e -> {
            boolean isSelected = cbSelectAll.isSelected();
            filteredData.forEach(inv -> inv.selectedProperty().set(isSelected));
            updateSelectionState();
        });

        colSelect.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colSelect.setCellFactory(tc -> new TableCell<>() {
            private final CheckBox cb = new CheckBox();
            {
                cb.setStyle("-fx-cursor: hand;");
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    cb.selectedProperty().bindBidirectional(
                            getTableView().getItems().get(getIndex()).selectedProperty());
                    cb.setOnAction(e -> updateSelectionState());
                    setGraphic(cb);
                }
            }
        });
    }

    // ============================================
    // FILTERS & SEARCH
    // ============================================
    // SỬA #1: Bỏ ToggleGroup, dùng style động để highlight filter đang chọn
    private void setupFiltersAndSearch() {
        txtSearch.textProperty().addListener((obs, old, neu) -> applyFilters());

        btnFilterAll.setOnAction(e -> {
            setActiveFilter("ALL");
            applyFilters();
        });
        btnFilterPaid.setOnAction(e -> {
            setActiveFilter("PAID");
            applyFilters();
        });
        btnFilterUnpaid.setOnAction(e -> {
            setActiveFilter("UNPAID");
            applyFilters();
        });
        setActiveFilter("ALL"); // highlight mặc định
    }

    private void setActiveFilter(String filter) {
        currentFilter = filter;
        String active = "-fx-background-color: #16a34a; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand;";
        String inactive = "-fx-background-color: #e5e7eb; -fx-text-fill: #374151; -fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand;";
        btnFilterAll.setStyle(filter.equals("ALL") ? active : inactive);
        btnFilterPaid.setStyle(filter.equals("PAID") ? active : inactive);
        btnFilterUnpaid.setStyle(filter.equals("UNPAID") ? active : inactive);
    }

    private void applyFilters() {
        String keyword = txtSearch.getText().toLowerCase().trim();

        List<InvoiceViewModel> result = masterData.stream()
                .filter(inv -> {
                    boolean matchSearch = inv.getCustomer().toLowerCase().contains(keyword) ||
                            inv.getInvoiceNumber().toLowerCase().contains(keyword) ||
                            inv.getPhone().contains(keyword);

                    boolean matchStatus = currentFilter.equals("ALL") ||
                            (currentFilter.equals("PAID") && inv.isPaid()) ||
                            (currentFilter.equals("UNPAID") && !inv.isPaid());

                    return matchSearch && matchStatus;
                })
                .collect(Collectors.toList());

        filteredData.setAll(result);
        updateStatistics();
    }

    // ============================================
    // BUTTONS
    // ============================================
    private void setupButtons() {
        btnAddInvoice.setOnAction(e -> openThemDatSanDialog());

        btnDeleteSelected.setOnAction(e -> handleDeleteSelected());

        btnCancelSelection.setOnAction(e -> {
            cbSelectAll.setSelected(false);
            filteredData.forEach(inv -> inv.selectedProperty().set(false));
            updateSelectionState();
        });
    }

    // ============================================
    // ACTIONS
    // ============================================
    private void handlePaymentClick(InvoiceViewModel inv) {
        openPaymentDialog(inv);
    }

    /**
     * Mở màn hình thanh toán và truyền đầy đủ thông tin hóa đơn.
     * Được gọi cả từ nút "Thanh toán" trong bảng lẫn sau khi tạo hóa đơn mới.
     */
    private void openPaymentDialog(InvoiceViewModel inv) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ThanhToanUI.fxml"));
            Parent root = loader.load();

            // Lấy đúng class controller từ FXML (ThanhToanController)
            ThanhToanController paymentController = loader.getController();

            // Truyền đầy đủ 6 tham số theo signature của setInvoiceData
            paymentController.setInvoiceData(
                    inv.getInvoiceNumber(),
                    inv.getCustomer(),
                    inv.getPhone(),
                    inv.getCourt(),
                    inv.getBookingTime(),
                    inv.getTotal());

            // Truyền chi tiết giá: tiền sân, tiền dịch vụ, giảm giá
            paymentController.setPaymentDetails(
                    inv.getCourtPrice(),
                    inv.getServicePrice(),
                    inv.getDiscount());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Thanh toán hóa đơn " + inv.getInvoiceNumber());
            stage.showAndWait();

            // Refresh data sau khi thanh toán xong và đóng cửa sổ
            loadDataFromDatabase();
            applyFilters();
        } catch (Exception e) {
            System.err.println("Error opening payment dialog: " + e.getMessage());
            e.printStackTrace();
            ghiLogThongBao("Lỗi", "Không thể mở dialog thanh toán: " + e.getMessage(), "error");
        }
    }

    private void handleDeleteSelected() {
        List<InvoiceViewModel> toDelete = filteredData.stream()
                .filter(inv -> inv.selectedProperty().get())
                .collect(Collectors.toList());

        if (toDelete.isEmpty()) {
            ghiLogThongBao("Thông báo", "Vui lòng chọn hóa đơn để xóa", "warning");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText("Xóa " + toDelete.size() + " hóa đơn?");
        confirm.setContentText("Dữ liệu sau khi xóa sẽ không thể khôi phục.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            int successCount = 0;
            for (InvoiceViewModel inv : toDelete) {
                if (hoaDonDAO.deleteHoaDon(inv.getInvoiceNumber())) {
                    successCount++;
                }
            }

            masterData.removeAll(toDelete);
            cbSelectAll.setSelected(false);
            applyFilters();
            updateSelectionState();

            ghiLogThongBao("Thành công", "Đã xóa " + successCount + "/" + toDelete.size() + " hóa đơn", "success");
        }
    }

    private void handleDeleteInvoice(InvoiceViewModel inv) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText("Xóa hóa đơn " + inv.getInvoiceNumber() + "?");
        confirm.setContentText("Dữ liệu sau khi xóa sẽ không thể khôi phục.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (hoaDonDAO.deleteHoaDon(inv.getInvoiceNumber())) {
                masterData.remove(inv);
                applyFilters();
                ghiLogThongBao("Thành công", "Đã xóa hóa đơn", "success");
            } else {
                ghiLogThongBao("Lỗi", "Không thể xóa hóa đơn này", "error");
            }
        }
    }

    private void openThemDatSanDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ThemDatSan.fxml"));
            Parent root = loader.load();
            ThemDatSanController controller = loader.getController();

            // Callback: sau khi đặt sân thành công, reload data
            // rồi tự động mở màn hình thanh toán cho hóa đơn vừa tạo
            controller.setCallback(() -> {
                loadDataFromDatabase();
                applyFilters();
            });

            // Truyền thêm callback đặc biệt để nhận MaDS sau khi tạo thành công,
            // dùng để tra hóa đơn và mở trang thanh toán ngay lập tức
            controller.setOnSuccessCallback((maDatSan) -> {
                // Tải lại data trước để có hóa đơn mới
                loadDataFromDatabase();
                applyFilters();

                // Tra hóa đơn vừa tạo theo MaDS (do trigger Oracle tự tạo)
                HoaDonDAO tempDAO = new HoaDonDAO();
                HOADON hoaDonMoi = tempDAO.getHoaDonByMaDS(maDatSan);

                if (hoaDonMoi != null) {
                    // Lấy DATSAN để build ViewModel đầy đủ
                    DATSAN datSanMoi = datSanDAO.getDatSanByMa(maDatSan);
                    InvoiceViewModel vmMoi = new InvoiceViewModel(hoaDonMoi, datSanMoi);
                    // Mở ngay màn hình thanh toán trên JavaFX thread
                    javafx.application.Platform.runLater(() -> openPaymentDialog(vmMoi));
                } else {
                    System.err.println("Không tìm thấy hóa đơn cho MaDS: " + maDatSan);
                }
            });

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Thêm hóa đơn mới");
            stage.showAndWait();
        } catch (Exception e) {
            System.err.println("Error opening add invoice dialog: " + e.getMessage());
            ghiLogThongBao("Lỗi", "Không thể mở dialog thêm hóa đơn", "error");
        }
    }

    // ============================================
    // MODAL - Invoice Details
    // ============================================
    // SỬA #3: Bind cả nút X icon lẫn nút Đóng
    private void setupModals() {
        btnCloseModal.setOnAction(e -> modalOverlay.setVisible(false));
        btnCloseModalIcon.setOnAction(e -> modalOverlay.setVisible(false));
    }

    // SỬA #2: Dùng Labels riêng từ FXML thay vì add row động
    private void showModal(InvoiceViewModel inv) {
        lblModalTitle.setText("Chi tiết hóa đơn - " + inv.getInvoiceNumber());

        // Thông tin cơ bản — set thẳng vào Labels của FXML
        lblModalCustomer.setText(inv.getCustomer());
        lblModalPhone.setText(inv.getPhone());
        lblModalCourt.setText(inv.getCourt());
        lblModalTime.setText(inv.getBookingTime());

        // Chi tiết thanh toán
        String durationText = "Tiền sân";
        if (inv.getDatSan() != null && inv.getDatSan().getThoiLuong() > 0) {
            durationText = "Tiền sân (" + inv.getDatSan().getThoiLuong() + "h)";
        }
        lblModalCourtDuration.setText(durationText);
        lblModalCourtPrice.setText(formatCurrency(inv.getCourtPrice()));

        // Danh sách dịch vụ + giảm giá (build động vào vboxModalServicesList)
        vboxModalServicesList.getChildren().clear();
        if (inv.getServicePrice() > 0) {
            java.util.List<Object[]> dsDichVu = hoaDonDAO.getCTDVByHoaDon(inv.getInvoiceNumber());

            if (dsDichVu != null && !dsDichVu.isEmpty()) {
                Label lblHeaderSvc = new Label("Chi tiết dịch vụ:");
                lblHeaderSvc.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-padding: 4 0;");
                vboxModalServicesList.getChildren().add(lblHeaderSvc);

                for (Object[] ct : dsDichVu) {
                    String tenDV = (String) ct[0];
                    long soLuong = (Long) ct[1];
                    long donGia = (Long) ct[2];
                    long thanhTienCT = soLuong * donGia;

                    HBox itemRow = new HBox();
                    Label lblItemName = new Label("- " + tenDV + " (x" + soLuong + ")");
                    lblItemName.setStyle("-fx-text-fill: #4b5563;");

                    HBox.setHgrow(lblItemName, Priority.ALWAYS);
                    lblItemName.setMaxWidth(Double.MAX_VALUE);

                    Label lblItemPrice = new Label(formatCurrency(thanhTienCT));
                    lblItemPrice.setStyle("-fx-text-fill: #4b5563;");

                    itemRow.getChildren().addAll(lblItemName, lblItemPrice);
                    vboxModalServicesList.getChildren().add(itemRow);
                }

                // Thêm dòng tổng tiền dịch vụ
                HBox totalSvcRow = new HBox();
                totalSvcRow.setStyle("-fx-padding: 4 0 0 0; -fx-border-color: #e5e7eb; -fx-border-width: 1 0 0 0;");
                Label lblTotalSvc = new Label("Tổng tiền dịch vụ:");
                lblTotalSvc.setStyle("-fx-font-style: italic; -fx-text-fill: #374151;");
                HBox.setHgrow(lblTotalSvc, Priority.ALWAYS);
                lblTotalSvc.setMaxWidth(Double.MAX_VALUE);
                Label lblTotalSvcVal = new Label(formatCurrency(inv.getServicePrice()));
                lblTotalSvcVal.setStyle("-fx-font-weight: bold; -fx-text-fill: #16a34a;");
                totalSvcRow.getChildren().addAll(lblTotalSvc, lblTotalSvcVal);
                vboxModalServicesList.getChildren().add(totalSvcRow);

            } else {
                HBox serviceRow = new HBox();
                Label lblSvc = new Label("Tiền dịch vụ");
                HBox.setHgrow(lblSvc, Priority.ALWAYS);
                lblSvc.setMaxWidth(Double.MAX_VALUE);
                Label lblSvcVal = new Label(formatCurrency(inv.getServicePrice()));
                lblSvcVal.setStyle("-fx-text-fill: #4b5563;");
                serviceRow.getChildren().addAll(lblSvc, lblSvcVal);
                vboxModalServicesList.getChildren().add(serviceRow);
            }
        }
        if (inv.getDiscount() > 0) {
            HBox discountRow = new HBox();
            Label lblDisc = new Label("Giảm giá/Voucher");
            HBox.setHgrow(lblDisc, Priority.ALWAYS);
            lblDisc.setMaxWidth(Double.MAX_VALUE);
            Label lblDiscVal = createStyledLabel("-" + formatCurrency(inv.getDiscount()), "-fx-text-fill: #dc2626;");
            discountRow.getChildren().addAll(lblDisc, lblDiscVal);
            vboxModalServicesList.getChildren().add(discountRow);
        }

        lblModalTotal.setText(formatCurrency(inv.getTotal()));

        // Thông tin thanh toán
        if (inv.isPaid()) {
            vboxPaymentInfo.setVisible(true);
            vboxPaymentInfo.setManaged(true);
            String ngayXuat = inv.getHoaDon().getNgayXuat() != null
                    ? inv.getHoaDon().getNgayXuat().toString()
                    : "-";
            lblModalPaymentDate.setText(ngayXuat);
            lblModalPaymentMethod.setText(inv.getHoaDon().getTrangThai());
        } else {
            vboxPaymentInfo.setVisible(false);
            vboxPaymentInfo.setManaged(false);
        }

        btnPayModal.setVisible(!inv.isPaid());
        btnPayModal.setManaged(!inv.isPaid());
        btnPayModal.setOnAction(e -> {
            handlePaymentClick(inv);
            modalOverlay.setVisible(false);
        });

        modalOverlay.setVisible(true);
    }

    // Các helper method add-row động đã không còn dùng cho modal chính.
    // Vẫn giữ lại phòng khi cần dùng ở chỗ khác.

    // ============================================
    // DATA & REFRESH
    // ============================================
    private void loadDataFromDatabase() {
        try {
            ObservableList<HOADON> dbList = hoaDonDAO.getAllHoaDon();
            List<InvoiceViewModel> tempList = new ArrayList<>();

            for (HOADON hd : dbList) {
                DATSAN ds = null;
                if (hd.getMaDS() != null && !hd.getMaDS().isEmpty()) {
                    ds = datSanDAO.getDatSanByMa(hd.getMaDS());
                }
                tempList.add(new InvoiceViewModel(hd, ds));
            }

            // Cập nhật UI trên JavaFX Application Thread
            Platform.runLater(() -> {
                masterData.setAll(tempList);
                updateStatistics();
                applyFilters();
            });

        } catch (Exception e) {
            System.err.println("Error loading invoices: " + e.getMessage());
            Platform.runLater(() -> ghiLogThongBao("Lỗi", "Không thể tải danh sách hóa đơn", "error"));
        }
    }

    private void startAutoRefresh() {
        scheduler = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r, "HoaDon-Refresh");
            t.setDaemon(true);
            return t;
        });

        scheduler.scheduleAtFixedRate(() -> {
            try {
                loadDataFromDatabase();
            } catch (Exception e) {
                System.err.println("Error in auto-refresh: " + e.getMessage());
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    private void updateStatistics() {
        long revenue = masterData.stream()
                .filter(InvoiceViewModel::isPaid)
                .mapToLong(InvoiceViewModel::getTotal)
                .sum();

        long unpaid = masterData.stream()
                .filter(inv -> !inv.isPaid())
                .mapToLong(InvoiceViewModel::getTotal)
                .sum();

        lblTotalRevenue.setText(formatCurrency(revenue));
        lblTotalUnpaid.setText(formatCurrency(unpaid));
        lblTotalInvoices.setText(String.valueOf(filteredData.size()));
    }

    // SỬA #4: Cập nhật thêm lblSelectedCount
    private void updateSelectionState() {
        long count = filteredData.stream().filter(inv -> inv.selectedProperty().get()).count();

        boxSelectedCount.setVisible(count > 0);
        lblSelectedCount.setText(String.valueOf(count)); // update số đã chọn ở footer

        btnDeleteSelected.setVisible(count > 0);
        btnDeleteSelected.setManaged(count > 0);
        btnCancelSelection.setVisible(count > 0);
        btnCancelSelection.setManaged(count > 0);

        btnDeleteSelected.setText("Xóa (" + count + ")");
    }

    // ============================================
    // UTILITIES
    // ============================================
    private String formatCurrency(long amount) {
        return currencyFormat.format(amount).replace("₫", "đ");
    }

    private Label createStyledLabel(String text, String style) {
        Label label = new Label(text);
        label.setStyle(style);
        return label;
    }

    private Button createStyledButton(String text, String style) {
        Button btn = new Button(text);
        btn.setStyle(style + " -fx-padding: 4 12; -fx-cursor: hand;");
        return btn;
    }

    /**
     * Helper method để ghi log thông báo rút gọn
     */
    private void ghiLogThongBao(String tieuDe, String noiDung, String loai) {
        try {
            ThongBaoDAO.themThongBao(tieuDe, noiDung, loai);
        } catch (Exception e) {
            System.err.println("Không thể lưu thông báo: " + e.getMessage());
        }
    }
     private void setupEventListeners() {
        EventBus.subscribe(eventType -> {
            // Khi tạo đơn dịch vụ thành công
            if (EventBus.EVENT_SERVICE_ORDER_CREATED.equals(eventType)) {
                System.out.println("🔔 HoaDonController nhận được sự kiện: SERVICE_ORDER_CREATED!");
                
                Platform.runLater(() -> {
                    // Reload dữ liệu
                    loadDataFromDatabase();
                    
                    // Apply lại filter hiện tại
                    applyFilters();
                    
                    // Highlight và scroll tới hóa đơn mới nhất (nếu có)
                    if (!filteredData.isEmpty()) {
                        tableInvoices.getSelectionModel().selectFirst();
                        tableInvoices.scrollTo(0);
                    }
                    
                    // Thông báo cho người dùng
                    ghiLogThongBao("✨ Dịch vụ mới", "Hóa đơn dịch vụ đã được thêm thành công", "success");
                });
            }
            // Khi thanh toán thành công (tùy chọn)
            else if (EventBus.EVENT_INVOICE_PAID.equals(eventType)) {
                System.out.println("🔔 HoaDonController nhận được sự kiện: INVOICE_PAID!");
                
                Platform.runLater(() -> {
                    loadDataFromDatabase();
                    applyFilters();
                });
            }
            // Khi xóa hóa đơn (tùy chọn)
            else if (EventBus.EVENT_INVOICE_DELETED.equals(eventType)) {
                System.out.println("🔔 HoaDonController nhận được sự kiện: INVOICE_DELETED!");
                
                Platform.runLater(() -> {
                    loadDataFromDatabase();
                    applyFilters();
                });
            }
        });
    }
    /**
     * Hàm gọi khi đóng cửa sổ để tránh Memory Leak
     */
    public void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            System.out.println("Đã dọn dẹp luồng Auto-Refresh của Hóa Đơn.");
        }
    }
    
}
