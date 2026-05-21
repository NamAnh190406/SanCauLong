package com.example.guidemo;

import java.time.LocalDate;

/**
 *
 * @author Hi
 */
public class HOADON {
    public String maHD;
    public long tongTienDV;
    public long soTienGiam;
    public long thanhTien;
    public String ghichu;
    public LocalDate ngayXuat;
    public String maDS;
    public HOADON()
    {
    }
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
    public String getMaDS() { return maDS; }
    public void setMaDS(String maDS) { this.maDS = maDS; }
    public long tinhThanhTien()
    {
        this.thanhTien=tongTienDV-soTienGiam;
        return this.thanhTien;
    }
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
}
