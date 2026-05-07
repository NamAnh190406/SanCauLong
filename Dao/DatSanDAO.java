/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;
import com.hoc.app_doan_scl.Databasehelper;
import Model.DATSAN;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
/**
 *
 * @author Hi
 */
public class DatSanDAO {
    Connection con;
    Databasehelper condb;

    public DatSanDAO() throws SQLException {
        condb= new Databasehelper();
        con= condb.createCon();
    }
    public ObservableList<DATSAN> getallDatsans()
    {
        ObservableList<DATSAN> list = FXCollections.observableArrayList();
        String sql=" SELECT * FROM DATSAN ORDER BY MaDS";
        try {
            Statement stmt = con.createStatement();
            ResultSet rs= stmt.executeQuery(sql);
            while(rs.next())
            {
                DATSAN ds = new DATSAN(
                        rs.getString("MaDS"),
                        rs.getString("MaKH"),
                        rs.getString("MaNV"),
                        rs.getString("MaHD"),
                        rs.getDate("NgayDat").toLocalDate(),
                        rs.getLong("TongTienTamTinh"),
                        rs.getString("TrangThai")
                );
                list.add(ds);
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
    public ObservableList<DATSAN> getbangDatsans(String maKH)
    {
        ObservableList<DATSAN> list =FXCollections.observableArrayList();
        String sql="SELECT * FROM DATSAN WHERE MaKH = ? ORDER BY MaDS";
        try {
            PreparedStatement pstmt=con.prepareStatement(sql);
            pstmt.setString(1,maKH);
            ResultSet rs=pstmt.executeQuery();
            while(rs.next())
            {
            DATSAN ds= new DATSAN(
                        rs.getString("MaDS"),
                        rs.getString("MaKH"),
                        rs.getString("MaNV"),
                        rs.getString("MaHD"),
                        rs.getDate("NgayDat").toLocalDate(),
                        rs.getLong("TongTienTamTinh"),
                        rs.getString("TrangThai")
            );
               list.add(ds);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy thông tin đặt sân theo khách hàng: " + e.getMessage());
        }
        return list;
    }
    public boolean addDatSan(DATSAN ds)
    {
        String sql="INSERT INTO DATSAN (MaDS, MaKH, MaNV, MaHD, NgayDat, TrangThai, TongTienTamTinh) VALUES (?,?,?,?,?,?,?)";
        try{
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, ds.getMaDS());
            pstmt.setString(2,ds.getMaKH());
            pstmt.setString(3, ds.getMaNV());
            pstmt.setString(4, ds.getMaHD());
            pstmt.setDate(5, java.sql.Date.valueOf(ds.getNgayDat()));
            pstmt.setString(6, ds.getTrangThai());
            pstmt.setLong(7, ds.getTongTienTamTinh());
            int result = pstmt.executeUpdate();
            con.commit();
            pstmt.close();
            return result>0;
        }
        catch(SQLException e)
        {
            try{
                con.rollback();
            }
            catch(SQLException ex)
            {
                ex.printStackTrace();
            }
            System.out.println("Lỗi thêm đặt sân: "+e.getMessage());
            return false;
        }
    }
    public boolean updateDatSan (DATSAN ds)
    {
        String sql="UPDATE DATSAN SET MaKH=?, MaNV=?, MaHD=?, NgayDat=?, TrangThai=?, TongTienTamTinh=? WHERE MaDS=?";
        try{
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, ds.getMaKH());
            pstmt.setString(2, ds.getMaNV());
            pstmt.setString(3, ds.getMaHD());
            pstmt.setDate(4, java.sql.Date.valueOf(ds.getNgayDat()));
            pstmt.setString(5, ds.getTrangThai());
            pstmt.setLong(6, ds.getTongTienTamTinh());
            pstmt.setString(7, ds.getMaDS());
            int result = pstmt.executeUpdate();
            con.commit();
            pstmt.close();
            return result > 0;
        }
        catch(SQLException e)
        {
            try {
                con.rollback();
            } catch(SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Lỗi cập nhật đặt sân: " + e.getMessage());
            return false;
        }
    }
    public  boolean deleteDatSan (String maDS)
    {
        String sql = "DELETE FROM DATSAN WHERE MaDS = ?";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maDS);
            int result = pstmt.executeUpdate();
            con.commit();
            pstmt.close();
            return result > 0;
        } catch(SQLException e) {
            try {
                con.rollback();
            } catch(SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Lỗi xóa đặt sân: " + e.getMessage());
            return false;
        }
    }
    public boolean huyDatSan( DATSAN ds)
    {
        String sql = "UPDATE DATSAN SET TrangThai = ? WHERE MaDS = ?";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, "Da Huy");
            pstmt.setString(2, ds.getMaDS());
            int result = pstmt.executeUpdate();
            con.commit();
            pstmt.close();
            return result > 0;
        } catch(SQLException e) {
            try {
                con.rollback();
            } catch(SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Lỗi hủy đặt sân: " + e.getMessage());
            return false;
        }
    }
    public void xacNhanThanhToan( DATSAN ds)
    {
        String sql = "UPDATE DATSAN SET TrangThai = ? WHERE MaDS = ?";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, "Da Thanh Toan");
            pstmt.setString(2, ds.getMaDS());
            pstmt.executeUpdate();
            con.commit();
            pstmt.close();
        } catch(SQLException e) {
            try {
                con.rollback();
            } catch(SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Lỗi xác nhận thanh toán: " + e.getMessage());
        }
    }
    public long TinhThanhTien(String maDS)
    {
        String sql = "SELECT COALESCE(SUM(DonGia * SoLuong), 0) as ThanhTien FROM CTDS WHERE MaDS = ?";
        // coalesce la ham hop nhat neu nhu du lieu truyen vao bang null thi se mac dinh la 0
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maDS);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                return rs.getLong("ThanhTien");
            }
            rs.close();
            pstmt.close();
        } catch(SQLException e) {
            System.out.println("Lỗi tính thành tiền: " + e.getMessage());
        }
        return 0;
    
    }
}
