package Model;

import java.time.LocalDate;
import java.time.LocalTime;


public class DATSAN {
    private String maDS;           // ID đặt sân (tự tăng)
    private String maKH;           // Mã khách hàng
    private String tenKH;          // Tên khách hàng
    private String sdtKH;          // SĐT khách hàng
    private String maNV;           // Mã nhân viên
    private String maHD;           // Mã hóa đơn
    private String maSan;          // Mã sân (Sân 1, Sân 2, ...)
    private String maKG;           // Mã khoảng giờ (06:00, 07:00, ...
    private String tenSan;
    private String khungGio;
    private LocalDate ngayDat;     // Ngày đặt sân
    private LocalTime gioStart;    // Giờ bắt đầu
    private int thoiLuong;         // Thời lượng (giờ)
    private String trangThai;      // Trạng thái: "Booked" / "Playing" / "Completed" / "Cancelled"
    private long tongTienTamTinh;  // Tổng tiền tạm tính
    private LocalDate ngayTao;     // Ngày tạo booking
    
    public DATSAN() {}
//    DATSAN ds = new DATSAN(
//                        rs.getString("MaDS"),
//                        rs.getString("MaKH"),
//                        rs.getString("MaNV"),
//                        rs.getString("MaHD"),
//                        rs.getDate("NgayDat").toLocalDate(),
//                        rs.getLong("TongTienTamTinh"),
//                        rs.getString("TrangThai")
//                );
    public DATSAN(String mads, String makh, String manv, String mahd, LocalDate ngaydat, long tien, String trangthai)
    {
        this.maDS = mads;
        this.maKH = makh;
        this.maNV = manv;
        this.maHD = mahd;
        this.ngayDat = ngaydat;
        this.tongTienTamTinh = tien;
        this.trangThai = trangthai;
    }
    public DATSAN(String maDS, String maKH, String tenKH, String sdtKH, String maNV, 
                  String maHD, String maSan, String maKG, LocalDate ngayDat, 
                  LocalTime gioStart, int thoiLuong, String trangThai, long tongTien) {
        this.maDS = maDS;
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.sdtKH = sdtKH;
        this.maNV = maNV;
        this.maHD = maHD;
        this.maSan = maSan;
        this.maKG = maKG;
        this.ngayDat = ngayDat;
        this.gioStart = gioStart;
        this.thoiLuong = thoiLuong;
        this.trangThai = trangThai;
        this.tongTienTamTinh = tongTien;
        this.ngayTao = LocalDate.now();
    }
    
    public DATSAN(String tenKH, String sdtKH, String maSan, String maKG, 
                  LocalDate ngayDat, LocalTime gioStart, int thoiLuong) {
        this.tenKH = tenKH;
        this.sdtKH = sdtKH;
        this.maSan = maSan;
        this.maKG = maKG;
        this.ngayDat = ngayDat;
        this.gioStart = gioStart;
        this.thoiLuong = thoiLuong;
        this.trangThai = "Booked";
        this.ngayTao = LocalDate.now();
    }
    
    // ========== GETTER ==========
    
    public String getMaDS() {
        return maDS;
    }
    
    public String getMaKH() {
        return maKH;
    }
    
    public String getTenKH() {
        return tenKH;
    }
    
    public String getSdtKH() {
        return sdtKH;
    }
    
    public String getMaNV() {
        return maNV;
    }
    
    public String getMaHD() {
        return maHD;
    }
    
    public String getMaSan() {
        return maSan;
    }
    
    public String getMaKG() {
        return maKG;
    }
    
    public LocalDate getNgayDat() {
        return ngayDat;
    }
    
    public LocalTime getGioStart() {
        return gioStart;
    }
    
    public int getThoiLuong() {
        return thoiLuong;
    }
    
    public String getTrangThai() {
        return trangThai;
    }
    
    public long getTongTienTamTinh() {
        return tongTienTamTinh;
    }
    
    public LocalDate getNgayTao() {
        return ngayTao;
    }
    
    
    // ========== SETTER ==========
    
    public void setMaDS(String maDS) {
        this.maDS = maDS;
    }
    
    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }
    
    public void setTenKH(String tenKH) {
        this.tenKH = tenKH;
    }
    
    public void setSdtKH(String sdtKH) {
        this.sdtKH = sdtKH;
    }
    
    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }
    
    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }
    
    public void setMaSan(String maSan) {
        this.maSan = maSan;
    }
    
    public void setMaKG(String maKG) {
        this.maKG = maKG;
    }
    
    public void setNgayDat(LocalDate ngayDat) {
        this.ngayDat = ngayDat;
    }
    
    public void setGioStart(LocalTime gioStart) {
        this.gioStart = gioStart;
    }
    
    public void setThoiLuong(int thoiLuong) {
        this.thoiLuong = thoiLuong;
    }
    
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
    
    public void setTongTienTamTinh(long tongTien) {
        this.tongTienTamTinh = tongTien;
    }
    
    public void setNgayTao(LocalDate ngayTao) {
        this.ngayTao = ngayTao;
    }
    public String getTenSan() {
        return tenSan;
    }

    public void setTenSan(String tenSan) {
        this.tenSan = tenSan;
    }

    public String getKhungGio() {
        return khungGio;
    }

    public void setKhungGio(String khungGio) {
        this.khungGio = khungGio;
    }
    // ========== BUSINESS METHODS ==========
    
 
    public boolean batDauChoi() {
        if ("Booked".equals(this.trangThai)) {
            this.trangThai = "Playing";
            return true;
        }
        return false;
    }
    

    public boolean ketThucChoi() {
        if ("Playing".equals(this.trangThai)) {
            this.trangThai = "Completed";
            return true;
        }
        return false;
    }

    public boolean huyDatSan() {
        if (!"Cancelled".equals(this.trangThai) && !"Completed".equals(this.trangThai)) {
            this.trangThai = "Cancelled";
            return true;
        }
        return false;
    }
    

    public void xacNhanThanhToan() {
        this.trangThai = "Paid";
    }

    public boolean layCungKhoangGio(DATSAN other) {
        if (this.maSan.equals(other.getMaSan()) && 
            this.ngayDat.equals(other.getNgayDat())) {
            
            LocalTime startTime = this.gioStart;
            LocalTime endTime = this.gioStart.plusHours(this.thoiLuong);
            
            LocalTime otherStart = other.getGioStart();
            LocalTime otherEnd = otherStart.plusHours(other.getThoiLuong());
            
            // Kiểm tra xem 2 khoảng thời gian có giao nhau không
            return !(endTime.isBefore(otherStart) || startTime.isAfter(otherEnd));
        }
        return false;
    }

    public LocalTime getGioEnd() {
        return gioStart.plusHours(thoiLuong);
    }

    public String getTrangThaibk() {
        switch (this.trangThai) {
            case "Booked":
                return "Đã đặt";
            case "Playing":
                return "Đang chơi";
            case "Completed":
                return "Hoàn thành";
            case "Cancelled":
                return "Đã hủy";
            case "Paid":
                return "Đã thanh toán";
            default:
                return trangThai;
        }
    }
    
    public boolean isValid() {
        return tenKH != null && !tenKH.isEmpty() &&
               sdtKH != null && sdtKH.matches("\\d{10}") &&
               maSan != null && !maSan.isEmpty() &&
               ngayDat != null &&
               gioStart != null &&
               thoiLuong > 0 && thoiLuong <= 3;
    }
    
    @Override
    public String toString() {
        return "DATSAN{" +
                "maDS='" + maDS + '\'' +
                ", tenKH='" + tenKH + '\'' +
                ", sdtKH='" + sdtKH + '\'' +
                ", maSan='" + maSan + '\'' +
                ", ngayDat=" + ngayDat +
                ", gioStart=" + gioStart +
                ", thoiLuong=" + thoiLuong +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}