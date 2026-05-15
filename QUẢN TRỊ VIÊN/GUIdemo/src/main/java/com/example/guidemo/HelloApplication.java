package com.example.guidemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.Parent;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Cách viết chuẩn để giao diện luôn tràn màn hình
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root); // Không để thông số kích thước ở đây

        stage.setScene(scene);
        stage.setMaximized(true); // Phóng to trước khi show
        stage.show();
    }
}
