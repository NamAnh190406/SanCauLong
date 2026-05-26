package Controller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

public class CaiDatController implements Initializable {

    @FXML private CheckBox chkDarkMode;
    @FXML private CheckBox chkNotifications;
    @FXML private CheckBox chkAutoBackup;
    @FXML private ComboBox<String> cbLanguage;
    @FXML private ComboBox<String> cbPrinter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbLanguage.getItems().addAll("Tiáº¿ng Viá»‡t", "English");
        cbLanguage.getSelectionModel().selectFirst();

        cbPrinter.getItems().addAll("HP LaserJet Pro", "Canon PIXMA", "Epson L3110");
        cbPrinter.getSelectionModel().selectFirst();

        chkDarkMode.selectedProperty().addListener((obs, oldVal, newVal) -> {
            // Placeholder cho logic thay Ä‘á»•i theme
        });
    }

    @FXML
    private void onBackupNow() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sao lÆ°u dá»¯ liá»‡u");
        alert.setHeaderText(null);
        alert.setContentText("ÄÃ£ sao lÆ°u dá»¯ liá»‡u thÃ nh cÃ´ng!");
        alert.showAndWait();
    }

    @FXML
    private void onRestoreData() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("KhÃ´i phá»¥c dá»¯ liá»‡u");
        alert.setHeaderText(null);
        alert.setContentText("TÃ­nh nÄƒng Ä‘ang Ä‘Æ°á»£c phÃ¡t triá»ƒn.");
        alert.showAndWait();
    }
}
