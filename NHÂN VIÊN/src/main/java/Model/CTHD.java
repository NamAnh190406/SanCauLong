/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;
/**
 *
 * @author Hi
 */
//Thuộc tính: 
//MaHD: String: Mã hoá đơn
//MaCTDS: String: Mã chi tiết đặt sân
//TienThanhToan: long: Tiền thanh toán
//Phương thức: 
//TinhThanhTien(): long


public class CTHD {
    private String maHD;
    private String maCTDS;
    private String maSan;
    private int soLuong;
    private long donGia;
    private long tienThanhToan;
    public CTHD() {
    }
    
    public CTHD(String maHD, String maCTDS, String maSan, int soLuong, long donGia) {
        this.maHD = maHD;
        this.maCTDS = maCTDS;
        this.maSan = maSan;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.tienThanhToan = tinhTienThanhToan(); 
    }
    public long tinhTienThanhToan() {
        return (long) this.soLuong * this.donGia;
    }
    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getMaCTDS() {
        return maCTDS;
    }

    public void setMaCTDS(String maCTDS) {
        this.maCTDS = maCTDS;
    }

    public String getMaSan() {
        return maSan;
    }

    public void setMaSan(String maSan) {
        this.maSan = maSan;
    }

    public long getTienThanhToan() {
        return tienThanhToan;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
        this.tienThanhToan = tinhTienThanhToan(); 
    }

    public long getDonGia() {
        return donGia;
    }

    public void setDonGia(long donGia) {
        this.donGia = donGia;
        this.tienThanhToan = tinhTienThanhToan(); 
    }
    @Override
    public String toString() {
        return "CTHD{" +
                "maHD='" + maHD + '\'' +
                ", maCTDS='" + maCTDS + '\'' +
                ", maSan='" + maSan + '\'' +
                ", tienThanhToan=" + tienThanhToan +
                '}';
    }
    
}
