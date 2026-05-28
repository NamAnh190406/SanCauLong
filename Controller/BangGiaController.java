package com.mycompany.mavenproject1;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BangGiaController implements Initializable {

    @FXML
    private FontAwesomeIconView btnBack;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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