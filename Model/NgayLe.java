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
public class NgayLe {
    private String maNgayLe;
    private String tenNgayLe;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private long giaPhuThu;
    private String moTa;
    public NgayLe() {
    }
 
    public NgayLe(String maNgayLe, String tenNgayLe, LocalDate ngayBatDau, LocalDate ngayKetThuc, long giaPhuThu, String moTa) {
        this.maNgayLe = maNgayLe;
        this.tenNgayLe = tenNgayLe;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.giaPhuThu = giaPhuThu;
        this.moTa = moTa;
    }
    public String getMaNgayLe() {
        return maNgayLe;
    }
 
    public void setMaNgayLe(String maNgayLe) {
        this.maNgayLe = maNgayLe;
    }
 
    public String getTenNgayLe() {
        return tenNgayLe;
    }
 
    public void setTenNgayLe(String tenNgayLe) {
        this.tenNgayLe = tenNgayLe;
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
 
    public long getGiaPhuThu() {
        return giaPhuThu;
    }
 
    public void setGiaPhuThu(long giaPhuThu) {
        this.giaPhuThu = giaPhuThu;
    }
 
    public String getMoTa() {
        return moTa;
    }
 
    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
    public boolean kiemTraNgayLe(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return !date.isBefore(ngayBatDau) && !date.isAfter(ngayKetThuc);
    }
    public double tinhPhuThu(long giaGoc) {
        if (giaGoc <= 0) {
            return 0;
        }
        return giaGoc + giaPhuThu;
    }
 
    @Override
    public String toString() {
        return "NGAYLE{" +
                "maNgayLe='" + maNgayLe + '\'' +
                ", tenNgayLe='" + tenNgayLe + '\'' +
                ", ngayBatDau=" + ngayBatDau +
                ", ngayKetThuc=" + ngayKetThuc +
                ", giaPhuThu=" + giaPhuThu +
                ", moTa='" + moTa + '\'' +
                '}';
    }
}
