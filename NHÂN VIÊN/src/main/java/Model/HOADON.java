/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;
import java.time.LocalDate;

/**
 *
 * @author Hi
 */
public class HOADON {
    // Hai loại hóa đơn trong hệ thống
    public static final String LOAI_DAT_SAN  = "DAT_SAN";   // Hóa đơn thuê sân
    public static final String LOAI_DICH_VU  = "DICH_VU";   // Hóa đơn dịch vụ

    private String maHD;
    private String loaiHD;         // Loại: DAT_SAN | DICH_VU
    private long tongTienDV;
    private long soTienGiam;
    private long thanhTien;
    private String ghichu;
    private String trangThai;
    private LocalDate ngayXuat;
    private String maDS;

    public HOADON() {}
    public HOADON(String maHD, long tongTienDV, long soTienGiam, String ghichu, LocalDate ngayXuat, String maDS)
    {
        this.maHD = maHD;
        this.tongTienDV = tongTienDV;
        this.soTienGiam = soTienGiam;
        this.ghichu = ghichu;
        this.ngayXuat = ngayXuat;
        this.thanhTien = tinhThanhTien();
        this.maDS=maDS;
    }
    public String getLoaiHD() { return loaiHD; }
    public void setLoaiHD(String loaiHD) { this.loaiHD = loaiHD; }

    public boolean isDatSan()  { return LOAI_DAT_SAN.equals(loaiHD); }
    public boolean isDichVu()  { return LOAI_DICH_VU.equals(loaiHD); }

    public String getMaDS() { return maDS; }
    public void setMaDS(String maDS) { this.maDS = maDS; }
//    public long tinhThanhTien()
//    {
//        this.thanhTien=tongTienDV-soTienGiam;
//        return this.thanhTien;
//    }
    public String getMaHD()
    {
        return maHD;
    }
    public long getTongTienDV()
    {
        return tongTienDV;
    }
    public long getSoTienGiam()
    {
        return soTienGiam;
    }
    public String getGhiChu()
    {
        return ghichu;
    }
    public LocalDate getNgayXuat()
    {
        return ngayXuat;
    }
    public long getThanhTien()
    {
        return thanhTien;
    }
    public  String getTrangThai()
    {
        return trangThai;
    }
    public void setMaHD(String maHD)
    {
        this.maHD=maHD;
    }
    public void setTongTienDV(long tongTienDV)
    {
        this.tongTienDV=tongTienDV;
        this.thanhTien=tinhThanhTien();
    }
    public void setSoTienGiam(long soTienGiam)
    {
        this.soTienGiam=soTienGiam;
        this.thanhTien=tinhThanhTien();
    }
    public void setGhiChu(String ghiChu)
    {
        this.ghichu=ghiChu;
    }
    public void setTrangThai(String trangthai)
    {
        this.trangThai= trangthai;
    }
    public void setNgayXuat(LocalDate ngayXuat)
    {
        this.ngayXuat=ngayXuat;
    }
    public void xuatHoaDon()
    {
        System.out.println("          HÓA ĐƠN           ");
        System.out.println("Mã HĐ: " + maHD);
        System.out.println("Ngày xuất: " + ngayXuat);
        System.out.println("Tổng tiền DV: " + tongTienDV);
        System.out.println("Tiền giảm: " + soTienGiam);
        System.out.println("Thành tiền: " + thanhTien);
        System.out.println("Ghi chú: " + ghichu);
        System.out.println("                            ");
    }
    @Override
    public String toString()
    {
        return maHD + " - VNĐ" + thanhTien;
    }
    public void setThanhTien(long thanhTien) { this.thanhTien = thanhTien; }
    public long tinhThanhTien() {
        this.thanhTien = tongTienDV - soTienGiam;
        return this.thanhTien;
    }
}
