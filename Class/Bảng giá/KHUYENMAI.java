import java.time.LocalDate;

/**
 *
 * @author Hi
 */
public class KHUYENMAI {
    public String maKM;
    public String tenKM;
    public double phanTramGiam;
    public long giaTriToiDa;
    public LocalDate ngayBatDau;
    public LocalDate ngayKetThuc;
    public String dieuKienApDung;
    public int soLuong;
    public String trangThai;
    public KHUYENMAI() {
    }
 
    public KHUYENMAI(String maKM, String tenKM, double phanTramGiam, long giaTriToiDa, 
                     LocalDate ngayBatDau, LocalDate ngayKetThuc, String dieuKienApDung, int soLuong, String trangThai) {
        this.maKM = maKM;
        this.tenKM = tenKM;
        this.phanTramGiam = phanTramGiam;
        this.giaTriToiDa = giaTriToiDa;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.dieuKienApDung = dieuKienApDung;
        this.soLuong = soLuong;
        this.trangThai = trangThai;
    }
    public  KHUYENMAI (String maKM, String tenKM, double phanTramGiam, long giaTriToiDa, 
                     LocalDate ngayBatDau, LocalDate ngayKetThuc)
    {
        this.maKM = maKM;
        this.tenKM = tenKM;
        this.phanTramGiam = phanTramGiam;
        this.giaTriToiDa = giaTriToiDa;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
    }
    public String getMaKM() {
        return maKM;
    }
 
    public void setMaKM(String maKM) {
        this.maKM = maKM;
    }
 
    public String getTenKM() {
        return tenKM;
    }
 
    public void setTenKM(String tenKM) {
        this.tenKM = tenKM;
    }
 
    public double getPhanTramGiam() {
        return phanTramGiam;
    }
 
    public void setPhanTramGiam(double phanTramGiam) {
        this.phanTramGiam = phanTramGiam;
    }
 
    public long getGiaTriToiDa() {
        return giaTriToiDa;
    }
 
    public void setGiaTriToiDa(long giaTriToiDa) {
        this.giaTriToiDa = giaTriToiDa;
    }
 
    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }
 
    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }
 
    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }
 
    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }
 
    public String getDieuKienApDung() {
        return dieuKienApDung;
    }
 
    public void setDieuKienApDung(String dieuKienApDung) {
        this.dieuKienApDung = dieuKienApDung;
    }
 
    public int getSoLuong() {
        return soLuong;
    }
 
    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }
 
    public String getTrangThai() {
        return trangThai;
    }
 
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
    public boolean kiemTraHopLe(long giaTriDonHang) {
        LocalDate today = LocalDate.now();
        boolean kiemTraNgay = !today.isBefore(ngayBatDau) && !today.isAfter(ngayKetThuc);
        boolean kiemTraTrangThai = "Hoạt động".equals(trangThai);
        boolean kiemTraSoLuong = soLuong > 0;
        boolean kiemTraGiaTri = giaTriDonHang > 0;
        
        return kiemTraNgay && kiemTraTrangThai && kiemTraSoLuong && kiemTraGiaTri;
    }
    public long tinhTienGiam(long giaTriDonHang) {
        if (!kiemTraHopLe(giaTriDonHang)) {
            return 0;
        }
        long tienGiam = (long) (giaTriDonHang * phanTramGiam / 100);
        if (tienGiam > giaTriToiDa) {
            tienGiam = giaTriToiDa;
        }
        return tienGiam;
    }
    public void capNhatSoLuong() {
        if (soLuong > 0) {
            soLuong--;
        }
        if (soLuong <= 0) {
            trangThai = "Hết";
        }
    }
 
    @Override
    public String toString() {
        return tenKM + " (" + phanTramGiam + "%)";
    }
 
}
