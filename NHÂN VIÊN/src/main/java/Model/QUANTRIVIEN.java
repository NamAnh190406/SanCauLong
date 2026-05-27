package Model;
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
            pstmt.setLong(1, bg.getDonGia());
            pstmt.setString(2, bg.getMaBG());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật bảng giá: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean TaoKM(KhuyenMai km) {
        String sql = "INSERT INTO KHUYENMAI (MaKM, TenKM, PhanTramGG, GTriToiDa, NgayBD, NgayKT) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, km.getMaKM());
            pstmt.setString(2, km.getTenKM());
            pstmt.setDouble(3, km.getPhanTramGiam());

            if (km.getGiaTriToiDa() > 0) {
                pstmt.setLong(4, km.getGiaTriToiDa());
            } else {
                pstmt.setNull(4, Types.NUMERIC);
            }

            pstmt.setObject(5, km.getNgayBatDau());
            pstmt.setObject(6, km.getNgayKetThuc());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi tạo khuyến mãi: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<HOADON> XemBaoCao(java.util.Date tuNgay, java.util.Date denNgay) {
        List<HOADON> danhSachBaoCao = new ArrayList<>();
        // Lưu ý: Hàm TRUNC hoạt động tốt trên Oracle. Nếu dùng SQL Server/MySQL có thể phải đổi hàm khác.
        String sql = "SELECT hd.* FROM HOADON hd JOIN DATSAN ds ON hd.MaDS = ds.MaDS " +
                "WHERE TRUNC(ds.NgayDat) >= TRUNC(?) AND TRUNC(ds.NgayDat) <= TRUNC(?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, new java.sql.Date(tuNgay.getTime()));
            pstmt.setDate(2, new java.sql.Date(denNgay.getTime()));

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                HOADON hd = new HOADON();
                
                // Ví dụ: hd.setMaHD(rs.getString("MaHoaDon"));
                hd.setMaHD(rs.getString("MaHoaDon")) ;
                hd.setTongTienDV(rs.getLong("TongTienDV")); 
                hd.setSoTienGiam(rs.getLong("SoTienGG"));
                hd.tinhThanhTien();
                hd.setGhiChu(rs.getString("Ghichu"));
                hd.setMaDS(rs.getString("MaDS")) ;
                
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
            System.out.println("Đã khóa tài khoản: " + maTK);
        } catch (SQLException e) {
            System.err.println("Lỗi khóa tài khoản: " + e.getMessage());
            e.printStackTrace();
        }
    }
}