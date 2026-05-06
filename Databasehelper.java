/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hoc.app_doan_scl;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Hi
 */

public class Databasehelper {
    private Connection con =null;
    private final String url ="jdbc:oracle:thin:@localhost:1521:orcl";
    private final String user="system";
    private final String password ="Chon2006";
    private final String driver ="oracle.jdbc.driver.OracleDriver";
    public Connection createCon() throws SQLException
    {
        try{
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Ket noi thanh cong!");
            return con;
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Khong tin thay driver"+ e.getMessage());
            return null;
        }
        catch(SQLException e){
            System.out.println("Loi ket noi Oracle"+ e.getMessage());
            return null;
        }
        
    }
    public void closeCon(Connection conn) throws SQLException
    {
        try{
            if (conn!=null&& !conn.isClosed())
            {
                conn.close();
                System.out.println("Dong ket noi thanh cong");
            }
        }
        catch(SQLException e){
            System.out.println("Loi dong ket noi "+ e.getMessage());
        }
    }
}