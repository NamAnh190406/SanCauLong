import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class QUANTRIVIEN {
    public String MaQTV;
    public String HoTenQTV;
    public String SDT;
    public String Email;
    public String ChucVu;
    public String MaTK;

    private Connection conn;

    public QUANTRIVIEN(Connection conn) {
        this.conn = conn;
    }

    public boolean ThemSan(SAN san) {
        String sql = "INSERT INTO SAN (MaSan, TenSan, LoaiSan, LoaiMatSan, KhongGian, SLNguoiChoi, GiaThueTheoGio, TrangThai, MoTa, DiaChi) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, san.MaSan);
            pstmt.setString(2, san.TenSan);
            pstmt.setString(3, san.LoaiSan);
            pstmt.setString(4, san.LoaiMatSan);
            pstmt.setString(5, san.KhongGian);
            pstmt.setInt(6, san.SLNguoiChoi);
            pstmt.setLong(7, san.GiaThueTheoGio);
            pstmt.setString(8, san.TrangThai);
            pstmt.setString(9, san.MoTa);
            pstmt.setString(10, san.DiaChi);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm sân: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean CapNhatBangGia(BANGGIA bg) {
        String sql = "UPDATE BANGGIA SET DonGia = ? WHERE MaBG = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, bg.DonGia);
            pstmt.setString(2, bg.MaBG);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật bảng giá: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean TaoKM(KHUYENMAI km) {
        String sql = "INSERT INTO KHUYENMAI (MaKM, TenKM, PhanTramGG, GTriToiDa, NgayBD, NgayKT) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, km.maKM);
            pstmt.setString(2, km.tenKM);
            pstmt.setDouble(3, km.phanTramGiam);

            if (km.giaTriToiDa > 0) {
                pstmt.setLong(4, km.giaTriToiDa);
            } else {
                pstmt.setNull(4, Types.NUMERIC);
            }

            pstmt.setObject(5,km.ngayBatDau);
            pstmt.setObject(5, km.ngayKetThuc);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi tạo khuyến mãi: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<HOADON> XemBaoCao(java.util.Date tuNgay, java.util.Date denNgay) {
        List<HOADON> danhSachBaoCao = new ArrayList<>();
        String sql = "SELECT hd.* FROM HOADON hd JOIN DATSAN ds ON hd.MaDS = ds.MaDS " +
                "WHERE TRUNC(ds.NgayDat) >= TRUNC(?) AND TRUNC(ds.NgayDat) <= TRUNC(?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, new java.sql.Date(tuNgay.getTime()));
            pstmt.setDate(2, new java.sql.Date(denNgay.getTime()));

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                HOADON hd = new HOADON();
                hd.maHD = rs.getString("MaHoaDon");
                hd.tongTienDV = rs.getLong("TongTienDV");
                hd.soTienGiam = rs.getLong("SoTienGG");
                hd.thanhTien = rs.getLong("ThanhTien");
                hd.ghichu = rs.getString("Ghichu");
                hd.maDS = rs.getString("MaDS");
                danhSachBaoCao.add(hd);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi xuất báo cáo: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSachBaoCao;
    }

    public void KhoaTK(String maTK) {
        String sql = "UPDATE TAIKHOAN SET TrangThai = 'KhoaAccount' WHERE Ma_TK = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maTK);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khóa tài khoản: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
