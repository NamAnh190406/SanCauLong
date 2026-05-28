package com.mycompany.mavenproject1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;


public class CaiDatNVController implements Initializable {

    // ==========================================
    // FXML Injection Fields
    // ==========================================

    @FXML
    private ComboBox<String> cbPrinter;

    // ==========================================
    // Initialization Method
    // ==========================================

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Populate choices for Printer Dropdown
        initPrinterComboBox();

        // 2. Register Event Listeners
        registerSettingsListeners();
    }

    // ==========================================
    // Helper Initialization Methods
    // ==========================================

    /**
     * Fills the printer selection ComboBox with simulated system printers.
     */
    private void initPrinterComboBox() {
        ObservableList<String> printers = FXCollections.observableArrayList(
            "HP LaserJet Pro M404dn (Mặc định)",
            "Canon LBP2900",
            "Epson L3110 Series",
            "Microsoft Print to PDF",
            "Xprinter XP-365B (In hóa đơn)"
        );
        cbPrinter.setItems(printers);
        
        // Select the default printer
        cbPrinter.getSelectionModel().select(0);
    }

    /**
     * Adds change listeners to ComboBoxes for responsive UX feedback.
     */
    private void registerSettingsListeners() {
        // Printer change listener
        cbPrinter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("[Cài đặt] Đã thay đổi máy in hóa đơn mặc định: " + newValue);
            }
        });
    }

    // ==========================================
    // Event Action Handlers
    // ==========================================

    /**
     * Handles navigation back to the main screen ("ManHinhChinhNV").
     * Mapped to onMouseClicked="#handleThoat" on the FontAwesomeIconView.
     * 
     * IMPORTANT: We use a zero-argument method here.
     * This avoids any overloading conflicts or "argument type mismatch" errors
     * because JavaFX can invoke a zero-argument method for any event type.
     */
    @FXML
    private void handleThoat() {
        navigateBack();
    }

    /**
     * Core helper method to change screen root to "ManHinhChinhNV".
     */
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
