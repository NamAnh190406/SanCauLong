package Model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import Utils.Databasehelper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

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
    public static Connection conn;

    public SAN(Connection conn) {
        this.conn = conn;
    }
    public SAN(String maSan, String tenSan, String loaiSan,long giaThue,String trangThai)
    {
        this.MaSan=maSan;
        this.TenSan=tenSan;
        this.LoaiSan=loaiSan;
        this.GiaThueTheoGio=giaThue;
        this.TrangThai=trangThai;
    }
    public ArrayList<String> getallMaSans() {
        ArrayList<String> list = new ArrayList<String>();
        String sql = "SELECT MaSan FROM San";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("MaSan"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<String> getallTenSans() {
        ArrayList<String> list = new ArrayList<String>();
        String sql = "SELECT TenSan FROM San";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("TenSan"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean KiemTraLichTrong(LocalDateTime ldt) {
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
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                LocalTime bd = rs.getTime("GioBD").toLocalTime();
                LocalTime kt = rs.getTime("GioKT").toLocalTime();
                String makg = rs.getString("MaKG");

                if (!ldt.toLocalTime().isBefore(bd) && ldt.toLocalTime().isBefore(kt)) {
                    return makg;
                }
            }
        } catch (SQLException e) {
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

    public void CapNhatTrangThai(String trangThaiMoi) {
        String sql = "UPDATE SAN SET TrangThai = ? WHERE MaSan = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, trangThaiMoi);
            pstmt.setString(2, this.MaSan);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                this.TrangThai = trangThaiMoi;
                System.out.println("Cập nhật trạng thái sân " + MaSan + " thành công.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int countTongSan() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM San";
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public int countSanTrong() {
        int count = 0;
        // LƯU Ý: Đảm bảo cột TrangThai trong Database lưu số 0 hoặc chữ "Trống". Sửa
        // lại SQL cho khớp kiểu dữ liệu!
        String sql = "SELECT COUNT(*) FROM San WHERE TrangThai = 'Sân Trống'";
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
    public static ObservableList<SAN> getDanhSachSanTuDB() {
    ObservableList<SAN> danhSachSan = FXCollections.observableArrayList();
    
    // Câu lệnh SQL (Bạn nhớ thay đổi tên bảng và tên cột cho khớp với Oracle DB của bạn nhé)
    String sql = "SELECT MaSan, TenSan, LoaiSan, GiaThue, TrangThai FROM B_SAN"; 
    
    Databasehelper db = new Databasehelper();
    
    // Sử dụng try-with-resources để Java tự động đóng kết nối (Connection/ResultSet) khi xong việc
    try (Connection conn = db.createCon();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
         
        // Duyệt qua từng dòng dữ liệu Database trả về
        while (rs.next()) {
            // Lấy dữ liệu từ ResultSet
            String maSan = rs.getString("MaSan");
            String tenSan = rs.getString("TenSan");
            String loaiSan = rs.getString("LoaiSan");
            int giaThue = rs.getInt("GiaThue");
            String trangThai = rs.getString("TrangThai");
            
            // Tạo object Model và thêm vào danh sách
            SAN san = new SAN(maSan, tenSan, loaiSan, giaThue, trangThai);
            danhSachSan.add(san);
        }
        
    } catch (Exception e) {
        System.out.println("Lỗi khi tải dữ liệu Sân từ DB: " + e.getMessage());
        e.printStackTrace();
    }
    
    return danhSachSan;
}
    public String LayTTSan() {
        return "Thông tin sân: " + TenSan + " - " + LoaiSan;
    }
}