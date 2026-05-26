package Utils;

import java.sql.Connection;
import java.sql.Statement;

public class RunDBMigration {
    public static void main(String[] args) {
        Databasehelper db = new Databasehelper();
        try (Connection con = db.createCon(); Statement stmt = con.createStatement()) {

            // 1. Thêm cột LoaiHD
            try {
                stmt.execute("ALTER TABLE HOADON ADD LoaiHD VARCHAR2(20)");
                System.out.println("✅ Đã thêm cột LoaiHD vào bảng HOADON.");
            } catch (Exception e) {
                if (e.getMessage().contains("ORA-01430")) {
                    System.out.println("Cột LoaiHD đã tồn tại.");
                } else {
                    System.err.println("Lỗi thêm cột: " + e.getMessage());
                }
            }

            // 2. Cập nhật dữ liệu cũ
            try {
                stmt.execute("UPDATE HOADON SET LoaiHD = 'DAT_SAN' WHERE MaDS IS NOT NULL");
                stmt.execute("UPDATE HOADON SET LoaiHD = 'DICH_VU' WHERE MaDS IS NULL");
                System.out.println("✅ Đã cập nhật dữ liệu Loại Hóa Đơn cũ.");
            } catch (Exception e) {
                System.err.println("Lỗi cập nhật dữ liệu: " + e.getMessage());
            }

            // Bảng DICHVU
            try {
                stmt.execute("CREATE TABLE DICHVU (" +
                        "MaDV VARCHAR2(20) PRIMARY KEY, " +
                        "TenDV VARCHAR2(100), " +
                        "DanhMuc VARCHAR2(50), " +
                        "DonVi VARCHAR2(20), " +
                        "Gia NUMBER(18, 0), " +
                        "TonKho NUMBER, " +
                        "TonKhoToiThieu NUMBER, " +
                        "TrangThai VARCHAR2(50))");
                System.out.println("✅ Đã tạo bảng DICHVU.");
            } catch (Exception e) {
                if (e.getMessage().contains("ORA-00955")) {
                    System.out.println("Bảng DICHVU đã tồn tại, tiến hành thêm cột nếu thiếu...");
                    String[] cols = {
                        "ALTER TABLE DICHVU ADD LOAIDV VARCHAR2(50)",
                        "ALTER TABLE DICHVU ADD DONVI VARCHAR2(20)",
                        "ALTER TABLE DICHVU ADD SOLUONGTON NUMBER",
                        "ALTER TABLE DICHVU ADD TONKHOTOITHIEU NUMBER",
                        "ALTER TABLE DICHVU ADD TRANGTHAI VARCHAR2(50)"
                    };
                    for (String q : cols) {
                        try {
                            stmt.execute(q);
                            System.out.println("Đã thêm cột mới: " + q);
                        } catch (Exception ex) {} // Bỏ qua nếu cột đã tồn tại
                    }
                    try {
                        stmt.execute("UPDATE DICHVU SET TRANGTHAI = 'Hoạt động' WHERE TRANGTHAI IS NULL");
                        stmt.execute("UPDATE DICHVU SET SOLUONGTON = 0 WHERE SOLUONGTON IS NULL");
                    } catch (Exception ex) {}
                }
            }

            // Bảng DONDICHVU
            try {
                stmt.execute("CREATE TABLE DONDICHVU (" +
                        "MaDonDV VARCHAR2(20) PRIMARY KEY, " +
                        "MaKH VARCHAR2(20), " +
                        "MaSan VARCHAR2(20), " +
                        "TongTien NUMBER(18, 0), " +
                        "TrangThai VARCHAR2(50), " +
                        "GhiChu VARCHAR2(200), " +
                        "NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
                System.out.println("✅ Đã tạo bảng DONDICHVU.");
            } catch (Exception e) {
                if (e.getMessage().contains("ORA-00955")) {
                    System.out.println("Bảng DONDICHVU đã tồn tại.");
                }
            }

            // Bảng CHITIETDONDV
            try {
                stmt.execute("CREATE TABLE CHITIETDONDV (" +
                        "MaDonDV VARCHAR2(20), " +
                        "MaDV VARCHAR2(20), " +
                        "SoLuong NUMBER, " +
                        "DonGia NUMBER(18, 0), " +
                        "PRIMARY KEY (MaDonDV, MaDV))");
                System.out.println("✅ Đã tạo bảng CHITIETDONDV.");
            } catch (Exception e) {
                if (e.getMessage().contains("ORA-00955")) {
                    System.out.println("Bảng CHITIETDONDV đã tồn tại.");
                }
            }

            System.out.println("🎉 HOÀN TẤT CHẠY MIGRATION MỚI!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
