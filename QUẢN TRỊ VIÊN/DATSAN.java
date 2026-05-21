package com.example.guidemo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import java.time.LocalDate;

public class DATSAN {
    public String maDS;
    public String maKH;
    public String maNV;
    public String maHD;
    public String maSan;
    public String maKG;
    public LocalDate ngayDat;
    public String trangThai;
    public long tongTienTamTinh;

    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    public DATSAN() {}

    // Constructor đầy đủ dùng để load dữ liệu từ Database lên bảng hiển thị
    public DATSAN(String maDS, LocalDate ngayDat, String trangThai, long tongTienTamTinh, String maKH, String maSan, String maKG) {
        this.maDS = maDS;
        this.ngayDat = ngayDat;
        this.trangThai = trangThai;
        this.tongTienTamTinh = tongTienTamTinh;
        this.maKH = maKH;
        this.maSan = maSan;
        this.maKG = maKG;
    }

    // Các hàm Getter/Setter chuẩn hóa cho JavaFX TableView
    public String getMaDS() { return maDS; }
    public void setMaDS(String maDS) { this.maDS = maDS; }

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }

    public String getMaSan() { return maSan; }
    public void setMaSan(String maSan) { this.maSan = maSan; }

    public String getMaKG() { return maKG; }
    public void setMaKG(String maKG) { this.maKG = maKG; }

    public LocalDate getNgayDat() { return ngayDat; }
    public void setNgayDat(LocalDate ngayDat) { this.ngayDat = ngayDat; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public long getTongTienTamTinh() { return tongTienTamTinh; }
    public void setTongTienTamTinh(long tongTienTamTinh) { this.tongTienTamTinh = tongTienTamTinh; }

    // Xử lý Property cho Checkbox chọn hàng loạt
    public BooleanProperty selectedProperty() { return selected; }
    public boolean isSelected() { return selected.get(); }
    public void setSelected(boolean selected) { this.selected.set(selected); }

    // Các hàm logic nghiệp vụ của bạn
    public boolean HuyDatSan(String mads){
        if (this.maDS != null && this.maDS.equals(mads)) {
            this.trangThai = "DaHuy";
            return true;
        }
        return false;
    }

    public void XacNhanThanhToan() {
        this.trangThai = "HoanThanh";
    }
}