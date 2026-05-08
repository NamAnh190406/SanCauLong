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
            pstmt.setString(1, ds.maDS);
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
            pstmt.setString(1, ctdv.maCTDV);
            pstmt.setLong(2, ctdv.soLuong);
            pstmt.setLong(3, ctdv.thanhTien);
            pstmt.setString(4, ctdv.maDS); // Lớp CTDV có MaCTDS, map vào cột MaDS
            pstmt.setString(5, ctdv.maDV);

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
            pstmt.setString(1, hoadon.maHD);
            pstmt.setLong(2, hoadon.tongTienDV);
            pstmt.setLong(3, hoadon.soTienGiam);
            pstmt.setLong(4, hoadon.thanhTien);
            pstmt.setString(5, hoadon.ghichu);
            pstmt.setString(6, hoadon.maDS);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
