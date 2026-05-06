/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import com.hoc.app_doan_scl.Databasehelper;
import Model.ThanhToan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;

/**
 *
 * @author Hi
 */
public class ThanhToanDAO {
    private Connection con;
    private Databasehelper connectDB;

    public ThanhToanDAO() throws SQLException {
        connectDB = new Databasehelper();
        con = connectDB.createCon();
    }

    public ObservableList<ThanhToan> getAllThanhToan() {
        ObservableList<ThanhToan> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM THANHTOAN ORDER BY ThoiGianTT DESC";

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                ThanhToan tt = new ThanhToan(
                    rs.getString("MaTT"),
                    rs.getString("PTTT"),
                    rs.getTimestamp("ThoiGianTT"),
                    rs.getString("TrangThai"),
                    rs.getString("MaHoaDon"),
                    rs.getLong("SoTien")
                );
                list.add(tt);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy dữ liệu thanh toán: " + e.getMessage());
        }
        return list;
    }

    public ObservableList<ThanhToan> getThanhToanByHoaDon(String maHoaDon) {
        ObservableList<ThanhToan> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM THANHTOAN WHERE MaHoaDon = ? ORDER BY ThoiGianTT DESC";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maHoaDon);
            ResultSet rs = pstmt.executeQuery();
//String maTT, String pttt, Timestamp thoiGianTT,String trangThai, String maHoaDon, long soTien
            while (rs.next()) {
                ThanhToan tt = new ThanhToan(
                    rs.getString("MaTT"),
                    rs.getString("PTTT"),
                    rs.getTimestamp("ThoiGianTT"),
                    rs.getString("TrangThai"),
                    rs.getString("MaHoaDon"),
                    rs.getLong("SoTien")
                );
                list.add(tt);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy thanh toán theo hóa đơn: " + e.getMessage());
        }
        return list;
    }

    public ObservableList<ThanhToan> getThanhToanByTrangThai(String trangThai) {
        ObservableList<ThanhToan> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM THANHTOAN WHERE TrangThai = ? ORDER BY ThoiGianTT DESC";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, trangThai);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ThanhToan tt = new ThanhToan(
                    rs.getString("MaTT"),
                    rs.getString("PTTT"),
                    rs.getTimestamp("ThoiGianTT"),
                    rs.getString("TrangThai"),
                    rs.getString("MaHoaDon"),
                    rs.getLong("SoTien")
                );
                list.add(tt);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy thanh toán theo trạng thái: " + e.getMessage());
        }
        return list;
    }

    public boolean addThanhToan(ThanhToan tt) {
        String sql = "INSERT INTO THANHTOAN (MaTT, PTTT, ThoiGianTT, TrangThai, MaHoaDon) " +
                    "VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?)";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, tt.getMaTT());
            pstmt.setString(2, tt.getPttt());
            pstmt.setString(3, tt.getTrangThai());
            pstmt.setString(4, tt.getMaHoaDon());

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
            System.out.println("Lỗi thêm thanh toán: " + e.getMessage());
            return false;
        }
    }

    public boolean updateThanhToan(ThanhToan tt) {
        String sql = "UPDATE THANHTOAN SET PTTT=?, TrangThai=? WHERE MaTT=?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, tt.getPttt());
            pstmt.setString(2, tt.getTrangThai());
            pstmt.setString(3, tt.getMaTT());

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
            System.out.println("Lỗi cập nhật thanh toán: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTrangThaiThanhToan(String maTT, String trangThai) {
        String sql = "UPDATE THANHTOAN SET TrangThai=? WHERE MaTT=?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, trangThai);
            pstmt.setString(2, maTT);

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
            System.out.println("Lỗi cập nhật trạng thái: " + e.getMessage());
            return false;
        }
    }

    public ThanhToan getThanhToanByMa(String maTT) {
        String sql = "SELECT * FROM THANHTOAN WHERE MaTT=?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maTT);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new ThanhToan(
                    rs.getString("MaTT"),
                    rs.getString("PTTT"),
                    rs.getTimestamp("ThoiGianTT"),
                    rs.getString("TrangThai"),
                    rs.getString("MaHoaDon"),
                    rs.getLong("SoTien")
                );
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi tìm thanh toán: " + e.getMessage());
        }
        return null;
    }

    public Long countThanhToanThanhCong() {
        String sql = "SELECT COUNT(*) AS cnt FROM THANHTOAN WHERE TrangThai = 'ThanhCong'";

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getLong("cnt");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi thống kê: " + e.getMessage());
        }
        return 0L;
    }

    public void closeConnection() throws SQLException {
        connectDB.closeCon(con);
    }

}
