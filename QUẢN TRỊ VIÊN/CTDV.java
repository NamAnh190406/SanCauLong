package com.example.guidemo;



public class CTDV {
    public String maCTDV;
    public String maDV;
    public String maDS;
    public long soLuong;
    public long donGia;
    public long thanhTien;

    public CTDV() {
    }
    public CTDV(String maCTDV, String maDS, String maDV, long soLuong, long donGia) {
        this.maCTDV = maCTDV;
        this.maDS = maDS;
        this.maDV = maDV;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    public String getMaCTDV() { return maCTDV; }
    public void setMaCTDV(String maCTDV) { this.maCTDV = maCTDV; }

    public String getMaDV() { return maDV; }
    public void setMaDV(String maDV) { this.maDV = maDV; }

    public String getMaDS() { return maDS; }
    public void setMaDS(String maDS) { this.maDS = maDS; }

    public long getSoLuong() { return soLuong; }
    public void setSoLuong(long soLuong) {
        this.soLuong = soLuong;
        this.thanhTien = tinhThanhTien();
    }

    public long getDonGia() { return donGia; }
    public void setDonGia(long donGia) {
        this.donGia = donGia;
        this.thanhTien = tinhThanhTien();
    }

    public long getThanhTien() { return thanhTien; }

    public long tinhThanhTien() {
        this.thanhTien = soLuong * donGia;
        return this.thanhTien;
    }

    public boolean thayDoiSoLuong(long soLuongMoi) {
        if (soLuongMoi < 0) {
            return false;
        }
        this.soLuong = soLuongMoi;
        this.thanhTien = tinhThanhTien();
        return true;
    }

    @Override
    public String toString() {
        return "CTDV{" +
                "maCTDV='" + maCTDV + '\'' +
                ", maDV='" + maDV + '\'' +
                ", maDS='" + maDS + '\'' +
                ", soLuong=" + soLuong +
                ", donGia=" + donGia +
                ", thanhTien=" + thanhTien +
                '}';
    }
}
