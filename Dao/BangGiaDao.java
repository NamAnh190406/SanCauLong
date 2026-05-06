/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;
import com.hoc.app_doan_scl.Databasehelper;
import Model.SDMAGG;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.sql.Timestamp;
import java.time.LocalDate;

/**
 *
 * @author Hi
 */
public class SDMAGGDAO {
    private Connection con;
    private Databasehelper connectDB;

    public SDMAGGDAO() throws SQLException {
        connectDB = new Databasehelper();
        con = connectDB.createCon();
    }
    public ObservableList<SDMAGG> getAllSuDungGG() {
        ObservableList<SDMAGG> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM SuDungGG ORDER BY NgaySD DESC";

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
//String maSuDung, LocalDate ngaySuDung, String makm, String makh
            while (rs.next()) {
                SDMAGG sg = new SDMAGG(
                    rs.getString("MaCoupon"),
                    rs.getDate("NgaySD").toLocalDate(),
                    rs.getString("MaKM"),
                    rs.getString("MaKH")
                );
                list.add(sg);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy dữ liệu mã giảm giá: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public ObservableList<SDMAGG> getSuDungGGByKhachHang(String maKH) {
        ObservableList<SDMAGG> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM SuDungGG WHERE MaKH = ? ORDER BY NgaySD DESC";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maKH);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                SDMAGG sg = new SDMAGG(
                    rs.getString("MaCoupon"),
                    rs.getDate("NgaySD").toLocalDate(),
                    rs.getString("MaKM"),
                    rs.getString("MaKH")
                );
                list.add(sg);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy mã giảm giá của khách hàng: " + e.getMessage());
        }
        return list;
    }

    public ObservableList<SDMAGG> getSuDungGGByKhuyenMai(String maKM) {
        ObservableList<SDMAGG> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM SuDungGG WHERE MaKM = ? ORDER BY NgaySD DESC";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maKM);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                SDMAGG sg = new SDMAGG(
                    rs.getString("MaCoupon"),
                    rs.getDate("NgaySD").toLocalDate(),
                    rs.getString("MaKM"),
                    rs.getString("MaKH")
                );
                list.add(sg);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy mã giảm giá theo khuyến mãi: " + e.getMessage());
        }
        return list;
    }

    public SDMAGG getSuDungGGByMaCoupon(String maCoupon) {
        String sql = "SELECT * FROM SuDungGG WHERE MaCoupon = ?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maCoupon);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new SDMAGG(
                    rs.getString("MaCoupon"),
                    rs.getDate("NgaySD").toLocalDate(),
                    rs.getString("MaKM"),
                    rs.getString("MaKH")
                );
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi tìm mã giảm giá: " + e.getMessage());
        }
        return null;
    }
    public boolean addSuDungGG(SDMAGG sg) {
        String sql = "INSERT INTO SuDungGG (MaCoupon, NgaySD, MaKH, MaKM) VALUES (?, CURRENT_TIMESTAMP, ?, ?)";

        try {
            if (!sg.kiemTraDaDung(sg.getMaKH(),sg.getMaKM())) {
                System.out.println("Khách hàng đã sử dụng khuyến mãi này rồi!");
                return false;
            }

            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, sg.getMaSuDung());
            pstmt.setString(2, sg.getMaKH());
            pstmt.setString(3, sg.getMaKM());

            int result = pstmt.executeUpdate();
            con.commit();
            pstmt.close();

            if (result > 0) {
                System.out.println("Thêm mã giảm giá thành công");
            }
            return result > 0;
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Lỗi thêm mã giảm giá: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteSuDungGG(String maCoupon) {
        String sql = "DELETE FROM SuDungGG WHERE MaCoupon = ?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maCoupon);

            int result = pstmt.executeUpdate();
            con.commit();
            pstmt.close();

            if (result > 0) {
                System.out.println("Xóa mã giảm giá thành công");
            }
            return result > 0;
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Lỗi xóa mã giảm giá: " + e.getMessage());
            return false;
        }
    }

    public String getKhuyenMaiHienTaiOfKhachHang(String maKH) {
        String sql = "SELECT MaKM FROM SuDungGG WHERE MaKH = ? ORDER BY NgaySD DESC FETCH FIRST 1 ROW ONLY";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maKH);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("MaKM");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy khuyến mãi hiện tại: " + e.getMessage());
        }
        return null;
    }

    public Long countSuDungByKhuyenMai(String maKM) {
        String sql = "SELECT COUNT(*) AS cnt FROM SuDungGG WHERE MaKM = ?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maKM);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("cnt");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi thống kê sử dụng: " + e.getMessage());
        }
        return 0L;
    }

    public ObservableList<SDMAGG> getSuDungGGByThoiGian(LocalDate ngayBD, LocalDate ngayKT) {
        ObservableList<SDMAGG> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM SuDungGG WHERE TRUNC(NgaySD) BETWEEN ? AND ? ORDER BY NgaySD DESC";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setDate(1, java.sql.Date.valueOf(ngayBD));
            pstmt.setDate(2, java.sql.Date.valueOf(ngayKT));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                SDMAGG sg = new SDMAGG(
                    rs.getString("MaCoupon"),
                    rs.getDate("NgaySD").toLocalDate(),
                    rs.getString("MaKM"),
                    rs.getString("MaKH")
                );
                list.add(sg);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy dữ liệu theo thời gian: " + e.getMessage());
        }
        return list;
    }

    public Long getTongSuDungInRange(LocalDate ngayBD, LocalDate ngayKT) {
        String sql = "SELECT COUNT(*) AS cnt FROM SuDungGG WHERE TRUNC(NgaySD) BETWEEN ? AND ?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setDate(1, java.sql.Date.valueOf(ngayBD));
            pstmt.setDate(2, java.sql.Date.valueOf(ngayKT));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("cnt");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi thống kê sử dụng: " + e.getMessage());
        }
        return 0L;
    }

    public void closeConnection() throws SQLException {
        connectDB.closeCon(con);
    }

}
