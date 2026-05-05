import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SAN {
    public String MaSan;
    public String TenSan;
    public String LoaiSan;
    public String LoaiMatSan;
    public String KhongGian;
    public int SLNguoiChoi;
    public long GiaThueTheoGio;
    public String TrangThai;
    public String MoTa;
    public String DiaChi;
    public Connection conn;


    SAN() {
        this.conn = conn;
    }

    public boolean KiemTraLichTrong(LocalDateTime ldt) { //Cai nay luu ngay va tgian
        String sql = "{ ? = call f_SanTrong(?, ?, ?) }";
        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.setString(2, this.MaSan);
            cstmt.setObject(3, ldt.toLocalDate());
            cstmt.setString(4, TraVeMaKG(ldt));

            cstmt.execute();
            return "Sân Trống".equals(cstmt.getString(1));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String TraVeMaKG(LocalDateTime ldt) {
        String sql = "SELECT * FROM KHUNGGIO";
        try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                LocalTime bd = rs.getTime("GioBD").toLocalTime();
                LocalTime kt = rs.getTime("GioKT").toLocalTime();
                String makg = rs.getString("MaKG");
                if(ldt.toLocalTime().isAfter(bd) && ldt.toLocalTime().isBefore(kt)) {
                    return makg;
                }
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public long TinhGiaThue(LocalDateTime Tgian) {
        String sqlCheckLe = "SELECT GiaPhuThu FROM NGAYLE WHERE NgayCuThe = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlCheckLe)) {
            pstmt.setObject(1, Tgian.toLocalDate());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                long GiaPT = rs.getLong("GiaPhuThu");
                return GiaThueTheoGio + GiaPT;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
         return GiaThueTheoGio;
    }

    /**
     * CapNhatTrangThai(String): Cập nhật trạng thái sân (HoatDong, BaoDuong, Dong)
     */
    public void CapNhatTrangThai(Connection conn, String trangThaiMoi) {
        String sql = "UPDATE SAN SET TrangThai = ? WHERE MaSan = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, trangThaiMoi);
            pstmt.setString(2, this.MaSan);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                this.TrangThai = trangThaiMoi; // Cập nhật luôn thuộc tính của Object
                System.out.println("Cập nhật trạng thái sân " + MaSan + " thành công.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String LayTTSan() {
        return "";
    }
}