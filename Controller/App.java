package com.mycompany.mavenproject1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
public void start(Stage stage) throws IOException {
    scene = new Scene(loadFXML("primary"));  // ← gán vào field 'scene'
    stage.setScene(scene);
    stage.setMaximized(true);  // ← full screen như Bảng Giá
    stage.show();
}

   static void setRoot(String fxml) throws IOException {
    URL fxmlUrl = App.class.getResource(fxml + ".fxml");
    System.out.println("Đang load FXML: " + fxml + ".fxml → URL = " + fxmlUrl);
    
    if (fxmlUrl == null) {
        System.err.println("KHÔNG TÌM THẤY FILE: " + fxml + ".fxml");
        System.err.println("Hãy kiểm tra tên file trong thư mục resources!");
        return; // Không crash, chỉ in lỗi
    }
    
    FXMLLoader loader = new FXMLLoader(fxmlUrl);
    scene.setRoot(loader.load());
}

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}
