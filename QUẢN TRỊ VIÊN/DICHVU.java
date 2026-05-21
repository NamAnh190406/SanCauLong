package com.example.guidemo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Model DICHVU mapping chính xác 1:1 với cấu trúc bảng Oracle DB
 * @author Hi
 */
public class DICHVU {
    // Các thuộc tính mapping chính xác với Oracle
    private String maDV;        // MAP: MaDV VARCHAR2(20)
    private String tenDV;       // MAP: TenDV VARCHAR2(100)
    private String donViTinh;   // MAP: DonViTinh VARCHAR2(30)
    private long giaBan;        // MAP: GiaBan NUMBER
    private int slTonKho;       // MAP: SLTonkho NUMBER
    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    public DICHVU() {
    }
    public DICHVU(String maDV, String tenDV, String donViTinh, long giaBan, int slTonKho) {
        this.maDV = maDV;
        this.tenDV = tenDV;
        this.donViTinh = donViTinh;
        this.giaBan = giaBan;
        this.slTonKho = slTonKho;
    }
    public DICHVU(String maDV, String tenDV, long donGia) {
        this.maDV = maDV;
        this.tenDV = tenDV;
        this.giaBan = donGia;
        this.donViTinh = "";
        this.slTonKho = 0;
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

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public long getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(long giaBan) {
        this.giaBan = giaBan;
    }

    public int getSlTonKho() {
        return slTonKho;
    }

    public void setSlTonKho(int slTonKho) {
        this.slTonKho = slTonKho;
    }

    // Hàm trả về trạng thái động dựa trực tiếp trên số lượng tồn kho
    public String getTrangThai() {
        return (this.slTonKho > 0) ? "Hoạt động" : "Hết hàng";
    }

    // Getter/Setter cho Checkbox trên TableView
    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }


    public boolean kiemTraTonKho(int soLuongCanKiem) {
        return this.slTonKho >= soLuongCanKiem && "Hoạt động".equals(getTrangThai());
    }


    public void capNhatTonKho(int soLuongThayDoi) {
        this.slTonKho += soLuongThayDoi;
        if (this.slTonKho < 0) {
            this.slTonKho = 0; // Đảm bảo không bị âm kho
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
        return "DICHVU{" +
                "maDV='" + maDV + '\'' +
                ", tenDV='" + tenDV + '\'' +
                ", donViTinh='" + donViTinh + '\'' +
                ", giaBan=" + giaBan +
                ", slTonKho=" + slTonKho +
                ", trangThai='" + getTrangThai() + '\'' +
                '}';
    }
}