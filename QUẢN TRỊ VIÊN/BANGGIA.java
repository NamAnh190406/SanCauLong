package com.example.guidemo;

import java.sql.*;

public class BANGGIA {
    public String MaBG;
    public long DonGia;
    public String MaSan;
    public String MaKG;
    public Connection conn;

    public BANGGIA() {
        try {
            // Tự động thò tay sang class DBConnection để lấy kết nối gán cho chính mình
            this.conn = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            this.conn = null;
        }
    }

    public BANGGIA(String maBG, long donGia, String maSan, String maKG) {
        this.MaBG = maBG;
        this.DonGia = donGia;
        this.MaSan = maSan;
        this.MaKG = maKG;
    }

    public String getMaBG() { return MaBG; }
    public long getDonGia() { return DonGia; }
    public String getMaSan() { return MaSan; }
    public String getMaKG() { return MaKG; }
}