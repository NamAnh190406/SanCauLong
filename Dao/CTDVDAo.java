/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import com.hoc.app_doan_scl.Databasehelper;
import Model.CTDV;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
/**
 *
 * @author Hi
 */



public class CTDVDAo {
    private Connection con;
    private Databasehelper connectDB;

    public CTDVDAo() throws SQLException {
        connectDB = new Databasehelper();
        con = connectDB.createCon();
    }

    public ObservableList<CTDV> getAllCTDV() {
        ObservableList<CTDV> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM CTDV ORDER BY MaCTDV";

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                CTDV ctdv = new CTDV(
                    rs.getString("MaCTDV"),
                    rs.getString("MaDS"),
                    rs.getString("MaDV"),
                    rs.getLong("SoLuong"),
                    rs.getLong("DonGia")
                );
                list.add(ctdv);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy dữ liệu chi tiết dịch vụ: " + e.getMessage());
        }
        return list;
    }

    public ObservableList<CTDV> getCTDVByDatSan(String maDS) {
        ObservableList<CTDV> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM CTDV WHERE MaDS = ? ORDER BY MaCTDV";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maDS);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                CTDV ctdv = new CTDV(
                    rs.getString("MaCTDV"),
                    rs.getString("MaDS"),
                    rs.getString("MaDV"),
                    rs.getLong("SoLuong"),
                    rs.getLong("DonGia")
                );
                list.add(ctdv);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy chi tiết dịch vụ theo đặt sân: " + e.getMessage());
        }
        return list;
    }

    public boolean addCTDV(CTDV ctdv) {
        String sql = "INSERT INTO CTDV (MaCTDV, SoLuong, ThanhTien, MaDS, MaDV) " +
                    "VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, ctdv.getMaCTDV());
            pstmt.setLong(2, ctdv.getSoLuong());
            pstmt.setLong(3, ctdv.getThanhTien());
            pstmt.setString(4, ctdv.getMaDS());
            pstmt.setString(5, ctdv.getMaDV());

            int result = pstmt.executeUpdate();
            con.commit();
            pstmt.close();
            return result > 0;
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Lỗi thêm chi tiết dịch vụ: " + e.getMessage());
            return false;
        }
    }

    public boolean updateCTDV(CTDV ctdv) {
        String sql = "UPDATE CTDV SET SoLuong=?, ThanhTien=? WHERE MaCTDV=?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, ctdv.getSoLuong());
            pstmt.setLong(2, ctdv.getThanhTien());
            pstmt.setString(3, ctdv.getMaCTDV());

            int result = pstmt.executeUpdate();
            con.commit();
            pstmt.close();
            return result > 0;
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Lỗi cập nhật chi tiết dịch vụ: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCTDV(String maCTDV) {
        String sql = "DELETE FROM CTDV WHERE MaCTDV=?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maCTDV);

            int result = pstmt.executeUpdate();
            con.commit();
            pstmt.close();
            return result > 0;
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Lỗi xóa chi tiết dịch vụ: " + e.getMessage());
            return false;
        }
    }

    public Long getTongThanhTienByDatSan(String maDS) {
        String sql = "SELECT SUM(ThanhTien) AS tong FROM CTDV WHERE MaDS = ?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maDS);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("tong");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi tính tổng tiền: " + e.getMessage());
        }
        return 0L;
    }

    public void closeConnection() throws SQLException {
        connectDB.closeCon(con);
    }
}

