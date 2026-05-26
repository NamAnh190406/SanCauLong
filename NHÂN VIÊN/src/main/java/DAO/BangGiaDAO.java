/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;
import Utils.Databasehelper;
import Model.BANGGIA;
import java.sql.*;
import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
/**
 *
 * @author Hi
 */
public class BangGiaDAO {
    private Connection con;
    private Databasehelper condb;
    public BangGiaDAO() throws SQLException
    {
        condb=new Databasehelper();
        con= condb.createCon();
    }
    public ObservableList<BANGGIA> getallBanggias()
    {
        ObservableList<BANGGIA> list= FXCollections.observableArrayList();
        String sql= "SELECT * FROM BANGGIA order by MaBG";
        try{
            Statement stmt =con.createStatement();
            ResultSet rs= stmt.executeQuery(sql);
            while(rs.next())
            {
                BANGGIA bg= new BANGGIA(
                    rs.getString("MaBG"),
                    rs.getLong("DonGia"),
                    rs.getString("MaSan"),
                    rs.getString("MaKG")
                );
                list.add(bg);
         
            }
            rs.close();
            stmt.close();
        }
        catch(SQLException e)
        {
            System.out.println("Loi lay du lieu: "+ e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    public ObservableList<BANGGIA> getbangBanggias(String maSan)
    {
        ObservableList<BANGGIA> list =FXCollections.observableArrayList();
        String sql="SELECT * FROM BANGGIA WHERE MaSan = ? ORDER BY MaBG";
        try {
            PreparedStatement pstmt=con.prepareCall(sql);
            pstmt.setString(1,maSan);
            ResultSet rs=pstmt.executeQuery();
            while(rs.next())
            {
            BANGGIA bg= new BANGGIA(
                    rs.getString("MaBG"),
                    rs.getLong("DonGia"),
                    rs.getString("MaSan"),
                    rs.getString("MaKG")
            );
               list.add(bg);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy bảng giá theo sân: " + e.getMessage());
        }
        return list;
    }
    public Long getDonGiaBySanAndKhungGio(String maSan, String maKG) {
        String sql = "SELECT DonGia FROM BANGGIA WHERE MaSan = ? AND MaKG = ?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maSan);
            pstmt.setString(2, maKG);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("DonGia");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy đơn giá: " + e.getMessage());
        }
        return 0L;
    }

    public boolean addBangGia(BANGGIA bg) {
        String sql = "INSERT INTO BANGGIA (MaBG, DonGia, MaSan, MaKG) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, bg.getMaBG());
            pstmt.setLong(2, bg.getDonGia());
            pstmt.setString(3, bg.getMaSan());
            pstmt.setString(4, bg.getMaKG());

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
            System.out.println("Lỗi thêm bảng giá: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBangGia(BANGGIA bg) {
        String sql = "UPDATE BANGGIA SET DonGia = ? WHERE MaBG = ?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, bg.getDonGia());
            pstmt.setString(2, bg.getMaBG());

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
            System.out.println("Lỗi cập nhật bảng giá: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBangGia(String maBG) {
        String sql = "DELETE FROM BANGGIA WHERE MaBG=?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maBG);

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
            System.out.println("Lỗi xóa bảng giá: " + e.getMessage());
            return false;
        }
    }

    public ObservableList<BANGGIA> getBangGiaHieuLuc(String maSan, Timestamp ngayKiemTra) {
        ObservableList<BANGGIA> list = FXCollections.observableArrayList();
        if (ngayKiemTra == null) {
            ngayKiemTra = Timestamp.valueOf(LocalDateTime.now());
        }
        String sql = "SELECT * FROM BANGGIA WHERE MaSan = ? AND TrangThai = N'Hoạt động' AND (? >= NgayBD OR NgayBD IS NULL) AND (? <= NgayKT OR NgayKT IS NULL) ORDER BY MaBG";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, maSan);
            pstmt.setTimestamp(2, ngayKiemTra);
            pstmt.setTimestamp(3, ngayKiemTra);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BANGGIA bg = new BANGGIA(
                        rs.getString("MaBG"),
                        rs.getLong("DonGia"),
                        rs.getString("MaSan"),
                        rs.getString("MaKG")
                    );
                    list.add(bg);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy bảng giá hiệu lực: " + e.getMessage());
        }
        return list;
    }
    public void closeConnection() throws SQLException {
        condb.closeCon(con);
    }

}
