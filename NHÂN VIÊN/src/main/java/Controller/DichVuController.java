package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DichVuController implements Initializable {

    // ================= FXML INJECTIONS =================

    @FXML
    private Label lblLowStock;
    @FXML
    private Button btnTaoDonh, btnThemDV;
    @FXML
    private Button btnTabDon, btnTabDM;

    @FXML
    private VBox paneDon, paneDM;

    // --- Tab Đơn Đặt Dịch Vụ ---
    @FXML
    private TextField txtSearchDon;
    @FXML
    private Button btnFilterAll, btnFilterPending, btnFilterServing, btnFilterDone, btnFilterCancelled;
    @FXML
    private Label lblCountPending, lblCountServing, lblCountDone, lblCountCancelled;
    @FXML
    private VBox vboxOrderList;

    // --- Tab Danh Mục Dịch Vụ ---
    @FXML
    private VBox boxLowStock;
    @FXML
    private HBox hboxLowStockItems;
    @FXML
    private TextField txtSearchDV;
    @FXML
    private Button btnCatAll, btnCatDrink, btnCatEquip, btnCatFood, btnCatOther;
    @FXML
    private FlowPane flowServices;

    // --- Modal: Tạo Đơn Dịch Vụ ---
    @FXML
    private StackPane overlayTaoDon;
    @FXML
    private Button btnCloseTaoDon;
    @FXML
    private VBox vboxDVChon;
    @FXML
    private TextField txtOrderCustomer;
    @FXML
    private ComboBox<String> cbxOrderCourt;
    @FXML
    private TextField txtOrderNote;
    @FXML
    private VBox vboxOrderLines;
    @FXML
    private Label lblOrderTotal;
    @FXML
    private Button btnHuyTaoDon, btnXacNhanTaoDon;

    // --- Modal: Thêm/Sửa Dịch Vụ ---
    @FXML
    private StackPane overlayDichVuForm;
    @FXML
    private Label lblDVFormTitle;
    @FXML
    private Button btnCloseDVForm;
    @FXML
    private TextField txtDVName;
    @FXML
    private ComboBox<String> cbxDVCategory;
    @FXML
    private TextField txtDVUnit, txtDVPrice, txtDVStock, txtDVMinStock;
    @FXML
    private Button btnHuyDVForm, btnLuuDV;

    // --- Modal: Nhập Kho ---
    @FXML
    private StackPane overlayRestock;
    @FXML
    private Button btnCloseRestock;
    @FXML
    private Label lblRestockName, lblRestockCurrent, lblRestockAfter;
    @FXML
    private TextField txtRestockQty;
    @FXML
    private Button btnHuyRestock, btnXacNhanRestock;

    // ================= MODELS (In-memory cho demo) =================

    enum ServiceCategory {
        DRINK, EQUIPMENT, FOOD, OTHER
    }

    enum OrderStatus {
        PENDING, SERVING, DONE, CANCELLED
    }

    static class ServiceItem {
        String id;
        String name;
        ServiceCategory category;
        long price;
        String unit;
        int stock;
        int minStock;

        public ServiceItem(String id, String name, ServiceCategory category, long price, String unit, int stock,
                int minStock) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
            this.unit = unit;
            this.stock = stock;
            this.minStock = minStock;
        }
    }

    static class OrderLine {
        String serviceId;
        String serviceName;
        int quantity;
        long price;

        public OrderLine(String serviceId, String serviceName, int quantity, long price) {
            this.serviceId = serviceId;
            this.serviceName = serviceName;
            this.quantity = quantity;
            this.price = price;
        }
    }

    static class ServiceOrder {
        int id;
        String orderNumber;
        String customer;
        String court;
        List<OrderLine> lines;
        long total;
        OrderStatus status;
        String createdAt;
        String note;

        public ServiceOrder(int id, String orderNumber, String customer, String court, List<OrderLine> lines,
                long total, OrderStatus status, String createdAt, String note) {
            this.id = id;
            this.orderNumber = orderNumber;
            this.customer = customer;
            this.court = court;
            this.lines = new ArrayList<>(lines);
            this.total = total;
            this.status = status;
            this.createdAt = createdAt;
            this.note = note;
        }
    }

    // ================= STATE =================

    private ObservableList<ServiceItem> services = FXCollections.observableArrayList();
    private ObservableList<ServiceOrder> orders = FXCollections.observableArrayList();
    private List<OrderLine> currentOrderLines = new ArrayList<>();

    private OrderStatus orderFilter = null; // null = all
    private ServiceCategory catFilter = null; // null = all

    private ServiceItem editingService = null;
    private ServiceItem restockTarget = null;

    private NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDataFromDB();
        setupTabs();
        setupFilters();
        setupModals();

        refreshAll();
    }

    private void loadDataFromDB() {
        services.clear();
        orders.clear(); // Fix: Làm sạch danh sách Hóa đơn trước khi tải mới để tránh lặp dữ liệu

        DAO.DichVuDAO dao = null;
        try {
            dao = new DAO.DichVuDAO();
            ObservableList<Model.DichVu> dbServices = dao.getAllDichVu();
            for (Model.DichVu d : dbServices) {
                ServiceCategory cat = getCatEnum(d.getLoaiDV());
                services.add(
                        new ServiceItem(d.getMaDV(), d.getTenDV(), cat, d.getGiaBan(), "cái", d.getSoLuongTon(), 5));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dao != null) {
                try {
                    dao.closeCon();
                } catch (Exception e) {
                }
            }
        }

        // Load đơn dịch vụ thực từ DB (HOADON WHERE LoaiHD='DICH_VU')
        DAO.HoaDonDAO hoaDonDAO = new DAO.HoaDonDAO();
        java.util.List<Object[]> dbOrders = hoaDonDAO.getHoaDonDichVu();
        int seq = 1;
        for (Object[] row : dbOrders) {
            String maHD = (String) row[0];
            String tenKH = (String) row[1];
            String tenSan = row[2] != null ? (String) row[2] : "---";
            long total = (Long) row[3];
            String ttDB = row[4] != null ? (String) row[4] : "";
            String ghiChu = row[5] != null ? (String) row[5] : "";
            java.time.LocalDate ngay = (java.time.LocalDate) row[6];

            // Map trạng thái DB → OrderStatus enum
            OrderStatus status;
            if (ttDB.equalsIgnoreCase("Da Thanh Toan") || ttDB.equalsIgnoreCase("HoanThanh")) {
                status = OrderStatus.DONE;
            } else if (ttDB.equalsIgnoreCase("DangPhucVu") || ttDB.equalsIgnoreCase("Dang Phuc Vu")) {
                status = OrderStatus.SERVING;
            } else if (ttDB.equalsIgnoreCase("DaHuy") || ttDB.equalsIgnoreCase("Da Huy")) {
                status = OrderStatus.CANCELLED;
            } else {
                status = OrderStatus.PENDING; // "Chua Thanh Toan" và mặc định
            }

            // Lấy chi tiết CTDV
            java.util.List<OrderLine> lines = new java.util.ArrayList<>();
            for (Object[] ct : hoaDonDAO.getCTDVByHoaDon(maHD)) {
                lines.add(new OrderLine(
                        (String) ct[3], // MaDV
                        (String) ct[0], // TenDV
                        ((Long) ct[1]).intValue(), // SoLuong
                        (Long) ct[2] // DonGia
                ));
            }

            String timeStr = ngay != null ? ngay.toString() : "";
            orders.add(new ServiceOrder(seq++, maHD, tenKH, tenSan, lines, total, status, timeStr, ghiChu));
        }

        cbxDVCategory.setItems(FXCollections.observableArrayList("Đồ uống", "Thiết bị", "Đồ ăn", "Khác"));
    }

    private void setupTabs() {
        btnTabDon.setOnAction(e -> {
            paneDon.setVisible(true);
            paneDon.setManaged(true);
            paneDM.setVisible(false);
            paneDM.setManaged(false);
            btnTaoDonh.setVisible(true);
            btnTaoDonh.setManaged(true);
            btnThemDV.setVisible(false);
            btnThemDV.setManaged(false);

            btnTabDon.setStyle(
                    "-fx-background-color: white; -fx-text-fill: #16a34a; -fx-background-radius: 6; -fx-padding: 8 16; -fx-cursor: hand; -fx-font-weight: bold;");
            btnTabDM.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: #6b7280; -fx-background-radius: 6; -fx-padding: 8 16; -fx-cursor: hand;");
            refreshOrders();
        });

        btnTabDM.setOnAction(e -> {
            paneDon.setVisible(false);
            paneDon.setManaged(false);
            paneDM.setVisible(true);
            paneDM.setManaged(true);
            btnTaoDonh.setVisible(false);
            btnTaoDonh.setManaged(false);
            btnThemDV.setVisible(true);
            btnThemDV.setManaged(true);

            btnTabDM.setStyle(
                    "-fx-background-color: white; -fx-text-fill: #16a34a; -fx-background-radius: 6; -fx-padding: 8 16; -fx-cursor: hand; -fx-font-weight: bold;");
            btnTabDon.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: #6b7280; -fx-background-radius: 6; -fx-padding: 8 16; -fx-cursor: hand;");
            refreshCatalog();
        });
    }

    private void setupFilters() {
        // Order filters
        txtSearchDon.textProperty().addListener((obs, oldV, newV) -> refreshOrders());
        btnFilterAll.setOnAction(e -> {
            orderFilter = null;
            updateOrderFilterButtons(btnFilterAll);
            refreshOrders();
        });
        btnFilterPending.setOnAction(e -> {
            orderFilter = OrderStatus.PENDING;
            updateOrderFilterButtons(btnFilterPending);
            refreshOrders();
        });
        btnFilterServing.setOnAction(e -> {
            orderFilter = OrderStatus.SERVING;
            updateOrderFilterButtons(btnFilterServing);
            refreshOrders();
        });
        btnFilterDone.setOnAction(e -> {
            orderFilter = OrderStatus.DONE;
            updateOrderFilterButtons(btnFilterDone);
            refreshOrders();
        });
        btnFilterCancelled.setOnAction(e -> {
            orderFilter = OrderStatus.CANCELLED;
            updateOrderFilterButtons(btnFilterCancelled);
            refreshOrders();
        });

        // Catalog filters
        txtSearchDV.textProperty().addListener((obs, oldV, newV) -> refreshCatalog());
        btnCatAll.setOnAction(e -> {
            catFilter = null;
            updateCatFilterButtons(btnCatAll);
            refreshCatalog();
        });
        btnCatDrink.setOnAction(e -> {
            catFilter = ServiceCategory.DRINK;
            updateCatFilterButtons(btnCatDrink);
            refreshCatalog();
        });
        btnCatEquip.setOnAction(e -> {
            catFilter = ServiceCategory.EQUIPMENT;
            updateCatFilterButtons(btnCatEquip);
            refreshCatalog();
        });
        btnCatFood.setOnAction(e -> {
            catFilter = ServiceCategory.FOOD;
            updateCatFilterButtons(btnCatFood);
            refreshCatalog();
        });
        btnCatOther.setOnAction(e -> {
            catFilter = ServiceCategory.OTHER;
            updateCatFilterButtons(btnCatOther);
            refreshCatalog();
        });
    }

    private void updateOrderFilterButtons(Button activeBtn) {
        Button[] btns = { btnFilterAll, btnFilterPending, btnFilterServing, btnFilterDone, btnFilterCancelled };
        for (Button b : btns) {
            b.setStyle(b == activeBtn
                    ? "-fx-background-color: #16a34a; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 14; -fx-cursor: hand;"
                    : "-fx-background-color: white; -fx-text-fill: #6b7280; -fx-border-color: #e5e7eb; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 14; -fx-cursor: hand;");
        }
    }

    private void updateCatFilterButtons(Button activeBtn) {
        Button[] btns = { btnCatAll, btnCatDrink, btnCatEquip, btnCatFood, btnCatOther };
        for (Button b : btns) {
            b.setStyle(b == activeBtn
                    ? "-fx-background-color: #16a34a; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 14; -fx-cursor: hand;"
                    : "-fx-background-color: white; -fx-text-fill: #6b7280; -fx-border-color: #e5e7eb; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 14; -fx-cursor: hand;");
        }
    }

    private void setupModals() {
        // Tạo đơn
        btnTaoDonh.setOnAction(e -> openTaoDonModal());
        btnCloseTaoDon.setOnAction(e -> overlayTaoDon.setVisible(false));
        btnHuyTaoDon.setOnAction(e -> overlayTaoDon.setVisible(false));
        btnXacNhanTaoDon.setOnAction(e -> createOrder());

        // Thêm sửa dịch vụ
        btnThemDV.setOnAction(e -> openDVForm(null));
        btnCloseDVForm.setOnAction(e -> overlayDichVuForm.setVisible(false));
        btnHuyDVForm.setOnAction(e -> overlayDichVuForm.setVisible(false));
        btnLuuDV.setOnAction(e -> saveService());

        // Nhập kho
        btnCloseRestock.setOnAction(e -> overlayRestock.setVisible(false));
        btnHuyRestock.setOnAction(e -> overlayRestock.setVisible(false));
        btnXacNhanRestock.setOnAction(e -> confirmRestock());

        txtRestockQty.textProperty().addListener((obs, oldV, newV) -> {
            if (restockTarget != null) {
                int qty = 0;
                try {
                    qty = Integer.parseInt(newV);
                } catch (Exception ignored) {
                }
                lblRestockAfter.setText("Sau khi nhập: " + (restockTarget.stock + qty) + " " + restockTarget.unit);
            }
        });
    }

    private void refreshAll() {
        refreshOrders();
        refreshCatalog();

        // Low stock alerts
        List<ServiceItem> lowStock = services.stream().filter(s -> s.stock <= s.minStock).collect(Collectors.toList());
        if (!lowStock.isEmpty()) {
            lblLowStock.setText("⚠ " + lowStock.size() + " mặt hàng sắp hết");
            lblLowStock.setVisible(true);
            lblLowStock.setManaged(true);

            boxLowStock.setVisible(true);
            boxLowStock.setManaged(true);
            hboxLowStockItems.getChildren().clear();
            for (ServiceItem item : lowStock) {
                Button btnAlert = new Button(item.name + ": còn " + item.stock + " " + item.unit + " → Nhập thêm");
                btnAlert.setStyle(
                        "-fx-background-color: white; -fx-border-color: #fca5a5; -fx-border-radius: 12; -fx-background-radius: 12; -fx-text-fill: #dc2626; -fx-padding: 4 10; -fx-font-size: 11px; -fx-cursor: hand;");
                btnAlert.setOnAction(e -> openRestockModal(item));
                hboxLowStockItems.getChildren().add(btnAlert);
            }
        } else {
            lblLowStock.setVisible(false);
            lblLowStock.setManaged(false);
            boxLowStock.setVisible(false);
            boxLowStock.setManaged(false);
        }
    }

    // ================= ORDERS LOGIC =================

    private void refreshOrders() {
        String search = txtSearchDon.getText().toLowerCase();
        List<ServiceOrder> filtered = orders.stream()
                .filter(o -> orderFilter == null || o.status == orderFilter)
                .filter(o -> search.isEmpty() || o.customer.toLowerCase().contains(search)
                        || o.orderNumber.toLowerCase().contains(search) || o.court.toLowerCase().contains(search))
                .sorted((a, b) -> Integer.compare(b.id, a.id)) // newest first
                .collect(Collectors.toList());

        // Update stats
        long p = orders.stream().filter(o -> o.status == OrderStatus.PENDING).count();
        long s = orders.stream().filter(o -> o.status == OrderStatus.SERVING).count();
        long d = orders.stream().filter(o -> o.status == OrderStatus.DONE).count();
        long c = orders.stream().filter(o -> o.status == OrderStatus.CANCELLED).count();
        lblCountPending.setText(String.valueOf(p));
        lblCountServing.setText(String.valueOf(s));
        lblCountDone.setText(String.valueOf(d));
        lblCountCancelled.setText(String.valueOf(c));

        // Render list
        vboxOrderList.getChildren().clear();
        for (ServiceOrder order : filtered) {
            VBox card = new VBox(0);
            card.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #e5e7eb; -fx-border-radius: 8;");

            // Top row
            HBox topRow = new HBox(12);
            topRow.setAlignment(Pos.CENTER_LEFT);
            topRow.setStyle("-fx-padding: 12 16; -fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;");

            Label lblNum = new Label(order.orderNumber);
            lblNum.setStyle("-fx-font-weight: bold;");
            Label lblCust = new Label(order.customer);
            lblCust.setStyle("-fx-text-fill: #4b5563;");
            Label lblCourt = new Label(order.court);
            lblCourt.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11px;");

            Label lblStatus = new Label(getStatusLabel(order.status));
            lblStatus.setStyle(getStatusStyle(order.status)
                    + " -fx-padding: 4 8; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");

            HBox infoBox = new HBox(8, lblNum, new Label("·"), lblCust, new Label("·"), lblCourt, lblStatus);
            infoBox.setAlignment(Pos.CENTER_LEFT);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label lblTime = new Label(order.createdAt);
            lblTime.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 11px;");
            Label lblTotal = new Label(formatCurrency(order.total));
            lblTotal.setStyle("-fx-font-weight: bold; -fx-text-fill: #16a34a;");

            topRow.getChildren().addAll(infoBox, spacer, lblTime, lblTotal);

            // Bottom row
            HBox botRow = new HBox(16);
            botRow.setAlignment(Pos.CENTER_LEFT);
            botRow.setStyle("-fx-padding: 12 16;");

            FlowPane linesPane = new FlowPane();
            linesPane.setHgap(8);
            linesPane.setVgap(8);
            for (OrderLine line : order.lines) {
                Label l = new Label(line.serviceName + " ×" + line.quantity + " (" + formatCurrency(line.price * line.quantity) + ")");
                l.setStyle(
                        "-fx-background-color: #f3f4f6; -fx-text-fill: #4b5563; -fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 11px;");
                linesPane.getChildren().add(l);
            }
            if (order.note != null && !order.note.isEmpty()) {
                Label lNote = new Label("📝 " + order.note);
                lNote.setStyle(
                        "-fx-background-color: #fefce8; -fx-border-color: #fef08a; -fx-border-radius: 12; -fx-text-fill: #a16207; -fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 11px;");
                linesPane.getChildren().add(lNote);
            }

            Region spacer2 = new Region();
            HBox.setHgrow(spacer2, Priority.ALWAYS);
            HBox actionBox = new HBox(8);

            // Fix: Gọi CSDL khi cập nhật trạng thái đơn hàng
            if (order.status == OrderStatus.PENDING) {
                Button btnStart = new Button("Bắt đầu phục vụ");
                btnStart.setStyle(
                        "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 12px;");
                btnStart.setOnAction(e -> {
                    try {
                        DAO.HoaDonDAO hdDAO = new DAO.HoaDonDAO();
                        if (hdDAO.updateTrangThaiHoaDon(order.orderNumber, "Dang Phuc Vu")) {
                            loadDataFromDB();
                            refreshAll();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                actionBox.getChildren().add(btnStart);
            } else if (order.status == OrderStatus.SERVING) {
                Button btnDone = new Button("Hoàn thành");
                btnDone.setStyle(
                        "-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 12px;");
                btnDone.setOnAction(e -> {
                    try {
                        DAO.HoaDonDAO hdDAO = new DAO.HoaDonDAO();
                        if (hdDAO.updateTrangThaiHoaDon(order.orderNumber, "Hoan Thanh")) {
                            loadDataFromDB();
                            refreshAll();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                actionBox.getChildren().add(btnDone);
            }

            if (order.status == OrderStatus.PENDING || order.status == OrderStatus.SERVING) {
                Button btnCancel = new Button("Hủy đơn");
                btnCancel.setStyle(
                        "-fx-background-color: #fef2f2; -fx-text-fill: #dc2626; -fx-border-color: #fecaca; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 12px;");
                btnCancel.setOnAction(e -> {
                    try {
                        DAO.HoaDonDAO hdDAO = new DAO.HoaDonDAO();
                        if (hdDAO.updateTrangThaiHoaDon(order.orderNumber, "Da Huy")) {
                            loadDataFromDB();
                            refreshAll();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                actionBox.getChildren().add(btnCancel);
            } else {
                Button btnDel = new Button("Xóa");
                btnDel.setStyle(
                        "-fx-background-color: transparent; -fx-text-fill: #9ca3af; -fx-cursor: hand; -fx-font-size: 12px;");
                btnDel.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa đơn hàng này?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        try {
                            DAO.HoaDonDAO hdDAO = new DAO.HoaDonDAO();
                            if (hdDAO.deleteHoaDon(order.orderNumber)) {
                                loadDataFromDB();
                                refreshAll();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                actionBox.getChildren().add(btnDel);
            }

            botRow.getChildren().addAll(linesPane, spacer2, actionBox);
            card.getChildren().addAll(topRow, botRow);
            vboxOrderList.getChildren().add(card);
        }
    }

    private String getStatusLabel(OrderStatus s) {
        switch (s) {
            case PENDING:
                return "Chờ phục vụ";
            case SERVING:
                return "Đang phục vụ";
            case DONE:
                return "Hoàn thành";
            case CANCELLED:
                return "Đã hủy";
            default:
                return "";
        }
    }

    private String getStatusStyle(OrderStatus s) {
        switch (s) {
            case PENDING:
                return "-fx-background-color: #fef3c7; -fx-text-fill: #b45309;";
            case SERVING:
                return "-fx-background-color: #dbeafe; -fx-text-fill: #1d4ed8;";
            case DONE:
                return "-fx-background-color: #dcfce7; -fx-text-fill: #15803d;";
            case CANCELLED:
                return "-fx-background-color: #fee2e2; -fx-text-fill: #b91c1c;";
            default:
                return "";
        }
    }

    // ================= CATALOG LOGIC =================

    private void refreshCatalog() {
        String search = txtSearchDV.getText().toLowerCase();
        List<ServiceItem> filtered = services.stream()
                .filter(s -> catFilter == null || s.category == catFilter)
                .filter(s -> search.isEmpty() || s.name.toLowerCase().contains(search))
                .collect(Collectors.toList());

        flowServices.getChildren().clear();
        for (ServiceItem s : filtered) {
            boolean isLow = s.stock <= s.minStock;

            VBox card = new VBox(8);
            card.setPrefWidth(240);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: "
                    + (isLow ? "#fca5a5" : "#e5e7eb")
                    + "; -fx-border-radius: 8; -fx-padding: 16; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 4, 0, 0, 2);");
            if (isLow) {
                card.setStyle(card.getStyle() + " -fx-border-width: 1 1 1 4;");
            }

            HBox top = new HBox();
            Label lblCat = new Label(getCatLabel(s.category));
            lblCat.setStyle(getCatStyle(s.category)
                    + " -fx-padding: 2 8; -fx-background-radius: 12; -fx-font-size: 10px; -fx-font-weight: bold;");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Button btnEdit = new Button("✎");
            btnEdit.setStyle("-fx-background-color: transparent; -fx-text-fill: #9ca3af; -fx-cursor: hand;");
            btnEdit.setOnAction(e -> openDVForm(s));
            Button btnDel = new Button("🗑");
            btnDel.setStyle("-fx-background-color: transparent; -fx-text-fill: #9ca3af; -fx-cursor: hand;");
            btnDel.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa dịch vụ này?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        DAO.DichVuDAO dao = new DAO.DichVuDAO();
                        if (dao.deletaDichVu(s.id)) {
                            loadDataFromDB();
                            refreshAll();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            top.getChildren().addAll(lblCat, spacer, btnEdit, btnDel);

            Label lblName = new Label(s.name);
            lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            Label lblPrice = new Label(formatCurrency(s.price) + "/" + s.unit);
            lblPrice.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");

            VBox stockBox = new VBox(4);
            stockBox.setStyle("-fx-border-color: #f3f4f6; -fx-border-width: 1 0 0 0; -fx-padding: 12 0 0 0;");
            HBox stockInfo = new HBox();
            Label lblLStock = new Label("Tồn kho");
            lblLStock.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");
            Region spacer2 = new Region();
            HBox.setHgrow(spacer2, Priority.ALWAYS);
            Label lblRStock = new Label((isLow ? "⚠ " : "") + s.stock + " " + s.unit);
            lblRStock.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: "
                    + (isLow ? "#dc2626" : "#374151") + ";");
            stockInfo.getChildren().addAll(lblLStock, spacer2, lblRStock);

            ProgressBar pBar = new ProgressBar();
            pBar.setProgress(Math.min(1.0, (double) s.stock / (s.minStock * 4)));
            pBar.setMaxWidth(Double.MAX_VALUE);
            pBar.setStyle("-fx-accent: " + (isLow ? "#f87171" : "#4ade80") + ";");

            stockBox.getChildren().addAll(stockInfo, pBar);

            Button btnRestock = new Button("Nhập thêm kho");
            btnRestock.setMaxWidth(Double.MAX_VALUE);
            btnRestock.setStyle(
                    "-fx-background-color: #f3f4f6; -fx-text-fill: #4b5563; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 11px;");
            btnRestock.setOnAction(e -> openRestockModal(s));

            card.getChildren().addAll(top, lblName, lblPrice, stockBox, btnRestock);
            flowServices.getChildren().add(card);
        }
    }

    private String getCatLabel(ServiceCategory c) {
        switch (c) {
            case DRINK:
                return "Đồ uống";
            case EQUIPMENT:
                return "Thiết bị";
            case FOOD:
                return "Đồ ăn";
            case OTHER:
                return "Khác";
            default:
                return "";
        }
    }

    private String getCatStyle(ServiceCategory c) {
        switch (c) {
            case DRINK:
                return "-fx-background-color: #dbeafe; -fx-text-fill: #1d4ed8;";
            case EQUIPMENT:
                return "-fx-background-color: #f3e8ff; -fx-text-fill: #7e22ce;";
            case FOOD:
                return "-fx-background-color: #ffedd5; -fx-text-fill: #c2410c;";
            case OTHER:
                return "-fx-background-color: #f3f4f6; -fx-text-fill: #374151;";
            default:
                return "";
        }
    }

    // ================= MODAL LOGIC =================

    /**
     * Hiển thị tiền tệ theo định dạng Việt Nam (với dấu chấm phân cách hàng nghìn và ký hiệu đ)
     */
    private String formatCurrency(long amount) {
        return currencyFormat.format(amount) + " đ";
    }

    private void openTaoDonModal() {
        txtOrderCustomer.clear();
        txtOrderNote.clear();
        currentOrderLines.clear();

        // ===== THAY ĐỔI: Nạp TẤT CẢ sân của hệ thống, không lọc theo trạng thái =====
        cbxOrderCourt.getItems().clear();
        try {
            DAO.DatSanDAO dsDAO = new DAO.DatSanDAO();
            ObservableList<Model.DATSAN> dsList = dsDAO.getallDatsans();
            for (Model.DATSAN ds : dsList) {
                String trangThai = ds.getTrangThai();
                // Chỉ hiển thị các sân đang hoạt động (chưa hoàn thành hoặc bị hủy)
                if (trangThai != null && (trangThai.equalsIgnoreCase("DaHuy") 
                        || trangThai.equalsIgnoreCase("Cancelled") 
                        || trangThai.equalsIgnoreCase("HoanThanh"))) {
                    continue; 
                }
                
                String tenSan = (ds.getTenSan() != null && !ds.getTenSan().isEmpty()) ? ds.getTenSan() : ds.getMaSan();
                String tenKH = (ds.getTenKH() != null && !ds.getTenKH().isEmpty()) ? ds.getTenKH() : "Khách";
                
                cbxOrderCourt.getItems().add(ds.getMaDS() + " - " + tenSan + " (" + tenKH + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Thêm option mua mang đi (không chọn sân cụ thể)
        cbxOrderCourt.getItems().add("--- Mua mang đi (không chọn sân) ---");
        
        cbxOrderCourt.getSelectionModel().selectFirst();

        renderTaoDonModal();
        overlayTaoDon.setVisible(true);
    }

    private void renderTaoDonModal() {
        // Render left list
        vboxDVChon.getChildren().clear();
        Label lblLeft = new Label("Chọn dịch vụ");
        lblLeft.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px; -fx-font-weight: bold;");
        vboxDVChon.getChildren().add(lblLeft);

        for (ServiceItem s : services) {
            HBox row = new HBox();
            row.setStyle(
                    "-fx-background-color: #f9fafb; -fx-padding: 8 12; -fx-background-radius: 8; -fx-cursor: hand;");
            VBox info = new VBox(2);
            Label name = new Label(s.name);
            name.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #374151;");
            Label desc = new Label(formatCurrency(s.price) + "/" + s.unit + " · còn " + s.stock);
            desc.setStyle("-fx-font-size: 11px; -fx-text-fill: #9ca3af;");
            info.getChildren().addAll(name, desc);
            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);
            Label plus = new Label("+");
            plus.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold; -fx-font-size: 18px;");
            row.getChildren().addAll(info, sp, plus);

            row.setOnMouseClicked(e -> {
                Optional<OrderLine> exist = currentOrderLines.stream()
                        .filter(l -> l.serviceId != null && l.serviceId.equals(s.id)).findFirst();
                if (exist.isPresent()) {
                    exist.get().quantity++;
                } else {
                    currentOrderLines.add(new OrderLine(s.id, s.name, 1, s.price));
                }
                renderTaoDonModal();
            });
            vboxDVChon.getChildren().add(row);
        }

        // Render right list
        vboxOrderLines.getChildren().clear();
        long total = 0;
        for (OrderLine line : currentOrderLines) {
            total += line.price * line.quantity;
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-background-color: #f9fafb; -fx-padding: 8; -fx-background-radius: 8;");
            Label name = new Label(line.serviceName);
            name.setStyle("-fx-font-size: 12px;");
            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);

            Button minus = new Button("-");
            minus.setStyle(
                    "-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 4; -fx-cursor: hand;");
            Label qty = new Label(String.valueOf(line.quantity));
            qty.setPrefWidth(24);
            qty.setAlignment(Pos.CENTER);
            Button plus = new Button("+");
            plus.setStyle(
                    "-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 4; -fx-cursor: hand;");

            minus.setOnAction(e -> {
                if (line.quantity > 1)
                    line.quantity--;
                else
                    currentOrderLines.remove(line);
                renderTaoDonModal();
            });
            plus.setOnAction(e -> {
                line.quantity++;
                renderTaoDonModal();
            });

            Label price = new Label(formatCurrency(line.price * line.quantity));
            price.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 12px;");
            price.setPrefWidth(80);
            price.setAlignment(Pos.CENTER_RIGHT);

            row.getChildren().addAll(name, sp, minus, qty, plus, price);
            vboxOrderLines.getChildren().add(row);
        }
        lblOrderTotal.setText(formatCurrency(total));
        btnXacNhanTaoDon.setDisable(currentOrderLines.isEmpty());
    }

    private void createOrder() {
        if (txtOrderCustomer.getText().trim().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING, "Vui lòng nhập tên khách hàng");
            a.show();
            return;
        }
        if (cbxOrderCourt.getValue() == null) {
            Alert a = new Alert(Alert.AlertType.WARNING, "Vui lòng chọn sân để đặt dịch vụ!");
            a.show();
            return;
        }
        if (currentOrderLines.isEmpty())
            return;

        int newId = orders.isEmpty() ? 1 : orders.stream().mapToInt(o -> o.id).max().getAsInt() + 1;
        String num = String.format("DV-%03d", newId);
        long total = currentOrderLines.stream().mapToLong(l -> l.price * l.quantity).sum();

        ServiceOrder newOrder = new ServiceOrder(
                newId, num, txtOrderCustomer.getText(), cbxOrderCourt.getValue(),
                new ArrayList<>(currentOrderLines), total, OrderStatus.PENDING,
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")), txtOrderNote.getText());
        orders.add(newOrder);

        // Tạo hóa đơn vào bảng HOADON TRƯỚC để lấy maHD
        DAO.HoaDonDAO hdDAO = new DAO.HoaDonDAO();
        String ghiChu = "Khách hàng: " + txtOrderCustomer.getText();
        if (!txtOrderNote.getText().trim().isEmpty()) {
            ghiChu += " - " + txtOrderNote.getText();
        }

        String maDSSelected = null;
        String cbxValue = cbxOrderCourt.getValue();
        if (cbxValue != null && !cbxValue.equals("--- Mua mang đi (không chọn sân) ---")) {
            maDSSelected = cbxValue.split(" - ")[0]; // Lấy Mã Đặt Sân từ "MADS - MaSan"
        }

        String generatedMaHD = hdDAO.addHoaDonDichVu(maDSSelected, total, ghiChu);

        // Nếu không chọn sân (Mua mang đi), dùng maHD làm MaDS để liên kết
        String finalMaDS = (maDSSelected != null) ? maDSSelected : generatedMaHD;

        // Cập nhật tồn kho và tạo Chi Tiết Dịch Vụ (CTDV)
        DAO.CTDVDAo ctdvDAO = null;
        try {
            ctdvDAO = new DAO.CTDVDAo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (OrderLine line : currentOrderLines) {
            final String fMaDS = finalMaDS;
            final DAO.CTDVDAo fCtdvDAO = ctdvDAO;

            services.stream().filter(s -> s.id != null && s.id.equals(line.serviceId)).findFirst().ifPresent(s -> {
                s.stock = Math.max(0, s.stock - line.quantity);

                // Cập nhật tồn kho vào DB
                DAO.DichVuDAO dao = null;
                try {
                    dao = new DAO.DichVuDAO();
                    Model.DichVu dv = dao.getDichVuByMa(s.id);
                    if (dv != null) {
                        dv.setSoLuongTon(s.stock);
                        dao.updateDichVu(dv);
                    }
                } catch (Exception ex) {
                } finally {
                    if (dao != null)
                        try {
                            dao.closeCon();
                        } catch (Exception ex) {
                        }
                }

                // Lưu vào CTDV
                if (fCtdvDAO != null) {
                    String maCTDV = "CTDV"
                            + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
                    Model.CTDV ctdv = new Model.CTDV(maCTDV, fMaDS, s.id, (long) line.quantity,
                            (long) line.price, generatedMaHD);
                    fCtdvDAO.addCTDV(ctdv);
                }
            });
        }
        if (ctdvDAO != null) {
            try {
                ctdvDAO.closeConnection();
            } catch (Exception e) {
            }
        }

        if (generatedMaHD != null) {
            System.out.println("Đã tự động tạo Hóa đơn dịch vụ!");
            // Ghi thông báo hệ thống để chuông Dashboard cập nhật
            try {
                String tenKhach = txtOrderCustomer.getText().trim();

                // Lấy danh sách tên dịch vụ để hiển thị
                String tenCacDichVu = currentOrderLines.stream()
                        .map(l -> l.serviceName + " (x" + l.quantity + ")")
                        .collect(java.util.stream.Collectors.joining(", "));

                String noiDung = "Đơn dịch vụ cho " + tenKhach
                        + " · " + tenCacDichVu
                        + " · Tổng: " + formatCurrency(total);
                // DAO.ThongBaoDAO.themThongBao("Đặt dịch vụ mới", noiDung, "info"); // Bỏ comment nếu bạn dùng Thông Báo
            } catch (Exception ex) {
                System.err.println("Không ghi được thông báo: " + ex.getMessage());
            }
        }

        overlayTaoDon.setVisible(false);
        // Fix: Sau khi tạo đơn phải gọi loadDataFromDB()
        loadDataFromDB();
        refreshAll();
        // EventBus.publish(EventBus.EVENT_SERVICE_ORDER_CREATED); // Bỏ comment nếu dùng EventBus
    }

    private void openDVForm(ServiceItem s) {
        editingService = s;
        if (s == null) {
            lblDVFormTitle.setText("Thêm dịch vụ mới");
            btnLuuDV.setText("Thêm dịch vụ");
            txtDVName.clear();
            cbxDVCategory.getSelectionModel().selectFirst();
            txtDVUnit.clear();
            txtDVPrice.setText("0");
            txtDVStock.setText("0");
            txtDVMinStock.setText("5");
        } else {
            lblDVFormTitle.setText("Sửa dịch vụ");
            btnLuuDV.setText("Lưu thay đổi");
            txtDVName.setText(s.name);
            cbxDVCategory.setValue(getCatString(s.category));
            txtDVUnit.setText(s.unit);
            txtDVPrice.setText(String.valueOf(s.price));
            txtDVStock.setText(String.valueOf(s.stock));
            txtDVMinStock.setText(String.valueOf(s.minStock));
        }
        overlayDichVuForm.setVisible(true);
    }

    private void saveService() {
        if (txtDVName.getText().trim().isEmpty() || txtDVUnit.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng nhập đủ tên và đơn vị").show();
            return;
        }

        String name = txtDVName.getText();
        ServiceCategory cat = getCatEnum(cbxDVCategory.getValue());
        String unit = txtDVUnit.getText();
        long price = 0;
        int stock = 0, minStock = 5;
        try {
            price = Long.parseLong(txtDVPrice.getText());
        } catch (Exception e) {
        }
        try {
            stock = Integer.parseInt(txtDVStock.getText());
        } catch (Exception e) {
        }
        try {
            minStock = Integer.parseInt(txtDVMinStock.getText());
        } catch (Exception e) {
        }
        boolean isSuccess = false;
        if (editingService == null) {
            int newNum = services.size() + 1;
            String newId = String.format("DV%03d", newNum);

            DAO.DichVuDAO dao = null;
            try {
                dao = new DAO.DichVuDAO();
                Model.DichVu dv = new Model.DichVu(newId, name, getCatString(cat), price, stock, "Hoạt động");
                isSuccess = dao.addDichVu(dv);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (dao != null)
                    try {
                        dao.closeCon();
                    } catch (Exception ex) {
                    }
            }

        } else {
            DAO.DichVuDAO dao = null;
            try {
                dao = new DAO.DichVuDAO();
                Model.DichVu dv = new Model.DichVu(editingService.id, name, getCatString(cat), price, stock,
                        "Hoạt động");
                isSuccess = dao.updateDichVu(dv);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (dao != null)
                    try {
                        dao.closeCon();
                    } catch (Exception ex) {
                    }
            }
        }
        
        // Fix: Chỉnh sửa chữ có dấu tiếng Việt
        if (isSuccess) {
            overlayDichVuForm.setVisible(false);
            loadDataFromDB();
            refreshAll();
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Đã lưu dịch vụ thành công!");
            a.show();
        } else {
            Alert a = new Alert(Alert.AlertType.ERROR, "Lỗi khi lưu vào cơ sở dữ liệu!");
            a.show();
        }
    }

    private String getCatString(ServiceCategory c) {
        switch (c) {
            case DRINK:
                return "Đồ uống";
            case EQUIPMENT:
                return "Thiết bị";
            case FOOD:
                return "Đồ ăn";
            case OTHER:
                return "Khác";
            default:
                return "Đồ uống";
        }
    }

    private ServiceCategory getCatEnum(String s) {
        switch (s) {
            case "Đồ uống":
                return ServiceCategory.DRINK;
            case "Thiết bị":
                return ServiceCategory.EQUIPMENT;
            case "Đồ ăn":
                return ServiceCategory.FOOD;
            case "Khác":
                return ServiceCategory.OTHER;
            default:
                return ServiceCategory.DRINK;
        }
    }

    private void openRestockModal(ServiceItem s) {
        restockTarget = s;
        lblRestockName.setText(s.name);
        lblRestockCurrent.setText("Tồn kho hiện tại: " + s.stock + " " + s.unit);
        txtRestockQty.setText(String.valueOf(s.minStock * 2));
        overlayRestock.setVisible(true);
    }

    private void confirmRestock() {
        if (restockTarget == null)
            return;
        try {
            int qty = Integer.parseInt(txtRestockQty.getText());
            if (qty > 0) {
                restockTarget.stock += qty;
                // Cập nhật tồn kho vào DB
                DAO.DichVuDAO dao = null;
                try {
                    dao = new DAO.DichVuDAO();
                    Model.DichVu dv = dao.getDichVuByMa(restockTarget.id);
                    if (dv != null) {
                        dv.setSoLuongTon(restockTarget.stock);
                        dao.updateDichVu(dv);
                    }
                } catch (Exception ex) {
                } finally {
                    if (dao != null)
                        try {
                            dao.closeCon();
                        } catch (Exception ex) {
                        }
                }
            }
        } catch (Exception e) {
        }
        overlayRestock.setVisible(false);
        // Fix: Sau khi nhập kho phải gọi loadDataFromDB()
        loadDataFromDB();
        refreshAll();
    }
}