package Model;

import Utils.Databasehelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Model.DATSAN;
import java.time.LocalDate;

public class KHACHHANG {
    // Áp dụng tính đóng gói: chuyển các thuộc tính về private
    private String maKH;
    private String hoTen;
    private String sdt;
    private String email;
    private String diaChi;
    private java.util.Date ngayDK;
    private String hangThanhVien;
    private int diemTichLuy;

    // Constructor rỗng tiêu chuẩn
    public KHACHHANG() {}

    public KHACHHANG(String maKH, String hoTen, String sdt, String email, String diaChi, String hangThanhVien, int diemTichLuy) {
        this.maKH = maKH;
        this.hoTen = hoTen;
        this.sdt = sdt;
        this.email = email;
        this.diaChi = diaChi;
        this.hangThanhVien = hangThanhVien;
        this.diemTichLuy = diemTichLuy;
    }
    public String getDiaChi() { 
        return diaChi; 
    }
    
    public void setDiaChi(String diaChi) { 
        this.diaChi = diaChi; 
    }
    // ==================== CÁC HÀM XỬ LÝ NGHIỆP VỤ ====================

    // 1. Xem lịch sử đặt sân (Mở/đóng kết nối cục bộ qua try-with-resources)
    public List<DATSAN> XemLichSu() {
        List<DATSAN> LSDS = new ArrayList<>();
        String sql = "SELECT * FROM DATSAN WHERE MaKH = ?";
        
        Databasehelper db = new Databasehelper();
        try (Connection conn = db.createCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, this.maKH);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DATSAN ds = new DATSAN();
                    ds.setMaDS(rs.getString("MaDS"));
                    
                    Date sqlDate = rs.getDate("NgayDat");
                    if (sqlDate != null) {
                        ds.setNgayDat(sqlDate.toLocalDate());
                    }
                    
                    ds.setTrangThai(rs.getString("TrangThai"));
                    ds.setTongTienTamTinh(rs.getLong("TongTienTamTinh"));
                    ds.setMaKH(this.maKH);
                    ds.setMaSan(rs.getString("MaSan"));
                    ds.setMaKG(rs.getString("MaKG"));

                    LSDS.add(ds);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi xuất lịch sử của khách hàng " + this.maKH + ": " + e.getMessage());
        }
        return LSDS;
    }

    // 2. Cập nhật thông tin 
    public boolean CapNhatThongTin(String hoTenMoi, String sdtMoi, String emailMoi) {
        String sql = "UPDATE KHACHHANG SET HoTen = ?, SDT = ?, Email = ? WHERE MaKH = ?";
        
        Databasehelper db = new Databasehelper();
        try (Connection conn = db.createCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, hoTenMoi);
            pstmt.setString(2, sdtMoi);
            pstmt.setString(3, emailMoi);
            pstmt.setString(4, this.maKH);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                this.hoTen = hoTenMoi;
                this.sdt = sdtMoi;
                this.email = emailMoi;
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật thông tin khách hàng " + this.maKH + ": " + e.getMessage());
            return false;
        }
    }

    // 3. Tích điểm 
    public boolean TichDiem(int diemTichThem) {
        String sql = "UPDATE KHACHHANG SET DiemTichLuy = DiemTichLuy + ? WHERE MaKH = ?";
        
        Databasehelper db = new Databasehelper();
        try (Connection conn = db.createCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, diemTichThem);
            pstmt.setString(2, this.maKH);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                this.diemTichLuy += diemTichThem;
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Lỗi tích điểm cho khách hàng " + this.maKH + ": " + e.getMessage());
            return false;
        }
    }

    // ==================== GETTERS & SETTERS TIÊU CHUẨN ====================
    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getSDT() { return sdt; }
    public void setSDT(String sdt) { this.sdt = sdt; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public java.util.Date getNgayDK() { return ngayDK; }
    public void setNgayDK(java.util.Date ngayDK) { this.ngayDK = ngayDK; }

    public String getHangThanhVien() { return hangThanhVien; }
    public void setHangThanhVien(String hangThanhVien) { this.hangThanhVien = hangThanhVien; }

    public int getDiemTichLuy() { return diemTichLuy; }
    public void setDiemTichLuy(int diemTichLuy) { this.diemTichLuy = diemTichLuy; }
}