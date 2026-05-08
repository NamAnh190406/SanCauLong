/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;
import com.hoc.app_doan_scl.Databasehelper;
import Model.CTHD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 *
 * @author Hi
 */
public class CTHDDAO {
    private Connection con;
    private Databasehelper connectDB;
    public CTHDDAO() throws SQLException
    {
        connectDB =new Databasehelper();
        con= connectDB.createCon();
    }
    public void insert(CTHD cthd) {
        String sql = "INSERT INTO CTHD (maHD, maCTDS, maSan, tienThanhToan) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, cthd.getMaHD());
            pstmt.setString(2, cthd.getMaCTDS());
            pstmt.setString(3, cthd.getMaSan());
            pstmt.setLong(4, cthd.getTienThanhToan());
            pstmt.executeUpdate();
            System.out.println("Thêm CTHD thành công!");
        } catch (SQLException e) {
            System.err.println("Lỗi thêm CTHD: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void update(CTHD cthd) {
        String sql = "UPDATE CTHD SET maCTDS = ?, maSan = ?, tienThanhToan = ? WHERE maHD = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, cthd.getMaCTDS());
            pstmt.setString(2, cthd.getMaSan());
            pstmt.setLong(3, cthd.getTienThanhToan());
            pstmt.setString(4, cthd.getMaHD());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Cập nhật CTHD thành công!");
            } else {
                System.out.println("Không tìm thấy CTHD với mã: " + cthd.getMaHD());
            }
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật CTHD: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void delete(String maHD) {
        String sql = "DELETE FROM CTHD WHERE maHD = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, maHD);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Xóa CTHD thành công!");
            } else {
                System.out.println("Không tìm thấy CTHD với mã: " + maHD);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi xóa CTHD: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public CTHD getById(String maHD) {
        String sql = "SELECT * FROM CTHD WHERE maHD = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, maHD);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToCTHD(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm kiếm CTHD: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    public List<CTHD> getAll() {
        List<CTHD> list = new ArrayList<>();
        String sql = "SELECT * FROM CTHD";
        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(mapResultSetToCTHD(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách CTHD: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    public void closeCon() throws SQLException {
        connectDB.closeCon(con);
    }
    private CTHD mapResultSetToCTHD(ResultSet rs) throws SQLException {
        CTHD cthd = new CTHD();
        cthd.setMaHD(rs.getString("maHD"));
        cthd.setMaCTDS(rs.getString("maCTDS"));
        cthd.setMaSan(rs.getString("maSan"));
        cthd.setSoLuong(rs.getInt("soLuong"));
        cthd.setDonGia(rs.getLong("donGia"));
        return cthd;
    }
}
