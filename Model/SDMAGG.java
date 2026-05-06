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
public class SDMAGG {
    private String maSuDung;
    private LocalDate ngaySuDung;
    private long soTienGiam;
    private String maKH;
    private String maKM;
    public SDMAGG() {
    }
 
    public SDMAGG(String maSuDung, LocalDate ngaySuDung, String makm, String makh) {
        this.maSuDung = maSuDung;
        this.ngaySuDung = ngaySuDung;
        this.maKM=makm;
        this.maKH=makh;
    }
    public String getMaSuDung() {
        return maSuDung;
    }
 
    public void setMaSuDung(String maSuDung) {
        this.maSuDung = maSuDung;
    }
 public String getMaKH() {
        return maKH;
    }
 
    public void setMaKH(String maSuDung) {
        this.maKH = maSuDung;
    }
     public String getMaKM() {
        return maKM;
    }
 
    public void setMaKM(String maSuDung) {
        this.maKM = maSuDung;
    }
    public LocalDate getNgaySuDung() {
        return ngaySuDung;
    }
 
    public void setNgaySuDung(LocalDate ngaySuDung) {
        this.ngaySuDung = ngaySuDung;
    }
 
    public long getSoTienGiam() {
        return soTienGiam;
    }
 
    public void setSoTienGiam(long soTienGiam) {
        this.soTienGiam = soTienGiam;
    }
    public void ghiNhanSuDung(Object khachHang, Object khuyenMai, Object ctds) {
        try {
            if (khachHang == null || khuyenMai == null || ctds == null) {
                System.out.println("Dữ liệu không hợp lệ");
                return;
            }
            this.maSuDung = generateMaSuDung();
            this.ngaySuDung = LocalDate.now();
            System.out.println("Ghi nhận sử dụng mã giảm giá thành công!");
            System.out.println("Mã sử dụng: " + maSuDung);
            System.out.println("Ngày sử dụng: " + ngaySuDung);
        } catch (Exception e) {
            System.out.println("Lỗi ghi nhận sử dụng: " + e.getMessage());
        }
    }

    private String generateMaSuDung() {
        return "MAGG" + System.currentTimeMillis();
    }
    public boolean kiemTraDaDung(String makh, String makm) {
        if (maKH == null || maKM == null) {
            return false;
        }
        if(!makh.equals(this.maKH)||!makm.equals(this.maKM))
            return false;
        return maSuDung != null && !maSuDung.isEmpty() && ngaySuDung != null;
    }
    @Override
    public String toString() {
        return "SDMAGG{" +
                "maSuDung='" + maSuDung + '\'' +
                ", ngaySuDung=" + ngaySuDung +
                ", soTienGiam=" + soTienGiam +
                '}';
    }
}
