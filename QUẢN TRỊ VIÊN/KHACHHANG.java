package com.example.guidemo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KHACHHANG {
    public String MaKH;
    public String HoTen;
    public String SDT;
    public String Email;
    public java.util.Date NgayDK;
    public String HangThanhVien;
    public int DiemTichLuy;
    public Connection conn;

    // 🌟 THÊM BIẾN NÀY ĐỂ PHỤC VỤ CỘT CHỌN CHECKBOX
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    public KHACHHANG() {
        try {
            this.conn = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 🌟 THÊM CONSTRUCTOR NÀY ĐỂ ĐỔ DỮ LIỆU TỪ ORACLE VÀO LIST
    public KHACHHANG(String maKH, String hoTen, String sdt, String email, java.util.Date ngayDK, String hangThanhVien, int diemTichLuy) {
        this.MaKH = maKH;
        this.HoTen = hoTen;
        this.SDT = sdt;
        this.Email = email;
        this.NgayDK = ngayDK;
        this.HangThanhVien = hangThanhVien;
        this.DiemTichLuy = diemTichLuy;
    }

    // 🌟 THÊM CÁC HÀM GETTER ĐỂ TABLEVIEW ĐỌC DỮ LIỆU
    public String getMaKH() { return MaKH; }
    public String getHoTen() { return HoTen; }
    public String getSDT() { return SDT; }
    public String getEmail() { return Email; }
    public java.util.Date getNgayDK() { return NgayDK; }
    public String getHangThanhVien() { return HangThanhVien; }
    public int getDiemTichLuy() { return DiemTichLuy; }

    public BooleanProperty selectedProperty() { return selected; }
    public boolean isSelected() { return selected.get(); }
    public void setSelected(boolean selected) { this.selected.set(selected); }

    public List<DATSAN> XemLichSu() {
        List<DATSAN> LSDS = new ArrayList<>(); // Sửa lỗi khởi tạo List
        String sql = "SELECT * FROM DATSAN WHERE MaKH = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.MaKH);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                DATSAN ds = new DATSAN();
                ds.maDS = rs.getString("MaDS");

                // Chuyển từ sql.Date sang LocalDate
                Date sqlDate = rs.getDate("NgayDat");
                if (sqlDate != null) {
                    ds.ngayDat = sqlDate.toLocalDate();
                }

                ds.trangThai = rs.getString("TrangThai");
                // Chuyển từ DB sang long (thay vì getString)
                ds.tongTienTamTinh = rs.getLong("TongTienTamTinh");
                ds.maKH = this.MaKH;
                ds.maSan = rs.getString("MaSan");
                ds.maKG = rs.getString("MaKG");

                LSDS.add(ds);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi xuất lịch sử: " + e.getMessage());
        }
        return LSDS;
    }

    // 2. Cập nhật thông tin (Sửa lỗi dấu AND trong UPDATE và sai tham số)
    public boolean CapNhatThongTin(String HoTenms, String SDTms, String Emailms) {
        // SQL Update dùng dấu phẩy (,), không dùng AND
        String sql = "UPDATE KHACHHANG SET HoTen = ?, SDT = ?, Email = ? WHERE MaKH = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, HoTenms);
            pstmt.setString(2, SDTms);
            pstmt.setString(3, Emailms);
            pstmt.setString(4, this.MaKH);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. Tích điểm (Bổ sung phần thực thi còn thiếu)
    public boolean TichDiem(int DiemTichThem) {
        String sql = "UPDATE KHACHHANG SET DiemTichLuy = DiemTichLuy + ? WHERE MaKH = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, DiemTichThem);
            pstmt.setString(2, this.MaKH);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
