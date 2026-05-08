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

public class DATSAN {
    public String maDS;
    public String maKH;
    public String maNV;
    public String maHD;
    public LocalDate ngayDat;
    public String trangThai;
    public long tongTienTamTinh;
    public DATSAN(){}
    public DATSAN (String mads, String makh, String manv, String mahd, LocalDate ngay, long tongtien, String trangthai)
    {
        this.maDS=mads;
        this.maKH=makh;
        this.maNV= manv;
        this.maHD= mahd;
        this.ngayDat= ngay;
        this.trangThai= trangthai;
        this.tongTienTamTinh= tongtien;
    }
    public long getTongTienTamTinh(){
        return tongTienTamTinh;
    }
    public void setTongTienTamTinh( long tongTien)
    {
        this.tongTienTamTinh= tongTien;
    }
    public String getTrangThai()
    {
        return trangThai;
    }
    public void setTrangThai( String trangthai)
    {
        this.trangThai= trangthai;
    }
    public LocalDate getNgayDat()
    {
        return ngayDat;
    }
    public void setNgayDat(LocalDate ngay)
    {
        this.ngayDat= ngay;
    }
    public String getMaDS ()
    { 
        return maDS;
    }
    public void setMaDS( String mads)
    {
        this.maDS= mads;
    }
    public String getMaKH ()
    { 
        return maKH;
    }
    public void setMaKH( String makh)
    {
        this.maKH= makh;
    }
    public String getMaNV ()
    { 
        return maNV;
    }
    public void setMaNV( String manv)
    {
        this.maNV= manv;
    }
    public String getMaHD ()
    { 
        return maHD;
    }
    public void setMaHD( String mahd)
    {
        this.maHD= mahd;
    }
    public boolean HuyDatSan(String mads){
        if (this.maDS != null && this.maDS.equals(mads)) {
            this.trangThai = "Da Huy";
            return true;
        }
        return false;
    }
    public void XacNhanThanhToan() {
        this.trangThai = "Da Thanh Toan";
    }
    @Override
    public String toString() {
        return "DATSAN{" +
                "maDS='" + maDS + '\'' +
                ", maKH='" + maKH + '\'' +
                ", maNV='" + maNV + '\'' +
                ", maHD='" + maHD + '\'' +
                ", ngayDat=" + ngayDat +
                ", trangThai='" + trangThai + '\'' +
                ", tongTienTamTinh=" + tongTienTamTinh +
                '}';
    }
    
    
}
