package Controller;

import DAO.ThanhToanDAO;
import DAO.HoaDonDAO;
import Model.ThanhToan;
import Model.HOADON;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class ThanhToanController implements Initializable {

    @FXML private TableView<ThanhToan> tableThanhToan;
    @FXML private TableColumn<ThanhToan, String> colMaTT;
    @FXML private TableColumn<ThanhToan, String> colMaHD;
    @FXML private TableColumn<ThanhToan, String> colPTTT;
    @FXML private TableColumn<ThanhToan, Long> colSoTien;
    @FXML private TableColumn<ThanhToan, Object> colThoiGian;
    @FXML private TableColumn<ThanhToan, String> colTrangThai;
    @FXML private TableColumn<ThanhToan, Void> colThaoTac;
    @FXML private TextField tfSearch;
    @FXML private ComboBox<String> cbTrangThai;
    @FXML private Label lblTongGD;
    @FXML private Label lblDaThanhToan;
    @FXML private Label lblChoThanhToan;
    @FXML private Label lblTongThu;

    private ThanhToanDAO thanhToanDAO;
    private ObservableList<ThanhToan> danhSach;
    private FilteredList<ThanhToan> filteredList;
    private NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumns();
        setupComboBox();
        try {
            thanhToanDAO = new ThanhToanDAO();
            loadDuLieu();
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối: " + e.getMessage());
        }
    }

    private void setupComboBox() {
        cbTrangThai.setItems(FXCollections.observableArrayList(
            "Tất cả", "Đã thanh toán", "Chờ thanh toán", "Đã hoàn tiền"
        ));
        cbTrangThai.setValue("Tất cả");
    }

    private void setupColumns() {
        colMaTT.setCellValueFactory(new PropertyValueFactory<>("maTT"));
        colMaHD.setCellValueFactory(new PropertyValueFactory<>("maHoaDon"));
        colPTTT.setCellValueFactory(new PropertyValueFactory<>("pttt"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));

        // Format số tiền
        colSoTien.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatter.format(item) + " ₫");
            }
        });
        colSoTien.setCellValueFactory(new PropertyValueFactory<>("soTien"));

        // Format thời gian
        colThoiGian.setCellValueFactory(new PropertyValueFactory<>("thoiGianTT"));
        colThoiGian.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(sdf.format(item));
            }
        });

        // Màu trạng thái
        colTrangThai.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                if (item.contains("Đã thanh toán")) {
                    setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
                } else if (item.contains("Chờ")) {
                    setStyle("-fx-text-fill: #d97706; -fx-font-weight: bold;");
                } else if (item.contains("hoàn")) {
                    setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
                } else {
                    setStyle("");
                }
            }
        });

        // Cột thao tác
        colThaoTac.setCellFactory(col -> new TableCell<>() {
            private final Button btnXem = new Button("Xem");
            private final Button btnXacNhan = new Button("✓");
            private final HBox box = new HBox(4, btnXem, btnXacNhan);
            {
                btnXem.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 4; -fx-cursor: hand;");
                btnXacNhan.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 4; -fx-cursor: hand;");
                btnXem.setOnAction(e -> showChiTiet(getTableView().getItems().get(getIndex())));
                btnXacNhan.setOnAction(e -> xacNhanThanhToan(getTableView().getItems().get(getIndex())));
            }
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    public void loadDuLieu() {
        if (thanhToanDAO == null) return;
        danhSach = thanhToanDAO.getAllThanhToan();
        filteredList = new FilteredList<>(danhSach, p -> true);
        tableThanhToan.setItems(filteredList);
        capNhatCards();
    }

    private void capNhatCards() {
        if (danhSach == null) return;
        long tongGD = danhSach.size();
        long daThanhToan = danhSach.stream().filter(t -> t.getTrangThai() != null && t.getTrangThai().contains("Đã thanh toán")).count();
        long choThanhToan = danhSach.stream().filter(t -> t.getTrangThai() != null && t.getTrangThai().contains("Chờ")).count();
        long tongThu = danhSach.stream().filter(t -> t.getTrangThai() != null && t.getTrangThai().contains("Đã thanh toán")).mapToLong(ThanhToan::getSoTien).sum();

        lblTongGD.setText(String.valueOf(tongGD));
        lblDaThanhToan.setText(String.valueOf(daThanhToan));
        lblChoThanhToan.setText(String.valueOf(choThanhToan));
        lblTongThu.setText(formatter.format(tongThu) + " ₫");
    }

    @FXML
    private void onSearch() {
        String keyword = tfSearch.getText().toLowerCase().trim();
        filteredList.setPredicate(tt -> {
            if (keyword.isEmpty()) return true;
            return (tt.getMaTT() != null && tt.getMaTT().toLowerCase().contains(keyword))
                || (tt.getMaHoaDon() != null && tt.getMaHoaDon().toLowerCase().contains(keyword));
        });
    }

    @FXML
    private void onLoc() {
        String trangThai = cbTrangThai.getValue();
        String keyword = tfSearch.getText().toLowerCase().trim();
        filteredList.setPredicate(tt -> {
            boolean matchTT = "Tất cả".equals(trangThai) || (tt.getTrangThai() != null && tt.getTrangThai().contains(trangThai));
            boolean matchSearch = keyword.isEmpty()
                || (tt.getMaTT() != null && tt.getMaTT().toLowerCase().contains(keyword))
                || (tt.getMaHoaDon() != null && tt.getMaHoaDon().toLowerCase().contains(keyword));
            return matchTT && matchSearch;
        });
    }

    @FXML
    private void onXemTat() {
        tfSearch.clear();
        cbTrangThai.setValue("Tất cả");
        filteredList.setPredicate(p -> true);
    }

    @FXML
    private void onTaoThanhToan() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/TaoThanhToanUI.fxml"));
            Parent root = loader.load();
            TaoThanhToanController ctrl = loader.getController();
            ctrl.setOnSaveCallback(this::loadDuLieu);
            Stage stage = new Stage();
            stage.setTitle("Tạo Thanh Toán");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            showAlert("Lỗi mở form: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showChiTiet(ThanhToan tt) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi Tiết Thanh Toán");
        alert.setHeaderText("Mã GD: " + tt.getMaTT());
        alert.setContentText(
            "Mã hóa đơn: " + tt.getMaHoaDon() + "\n" +
            "Phương thức: " + tt.getPttt() + "\n" +
            "Số tiền: " + formatter.format(tt.getSoTien()) + " ₫\n" +
            "Thời gian: " + (tt.getThoiGianTT() != null ? sdf.format(tt.getThoiGianTT()) : "") + "\n" +
            "Trạng thái: " + tt.getTrangThai()
        );
        alert.showAndWait();
    }

    private void xacNhanThanhToan(ThanhToan tt) {
        if (thanhToanDAO == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText(null);
        confirm.setContentText("Xác nhận thanh toán cho mã GD: " + tt.getMaTT() + "?");
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                boolean ok = thanhToanDAO.updateTrangThaiThanhToan(tt.getMaTT(), "Đã thanh toán");
                if (ok) {
                    showAlert("Xác nhận thành công!", Alert.AlertType.INFORMATION);
                    loadDuLieu();
                } else {
                    showAlert("Xác nhận thất bại!", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}