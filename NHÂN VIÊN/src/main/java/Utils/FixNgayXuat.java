package Utils;

import java.sql.Connection;
import java.sql.Statement;

public class FixNgayXuat {
    public static void main(String[] args) {
        Databasehelper db = new Databasehelper();
        try (Connection con = db.createCon(); Statement stmt = con.createStatement()) {

            try {
                stmt.execute("ALTER TABLE HOADON ADD NgayXuat DATE");
                System.out.println("✅ Đã thêm cột NgayXuat vào bảng HOADON.");
            } catch (Exception e) {
                if (e.getMessage().contains("ORA-01430")) {
                    System.out.println("Cột NgayXuat đã tồn tại.");
                } else {
                    System.err.println("Lỗi thêm cột: " + e.getMessage());
                }
            }

            try {
                stmt.execute("UPDATE HOADON SET NgayXuat = SYSDATE WHERE NgayXuat IS NULL");
                System.out.println("✅ Đã cập nhật NgayXuat cho các dữ liệu cũ.");
            } catch (Exception e) {
                System.err.println("Lỗi cập nhật dữ liệu: " + e.getMessage());
            }

            System.out.println("🎉 HOÀN TẤT!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
