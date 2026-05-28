package com.mycompany.mavenproject1;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import java.io.IOException;

public class ThongTinSanController {

    @FXML
    private FontAwesomeIconView btnQuayLai;

    @FXML
    public void initialize() {
    }

    @FXML
    private void handleQuayLai(MouseEvent event) {
        try {
            App.setRoot("ManHinhChinh");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}