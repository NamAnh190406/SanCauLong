import java.sql.*;
import java.time.LocalTime;

public class KHUNGGIO {
    public String MaKG;
    public Timestamp GioBD;
    public Timestamp GioKT;
    public long HeSo;
    public Connection conn;

    public KHUNGGIO(Connection conn) {
        this.conn = conn;
    }


    public boolean KiemTraKhungGio(LocalTime bd, LocalTime kt) {
        String sql = "SELECT 1 FROM KHUNGGIO WHERE GioBD <= ? AND GioKT >= ?";
        try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTime(1, Time.valueOf(bd));
            pstmt.setTime(2,Time.valueOf(kt));

            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}