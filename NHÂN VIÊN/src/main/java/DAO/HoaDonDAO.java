package DAO;

import Utils.Databasehelper;
import Model.HOADON;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;

public class HoaDonDAO {
    private Databasehelper connectDB;

    public HoaDonDAO() {
        connectDB = new Databasehelper();
    }

    public ObservableList<HOADON> getAllHoaDon() {
        ObservableList<HOADON> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM HOADON ORDER BY MaHoaDon DESC";

        try (Connection con = connectDB.createCon();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                HOADON hd = new HOADON(
                        rs.getString("MaHoaDon"),
                        rs.getLong("TongTienDV"),
                        rs.getLong("SoTienGG"),
                        rs.getString("Ghichu"),
                        rs.getDate("NgayXuat") != null ? rs.getDate("NgayXuat").toLocalDate() : null,
                        rs.getString("MaDS"));
                hd.setTrangThai(rs.getString("TrangThai"));
                try {
                    hd.setLoaiHD(rs.getString("LoaiHD"));
                } catch (Exception ignored) {
                }
                try {
                    hd.setThanhTien(rs.getLong("ThanhTien"));
                } catch (Exception ignored) {
                }
                list.add(hd);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy dữ liệu hóa đơn: " + e.getMessage());
        }
        return list;
    }

    /**
     *
     * @param maHoaDon
     * @return
     */
    public boolean deleteHoaDon(String maHoaDon) {
        String sql = "DELETE FROM HOADON WHERE MaHoaDon = ?";

        try (Connection con = connectDB.createCon();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maHoaDon);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi xóa hóa đơn: " + e.getMessage());
            return false;
        }
    }

    public boolean addHoaDon(HOADON hd) {
        String sql = "INSERT INTO HOADON (MaHoaDon, TongTienDV, SoTienGG, ThanhTien, Ghichu, MaDS) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = connectDB.createCon();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, hd.getMaHD());
            pstmt.setLong(2, hd.getTongTienDV());
            pstmt.setLong(3, hd.getSoTienGiam());
            pstmt.setLong(4, hd.getThanhTien());
            pstmt.setString(5, hd.getGhiChu());
            pstmt.setString(6, hd.getMaDS());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm hóa đơn: " + e.getMessage());
            return false;
        }
    }

    public boolean addHoaDonDatSan(String maDatSan, long tongTienSan) {
        String sql = "INSERT INTO HOADON (MaHoaDon, MaDS, LoaiHD, TongTienDV, SoTienGG, Ghichu, TrangThai, NgayXuat) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, SYSDATE)";
        try (Connection con = connectDB.createCon();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            String maHD = "HDSAN"
                    + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
            pstmt.setString(1, maHD);
            pstmt.setString(2, maDatSan);
            pstmt.setString(3, HOADON.LOAI_DAT_SAN);
            pstmt.setLong(4, 0);
            pstmt.setLong(5, 0);
            pstmt.setString(6, "");
            pstmt.setString(7, "Chua Thanh Toan");

            boolean ok = pstmt.executeUpdate() > 0;
            if (ok)
                System.out.println("✓ Tạo hóa đơn đặt sân: " + maHD + " cho DS " + maDatSan);
            return ok;
        } catch (SQLException e) {
            System.err.println("Lỗi tạo hóa đơn đặt sân: " + e.getMessage());
            return false;
        }
    }

    public boolean addHoaDon(String maDatSan, long tongTien) {
        return addHoaDonDatSan(maDatSan, tongTien);
    }

    public String addHoaDonDichVu(String maDatSan, long tongTienDichVu, String ghiChu) {
        String sql = "INSERT INTO HOADON (MaHoaDon, MaDS, LoaiHD, TongTienDV, SoTienGG, Ghichu, TrangThai, NgayXuat) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, SYSDATE)";
        try (Connection con = connectDB.createCon();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            String maHD = "HDDV"
                    + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
            pstmt.setString(1, maHD);
            if (maDatSan != null && !maDatSan.isEmpty()) {
                pstmt.setString(2, maDatSan);
            } else {
                pstmt.setNull(2, java.sql.Types.VARCHAR);
            }
            pstmt.setString(3, HOADON.LOAI_DICH_VU);
            pstmt.setLong(4, tongTienDichVu);
            pstmt.setLong(5, 0);
            pstmt.setString(6, ghiChu != null ? ghiChu : "");
            pstmt.setString(7, "Chua Thanh Toan");
            if (pstmt.executeUpdate() > 0) {
                System.out.println("Tạo hóa đơn dịch vụ mới: " + maHD);
                return maHD;
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Lỗi tạo hóa đơn dịch vụ: " + e.getMessage());
            return null;
        }
    }

    public boolean capNhatNgayXuatVaThanhToan(String maHD) {
        String sql = "UPDATE HOADON SET NgayXuat = SYSDATE, TrangThai = 'Da Thanh Toan' WHERE MaHoaDon = ?";

        try (Connection con = connectDB.createCon();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maHD);
            int result = pstmt.executeUpdate();

            if (result > 0) {
                try {
                    ThongBaoDAO.themThongBao("Thanh toán hóa đơn",
                            "Hóa đơn " + maHD + " đã được thanh toán.", "success");
                } catch (Exception ex) {
                    System.err.println("Không thể ghi log: " + ex.getMessage());
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật ngày xuất: " + e.getMessage());
        }
        return false;
    }

    public HOADON getHoaDonByMaDS(String maDS) {
        String sql = "SELECT * FROM (SELECT * FROM HOADON WHERE MaDS = ? ORDER BY MaHoaDon DESC) WHERE ROWNUM <= 1";
        try (Connection con = connectDB.createCon();
                PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, maDS);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    HOADON hd = new HOADON(
                            rs.getString("MaHoaDon"),
                            rs.getLong("TongTienDV"),
                            rs.getLong("SoTienGG"),
                            rs.getString("Ghichu"),
                            rs.getDate("NgayXuat") != null ? rs.getDate("NgayXuat").toLocalDate() : null,
                            rs.getString("MaDS"));
                    hd.setTrangThai(rs.getString("TrangThai"));
                    return hd;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy hóa đơn theo MaDS: " + e.getMessage());
        }
        return null;
    }

    public boolean updateTrangThaiHoaDon(String maHD, String trangThaiMoi) {
        String sql = "UPDATE HOADON SET TrangThai = ? WHERE MaHoaDon = ?";

        try (Connection con = connectDB.createCon();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, trangThaiMoi);
            pstmt.setString(2, maHD);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi update trạng thái hóa đơn: " + e.getMessage());
            return false;
        }
    }

    public Long getTongDoanhThuByNgay(LocalDate ngay) {
        String sql = "SELECT SUM(NVL(hd.ThanhTien, 0) + NVL(ds.TongTienTamTinh, 0)) AS tong FROM HOADON hd " +
                "LEFT JOIN DATSAN ds ON hd.MaDS = ds.MaDS " +
                "WHERE hd.NgayXuat >= TRUNC(?) AND hd.NgayXuat < TRUNC(?) + 1 " +
                "AND (hd.TrangThai LIKE '%Da Thanh Toan%' OR hd.TrangThai = 'HoanThanh')";
        try (Connection con = connectDB.createCon();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setDate(1, java.sql.Date.valueOf(ngay));
            pstmt.setDate(2, java.sql.Date.valueOf(ngay));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("tong");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tính doanh thu: " + e.getMessage());
        }
        return 0L;
    }

    public java.util.List<Object[]> getHoaDonDichVu() {
        java.util.List<Object[]> result = new java.util.ArrayList<>();
        String sql = "SELECT hd.MaHoaDon, hd.TrangThai, hd.Ghichu, hd.ThanhTien, " +
                "       hd.NgayXuat, hd.MaDS, " +
                "       NVL(kh.HoTen, 'Khach le') AS TenKH, " +
                "       NVL(s.TenSan, hd.MaDS)   AS TenSan " +
                "FROM HOADON hd " +
                "LEFT JOIN DATSAN ds ON hd.MaDS = ds.MaDS " +
                "LEFT JOIN KHACHHANG kh ON ds.MaKH = kh.MaKH " +
                "LEFT JOIN SAN s ON ds.MaSan = s.MaSan " +
                "WHERE hd.LoaiHD = 'DICH_VU' " +
                "ORDER BY hd.NgayXuat DESC NULLS LAST";
        try (Connection con = connectDB.createCon();
                PreparedStatement pstmt = con.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Object[] row = new Object[8];
                row[0] = rs.getString("MaHoaDon");
                row[1] = rs.getString("TenKH");
                row[2] = rs.getString("TenSan");
                row[3] = rs.getLong("ThanhTien");
                row[4] = rs.getString("TrangThai");
                row[5] = rs.getString("Ghichu");
                row[6] = rs.getDate("NgayXuat") != null
                        ? rs.getDate("NgayXuat").toLocalDate()
                        : null;
                row[7] = rs.getString("MaDS");
                result.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy hóa đơn dịch vụ: " + e.getMessage());
        }
        return result;
    }

    public java.util.List<Object[]> getCTDVByHoaDon(String maHD) {
        java.util.List<Object[]> result = new java.util.ArrayList<>();
        String sql = "SELECT dv.TenDV, ct.SoLuong, ct.ThanhTien, ct.MaDV " +
                "FROM CTDV ct " +
                "JOIN DICHVU dv ON ct.MaDV = dv.MaDV " +
                "JOIN HOADON hd ON ct.MaHoaDon = hd.MaHoaDon " +
                "WHERE hd.MaHoaDon = ?";
        try (Connection con = connectDB.createCon();
                PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, maHD);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    long sl = rs.getLong("SoLuong");
                    long tt = rs.getLong("ThanhTien");
                    long dg = sl > 0 ? tt / sl : 0;
                    result.add(new Object[] {
                            rs.getString("TenDV"),
                            sl,
                            dg,
                            rs.getString("MaDV")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy CTDV: " + e.getMessage());
        }
        return result;
    }

}
