package Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import Model.KHACHHANG;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class CustomerManagementController implements Initializable {

    // ================== CONSTANTS ==================
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_REGEX = "^[0-9]{10,11}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);
    
    private static final String STATUS_PLATINUM = "BachKim";
    private static final String STATUS_GOLD = "Vang";
    private static final String STATUS_SILVER = "Bac";
    private static final String STATUS_NEW = "Dong";

    // ================== FXML INJECTIONS ==================
    @FXML private Button btnAddCustomer;
    @FXML private Button btnDeleteSelected;
    @FXML private Button btnCancelSelection;
    @FXML private TextField txtSearch;
    @FXML private TableView<CustomerModel> tableCustomers;
    @FXML private Label lblTotalCount;
    @FXML private HBox selectedCountBox;
    @FXML private Label lblSelectedCount;

    @FXML private TableColumn<CustomerModel, Boolean> colCheckbox;
    @FXML private TableColumn<CustomerModel, String> colId;
    @FXML private TableColumn<CustomerModel, String> colName;
    @FXML private TableColumn<CustomerModel, String> colPhone;
    @FXML private TableColumn<CustomerModel, String> colEmail;
    @FXML private TableColumn<CustomerModel, String> colAddress;
    @FXML private TableColumn<CustomerModel, Number> colTotalBookings;
    @FXML private TableColumn<CustomerModel, String> colStatus;
    @FXML private TableColumn<CustomerModel, Void> colActions;

    @FXML private VBox addCustomerModal;
    @FXML private TextField txtAddName;
    @FXML private TextField txtAddPhone;
    @FXML private TextField txtAddEmail;
    @FXML private TextField txtAddAddress;
    @FXML private Button btnCancelAdd;
    @FXML private Button btnConfirmAdd;

    // ================== DATA ==================
    private ObservableList<CustomerModel> masterDataList = FXCollections.observableArrayList();
    private FilteredList<CustomerModel> filteredData;
    private int nextId = 6;
    private CustomerModel editingCustomer = null;
    private CheckBox selectAllCheckBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            initMockData();
            setupTable();
            setupSearch();
            setupEventHandlers();
            updateFooterCounts();
        } catch (Exception e) {
            showError("Lỗi khởi tạo ứng dụng", "Có lỗi xảy ra khi tải dữ liệu: " + e.getMessage());
            System.err.println("Initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }
        private void addNewCustomer(String name, String phone, String email, String address) {
        String newId = String.format("KH%03d", nextId++);
        KHACHHANG khDb = new KHACHHANG(newId, name, phone, email, address, STATUS_NEW, 0);
        if (DAO.KhachHangDAO.themKhachHangMoi(khDb)) {
            CustomerModel newCustomerUI = new CustomerModel(newId, name, phone, email, address, 0, STATUS_NEW);
            masterDataList.add(newCustomerUI);
            closeModal();
            updateFooterCounts();
            showSuccess("Thành công", "Thêm khách hàng thành công!");
            Utils.NotificationHelper.themKhachHang(newId, name, phone);
        } else {
            showError("Lỗi", "Lỗi khi lưu vào cơ sở dữ liệu!");
        }
    }
    private void initMockData() {
        try {
            ObservableList<KHACHHANG> dbData = DAO.KhachHangDAO.getDanhSachKhachHang();
            
            if (dbData == null) {
                System.err.println("DAO returned null, initializing empty list");
                masterDataList = FXCollections.observableArrayList();
            } else {
                for (KHACHHANG kh : dbData) {
                    CustomerModel customerUI = new CustomerModel(
                            kh.getMaKH(),
                            kh.getHoTen(),
                            kh.getSDT(),
                            kh.getEmail(),
                            kh.getDiaChi(),
                            kh.getDiemTichLuy(),  // Tương ứng với totalBookings trên UI
                            kh.getHangThanhVien()
                    );
                    masterDataList.add(customerUI);
                }
                // Initialize nextId based on highest existing ID
                initializeNextId();
            }

            filteredData = new FilteredList<>(masterDataList, p -> true);
            SortedList<CustomerModel> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tableCustomers.comparatorProperty());
            tableCustomers.setItems(sortedData);
        } catch (Exception e) {
            System.err.println("Error loading customer data: " + e.getMessage());
            throw new RuntimeException("Failed to load customer data", e);
        }
    }

    /**
     * Initialize nextId based on the highest ID in the database
     */
    private void initializeNextId() {
        try {
            int maxId = masterDataList.stream()
                    .map(c -> extractIdNumber(c.getId()))
                    .max(Integer::compare)
                    .orElse(5);
            nextId = maxId + 1;
        } catch (Exception e) {
            System.err.println("Error initializing next ID: " + e.getMessage());
            nextId = 6; // Fallback value
        }
    }

    /**
     * Extract numeric part from ID (e.g., "KH005" -> 5)
     */
    private int extractIdNumber(String id) {
        if (id == null || id.length() < 3) return 0;
        try {
            return Integer.parseInt(id.substring(2));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void setupTable() {
        // Standard columns
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colPhone.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        colEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        colAddress.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        colTotalBookings.setCellValueFactory(cellData -> cellData.getValue().totalBookingsProperty());

        // Status column with color coding
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        colStatus.setCellFactory(column -> new TableCell<CustomerModel, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label lbl = new Label(item);
                    lbl.setStyle(getStatusStyle(item));
                    setGraphic(lbl);
                }
            }
        });

        // Checkbox column
        colCheckbox.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colCheckbox.setCellFactory(column -> new TableCell<CustomerModel, Boolean>() {
            private final CheckBox checkBox = new CheckBox();
            {
                setAlignment(Pos.CENTER);
                checkBox.setOnAction(e -> {
                    try {
                        CustomerModel customer = getTableView().getItems().get(getIndex());
                        if (customer != null) {
                            customer.setSelected(checkBox.isSelected());
                            updateSelectAllCheckbox();
                            updateFooterCounts();
                        }
                    } catch (IndexOutOfBoundsException ex) {
                        System.err.println("Index out of bounds: " + ex.getMessage());
                    }
                });
            }
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item);
                    setGraphic(checkBox);
                }
            }
        });

        // Select All checkbox
        selectAllCheckBox = new CheckBox();
        selectAllCheckBox.setOnAction(e -> {
            boolean isSelected = selectAllCheckBox.isSelected();
            for (CustomerModel customer : filteredData) {
                customer.setSelected(isSelected);
            }
            updateFooterCounts();
        });
        colCheckbox.setGraphic(selectAllCheckBox);

        // Actions column
        colActions.setCellFactory(column -> new TableCell<CustomerModel, Void>() {
            private final Button btnEdit = new Button("Sửa");
            private final Button btnDelete = new Button();
            private final HBox pane = new HBox(8, btnEdit, btnDelete);

            {
                btnEdit.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 8; -fx-cursor: hand; -fx-background-radius: 4;");
                btnDelete.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 4;");
                
                SVGPath trashIcon = new SVGPath();
                trashIcon.setContent("M3 6h18M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2M10 11v6M14 11v6");
                trashIcon.setStroke(Color.web("#ef4444"));
                trashIcon.setStrokeWidth(2);
                trashIcon.setStrokeLineCap(StrokeLineCap.ROUND);
                trashIcon.setStrokeLineJoin(StrokeLineJoin.ROUND);
                trashIcon.setFill(Color.TRANSPARENT);
                trashIcon.setScaleX(0.6);
                trashIcon.setScaleY(0.6);
                btnDelete.setGraphic(trashIcon);

                btnDelete.setOnAction(e -> deleteSingleCustomer());
                btnEdit.setOnAction(e -> editCustomer());
                pane.setAlignment(Pos.CENTER);
            }

            private void deleteSingleCustomer() {
                try {
                    int index = getIndex();
                    if (index < 0 || index >= getTableView().getItems().size()) {
                        return;
                    }
                    CustomerModel data = getTableView().getItems().get(index);
                    if (data == null) return;

                    if (showConfirmation("Xác nhận xóa", 
                        "Bạn có chắc chắn muốn xóa khách hàng " + data.getName() + " không?")) {
                        
                        List<String> idToDelete = List.of(data.getId());
                        if (DAO.KhachHangDAO.xoaKhachHang(idToDelete)) {
                            masterDataList.remove(data);
                            updateSelectAllCheckbox();
                            updateFooterCounts();
                        } else {
                            showError("Lỗi xóa", "Không thể xóa khách hàng: " + data.getId());
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Error deleting customer: " + ex.getMessage());
                    showError("Lỗi", "Có lỗi xảy ra khi xóa khách hàng");
                }
            }

            private void editCustomer() {
                try {
                    int index = getIndex();
                    if (index < 0 || index >= getTableView().getItems().size()) {
                        return;
                    }
                    CustomerModel data = getTableView().getItems().get(index);
                    if (data == null) return;

                    editingCustomer = data;
                    txtAddName.setText(data.getName());
                    txtAddPhone.setText(data.getPhone());
                    txtAddEmail.setText(data.getEmail());
                    txtAddAddress.setText(data.getAddress());
                    
                    btnConfirmAdd.setText("Lưu thay đổi");
                    addCustomerModal.setVisible(true);
                } catch (Exception ex) {
                    System.err.println("Error editing customer: " + ex.getMessage());
                    showError("Lỗi", "Có lỗi xảy ra khi chỉnh sửa khách hàng");
                }
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private String getStatusStyle(String status) {
        if (status == null) {
            return "-fx-background-color: #f3f4f6; -fx-text-fill: #374151; -fx-padding: 4 8; -fx-background-radius: 4; -fx-font-size: 12px;";
        }
        
        if (status.equals(STATUS_PLATINUM)) {
            return "-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-padding: 4 8; -fx-background-radius: 4; -fx-font-size: 12px; -fx-font-weight: bold;";
        } else if (status.equals(STATUS_GOLD)) {
            return "-fx-background-color: #fef9c3; -fx-text-fill: #ca8a04; -fx-padding: 4 8; -fx-background-radius: 4; -fx-font-size: 12px; -fx-font-weight: bold;";
        } else if (status.equals(STATUS_SILVER)) {
            return "-fx-background-color: #dbeafe; -fx-text-fill: #2563eb; -fx-padding: 4 8; -fx-background-radius: 4; -fx-font-size: 12px;";
        } else {
            return "-fx-background-color: #f3f4f6; -fx-text-fill: #374151; -fx-padding: 4 8; -fx-background-radius: 4; -fx-font-size: 12px;";
        }
    }

    private void setupSearch() {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(customer -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                
                // Null-safe checks
                String name = customer.getName();
                String phone = customer.getPhone();
                String email = customer.getEmail();
                
                return (name != null && name.toLowerCase().contains(lowerCaseFilter)) ||
                       (phone != null && phone.contains(lowerCaseFilter)) ||
                       (email != null && email.toLowerCase().contains(lowerCaseFilter));
            });
            updateSelectAllCheckbox();
            updateFooterCounts();
        });
    }

    private void setupEventHandlers() {
        btnAddCustomer.setOnAction(e -> openAddCustomerModal());
        btnCancelAdd.setOnAction(e -> closeModal());
        btnConfirmAdd.setOnAction(e -> handleConfirmAdd());
        btnDeleteSelected.setOnAction(e -> deleteSelectedCustomers());
        btnCancelSelection.setOnAction(e -> cancelSelection());
    }

    private void openAddCustomerModal() {
        editingCustomer = null;
        txtAddName.clear();
        txtAddPhone.clear();
        txtAddEmail.clear();
        txtAddAddress.clear();
        btnConfirmAdd.setText("Xác nhận");
        addCustomerModal.setVisible(true);
    }

    private void closeModal() {
        addCustomerModal.setVisible(false);
    }

    private void handleConfirmAdd() {
        // Input validation
        String name = txtAddName.getText().trim();
        String phone = txtAddPhone.getText().trim();
        String email = txtAddEmail.getText().trim();
        String address = txtAddAddress.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            showWarning("Lỗi nhập liệu", "Vui lòng nhập tên và số điện thoại!");
            return;
        }

        if (!isValidPhoneNumber(phone)) {
            showWarning("Lỗi nhập liệu", "Số điện thoại phải có 10-11 chữ số!");
            return;
        }

        if (!email.isEmpty() && !isValidEmail(email)) {
            showWarning("Lỗi nhập liệu", "Email không hợp lệ!");
            return;
        }

        try {
            if (editingCustomer == null) {
                // Add new customer
                addNewCustomer(name, phone, email, address);
            } else {
                // Update existing customer
                updateExistingCustomer(name, phone, email, address);
            }
        } catch (Exception e) {
            System.err.println("Error in handleConfirmAdd: " + e.getMessage());
            showError("Lỗi", "Có lỗi xảy ra khi xử lý yêu cầu");
        }
    }

//    private void addNewCustomer(String name, String phone, String email, String address) {
//        String newId = String.format("KH%03d", nextId++);
//        CustomerModel newCustomer = new CustomerModel(newId, name, phone, email, address, 0, STATUS_NEW);
//        
//        if (DAO.KhachHangDAO.themKhachHangMoi(newCustomer)) {
//            masterDataList.add(newCustomer);
//            closeModal();
//            updateFooterCounts();
//            showSuccess("Thành công", "Thêm khách hàng thành công!");
//        } else {
//            showError("Lỗi", "Lỗi khi lưu vào cơ sở dữ liệu!");
//        }
//    }

    private void updateExistingCustomer(String name, String phone, String email, String address) {
        KHACHHANG khDb = new KHACHHANG(
                editingCustomer.getId(),
                name, phone, email, address,
                editingCustomer.getStatus(),
                editingCustomer.getTotalBookings()
        );

    if (DAO.KhachHangDAO.capNhatKhachHang(khDb)) {
        editingCustomer.nameProperty().set(name);
        editingCustomer.phoneProperty().set(phone);
        editingCustomer.emailProperty().set(email);
        editingCustomer.addressProperty().set(address);
        closeModal();
        showSuccess("Thành công", "Cập nhật thành công!");
        Utils.NotificationHelper.capNhatKhachHang(editingCustomer.getId(), name);
    } else {
        showError("Lỗi", "Lỗi khi cập nhật vào cơ sở dữ liệu!");
    }
}

    private void deleteSelectedCustomers() {
        if (showConfirmation("Xác nhận xóa",
            "Bạn có chắc chắn muốn xóa những khách hàng đã chọn không?")) {

            List<CustomerModel> toDelete = filteredData.stream()
                    .filter(CustomerModel::isSelected)
                    .collect(Collectors.toList());

            if (toDelete.isEmpty()) {
                showWarning("Cảnh báo", "Chưa chọn khách hàng nào!");
                return;
            }

            List<String> listIdsToDelete = toDelete.stream()
                    .map(CustomerModel::getId)
                    .collect(Collectors.toList());

            if (DAO.KhachHangDAO.xoaKhachHang(listIdsToDelete)) {
                toDelete.forEach(kh ->
                    Utils.NotificationHelper.xoaKhachHang(kh.getName(), kh.getId())
                );
                masterDataList.removeAll(toDelete);
                updateSelectAllCheckbox();
                updateFooterCounts();
                showSuccess("Thành công", "Xóa thành công!");
            } else {
                showError("Lỗi", "Lỗi khi xóa khách hàng khỏi cơ sở dữ liệu!");
            }
        }
    }

    private void cancelSelection() {
        for (CustomerModel customer : masterDataList) {
            customer.setSelected(false);
        }
        updateSelectAllCheckbox();
        updateFooterCounts();
    }

    /**
     * Update "Select All" checkbox state based on individual selections
     */
    private void updateSelectAllCheckbox() {
        if (selectAllCheckBox == null) return;
        
        long selectedCount = filteredData.stream()
                .filter(CustomerModel::isSelected)
                .count();
        
        if (selectedCount == 0) {
            selectAllCheckBox.setSelected(false);
            selectAllCheckBox.setIndeterminate(false);
        } else if (selectedCount == filteredData.size()) {
            selectAllCheckBox.setSelected(true);
            selectAllCheckBox.setIndeterminate(false);
        } else {
            selectAllCheckBox.setIndeterminate(true);
        }
    }

    private void updateFooterCounts() {
        lblTotalCount.setText(String.valueOf(filteredData.size()));
        
        long selectedCount = filteredData.stream()
                .filter(CustomerModel::isSelected)
                .count();
        
        boolean hasSelection = selectedCount > 0;
        
        lblSelectedCount.setText(String.valueOf(selectedCount));
        selectedCountBox.setVisible(hasSelection);
        selectedCountBox.setManaged(hasSelection);
        
        btnDeleteSelected.setText("Xóa (" + selectedCount + ")");
        btnDeleteSelected.setVisible(hasSelection);
        btnDeleteSelected.setManaged(hasSelection);
        btnCancelSelection.setVisible(hasSelection);
        btnCancelSelection.setManaged(hasSelection);
        btnAddCustomer.setVisible(!hasSelection);
        btnAddCustomer.setManaged(!hasSelection);
    }

    // ================== VALIDATION METHODS ==================
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    // ================== HELPER METHODS ==================
    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.setHeaderText(null);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private void showSuccess(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    private void showWarning(String title, String message) {
        showAlert(Alert.AlertType.WARNING, title, message);
    }

    private void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    // ================== MODEL CLASS ==================
    public static class CustomerModel {
        private final StringProperty id;
        private final StringProperty name;
        private final StringProperty phone;
        private final StringProperty email;
        private final StringProperty address;
        private final IntegerProperty totalBookings;
        private final StringProperty status;
        private final BooleanProperty selected;

        public CustomerModel(String id, String name, String phone, String email, String address, int totalBookings, String status) {
            this.id = new SimpleStringProperty(id);
            this.name = new SimpleStringProperty(name);
            this.phone = new SimpleStringProperty(phone);
            this.email = new SimpleStringProperty(email);
            this.address = new SimpleStringProperty(address);
            this.totalBookings = new SimpleIntegerProperty(totalBookings);
            this.status = new SimpleStringProperty(status);
            this.selected = new SimpleBooleanProperty(false);
        }

        public String getId() { return id.get(); }
        public String getName() { return name.get(); }
        public String getPhone() { return phone.get(); }
        public String getEmail() { return email.get(); }
        public String getAddress() { return address.get(); }
        public int getTotalBookings() { return totalBookings.get(); }
        public String getStatus() { return status.get(); }
        public boolean isSelected() { return selected.get(); }

        public void setSelected(boolean sel) { this.selected.set(sel); }

        public StringProperty idProperty() { return id; }
        public StringProperty nameProperty() { return name; }
        public StringProperty phoneProperty() { return phone; }
        public StringProperty emailProperty() { return email; }
        public StringProperty addressProperty() { return address; }
        public IntegerProperty totalBookingsProperty() { return totalBookings; }
        public StringProperty statusProperty() { return status; }
        public BooleanProperty selectedProperty() { return selected; }
    }
}