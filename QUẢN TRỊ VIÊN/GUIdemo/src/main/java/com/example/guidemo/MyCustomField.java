package com.example.guidemo; // Đổi lại đúng tên package của bạn nhé

import javafx.scene.control.TextField;

public class MyCustomField extends TextField {
    public MyCustomField() {
        super();
        // Chỉnh cho nó đẹp chung cho mọi ô nhập
        this.setStyle("-fx-border-color: #2196F3; -fx-border-radius: 5; -fx-padding: 10;");
        this.setPromptText("Nhập dữ liệu vào đây...");
    }
}
