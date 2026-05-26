package Utils;

import DAO.ThongBaoDAO;

/**
 * NotificationHelper — Utility dùng chung để ghi thông báo hệ thống.
 *
 * Mọi sự kiện quan trọng (đặt sân, thêm KH, check-in...) đều gọi qua class này.
 * Cung cấp các method tĩnh để không cần khởi tạo đối tượng.
 *
 * Loại thông báo (loai): "success" | "warning" | "error" | "info"
 */
public class NotificationHelper {

    // =====================================================================
    // ĐẶT SÂN
    // =====================================================================

    /** Khi đặt sân thành công */
    public static void datSanThanhCong(String maDatSan, String tenKH, String maSan, String ngayDat) {
        gui("🏸 Đặt sân thành công",
            "Khách hàng: " + tenKH + " đã đặt " + maSan +
            " vào ngày " + ngayDat + " | Mã ĐS: " + maDatSan,
            "success");
    }

    /** Khi hủy đặt sân */
    public static void huyDatSan(String maDatSan, String tenKH) {
        gui("⚠ Hủy đặt sân",
            "Phiếu đặt sân " + maDatSan + " của " + tenKH + " đã bị hủy.",
            "warning");
    }

    // =====================================================================
    // CHECK-IN / CHECK-OUT SÂN
    // =====================================================================

    /** Khi sân chuyển sang trạng thái "Đang sử dụng" (check-in) */
    public static void checkInSan(String maSan, String tenSan, String tenKH) {
        gui("🟢 Check-in sân",
            tenKH + " đã bắt đầu sử dụng " + tenSan + " (" + maSan + ")",
            "success");
    }

    /** Khi sân chuyển sang "Trống" (check-out / kết thúc) */
    public static void checkOutSan(String maSan, String tenSan) {
        gui("🔵 Kết thúc sân",
            tenSan + " (" + maSan + ") đã kết thúc sử dụng và hiện đang Trống.",
            "info");
    }

    // =====================================================================
    // KHÁCH HÀNG
    // =====================================================================

    /** Khi thêm khách hàng mới */
    public static void themKhachHang(String maKH, String tenKH, String sdt) {
        gui("👤 Thêm khách hàng",
            "Đã thêm khách hàng mới: " + tenKH + " | SĐT: " + sdt + " | Mã KH: " + maKH,
            "success");
    }

    /** Khi cập nhật thông tin khách hàng */
    public static void capNhatKhachHang(String maKH, String tenKH) {
        gui("✏ Cập nhật khách hàng",
            "Thông tin khách hàng " + tenKH + " (Mã: " + maKH + ") đã được cập nhật.",
            "info");
    }

    /** Khi xóa khách hàng */
    public static void xoaKhachHang(String tenKH, String maKH) {
        gui("🗑 Xóa khách hàng",
            "Đã xóa khách hàng: " + tenKH + " (Mã: " + maKH + ")",
            "warning");
    }

    // =====================================================================
    // HÓA ĐƠN & THANH TOÁN
    // =====================================================================

    /** Khi tạo hóa đơn đặt sân */
    public static void taoHoaDonDatSan(String maHD, String maDatSan, long soTien) {
        gui("📄 Tạo hóa đơn đặt sân",
            "Hóa đơn " + maHD + " được tạo cho phiếu đặt sân " + maDatSan +
            " | Tổng tiền: " + formatTien(soTien),
            "info");
    }

    /** Khi tạo hóa đơn dịch vụ */
    public static void taoHoaDonDichVu(String maHD, long soTien, String ghiChu) {
        gui("📄 Tạo hóa đơn dịch vụ",
            "Hóa đơn dịch vụ " + maHD + " | " + formatTien(soTien) +
            (ghiChu != null && !ghiChu.isEmpty() ? " | Ghi chú: " + ghiChu : ""),
            "info");
    }

    /** Khi thanh toán thành công */
    public static void thanhToanThanhCong(String maHD, String phuongThuc, long soTien) {
        gui("✅ Thanh toán thành công",
            "Hóa đơn " + maHD + " đã thanh toán bằng " + phuongThuc +
            " | Số tiền: " + formatTien(soTien),
            "success");
    }

    // =====================================================================
    // LỖI HỆ THỐNG
    // =====================================================================

    /** Thông báo lỗi chung */
    public static void loiHeThong(String nguonLoi, String chiTiet) {
        gui("❌ Lỗi hệ thống",
            "[" + nguonLoi + "] " + chiTiet,
            "error");
    }

    // =====================================================================
    // INTERNAL
    // =====================================================================

    /**
     * Gửi thông báo vào DB. Nếu lỗi thì chỉ log ra console, không ném Exception
     * để không làm gián đoạn luồng nghiệp vụ chính.
     */
    private static void gui(String tieuDe, String noiDung, String loai) {
        try {
            ThongBaoDAO.themThongBao(tieuDe, noiDung, loai);
        } catch (Exception e) {
            System.err.println("[NotificationHelper] Không thể lưu thông báo: " + e.getMessage());
        }
    }

    /** Format tiền VND gọn (không cần import NumberFormat) */
    private static String formatTien(long soTien) {
        return String.format("%,d đ", soTien).replace(',', '.');
    }
}
