package Controller;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import Model.SAN; 
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class QuanLySanController implements Initializable {

    @FXML private TextField txtTimKiem;
    @FXML private Button btnThemSan; 
    @FXML private TableView<SanModel> tableSan;
    @FXML private TableColumn<SanModel, String> colMaSan;
    @FXML private TableColumn<SanModel, String> colTenSan;
    @FXML private TableColumn<SanModel, String> colLoaiSan;
    @FXML private TableColumn<SanModel, Number> colGiaThue;
    @FXML private TableColumn<SanModel, String> colTrangThai;

    private ObservableList<SanModel> masterData;
    private FilteredList<SanModel> filteredData;

    private final NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadData();
        setupTableColumns();
        setupSearch();
    }

    private void loadData() {
        ObservableList<SAN> dsSanTuDB = SAN.getDanhSachSanTuDB();
        masterData = FXCollections.observableArrayList();
        for (SAN s : dsSanTuDB) {
            masterData.add(new SanModel(s.MaSan, s.TenSan, s.LoaiSan, s.GiaThueTheoGio, s.TrangThai));
        }
        filteredData = new FilteredList<>(masterData, p -> true);
        SortedList<SanModel> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableSan.comparatorProperty());
        tableSan.setItems(sortedData);
    }

    private void setupTableColumns() {
        colMaSan.setCellValueFactory(cell -> cell.getValue().maSanProperty());
        colTenSan.setCellValueFactory(cell -> cell.getValue().tenSanProperty());
        colLoaiSan.setCellValueFactory(cell -> cell.getValue().loaiSanProperty());
        colGiaThue.setCellValueFactory(cell -> cell.getValue().giaThueProperty());
        colTrangThai.setCellValueFactory(cell -> cell.getValue().trangThaiProperty());

        // Cột Giá Thuê
        colGiaThue.setCellFactory(column -> new TableCell<SanModel, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(item) + " đ");
                    setAlignment(Pos.CENTER_RIGHT);
                }
            }
        });

        // Cột Trạng Thái
        colTrangThai.setCellFactory(column -> new TableCell<SanModel, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label lbl = new Label(item);
                    lbl.setAlignment(Pos.CENTER);
                    lbl.setMinWidth(90);
                    if ("Trống".equals(item)) lbl.setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #16a34a; -fx-padding: 4 8; -fx-background-radius: 6;");
                    else if ("Đang sử dụng".equals(item)) lbl.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-padding: 4 8; -fx-background-radius: 6;");
                    setGraphic(lbl);
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    private void setupSearch() {
        txtTimKiem.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(san -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filter = newVal.toLowerCase();
                return san.getTenSan().toLowerCase().contains(filter) || san.getMaSan().toLowerCase().contains(filter);
            });
        });
    }

    @FXML
    private void handleAddSan() {
    try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ThemSan.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Thêm Sân Mới");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.showAndWait();
            loadData(); 

        } catch (Exception e) {
            System.out.println("Lỗi mở form Thêm Sân: " + e.getMessage());
            e.printStackTrace();
        }   
    }
    public static class SanModel {
        private final StringProperty maSan = new SimpleStringProperty();
        private final StringProperty tenSan = new SimpleStringProperty();
        private final StringProperty loaiSan = new SimpleStringProperty();
        private final LongProperty giaThue = new SimpleLongProperty();
        private final StringProperty trangThai = new SimpleStringProperty();

        public SanModel(String ma, String ten, String loai, long gia, String tt) {
            this.maSan.set(ma);
            this.tenSan.set(ten);
            this.loaiSan.set(loai);
            this.giaThue.set(gia);
            this.trangThai.set(tt);
        }

        public StringProperty maSanProperty() { return maSan; }
        public String getMaSan() { return maSan.get(); }
        public StringProperty tenSanProperty() { return tenSan; }
        public String getTenSan() { return tenSan.get(); }
        public StringProperty loaiSanProperty() { return loaiSan; }
        public LongProperty giaThueProperty() { return giaThue; }
        public StringProperty trangThaiProperty() { return trangThai; }
    }
        private void setupTableEvents() {
        tableSan.setRowFactory(tv -> {
            TableRow<SanModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    SanModel rowData = row.getItem();
                    System.out.println("Đang mở chi tiết sân: " + rowData.getTenSan());

                    // Gợi ý logic tiếp theo:
                    // Mở một form mới (SuaSan.fxml) và truyền đối tượng rowData sang form đó
                }
            });
            return row;
        });
    }
}