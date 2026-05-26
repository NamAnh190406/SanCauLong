package com.hoc.app_doan_scl;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

public class MainApp extends Application {

    // 1. Declare the Scene as a static variable so it can be accessed globally
    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the initial FXML
        scene = new Scene(loadFXML("TrangChuUI")); 

        // Ẩn title bar mặc định của hệ điều hành -> dùng custom title bar
        primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.setTitle("He thong Quan ly San Cau Long");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(450);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    // 2. Add the static setRoot method that your controllers will call
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    // 3. Add a helper method to load the FXML files
    private static Parent loadFXML(String fxml) throws IOException {
        // NOTE: Make sure this path matches your project structure.
        // If your FXML files are in the "View" folder, keep "/View/" here.
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/View/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}