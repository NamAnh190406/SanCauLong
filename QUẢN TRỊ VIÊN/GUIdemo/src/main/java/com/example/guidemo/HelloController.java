package com.example.guidemo;

import javafx.fxml.FXML;

public class HelloController {

    // Khai báo đúng cái tên class dùng chung bạn đã tạo
    @FXML
    private com.example.guidemo.MyCustomField myInput1;

    @FXML
    private com.example.guidemo.MyCustomField myInput2;

    @FXML
    protected void onHelloButtonClick() {
        // Lấy dữ liệu ra xem class dùng chung chạy ổn không
        System.out.println("Ô 1: " + myInput1.getText());
        System.out.println("Ô 2: " + myInput2.getText());
    }
}