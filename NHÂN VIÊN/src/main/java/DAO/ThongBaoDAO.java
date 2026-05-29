package DAO;

import Model.THONGBAO;
import Utils.Databasehelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ThongBaoDAO {
    public static List<THONGBAO> getThongBaoMoiNhat() {

    List<THONGBAO> list = new ArrayList<>();

    String sql = "SELECT * FROM THONGBAO ORDER BY ThoiGian DESC FETCH FIRST 20 ROWS ONLY";

    Databasehelper db = new Databasehelper();

    try (Connection conn = db.createCon()) {

        

        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {

            list.add(new THONGBAO(
                rs.getInt("MaTB"),
                rs.getString("TieuDe"),
                rs.getString("NoiDung"),
                rs.getTimestamp("ThoiGian"),
                rs.getString("Loai"),
                rs.getInt("DaDoc") == 1
            ));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}

    public static void danhDauDaDoc(int maTB) {
        String sql = "UPDATE THONGBAO SET DaDoc = 1 WHERE MaTB = ?";
        Databasehelper db = new Databasehelper();
        try (Connection conn = db.createCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maTB);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void danhDauDaDocTatCa() {
        String sql = "UPDATE THONGBAO SET DaDoc = 1 WHERE DaDoc = 0";
        Databasehelper db = new Databasehelper();
        try (Connection conn = db.createCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}