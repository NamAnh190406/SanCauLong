package Controller;
import java.time.LocalDate;
import java.util.ArrayList;

import Model.SAN;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ThemDatSanForm extends Stage {

    private TextField txtCustomerName;
    private TextField txtCustomerPhone;
    private TextField txtCourt;
    private TextField txtMaDat;
    private ComboBox<String> cboSan;
    private DatePicker dpNgayDat;
    private ComboBox<String> cboCa;
    private TextField txtTienCoc;
    private TextArea txtGhiChu;
    private Button btnLuu;
    private Button btnHuy;

    public ThemDatSanForm() {
        this.setTitle("Thêm Đặt Sân");
        initComponents();
    }

    private void initComponents() {
        txtMaDat = new TextField();
        txtMaDat.setPromptText("Mã Đặt");

        txtCustomerName = new TextField();
        txtCustomerName.setPromptText("Nhập tên khách hàng");

        txtCustomerPhone = new TextField();
        txtCustomerPhone.setPromptText("Nhập số điện thoại khách hàng");

        txtCourt = new TextField();
        txtCourt.setPromptText("Nhập tên sân");

        cboSan = new ComboBox<>();
        LoadCbbSan();

        dpNgayDat = new DatePicker();

        cboCa = new ComboBox<>();
        cboCa.getItems().addAll(
                "Ca 1 (6h-8h)", "Ca 2 (8h-10h)", "Ca 3 (10h-12h)", "Ca 4 (12h-14h)",
                "Ca 5 (14h-16h)", "Ca 6 (16h-18h)", "Ca 7 (18h-20h)", "Ca 8 (20h-22h)"
        );

        txtTienCoc = new TextField();
        txtTienCoc.setPromptText("Nhập tiền cọc");

        txtGhiChu = new TextArea();
        txtGhiChu.setPromptText("Nhập ghi chú");
        txtGhiChu.setPrefRowCount(3);

        btnLuu = new Button("Lưu");
        btnHuy = new Button("Hủy");

        styleButton();

        btnLuu.setOnAction(e -> xulyLuu());
        btnHuy.setOnAction(e -> this.close());

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
        
        grid.add(createLabel("Mã Đặt:"), 0, 0);
        grid.add(txtMaDat, 1, 0);

        grid.add(createLabel("Tên Khách Hàng:"), 0, 1);
        grid.add(txtCustomerName, 1, 1);

        grid.add(createLabel("Số Điện Thoại:"), 0, 2);
        grid.add(txtCustomerPhone, 1, 2);

        grid.add(createLabel("Sân:"), 0, 3);
        grid.add(cboSan, 1, 3);

        grid.add(createLabel("Ngày Đặt:"), 0, 4);
        grid.add(dpNgayDat, 1, 4);

        grid.add(createLabel("Ca:"), 0, 5);
        grid.add(cboCa, 1, 5);

        grid.add(createLabel("Tiền Cọc:"), 0, 6);
        grid.add(txtTienCoc, 1, 6);

        grid.add(createLabel("Ghi Chú:"), 0, 7);
        grid.add(txtGhiChu, 1, 7);

        HBox buttons = new HBox(10, btnLuu, btnHuy);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        grid.add(buttons, 1, 8);

        // Set the final scene
        this.setScene(new Scene(grid, 400, 500));
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold;");
        return label;
    }

    private void styleButton() {
        btnLuu.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;"
                + "-fx-border-radius: 5px; -fx-background-radius: 8;"
                + "-fx-padding: 8px 16px; -fx-cursor: hand;");

        btnHuy.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;"
                + "-fx-border-radius: 5px; -fx-background-radius: 8;"
                + "-fx-padding: 8px 16px; -fx-cursor: hand;");
    }

    private void xulyLuu() {
        if (!validateForm()) {
            return;
        }

        String maDat = txtMaDat.getText().trim();
        String tenKH = txtCustomerName.getText().trim();
        String sdt = txtCustomerPhone.getText().trim();
        String san = cboSan.getValue();
        LocalDate ngayDat = dpNgayDat.getValue();
        String ca = cboCa.getValue();
        String tienCoc = txtTienCoc.getText().trim();
        String ghiChu = txtGhiChu.getText().trim();

        System.out.println("Mã đặt: " + maDat);
        System.out.println("Tên khách hàng: " + tenKH);
        System.out.println("Số điện thoại: " + sdt);
        System.out.println("Sân: " + san);
        System.out.println("Ngày Đặt: " + ngayDat);
        System.out.println("Ca: " + ca);
        System.out.println("Tiền cọc: " + tienCoc);
        System.out.println("Ghi Chú: " + ghiChu);

        // TODO: Kết nối với DatSanDAO để lưu dữ liệu vào cơ sở dữ liệu

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText("Đặt sân thành công!");
        alert.showAndWait();
        
        clearForm();
        this.close();
    }

    private boolean validateForm() {
        if (txtMaDat.getText().trim().isEmpty()) {
            showError("Vui lòng nhập mã đặt sân");
            return false;
        }
        if (txtCustomerName.getText().trim().isEmpty()) {
            showError("Vui lòng nhập tên khách hàng");
            return false;
        }
        if (txtCustomerPhone.getText().trim().isEmpty()) {
            showError("Vui lòng nhập số điện thoại");
            return false;
        }
        if (cboSan.getValue() == null) {
            showError("Vui lòng chọn sân");
            return false;
        }
        if (dpNgayDat.getValue() == null) {
            showError("Vui lòng chọn ngày đặt");
            return false;
        }
        if (cboCa.getValue() == null) {
            showError("Vui lòng chọn ca chơi");
            return false;
        }
        return true;
    }

    private void LoadCbbSan() {
        ArrayList<String> ListMaSan = new ArrayList<String>();
        ArrayList<String> ListTenSan = new ArrayList<String>();
        
        // Khởi tạo model SAN (Lưu ý: cách thiết kế này hơi lạ, 
        // thường lấy danh sách sẽ gọi qua SAN_DAO thay vì Model SAN)
        SAN dao = new SAN(null); 
        ListMaSan = dao.getallMaSans();
        ListTenSan = dao.getallTenSans();
        
        cboSan.setItems(FXCollections.observableArrayList(ListTenSan));
    }

    private void clearForm() {
        txtMaDat.clear();
        txtCustomerName.clear();
        txtCustomerPhone.clear();
        txtTienCoc.clear();
        txtGhiChu.clear();
        cboSan.setValue(null);
        cboCa.setValue(null);
        dpNgayDat.setValue(null);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}