/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;
import com.hoc.app_doan_scl.Databasehelper;
import Model.KhuyenMai;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.sql.*;
/**
 *
 * @author Hi
 */
public class KhuyenMaiDAO {
    private Connection con;
    private Databasehelper connectDB;

    public KhuyenMaiDAO() throws SQLException {
        connectDB = new Databasehelper();
        con = connectDB.createCon();
    }

    public ObservableList<KhuyenMai> getAllKhuyenMai() {
        ObservableList<KhuyenMai> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM KHUYENMAI ORDER BY NgayBD DESC";

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                KhuyenMai km = new KhuyenMai(
                    rs.getString("MaKM"),
                    rs.getString("TenKM"),
                    rs.getLong("PhanTramGG"),
                    rs.getLong("GTriToiDa"),
                    rs.getDate("NgayBD").toLocalDate(),
                    rs.getDate("NgayKT").toLocalDate()
                );
                list.add(km);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy dữ liệu khuyến mãi: " + e.getMessage());
        }
        return list;
    }

    public ObservableList<KhuyenMai> getKhuyenMaiHienHanh(LocalDate ngay) {
        ObservableList<KhuyenMai> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM KHUYENMAI WHERE NgayBD <= ? AND NgayKT >= ? ORDER BY TenKM";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setDate(2,java.sql.Date.valueOf(ngay));
            pstmt.setDate(2, java.sql.Date.valueOf(ngay));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                KhuyenMai km = new KhuyenMai(
                    rs.getString("MaKM"),
                    rs.getString("TenKM"),
                    rs.getLong("PhanTramGG"),
                    rs.getLong("GTriToiDa"),
                    rs.getDate("NgayBD").toLocalDate(),
                    rs.getDate("NgayKT").toLocalDate()
                );
                list.add(km);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy khuyến mãi hiện hành: " + e.getMessage());
        }
        return list;
    }

    public boolean addKhuyenMai(KhuyenMai km) {
        String sql = "INSERT INTO KHUYENMAI (MaKM, TenKM, PhanTramGG, GTriToiDa, NgayBD, NgayKT) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, km.getMaKM());
            pstmt.setString(2, km.getTenKM());
            pstmt.setDouble(3, km.getPhanTramGiam());
            pstmt.setDouble(4, km.getGiaTriToiDa());
            pstmt.setDate(5, java.sql.Date.valueOf(km.getNgayBatDau()));
            pstmt.setDate(6, java.sql.Date.valueOf(km.getNgayKetThuc()));

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
            System.out.println("Lỗi thêm khuyến mãi: " + e.getMessage());
            return false;
        }
    }

    public boolean updateKhuyenMai(KhuyenMai km) {
        String sql = "UPDATE KHUYENMAI SET TenKM=?, PhanTramGG=?, GTriToiDa=?, NgayBD=?, NgayKT=? WHERE MaKM=?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, km.getTenKM());
            pstmt.setDouble(2, km.getPhanTramGiam());
            pstmt.setLong(3, km.getGiaTriToiDa());
            pstmt.setDate(4, java.sql.Date.valueOf(km.getNgayBatDau()));
            pstmt.setDate(5, java.sql.Date.valueOf(km.getNgayKetThuc()));
            pstmt.setString(6, km.getMaKM());

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
            System.out.println("Lỗi cập nhật khuyến mãi: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteKhuyenMai(String maKM) {
        String sql = "DELETE FROM KHUYENMAI WHERE MaKM=?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maKM);

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
            System.out.println("Lỗi xóa khuyến mãi: " + e.getMessage());
            return false;
        }
    }

    public KhuyenMai getKhuyenMaiByMa(String maKM) {
        String sql = "SELECT * FROM KHUYENMAI WHERE MaKM=?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maKM);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new KhuyenMai(
                    rs.getString("MaKM"),
                    rs.getString("TenKM"),
                    rs.getDouble("PhanTramGG"),
                    rs.getLong("GTriToiDa"),
                    rs.getDate("NgayBD").toLocalDate(),
                    rs.getDate("NgayKT").toLocalDate()
                );
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi tìm khuyến mãi: " + e.getMessage());
        }
        return null;
    }

    public void closeConnection() throws SQLException {
        connectDB.closeCon(con);
    }

}
