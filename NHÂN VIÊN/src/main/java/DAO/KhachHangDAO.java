package DAO;

import Controller.CustomerManagementController.CustomerModel;
import Utils.Databasehelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import Model.KHACHHANG;

public class KhachHangDAO {

    // 1. Lấy toàn bộ khách hàng để đổ lên Bảng (TableView)
    public static ObservableList<KHACHHANG> getDanhSachKhachHang() {
        ObservableList<KHACHHANG> dsKhachHang = FXCollections.observableArrayList();
        String sql = "SELECT MaKH, HoTen, SDT, Email, DiaChi, DiemTichLuy, HangThanhVien FROM KHACHHANG";

        Databasehelper db = new Databasehelper();
        try (Connection conn = db.createCon();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                KHACHHANG kh = new KHACHHANG(
                        rs.getString("MaKH"),
                        rs.getString("HoTen"),
                        rs.getString("SDT") != null ? rs.getString("SDT") : "",
                        rs.getString("Email") != null ? rs.getString("Email") : "",
                        rs.getString("DiaChi") != null ? rs.getString("DiaChi") : "",
                        rs.getString("HangThanhVien") != null ? rs.getString("HangThanhVien") : "Đồng",
                        rs.getInt("DiemTichLuy")
                );
                dsKhachHang.add(kh);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải danh sách khách hàng: " + e.getMessage());
            e.printStackTrace();
        }
        return dsKhachHang;
    }

    // 2. Thêm khách hàng mới
    public static boolean themKhachHangMoi(KHACHHANG kh) {
        String sql = "INSERT INTO KHACHHANG (MaKH, HoTen, SDT, Email, DiaChi, HangThanhVien, DiemTichLuy, NgayDK) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, SYSDATE)";
                     
        Databasehelper db = new Databasehelper();
        try (Connection conn = db.createCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, kh.getMaKH());
            pstmt.setString(2, kh.getHoTen());
            pstmt.setString(3, kh.getSDT());
            pstmt.setString(4, kh.getEmail());
            pstmt.setString(5, kh.getDiaChi());
            pstmt.setString(6, kh.getHangThanhVien()); // Hạng thành viên
            pstmt.setInt(7, kh.getDiemTichLuy()); // Điểm tích lũy ban đầu = 0

            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Lỗi thêm khách hàng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    // 3. Xóa khách hàng (có thể nhận một hoặc nhiều ID)
    public static boolean xoaKhachHang(List<String> danhSachId) {
        if (danhSachId == null || danhSachId.isEmpty()) {
            return false;
        }
        StringBuilder sqlBuilder = new StringBuilder("DELETE FROM KHACHHANG WHERE MaKH IN (");
        for (int i = 0; i < danhSachId.size(); i++) {
            sqlBuilder.append("?");
            if (i < danhSachId.size() - 1) {
                sqlBuilder.append(", ");
            }
        }
        sqlBuilder.append(")");

        Databasehelper db = new Databasehelper();
        try (Connection conn = db.createCon();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {

            // Gán giá trị ID vào từng tham số ?
            for (int i = 0; i < danhSachId.size(); i++) {
                pstmt.setString(i + 1, danhSachId.get(i));
            }

            // ExecuteUpdate trả về số dòng bị ảnh hưởng
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("Lỗi khi xóa khách hàng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    // 4. Cập nhật thông tin khách hàng
    public static boolean capNhatKhachHang(KHACHHANG kh) {
        String sql = "UPDATE KHACHHANG SET HoTen = ?, SDT = ?, Email = ?, DiaChi = ? WHERE MaKH = ?";
                     
        Utils.Databasehelper db = new Utils.Databasehelper();
        try (java.sql.Connection conn = db.createCon();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, kh.getHoTen());
            pstmt.setString(2, kh.getSDT());
            pstmt.setString(3, kh.getEmail());
            pstmt.setString(4, kh.getDiaChi());
            pstmt.setString(5, kh.getMaKH()); // Điều kiện WHERE

            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Lỗi cập nhật khách hàng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Bạn có thể bê các hàm CapNhatThongTin, TichDiem, XemLichSu vào đây...
}