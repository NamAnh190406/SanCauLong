package Model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 *
 * @author Hi
 */
public class DanhGiaSan {
    private String maDanhGia;
    private int diemDG; 
    private String nhanXet;
    private Timestamp thoiDiemDanhGia;
    private String maKH;
    private String maSan;
    private boolean anDanhGia; 

    public DanhGiaSan() {
        this.anDanhGia = false;
    }
    public DanhGiaSan(String maDanhGia, int diemDG, String nhanXet, Timestamp thoiDiemDanhGia, String maKH, String maSan) {
        this.maDanhGia = maDanhGia;
        this.diemDG = diemDG;
        this.nhanXet = nhanXet;
        this.thoiDiemDanhGia = thoiDiemDanhGia;
        this.maKH = maKH;
        this.maSan = maSan;
        this.anDanhGia = false;
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

    public boolean isAnDanhGia() { return anDanhGia; }
    public void setAnDanhGia(boolean anDanhGia) { this.anDanhGia = anDanhGia; }

    public boolean chinhSuaDanhGia(int diemMoi, String nhanXetMoi) {
        if (this.anDanhGia) {
            System.out.println("Không thể chỉnh sửa đánh giá đã ẩn");
            return false;
        }
        
        if (diemMoi >= 1 && diemMoi <= 5) {
            this.diemDG = diemMoi; 
            this.nhanXet = nhanXetMoi;
            this.thoiDiemDanhGia = Timestamp.valueOf(LocalDateTime.now()); 
            System.out.println("Chỉnh sửa đánh giá thành công");
            return true;
        } else {
            System.out.println("Điểm đánh giá phải từ 1 đến 5");
            return false;
        }
    }

    public void anDanhGia() {
        this.anDanhGia = true;
        System.out.println("Đánh giá đã được ẩn");
    }

    public String getDanhGiaInfo() {
        if (this.anDanhGia) {
            return "Đánh giá này đã bị ẩn";
        }
        return "Mã ĐG: " + maDanhGia + "\n" +
               "Điểm: " + diemDG + "/5\n" +
               "Nhận xét: " + nhanXet + "\n" +
               "Thời điểm: " + thoiDiemDanhGia; 
    }

    @Override
    public String toString() {
        return "DanhGiaSan{" +
                "maDanhGia='" + maDanhGia + '\'' +
                ", diemDG=" + diemDG +
                ", nhanXet='" + nhanXet + '\'' +
                ", thoiDiemDanhGia=" + thoiDiemDanhGia +
                ", anDanhGia=" + anDanhGia +
                '}';
    }
}
