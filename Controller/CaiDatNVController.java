package com.mycompany.mavenproject1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;


public class CaiDatNVController implements Initializable {
    @FXML
    private ComboBox<String> cbPrinter;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initPrinterComboBox();
        registerSettingsListeners();
    }
    private void initPrinterComboBox() {
        ObservableList<String> printers = FXCollections.observableArrayList(
            "HP LaserJet Pro M404dn (Mặc định)",
            "Canon LBP2900",
            "Epson L3110 Series",
            "Microsoft Print to PDF",
            "Xprinter XP-365B (In hóa đơn)"
        );
        cbPrinter.setItems(printers);
        cbPrinter.getSelectionModel().select(0);
    }

    private void registerSettingsListeners() {
        // Printer change listener
        cbPrinter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("[Cài đặt] Đã thay đổi máy in hóa đơn mặc định: " + newValue);
            }
        });
    }

    @FXML
    private void handleThoat() {
        navigateBack();
    }

    private void navigateBack() {
        try {
            App.setRoot("ManHinhChinhNV");
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi ứng dụng");
            alert.setHeaderText("Không thể quay lại màn hình chính");
            alert.setContentText("Đã xảy ra lỗi khi chuyển đổi giao diện: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
