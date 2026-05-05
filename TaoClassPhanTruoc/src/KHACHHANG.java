import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KHACHHANG {
    public String MaKH;
    public String HoTen;
    public String SDT;
    public String Email;
    public Date NgayDK;
    public String HangThanhVien;
    public int DiemTichLuy;
    public String MaTK;
    public Connection conn;

    public KHACHHANG(Connection conn) {
        this.conn = conn;
    }

    public boolean DatSan(String maSan, java.sql.Date ngayDat, String maKG) {
        String sqlCheck = "{ ? = call f_SanTrong(?, ?, ?) }";
        try (CallableStatement cstmt = conn.prepareCall(sqlCheck)) {
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.setString(2, maSan);
            cstmt.setDate(3, ngayDat);
            cstmt.setString(4, maKG);
            cstmt.execute();

            if (!cstmt.getString(1).equals("Sân Trống")) {
                System.out.println("Sân đã bị đặt, vui lòng chọn khung giờ khác!");
                return false;
            }

            String sqlInsert = "INSERT INTO DATSAN (MaDS, NgayDat, TrangThai, MaKH, MaSan, MaKG) " +
                    "VALUES (?, ?, 'ChoDuyet', ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                String maDS = "DS" + (System.currentTimeMillis() % 100000);
                pstmt.setString(1, maDS);
                pstmt.setDate(2, ngayDat);
                pstmt.setString(3, this.MaKH);
                pstmt.setString(4, maSan);
                pstmt.setString(5, maKG);

                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
      3. Gửi đánh giá sân sau khi chơi (Dùng PreparedStatement)

    public boolean DanhGiaSan(Connection conn, String maSan, int diem, String nhanXet) {
        String sql = "INSERT INTO DANHGIASAN (MaDanhGia, DiemDG, NhanXet, MaKH, MaSan) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String maDG = "DG" + (System.currentTimeMillis() % 100000);
            pstmt.setString(1, maDG);
            pstmt.setInt(2, diem);
            pstmt.setString(3, nhanXet);
            pstmt.setString(4, this.MaKH);
            pstmt.setString(5, maSan);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Có thể lỗi do ràng buộc UNIQUE (mỗi khách chỉ đánh giá 1 sân 1 lần)
            System.out.println("Lỗi: Bạn đã đánh giá sân này rồi!");
            return false;
        }
    }
     **/
// Giai thich func: Ham nay la luu ds truy van cac ds cua kh
    public List<DATSAN> XemLichSu() {
        //xem lich su dat san?
        List<DATSAN> LSDS = new List<DATSAN>();
        String sql = "SELECT * FROM DATSAN DS WHERE DS.MAKH = ?";
        try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.MaKH);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                DATSAN ds = new DATSAN();
                ds.MaDS = rs.getString("MaDS");
                ds.NgayDat = rs.getString("NgayDat");
                ds.TrangThai = rs.getString("TrangThai");
                ds.TongTienTamTinh = rs.getString("TongTienTamTinh");
                ds.MaKH = this.MaKH;
                ds.MaSan = rs.getString("MaSan");
                ds.MaKG = rs.getString("MaKG");

                LSDS.add(ds);
            }
        } catch(SQLException e) {
            System.err.println("Lỗi xuất lịch sử: " + e.getMessage());
            e.printStackTrace();
        }
        return LSDS;
    }
    public boolean CapNhatThongTin(String HoTenms, String SDTms, String Emailms) {
        String sql = "UPDATE KHACHHANG SET HoTen = ? AND SDT = ? AND Email = ? WHERE MaKH = ?";
        try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, HoTenms);
            pstmt.setString(2, SDTms);
            pstmt.setString(3,SDTms);
            pstmt.setString(4, this.MaKH);

            return pstmt.executeUpdate()>0;
        } catch (SQLException e) {
            System.err.println("Lỗi update thông tin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<SAN> TimSan(String MaSan, String TenSan) {
        List<SAN> dssan = new ArrayList<>();
        String sql = "SELECT * FROM SAN s WHERE s.MaSan = ? AND TenSan = ?";
        try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, MaSan);
            pstmt.setString(2, TenSan);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                SAN san = new SAN();
                san.MaSan = rs.getString("MaSan");
                san.TenSan = rs.getString("TenSan");
                san.LoaiSan = rs.getString("LoaiSan");
                san.LoaiMatSan = rs.getString("LoaiMatSan");
                san.KhongGian = rs.getString("KhongGian");
                san.SLNguoiChoi = rs.getInt("SLNguoiChoi");
                san.GiaThueTheoGio = rs.getLong("GiaThueTheoGio");
                san.TrangThai = rs.getString("TrangThai");
                san.MoTa = rs.getString("MoTa");
                san.DiaChi = rs.getString("DiaChi");
                dssan.add(san);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dssan;
    }

    public int TichDiem(int DiemTichThem) {
        String sql = "UPDATE KHACHHANG SET DiemTichLuy = DiemTichLuy + ? WHERE MaKH = ?";
        try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, DiemTichThem);
            pstmt.setString(2, this.MaKH);


        }
    }
}