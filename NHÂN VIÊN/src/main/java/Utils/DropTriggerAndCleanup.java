package Utils;

import java.sql.Connection;
import java.sql.Statement;

public class DropTriggerAndCleanup {
    public static void main(String[] args) {
        Databasehelper dbHelper = new Databasehelper();
        try (Connection con = dbHelper.createCon();
             Statement stmt = con.createStatement()) {
            
            // 1. Vô hiệu hóa (DISABLE) hoặc DROP Trigger (Khuyên dùng DISABLE để có thể xem lại nếu cần)
            try {
                stmt.execute("ALTER TRIGGER TRG_DATSAN_TUDONGTAOHOADON DISABLE");
                System.out.println("✓ Đã vô hiệu hóa Trigger TRG_DATSAN_TUDONGTAOHOADON");
            } catch (Exception e) {
                System.out.println("Lưu ý: Không thể vô hiệu hóa Trigger (có thể nó không tồn tại hoặc đã bị xóa).");
            }

            // Delete child records from THANHTOAN
            stmt.executeUpdate("DELETE FROM THANHTOAN WHERE MaHoaDon IN (SELECT MaHoaDon FROM HOADON WHERE NgayXuat IS NULL OR LoaiHD IS NULL OR (MaHoaDon LIKE 'HD%' AND MaHoaDon NOT LIKE 'HDSAN%'))");


            int rowsDeleted = stmt.executeUpdate(
                "DELETE FROM HOADON WHERE NgayXuat IS NULL OR LoaiHD IS NULL OR (MaHoaDon LIKE 'HD%' AND MaHoaDon NOT LIKE 'HDSAN%')"
            );
            System.out.println("✓ Đã xóa " + rowsDeleted + " hóa đơn rác/lỗi do Trigger sinh ra.");
            
            System.out.println("\nHoàn tất Thống nhất Logic!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
