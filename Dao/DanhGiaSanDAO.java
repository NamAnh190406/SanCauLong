/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;
import com.hoc.app_doan_scl.Databasehelper;
import Model.DanhGiaSan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
/**
 *
 * @author Hi
 */
public class DanhGiaSanDAO {
    private Connection con;
    private Databasehelper connectDB;
    public DanhGiaSanDAO() throws SQLException {
        connectDB = new Databasehelper();
        con = connectDB.createCon();
    }
    public ObservableList<DanhGiaSan> getAllDanhGia() {
        ObservableList<DanhGiaSan> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM DANHGIASAN ORDER BY ThoiDiemDanhGia DESC";

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                DanhGiaSan dg = new DanhGiaSan(
                    rs.getString("MaDanhGia"),
                    rs.getInt("DiemDG"),
                    rs.getString("NhanXet"),
                    rs.getTimestamp("ThoiDiemDanhGia"),
                    rs.getString("MaKH"),
                    rs.getString("MaSan")
                );
                list.add(dg);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy dữ liệu đánh giá: " + e.getMessage());
        }
        return list;
    }

    public ObservableList<DanhGiaSan> getDanhGiaBySan(String maSan) {
        ObservableList<DanhGiaSan> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM DANHGIASAN WHERE MaSan = ? ORDER BY ThoiDiemDanhGia DESC";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maSan);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                DanhGiaSan dg = new DanhGiaSan(
                    rs.getString("MaDanhGia"),
                    rs.getInt("DiemDG"),
                    rs.getString("NhanXet"),
                    rs.getTimestamp("ThoiDiemDanhGia"),
                    rs.getString("MaKH"),
                    rs.getString("MaSan")
                );
                list.add(dg);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy đánh giá theo sân: " + e.getMessage());
        }
        return list;
    }

    public ObservableList<DanhGiaSan> getDanhGiaByKhachHang(String maKH) {
        ObservableList<DanhGiaSan> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM DANHGIASAN WHERE MaKH = ? ORDER BY ThoiDiemDanhGia DESC";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maKH);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                DanhGiaSan dg = new DanhGiaSan(
                    rs.getString("MaDanhGia"),
                    rs.getInt("DiemDG"),
                    rs.getString("NhanXet"),
                    rs.getTimestamp("ThoiDiemDanhGia"),
                    rs.getString("MaKH"),
                    rs.getString("MaSan")
                );
                list.add(dg);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy đánh giá theo khách hàng: " + e.getMessage());
        }
        return list;
    }

    public boolean addDanhGia(DanhGiaSan dg) {
        String sql = "INSERT INTO DANHGIASAN (MaDanhGia, DiemDG, NhanXet, ThoiDiemDanhGia, MaKH, MaSan) " +
                    "VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, ?)";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, dg.getMaDanhGia());
            pstmt.setLong(2, dg.getDiemDG());
            pstmt.setString(3, dg.getNhanXet());
            pstmt.setString(4, dg.getMaKH());
            pstmt.setString(5, dg.getMaSan());

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
            System.out.println("Lỗi thêm đánh giá: " + e.getMessage());
            return false;
        }
    }

    public boolean updateDanhGia(DanhGiaSan dg) {
        String sql = "UPDATE DANHGIASAN SET DiemDG=?, NhanXet=? WHERE MaDanhGia=?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, dg.getDiemDG());
            pstmt.setString(2, dg.getNhanXet());
            pstmt.setString(3, dg.getMaDanhGia());

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
            System.out.println("Lỗi cập nhật đánh giá: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteDanhGia(String maDanhGia) {
        String sql = "DELETE FROM DANHGIASAN WHERE MaDanhGia=?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maDanhGia);

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
            System.out.println("Lỗi xóa đánh giá: " + e.getMessage());
            return false;
        }
    }

    public Double getDiemTrungBinhBySan(String maSan) {
        String sql = "SELECT AVG(DiemDG) AS diem FROM DANHGIASAN WHERE MaSan = ?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maSan);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double diem = rs.getDouble("diem");
                return diem > 0 ? diem : 0.0;
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy điểm trung bình: " + e.getMessage());
        }
        return 0.0;
    }

    public Long countDanhGiaBySan(String maSan) {
        String sql = "SELECT COUNT(*) AS cnt FROM DANHGIASAN WHERE MaSan = ?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maSan);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("cnt");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi đếm đánh giá: " + e.getMessage());
        }
        return 0L;
    }

    public DanhGiaSan getDanhGiaByMa(String maDG) {
        String sql = "SELECT * FROM DANHGIASAN WHERE MaDanhGia=?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maDG);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new DanhGiaSan(
                    rs.getString("MaDanhGia"),
                    rs.getInt("DiemDG"),
                    rs.getString("NhanXet"),
                    rs.getTimestamp("ThoiDiemDanhGia"),
                    rs.getString("MaKH"),
                    rs.getString("MaSan")
                );
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi tìm đánh giá: " + e.getMessage());
        }
        return null;
    }

    public void closeConnection() throws SQLException {
        connectDB.closeCon(con);
    }

}
