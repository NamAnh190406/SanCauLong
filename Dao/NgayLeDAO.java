/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;
import com.hoc.app_doan_scl.Databasehelper;
import Model.NgayLe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;

/**
 *
 * @author Hi
 */
public class NgayLeDAO {
    private Connection con;
    private Databasehelper connectDB;

    public NgayLeDAO() throws SQLException {
        connectDB = new Databasehelper();
        con = connectDB.createCon();
    }

    public ObservableList<NgayLe> getAllNgayLe() {
        ObservableList<NgayLe> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM NGAYLE ORDER BY NgayCuThe";

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                NgayLe nl = new NgayLe(
                    rs.getString("MaNL"),
                    rs.getString("TenNL"),
                    rs.getDate("NgayBatDau").toLocalDate(),
                    rs.getDate("NgayKetThuc").toLocalDate(),
                    rs.getLong("GiaPhuThu"),
                    rs.getString("MoTa")
                );
                list.add(nl);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy dữ liệu ngày lễ: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public ObservableList<NgayLe> getNgayLeByThang(int thang, int nam) {
        ObservableList<NgayLe> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM NGAYLE WHERE EXTRACT(MONTH FROM NgayCuThe) = ? " + "AND EXTRACT(YEAR FROM NgayCuThe) = ? ORDER BY NgayCuThe";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, thang);
            pstmt.setInt(2, nam);
            ResultSet rs = pstmt.executeQuery();
//String maNgayLe, String tenNgayLe, LocalDate ngayBatDau, LocalDate ngayKetThuc, long giaPhuThu, String moTa
            while (rs.next()) {
                NgayLe nl = new NgayLe(
                    rs.getString("MaNL"),
                    rs.getString("TenNL"),
                    rs.getDate("NgayBatDau").toLocalDate(),
                    rs.getDate("NgayKetThuc").toLocalDate(),
                    rs.getLong("GiaPhuThu"),
                    rs.getString("MoTa")
                );
                list.add(nl);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy ngày lễ theo tháng: " + e.getMessage());
        }
        return list;
    }

    public NgayLe getNgayLeByNgay(LocalDate ngay) {
        String sql = "SELECT * FROM NGAYLE WHERE TRUNC(NgayCuThe) = TRUNC(?)";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setDate(1, java.sql.Date.valueOf(ngay));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new NgayLe(
                    rs.getString("MaNL"),
                    rs.getString("TenNL"),
                    rs.getDate("NgayBatDau").toLocalDate(),
                    rs.getDate("NgayKetThuc").toLocalDate(),
                    rs.getLong("GiaPhuThu"),
                    rs.getString("MoTa")
                );
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi tìm ngày lễ: " + e.getMessage());
        }
        return null;
    }

    public NgayLe getNgayLeByMa(String maNL) {
        String sql = "SELECT * FROM NGAYLE WHERE MaNL = ?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maNL);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new NgayLe(
                    rs.getString("MaNL"),
                    rs.getString("TenNL"),
                    rs.getDate("NgayBatDau").toLocalDate(),
                    rs.getDate("NgayKetThuc").toLocalDate(),
                    rs.getLong("GiaPhuThu"),
                    rs.getString("MoTa")
                );
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi tìm ngày lễ: " + e.getMessage());
        }
        return null;
    }

    public boolean addNgayLe(NgayLe nl) {
        
        String sql = "INSERT INTO NGAYLE (MaNL, TenNL, NgayCuThe, GiaPhuThu) VALUES (?, ?, ?, ?)";

        try {
            if ( !nl.kiemTraNgayLe(nl.getNgayBatDau())) {
                System.out.println("Dữ liệu ngày lễ không hợp lệ!");
                return false;
            }
            if (isNgayLe(nl.getNgayBatDau())) {
                System.out.println("Ngày lễ này đã tồn tại trong hệ thống!");
                return false;
            }
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, nl.getMaNgayLe());
            pstmt.setString(2, nl.getTenNgayLe());
            pstmt.setDate(3, java.sql.Date.valueOf(nl.getNgayBatDau()));
            pstmt.setDate(4, java.sql.Date.valueOf(nl.getNgayKetThuc()));
            pstmt.setLong(5, nl.getGiaPhuThu());

            int result = pstmt.executeUpdate();
            con.commit();
            pstmt.close();

            if (result > 0) {
                System.out.println("Thêm ngày lễ: " + nl.getTenNgayLe());
            }
            return result > 0;
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Lỗi thêm ngày lễ: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateNgayLe(NgayLe nl) {
        String sql = "UPDATE NGAYLE SET TenNL=?, NgayCuThe=?, GiaPhuThu=? WHERE MaNL=?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, nl.getTenNgayLe());
            pstmt.setDate(2, java.sql.Date.valueOf(nl.getNgayBatDau()));
            pstmt.setDate(3, java.sql.Date.valueOf(nl.getNgayKetThuc()));
            pstmt.setLong(4, nl.getGiaPhuThu());

            int result = pstmt.executeUpdate();
            con.commit();
            pstmt.close();

            if (result > 0) {
                System.out.println("Cập nhật ngày lễ: " + nl.getTenNgayLe());
            }
            return result > 0;
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Lỗi cập nhật ngày lễ: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteNgayLe(String maNL) {
        String sql = "DELETE FROM NGAYLE WHERE MaNL=?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maNL);

            int result = pstmt.executeUpdate();
            con.commit();
            pstmt.close();

            if (result > 0) {
                System.out.println("Xóa ngày lễ thành công");
            }
            return result > 0;
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Lỗi xóa ngày lễ: " + e.getMessage());
            return false;
        }
    }

    public Long getGiaPhuThuByNgay(LocalDate ngay) {
        String sql = "SELECT GiaPhuThu FROM NGAYLE WHERE TRUNC(NgayCuThe) = TRUNC(?)";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setDate(1, java.sql.Date.valueOf(ngay));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("GiaPhuThu");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy giá phụ thu: " + e.getMessage());
        }
        return 0L;
    }

    public boolean isNgayLe(LocalDate ngay) {
        return getNgayLeByNgay(ngay) != null;
    }

    public Long getTongGiaPhuThuInRange(LocalDate ngayBD, LocalDate ngayKT) {
        String sql = "SELECT SUM(GiaPhuThu) AS tong FROM NGAYLE " + "WHERE NgayCuThe BETWEEN ? AND ?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setDate(1, java.sql.Date.valueOf(ngayBD));
            pstmt.setDate(2, java.sql.Date.valueOf(ngayKT));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("tong");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi tính giá phụ thu: " + e.getMessage());
        }
        return 0L;
    }

    public void closeConnection() throws SQLException {
        connectDB.closeCon(con);
    }
}
