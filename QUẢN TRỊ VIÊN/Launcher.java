package com.example.guidemo;


public class Launcher {
    public static void main(String[] args) {
        // Gọi hàm main của App từ đây sẽ bypass được lỗi "missing runtime components"
        App.main(args);
    }
}
