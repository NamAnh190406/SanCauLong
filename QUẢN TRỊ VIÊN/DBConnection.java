package com.example.guidemo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Thay đổi thông tin URL, Username, Password phù hợp với cấu hình Oracle của bạn
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USER = "C##SANCAULONG";
    private static final String PASSWORD = "123";

    public static Connection getConnection() throws SQLException {
        try {
            // Đảm bảo bạn đã add file ojdbc.jar vào thư mục thư viện (Libraries) của dự án
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
