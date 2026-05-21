package com.example.guidemo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import java.io.IOException;

public class SanVaKhungGioController {

    @FXML private Button btnQuanLySan;
    @FXML private Button btnQuanLyKG;

    @FXML
    public void initialize() {
        datHieuUngHoverCard(btnQuanLySan);
        datHieuUngHoverCard(btnQuanLyKG);
    }

    private void datHieuUngHoverCard(Button btn) {
        String styleGoc = "-fx-background-color: #FFFFFF; -fx-background-radius: 15; -fx-border-color: #E0E0E0; -fx-border-radius: 15; -fx-border-width: 1.5; -fx-cursor: hand;";
        String styleHover = "-fx-background-color: #FFFFFF; -fx-background-radius: 15; -fx-border-color: #2E7D32; -fx-border-radius: 15; -fx-border-width: 2.5; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);";

        btn.setOnMouseEntered(e -> btn.setStyle(styleHover));
        btn.setOnMouseExited(e -> btn.setStyle(styleGoc));
    }

    @FXML
    private void handleMoQuanLySan() {
        dieuHuongGiaoDien("QLySan.fxml");
    }

    @FXML
    private void handleMoQuanLyKhungGio() {
        dieuHuongGiaoDien("QLyKhungGio.fxml");
    }

    private void dieuHuongGiaoDien(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent view = loader.load();

            if (TrangChuController.rootPane != null) {
                TrangChuController.rootPane.setCenter(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Lỗi điều hướng! Kiểm tra lại file: " + fxmlFile);
        }
    }
}