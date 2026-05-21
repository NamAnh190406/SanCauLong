package com.example.guidemo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import java.sql.Timestamp;


public class DANHGIASAN {
    private String maDanhGia;       // MAP: MaDanhGia VARCHAR2(20)
    private int diemDG;             // MAP: DiemDG NUMBER
    private String nhanXet;         // MAP: NhanXet VARCHAR2(1000)
    private Timestamp thoiDiemDanhGia; // MAP: ThoiDiemDanhGia TIMESTAMP
    private String maKH;            // MAP: MaKH VARCHAR2(20)
    private String maSan;           // MAP: MaSan VARCHAR2(20)
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    public DANHGIASAN() {
    }

    public DANHGIASAN(String maDanhGia, int diemDG, String nhanXet, Timestamp thoiDiemDanhGia, String maKH, String maSan) {
        this.maDanhGia = maDanhGia;
        this.diemDG = diemDG;
        this.nhanXet = nhanXet;
        this.thoiDiemDanhGia = thoiDiemDanhGia;
        this.maKH = maKH;
        this.maSan = maSan;
    }

    public String getMaDanhGia() { return maDanhGia; }
    public void setMaDanhGia(String maDanhGia) { this.maDanhGia = maDanhGia; }

    public int getDiemDG() { return diemDG; }
    public void setDiemDG(int diemDG) { this.diemDG = diemDG; }

    public String getNhanXet() { return nhanXet; }
    public void setNhanXet(String nhanXet) { this.nhanXet = nhanXet; }

    public Timestamp getThoiDiemDanhGia() { return thoiDiemDanhGia; }
    public void setThoiDiemDanhGia(Timestamp thoiDiemDanhGia) { this.thoiDiemDanhGia = thoiDiemDanhGia; }

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }

    public String getMaSan() { return maSan; }
    public void setMaSan(String maSan) { this.maSan = maSan; }

    // Getter/Setter cho Checkbox
    public BooleanProperty selectedProperty() { return selected; }
    public boolean isSelected() { return selected.get(); }
    public void setSelected(boolean selected) { this.selected.set(selected); }

    @Override
    public String toString() {
        return "DanhGiaSan{" +
                "maDanhGia='" + maDanhGia + '\'' +
                ", diemDG=" + diemDG +
                ", nhanXet='" + nhanXet + '\'' +
                ", thoiDiemDanhGia=" + thoiDiemDanhGia +
                ", maKH='" + maKH + '\'' +
                ", maSan='" + maSan + '\'' +
                '}';
    }
}