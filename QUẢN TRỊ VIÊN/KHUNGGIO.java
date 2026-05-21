package com.example.guidemo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import java.sql.*;
import java.time.LocalTime;
import java.text.SimpleDateFormat;

public class KHUNGGIO {
    public String MaKG;
    public Timestamp GioBD;
    public Timestamp GioKT;
    public long HeSo;
    public Connection conn;

    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    public KHUNGGIO(Connection conn) {
        this.conn = conn;
    }

    public KHUNGGIO(String maKG, Timestamp gioBD, Timestamp gioKT) {
        this.MaKG = maKG;
        this.GioBD = gioBD;
        this.GioKT = gioKT;
    }

    public String getMaKG() { return MaKG; }
    public Timestamp getGioBD() { return GioBD; }
    public Timestamp getGioKT() { return GioKT; }

    public String getGioBDStr() {
        if (GioBD == null) return "";
        return new SimpleDateFormat("HH:mm").format(GioBD);
    }

    public String getGioKTStr() {
        if (GioKT == null) return "";
        return new SimpleDateFormat("HH:mm").format(GioKT);
    }

    public BooleanProperty selectedProperty() { return selected; }
    public boolean isSelected() { return selected.get(); }
    public void setSelected(boolean selected) { this.selected.set(selected); }

    public boolean KiemTraKhungGio(LocalTime bd, LocalTime kt) {
        String sql = "SELECT 1 FROM KHUNGGIO WHERE GioBD <= ? AND GioKT >= ?";
        try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTime(1, Time.valueOf(bd));
            pstmt.setTime(2, Time.valueOf(kt));
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}