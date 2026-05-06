/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;
import java.sql.Timestamp;
import java.time.LocalDateTime;
/**
 *
 * @author Hi
 */
public class ThanhToan {
    private String maTT;
    private String pttt;
    private Timestamp thoiGianTT;
    private long soTien;
    private String trangThai;
    private String maHoaDon;
    public ThanhToan(){
    }
    public ThanhToan(String maTT, String pttt, Timestamp thoiGianTT,String trangThai, String maHoaDon, long soTien) {
        this.maTT = maTT;
        this.pttt = pttt;
        this.thoiGianTT = thoiGianTT;
        this.trangThai = trangThai;
        this.soTien=soTien;
        this.maHoaDon = maHoaDon;
    }
    public String getMaTT() { return maTT; }
    public void setMaTT(String maTT) { this.maTT = maTT; }

    public String getPttt() { return pttt; }
    public void setPttt(String pttt) { this.pttt = pttt; }

    public Timestamp getThoiGianTT() { return thoiGianTT; }
    public void setThoiGianTT(Timestamp thoiGianTT) { this.thoiGianTT = thoiGianTT; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }

    @Override
    public String toString() {
        return pttt + " - " + trangThai;
    }
    public boolean xuLyThanhToan(String phuongThucThanhToan) {
        try {
            if (phuongThucThanhToan == null || phuongThucThanhToan.isEmpty()) {
                return false;
            }
            this.pttt = phuongThucThanhToan;
            this.trangThai = "Đã thanh toán";
            this.thoiGianTT = Timestamp.valueOf(LocalDateTime.now());
            return true;
        } catch (Exception e) {
            System.out.println("Lỗi xử lý thanh toán: " + e.getMessage());
            return false;
        }
    }
    public void capNhatTrangThai(String trangThaiMoi) {
        this.trangThai = trangThaiMoi;
    }
    public boolean hoanTien(long soTienHoan) {
        if (soTienHoan <= 0 || soTienHoan > soTien) {
            return false;
        }
        this.soTien = this.soTien - soTienHoan;
        this.trangThai = "Đã hoàn tiền";
        return true;
    }
    public String xuatHoaDon() {
        StringBuilder hd = new StringBuilder();
        hd.append("           HÓA ĐƠN THANH TOÁN              \n");
        hd.append("Mã giao dịch: ").append(maTT).append("\n");
        hd.append("Phương thức: ").append(pttt).append("\n");
        hd.append("Số tiền: ").append(soTien).append("\n");
        hd.append("Thời gian: ").append(thoiGianTT).append("\n");
        hd.append("Trạng thái: ").append(trangThai).append("\n");
        hd.append("Hoá đơn: ").append(maHoaDon).append("\n");
        hd.append("                                         \n");
        return hd.toString();
    }
    
}
