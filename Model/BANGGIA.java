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
public class BANGGIA {
    private String maBG;
    private String tenBG;
    private LocalDate ngayBD;
    private LocalDate ngayKT;
    private long donGia;
    private String trangthai;
    private String maSan;
    private String maKG;
    public BANGGIA(){}

    public BANGGIA(String maBG, long aLong,  String maSan, String makg) {
        this.maBG = maBG;
        this.maKG = makg;
        this.maSan= maSan;
        this.donGia= aLong;
    }
    public Long getDonGia() { return donGia; }
    public void setDonGia(Long donGia) { this.donGia = donGia; }

    public String getMaSan()
    {
        return maSan;
    }
    public void setMaSan( String maSan)
    {
        this.maSan=maSan;
    }
    public String getMaKG()
    {
        return maKG;
    }
    public void setMaKG(String makg)
    {
        this.maKG=makg;
    }
    public String getMaBG(){
    return maBG;
    }
    public void setMaBG(String maBGia)
    {
        this.maBG=maBGia;
    }
    public String getTenBangGia()
    {
        return tenBG;
    }
    public void setTenBangGia(String Ten)
    {
        this.tenBG=Ten;
    }
    public LocalDate getNgayBD()
    {
        return ngayBD;
    }
    public  LocalDate getNgayKT()
    {
        return ngayKT;
    }
    public void setNgayBD(LocalDate ngayBD)
    {
        this.ngayBD=ngayBD;
    }
    public void setNgayKT (LocalDate ngayKT)
    {
        this.ngayKT=ngayKT;
    }
    public  String getTrangThai()
    {return trangthai;}
    public void setTrangThai(String TT)
    {
        this.trangthai=TT;
    }
    public boolean kiemTraHieuLuc(LocalDate ngay)
    {
        if (ngay==null)
            ngay=LocalDate.now();
        return !ngay.isBefore(ngayBD)&&!ngay.isAfter(ngayKT)&&"Hoạt động".equals(trangthai);
    }
    @Override
    public String toString() {
        return "BG: " + donGia;
    }
}
