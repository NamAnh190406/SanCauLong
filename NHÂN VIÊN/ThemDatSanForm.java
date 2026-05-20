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
        this.setTitle("ThÃªm Äáº·t SÃ¢n");
        initComponents();
    }

    private void initComponents() {
        txtMaDat = new TextField();
        txtMaDat.setPromptText("MÃ£ Äáº·t");

        txtCustomerName = new TextField();
        txtCustomerName.setPromptText("Nháº­p tÃªn khÃ¡ch hÃ ng");

        txtCustomerPhone = new TextField();
        txtCustomerPhone.setPromptText("Nháº­p sá»‘ Ä‘iá»‡n thoáº¡i khÃ¡ch hÃ ng");

        txtCourt = new TextField();
        txtCourt.setPromptText("Nháº­p tÃªn sÃ¢n");

        cboSan = new ComboBox<>();
        LoadCbbSan();

        dpNgayDat = new DatePicker();

        cboCa = new ComboBox<>();
        cboCa.getItems().addAll("Ca 1 (6h-8h)", "Ca 2 (8h-10h)", "Ca 3 (10h-12h)", "Ca 4 (12h-14h)",
                "Ca 5 (14h-16h)", "Ca 6 (16h-18h)", "Ca 7 (18h-20h)", "Ca 8 (20h-22h)");

        txtTienCoc = new TextField();
        txtTienCoc.setPromptText("Nháº­p tiá»n cá»c");

        txtGhiChu = new TextArea();
        txtGhiChu.setPromptText("Nháº­p ghi chÃº");
        txtGhiChu.setPrefRowCount(3);

        btnLuu = new Button("LÆ°u");
        btnHuy = new Button("Há»§y");

        styleButton();

        btnLuu.setOnAction(e -> xulyLuu());
        btnHuy.setOnAction(e -> this.close());

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(createLabel("MÃ£ Äáº·t:"), 0, 0);
        grid.add(txtMaDat, 1, 0);

        grid.add(createLabel("TÃªn KhÃ¡ch HÃ ng:"), 0, 1);
        grid.add(txtCustomerName, 1, 1);

        grid.add(createLabel("Sá»‘ Äiá»‡n Thoáº¡i:"), 0, 2);
        grid.add(txtCustomerPhone, 1, 2);

        grid.add(createLabel("SÃ¢n:"), 0, 3);
        grid.add(cboSan, 1, 3);

        grid.add(createLabel("NgÃ y Äáº·t:"), 0, 4);
        grid.add(dpNgayDat, 1, 4);

        grid.add(createLabel("Ca:"), 0, 5);
        grid.add(cboCa, 1, 5);

        grid.add(createLabel("Tiá»n Cá»c:"), 0, 6);
        grid.add(txtTienCoc, 1, 6);

        grid.add(createLabel("Ghi ChÃº:"), 0, 7);
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

        System.out.println("MÃ£ Äáº·t: " + maDat);
        System.out.println("TÃªn KhÃ¡ch HÃ ng: " + tenKH);
        System.out.println("Sá»‘ Äiá»‡n Thoáº¡i: " + sdt);
        System.out.println("SÃ¢n: " + san);
        System.out.println("NgÃ y Äáº·t: " + ngayDat);
        System.out.println("Ca: " + ca);
        System.out.println("Tiá»n Cá»c: " + tienCoc);
        System.out.println("Ghi ChÃº: " + ghiChu);

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("ThÃ´ng bÃ¡o");
        alert.setHeaderText(null);
        alert.setContentText("Äáº·t sÃ¢n thÃ nh cÃ´ng!");
        alert.showAndWait();
        clearForm();
        this.close();
    }

    private boolean validateForm() {
        if (txtMaDat.getText().trim().isEmpty()) {
            showError("Vui lÃ²ng nháº­p mÃ£ Ä‘áº·t sÃ¢n");
            return false;
        }
        if (txtCustomerName.getText().trim().isEmpty()) {
            showError("Vui lÃ²ng nháº­p tÃªn khÃ¡ch hÃ ng");
            return false;
        }
        if (txtCustomerPhone.getText().trim().isEmpty()) {
            showError("Vui lÃ²ng nháº­p sá»‘ Ä‘iá»‡n thoáº¡i");
            return false;
        }
        if (cboSan.getValue() == null) {
            showError("Vui lÃ²ng chá»n sÃ¢n");
            return false;
        }
        if (dpNgayDat.getValue() == null) {
            showError("Vui lÃ²ng chá»n ngÃ y Ä‘áº·t");
            return false;
        }
        if (cboCa.getValue() == null) {
            showError("Vui lÃ²ng chá»n ca chÆ¡i");
            return false;
        }
        return true;
    }

    private void LoadCbbSan() {
        ArrayList<String> ListMaSan = new ArrayList<String>();
        ArrayList<String> ListTenSan = new ArrayList<String>();
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
        alert.setTitle("Lá»—i");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}