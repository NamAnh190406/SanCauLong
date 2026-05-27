package DAO;

import Utils.Databasehelper;
import Model.ThanhToan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class ThanhToanDAO {
    private Databasehelper connectDB;

    public ThanhToanDAO() {
        connectDB = new Databasehelper();
    }

    public ObservableList<ThanhToan> getAllThanhToan() {
        ObservableList<ThanhToan> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM THANHTOAN ORDER BY ThoiGianTT DESC";

        try (Connection con = connectDB.createCon();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new ThanhToan(
                    rs.getString("MaTT"),
                    rs.getString("PTTT"),
                    rs.getTimestamp("ThoiGianTT"),
                    rs.getString("TrangThai"),
                    rs.getString("MaHoaDon"),
                    rs.getLong("SoTien")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy dữ liệu thanh toán: " + e.getMessage());
        }
        return list;
    }

    public ObservableList<ThanhToan> getThanhToanByHoaDon(String maHoaDon) {
        ObservableList<ThanhToan> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM THANHTOAN WHERE MaHoaDon = ? ORDER BY ThoiGianTT DESC";

        try (Connection con = connectDB.createCon();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
             
            pstmt.setString(1, maHoaDon);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new ThanhToan(
                        rs.getString("MaTT"),
                        rs.getString("PTTT"),
                        rs.getTimestamp("ThoiGianTT"),
                        rs.getString("TrangThai"),
                        rs.getString("MaHoaDon"),
                        rs.getLong("SoTien")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy thanh toán theo hóa đơn: " + e.getMessage());
        }
        return list;
    }

    public ObservableList<ThanhToan> getThanhToanByTrangThai(String trangThai) {
        ObservableList<ThanhToan> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM THANHTOAN WHERE TrangThai = ? ORDER BY ThoiGianTT DESC";

        try (Connection con = connectDB.createCon();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
             
            pstmt.setString(1, trangThai);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new ThanhToan(
                        rs.getString("MaTT"),
                        rs.getString("PTTT"),
                        rs.getTimestamp("ThoiGianTT"),
                        rs.getString("TrangThai"),
                        rs.getString("MaHoaDon"),
                        rs.getLong("SoTien")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy thanh toán theo trạng thái: " + e.getMessage());
        }
        return list;
    }

    /**
     * ✅ HÀM NÀY ĐƯỢC THÊM VÀO ĐỂ FIX LỖI "cannot find symbol" CHO CONTROLLER
     */
    public boolean insertThanhToan(String maHD, long soTien, String phuongThuc) {
        String maTT = "TT" + (System.currentTimeMillis() % 100000);
        String sql = "INSERT INTO THANHTOAN (MaTT, MaHoaDon, SoTien, PTTT, ThoiGianTT, TrangThai) " +
                     "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, 'ThanhCong')";

        try (Connection con = connectDB.createCon();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
             
            pstmt.setString(1, maTT);
            pstmt.setString(2, maHD);
            pstmt.setLong(3, soTien);
            pstmt.setString(4, phuongThuc);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi lưu lịch sử thanh toán: " + e.getMessage());
            return false;
        }
    }

    public boolean addThanhToan(ThanhToan tt) {
        String sql = "INSERT INTO THANHTOAN (MaTT, PTTT, ThoiGianTT, TrangThai, MaHoaDon, SoTien) " +
                     "VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?, ?)";

        try (Connection con = connectDB.createCon();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
             
            pstmt.setString(1, tt.getMaTT());
            pstmt.setString(2, tt.getPttt());
            pstmt.setString(3, tt.getTrangThai());
            pstmt.setString(4, tt.getMaHoaDon());
            pstmt.setLong(5, tt.getSoTien());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm thanh toán: " + e.getMessage());
            return false;
        }
    }

    public boolean updateThanhToan(ThanhToan tt) {
        String sql = "UPDATE THANHTOAN SET PTTT=?, TrangThai=? WHERE MaTT=?";

        try (Connection con = connectDB.createCon();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
             
            pstmt.setString(1, tt.getPttt());
            pstmt.setString(2, tt.getTrangThai());
            pstmt.setString(3, tt.getMaTT());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật thanh toán: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTrangThaiThanhToan(String maTT, String trangThai) {
        String sql = "UPDATE THANHTOAN SET TrangThai=? WHERE MaTT=?";

        try (Connection con = connectDB.createCon();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
             
            pstmt.setString(1, trangThai);
            pstmt.setString(2, maTT);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật trạng thái: " + e.getMessage());
            return false;
        }
    }

    public ThanhToan getThanhToanByMa(String maTT) {
        String sql = "SELECT * FROM THANHTOAN WHERE MaTT=?";

        try (Connection con = connectDB.createCon();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
             
            pstmt.setString(1, maTT);
            try (ResultSet rs = pstmt.executeQuery()) {
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
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm thanh toán: " + e.getMessage());
        }
        return null;
    }

    public Long countThanhToanThanhCong() {
        String sql = "SELECT COUNT(*) AS cnt FROM THANHTOAN WHERE TrangThai = 'ThanhCong'";

        try (Connection con = connectDB.createCon();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getLong("cnt");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thống kê: " + e.getMessage());
        }
        return 0L;
    }
}