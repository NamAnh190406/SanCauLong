/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author Hi
 */
public class DichVu {
    private String maDV;
    private String tenDV;
    private String loaiDV;
    private long giaBan;
    private int soLuongTon;
    private String trangThai;
    public DichVu() {
    }
    public DichVu(String maDV, String tenDV, String loaiDV, long giaBan, int soLuongTon, String trangThai) {
        this.maDV = maDV;
        this.tenDV = tenDV;
        this.loaiDV = loaiDV;
        this.giaBan = giaBan;
        this.soLuongTon = soLuongTon;
        this.trangThai = trangThai;
    }
    public DichVu(String maDV, String tenDV, long donGia) {
        this.maDV = maDV;
        this.tenDV = tenDV;
        this.giaBan = donGia;
    }
    public String getMaDV() {
        return maDV;
    }
 
    public void setMaDV(String maDV) {
        this.maDV = maDV;
    }
 
    public String getTenDV() {
        return tenDV;
    }
 
    public void setTenDV(String tenDV) {
        this.tenDV = tenDV;
    }
 
    public String getLoaiDV() {
        return loaiDV;
    }
 
    public void setLoaiDV(String loaiDV) {
        this.loaiDV = loaiDV;
    }
 
    public long getGiaBan() {
        return giaBan;
    }
 
    public void setGiaBan(long giaBan) {
        this.giaBan = giaBan;
    }
 
    public int getSoLuongTon() {
        return soLuongTon;
    }
 
    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }
 
    public String getTrangThai() {
        return trangThai;
    }
 
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
    public boolean kiemTraTonKho(int soLuongCanKiem) {
        return soLuongTon >= soLuongCanKiem && "Hoạt động".equals(trangThai);
    }
    public void capNhatTonKho(int soLuongThay) {
        this.soLuongTon = this.soLuongTon + soLuongThay;
        if (soLuongTon <= 0) {
            this.trangThai = "Hết hàng";
        } else {
            this.trangThai = "Hoạt động";
        }
    }
    public boolean capNhatGia(long giaMoi) {
        if (giaMoi <= 0) {
            return false;
        }
        this.giaBan = giaMoi;
        return true;
    }
    @Override
    public String toString() {
        return "DichVu{" +
                "maDV='" + maDV + '\'' +
                ", tenDV='" + tenDV + '\'' +
                ", loaiDV='" + loaiDV + '\'' +
                ", giaBan=" + giaBan +
                ", soLuongTon=" + soLuongTon +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}
