/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;
import com.hoc.app_doan_scl.Databasehelper;
import Model.HOADON;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;
/**
 *
 * @author Hi
 */
public class HoaDonDAO {
    private Connection con;
    private Databasehelper connectDB;

    public HoaDonDAO() throws SQLException {
        connectDB = new Databasehelper();
        con = connectDB.createCon();
    }

    public ObservableList<HOADON> getAllHoaDon() {
        ObservableList<HOADON> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM HOADON ORDER BY MaHoaDon";

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
//String maHD, long tongTienDV, long soTienGiam, String ghichu, LocalDate ngayXuat, String maDS
            while (rs.next()) {
                    HOADON hd = new HOADON(
                    rs.getString("MaHoaDon"),
                    rs.getLong("TongTienDV"),
                    rs.getLong("SoTienGG"),
                    rs.getString("Ghichu"),
                    rs.getDate("NgayXuat").toLocalDate(),
                    rs.getString("MaDS")
                );
                list.add(hd);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy dữ liệu hóa đơn: " + e.getMessage());
        }
        return list;
    }

    public ObservableList<HOADON> getHoaDonByNgay(LocalDate ngay) {
        ObservableList<HOADON> list = FXCollections.observableArrayList();
        String sql = "SELECT hd.* FROM HOADON hd JOIN DATSAN ds ON hd.MaDS = ds.MaDS " +
                    "WHERE TRUNC(ds.NgayDat) = TRUNC(?) ORDER BY hd.MaHoaDon";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setDate(1, java.sql.Date.valueOf(ngay));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                HOADON hd = new HOADON(
                    rs.getString("MaHoaDon"),
                    rs.getLong("TongTienDV"),
                    rs.getLong("SoTienGG"),
                    rs.getString("Ghichu"),
                    rs.getDate("NgayXuat").toLocalDate(),
                    rs.getString("MaDS")
                );
                list.add(hd);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy hóa đơn theo ngày: " + e.getMessage());
        }
        return list;
    }

    public boolean addHoaDon(HOADON hd) {
        String sql = "INSERT INTO HOADON (MaHoaDon, TongTienDV, SoTienGG, ThanhTien, Ghichu, MaDS) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, hd.getMaHD());
            pstmt.setLong(2, hd.getTongTienDV());
            pstmt.setLong(3, hd.getSoTienGiam());
            pstmt.setLong(4, hd.getThanhTien());
            pstmt.setString(5, hd.getGhiChu());
            pstmt.setString(6, hd.getMaDS());

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
            System.out.println("Lỗi thêm hóa đơn: " + e.getMessage());
            return false;
        }
    }

    public boolean updateHoaDon(HOADON hd) {
        String sql = "UPDATE HOADON SET TongTienDV=?, SoTienGG=?, ThanhTien=?, Ghichu=? WHERE MaHoaDon=?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, hd.getTongTienDV());
            pstmt.setLong(2, hd.getSoTienGiam());
            pstmt.setLong(3, hd.getThanhTien());
            pstmt.setString(4, hd.getGhiChu());
            pstmt.setString(5, hd.getMaHD());

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
            System.out.println("Lỗi cập nhật hóa đơn: " + e.getMessage());
            return false;
        }
    }

    public HOADON getHoaDonByMa(String maHoaDon) {
        String sql = "SELECT * FROM HOADON WHERE MaHoaDon=?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maHoaDon);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new HOADON(
                    rs.getString("MaHoaDon"),
                    rs.getLong("TongTienDV"),
                    rs.getLong("SoTienGG"),
                    rs.getString("Ghichu"),
                    rs.getDate("NgayXuat").toLocalDate(),
                    rs.getString("MaDS")
                );
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi tìm hóa đơn: " + e.getMessage());
        }
        return null;
    }

    public HOADON getHoaDonByDatSan(String maDS) {
        String sql = "SELECT * FROM HOADON WHERE MaDS=?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maDS);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new HOADON(
                    rs.getString("MaHoaDon"),
                    rs.getLong("TongTienDV"),
                    rs.getLong("SoTienGG"),
                    rs.getString("Ghichu"),
                    rs.getDate("NgayXuat").toLocalDate(),
                    rs.getString("MaDS")
                );
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi tìm hóa đơn: " + e.getMessage());
        }
        return null;
    }

    public Long getTongDoanhThuByNgay(LocalDate ngay) {
        String sql = "SELECT SUM(ThanhTien) AS tong FROM HOADON hd " +
                    "JOIN DATSAN ds ON hd.MaDS = ds.MaDS " +
                    "WHERE TRUNC(ds.NgayDat) = TRUNC(?)";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setDate(1, java.sql.Date.valueOf(ngay));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("tong");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi tính doanh thu: " + e.getMessage());
        }
        return 0L;
    }

    public void closeConnection() throws SQLException {
        connectDB.closeCon(con);
    }

}
