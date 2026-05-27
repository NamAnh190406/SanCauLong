package Controller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import com.hoc.app_doan_scl.MainApp;

public class CaiDatController implements Initializable {

    @FXML private CheckBox chkDarkMode;
    @FXML private CheckBox chkNotifications;
    @FXML private CheckBox chkAutoBackup;
    @FXML private ComboBox<String> cbLanguage;
    @FXML private ComboBox<String> cbPrinter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbLanguage.getItems().addAll("Tiếng Việt", "English");
        cbLanguage.getSelectionModel().selectFirst();

        cbPrinter.getItems().addAll("HP LaserJet Pro", "Canon PIXMA", "Epson L3110");
        cbPrinter.getSelectionModel().selectFirst();

        chkDarkMode.selectedProperty().addListener((obs, oldVal, newVal) -> {
            MainApp.setDarkMode(newVal);
        });
    }

    @FXML
    private void onBackupNow() {
        try {
            File backupDir = new File("backups");
            if (!backupDir.exists()) backupDir.mkdirs();
            File backupFile = new File(backupDir, "backup_" + System.currentTimeMillis() + ".csv");
            FileWriter writer = new FileWriter(backupFile);
            writer.write("Demo Backup File\nThis file simulates a database backup.");
            writer.close();
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sao lưu dữ liệu");
            alert.setHeaderText(null);
            alert.setContentText("Đã sao lưu dữ liệu thành công tại: " + backupFile.getAbsolutePath());
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setContentText("Lỗi sao lưu: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onRestoreData() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Khôi phục dữ liệu");
        alert.setHeaderText(null);
        alert.setContentText("Tính năng đang được phát triển.");
        alert.showAndWait();
    }
}
