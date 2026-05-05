import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NHANVIEN {
    public String MaNV;
    public String HoTen;
    public String SDT;
    public String ChucVu;
    public String CaLamViec;
    public String MaTK;

    public Connection conn;

    public NHANVIEN(Connection conn) {
        this.conn = conn;
    }

    public NHANVIEN(String maNV, String hoTen, String sdt, String chucVu, String caLamViec, String maTK, Connection conn) {
        this.MaNV = maNV;
        this.HoTen = hoTen;
        this.SDT = sdt;
        this.ChucVu = chucVu;
        this.CaLamViec = caLamViec;
        this.MaTK = maTK;
        this.conn = conn;
    }

    public boolean XNNhanSan(DATSAN ds) {
        if (conn == null || ds == null) return false;

        String sql = "UPDATE DATSAN SET TrangThai = 'DaDuyet' WHERE MaDS = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ds.MaDS);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean BanDV(CTDV ctdv) {
        if (conn == null || ctdv == null) return false;

        String sql = "INSERT INTO CTDV (MaCTDV, SoLuong, ThanhTien, MaDS, MaDV) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ctdv.MaCTDV);
            pstmt.setInt(2, ctdv.SoLuong);
            pstmt.setLong(3, ctdv.ThanhTien);
            pstmt.setString(4, ctdv.MaDS); // Lớp CTDV có MaCTDS, map vào cột MaDS
            pstmt.setString(5, ctdv.MaDV);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean XLThanhToanTaiQuay(HOADON hoadon) {
        if (conn == null || hoadon == null) return false;

        String sql = "INSERT INTO HOADON (MaHoaDon, TongTienDV, SoTienGG, ThanhTien, Ghichu, MaDS) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hoadon.MaHoaDon);
            pstmt.setLong(2, hoadon.TongTienDV);
            pstmt.setLong(3, hoadon.SoTienGG);
            pstmt.setLong(4, hoadon.ThanhTien);
            pstmt.setString(5, hoadon.GhiChu);
            pstmt.setString(6, hoadon.MaDS);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}