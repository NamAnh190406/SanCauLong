package com.mycompany.mavenproject1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBContext {
    
   public static Connection KetNoi() {
    Connection con = null;
    String url = "jdbc:oracle:thin:@localhost:1522:orcl";
    Properties pro = new Properties();
    pro.put("user", "\"sinhvien01\"");
    pro.put("password", "123456");
    try {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        con = DriverManager.getConnection(url, pro);
        if (con != null) System.out.println("Ket noi thanh cong!");
    } catch (ClassNotFoundException e) {
        System.out.println("Khong tim thay Driver");
    } catch (SQLException e) {
        System.out.println("Ket noi that bai!");
        e.printStackTrace();
    }
    return con;
}

    public static void main(String[] args) {
        DBContext.KetNoi();
    }
}