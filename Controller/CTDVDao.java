package com.mycompany.mavenproject1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CTDVDao {

    private static final String S = "";

    public static final String MA_VOT         = "DV001";
    public static final String MA_CAU         = "DV002";
    public static final String MA_NUOC_KHOANG = "DV003";
    public static final String MA_NUOC_NGOT   = "DV004";
    public static final String MA_KHAN        = "DV005";

    // Tính thành tiền 1 dịch vụ
    public long tinhThanhTien(String maDV, int soLuong) throws SQLException {
        String sql = "SELECT GiaBan FROM " + S + "DICHVU WHERE MaDV = ?";
        try (Connection con = DBContext.KetNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maDV);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getLong("GiaBan") * soLuong;
        }
        return 0;
    }

    // Tính tổng tiền tất cả dịch vụ
    public long tinhTongTienDV(Map<String, Integer> danhSachDV) throws SQLException {
        long tong = 0;
        for (Map.Entry<String, Integer> entry : danhSachDV.entrySet()) {
            if (entry.getValue() > 0) {
                tong += tinhThanhTien(entry.getKey(), entry.getValue());
            }
        }
        return tong;
    }

    // Lưu chi tiết dịch vụ
    public List<String> luuCTDV(String maDS, Map<String, Integer> danhSachDV) throws SQLException {
        List<String> dsMaCTDV = new ArrayList<>();

        String sqlMax = "SELECT NVL(MAX(TO_NUMBER(SUBSTR(MaCTDV,5))),0)+1 FROM " + S + "CTDV";
        String sqlIns = "INSERT INTO " + S + "CTDV (MaCTDV,SoLuong,ThanhTien,MaDS,MaDV) VALUES (?,?,?,?,?)";

        Connection con = null;
        try {
            con = DBContext.KetNoi();
            con.setAutoCommit(false);

            // Lấy số thứ tự hiện tại
            int nextNum = 1;
            try (PreparedStatement ps = con.prepareStatement(sqlMax);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) nextNum = rs.getInt(1);
            }

            try (PreparedStatement ps = con.prepareStatement(sqlIns)) {
                for (Map.Entry<String, Integer> entry : danhSachDV.entrySet()) {
                    if (entry.getValue() <= 0) continue;

                    String maCTDV = "CTDV" + String.format("%03d", nextNum++);
                    long thanhTien = tinhThanhTien(entry.getKey(), entry.getValue());

                    ps.setString(1, maCTDV);
                    ps.setInt(2, entry.getValue());
                    ps.setLong(3, thanhTien);
                    ps.setString(4, maDS);
                    ps.setString(5, entry.getKey());
                    ps.executeUpdate();

                    dsMaCTDV.add(maCTDV);
                }
            }

            con.commit();
        } catch (SQLException e) {
            if (con != null) try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw e;
        } finally {
            if (con != null) try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        return dsMaCTDV;
    }
}