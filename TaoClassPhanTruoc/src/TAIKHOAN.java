import java.sql.*;

public class TAIKHOAN {
    public String Ma_TK;
    public String Username;
    public String Password;
    public String VaiTro;    // Admin, NhanVien, KhachHang
    public String TrangThai; // HoatDong, KhoaAccount
    public Connection conn;
    public TAIKHOAN(Connection conn) {
        this.conn = conn;
    }

    public TAIKHOAN(String MaTK, String Username, String Password, String VaiTro, String TrangThai) {
        this.Ma_TK = MaTK;
        this.Username = Username;
        this.Password = Password;
        this.VaiTro = VaiTro;
        this.TrangThai = TrangThai;
    }

    public boolean DangNhap(String user, String pass) {
        String sql = "{ ? = call f_DangNhap(?, ?) }";

        try (CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.registerOutParameter(1, Types.VARCHAR);

            cstmt.setString(2, user);
            cstmt.setString(3, pass);

            cstmt.execute();

            String ketQua = cstmt.getString(1);

            if (ketQua.equals("Đăng nhập thành công!")) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Hàm phụ để lấy thông tin chi tiết sau khi đăng nhập thành công
     */
    private void loadThongTinTaiKhoan(String user) {
        String sql = "SELECT Ma_TK, VaiTro, TrangThai FROM TAIKHOAN WHERE Username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                this.Ma_TK = rs.getString("Ma_TK");
                this.Username = user;
                this.VaiTro = rs.getString("VaiTro");
                this.TrangThai = rs.getString("TrangThai");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void CapNhatTrangThai(String TTms) {
        String sql = "UPDATE TAIKHOAN SET TrangThai = ? WHERE Ma_TK = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, TTms);
            pstmt.setString(2, this.Ma_TK);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public boolean DatLaiMK(String passMoi) {
        String sql = "UPDATE TAIKHOAN SET Password = ? WHERE Ma_TK = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, passMoi);
            pstmt.setString(2, this.Ma_TK);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}