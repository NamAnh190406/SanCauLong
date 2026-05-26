package Service;

import DAO.DatSanDAO;
import DAO.HoaDonDAO;
import Model.DATSAN;
import Model.HOADON;
import Model.StatRow;
import View.ThongKeController.Filter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * Service layer for statistics – fetches REAL data from Oracle DB.
 * Used by View.ThongKeController (async task pattern).
 */
public class StatisticsService {

    private final HoaDonDAO hoaDonDAO;
    private final DatSanDAO datSanDAO;

    public StatisticsService() {
        this.hoaDonDAO = new HoaDonDAO();
        this.datSanDAO = new DatSanDAO();
    }

    // =========================================================
    // ENTRY POINT: trả về danh sách StatRow theo filter
    // =========================================================
    public List<StatRow> getStatsByFilter(Filter filter) {
        List<HOADON> allHD = hoaDonDAO.getAllHoaDon();
        List<DATSAN> allDS = datSanDAO.getallDatsans();

        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // Tạo map khung kết quả
        Map<String, StatRow> stats = buildEmptyMap(filter, now);

        // Lưu trữ khách hàng duy nhất (MaKH) cho từng bucket
        Map<String, Set<String>> customersPerBucket = new HashMap<>();
        for (String key : stats.keySet()) {
            customersPerBucket.put(key, new HashSet<>());
        }

        // 1. Tính Doanh thu từ HOADON
        for (HOADON hd : allHD) {
            String tt = hd.getTrangThai();
            if (tt == null || (!tt.equalsIgnoreCase("Da Thanh Toan") && !tt.equalsIgnoreCase("HoanThanh")))
                continue;

            LocalDate date = hd.getNgayXuat();
            if (date == null)
                continue;

            String key = resolveKey(filter, date, hd.getMaDS(), allDS, now, startOfWeek);
            if (key != null && stats.containsKey(key)) {
                long totalRevenue = hd.getThanhTien(); // Tiền dịch vụ - Giảm giá
                
                // Cộng thêm tiền sân từ DATSAN nếu có
                if (hd.getMaDS() != null && !hd.getMaDS().isEmpty()) {
                    for (DATSAN ds : allDS) {
                        if (ds.getMaDS().equals(hd.getMaDS())) {
                            totalRevenue += ds.getTongTienTamTinh();
                            break;
                        }
                    }
                }
                
                stats.get(key).addRevenue(totalRevenue);
            }
        }

        // 2. Tính Lượt đặt & Khách hàng từ DATSAN
        for (DATSAN ds : allDS) {
            String tt = ds.getTrangThai();
            if (tt != null && (tt.equalsIgnoreCase("DaHuy") || tt.equalsIgnoreCase("Cancelled")))
                continue;

            LocalDate date = ds.getNgayDat();
            if (date == null)
                continue;

            String key = resolveKey(filter, date, ds.getMaDS(), allDS, now, startOfWeek);
            if (key != null && stats.containsKey(key)) {
                stats.get(key).addBooking();
                if (ds.getMaKH() != null) {
                    customersPerBucket.get(key).add(ds.getMaKH());
                }
            }
        }

        // 3. Gán số khách hàng thực tế vào từng StatRow
        List<StatRow> result = new ArrayList<>(stats.values());
        for (StatRow r : result) {
            r.setCustomers(customersPerBucket.get(r.name()).size());
        }
        return result;
    }

    // =========================================================
    // Xây dựng Map rỗng theo filter
    // =========================================================
    private Map<String, StatRow> buildEmptyMap(Filter filter, LocalDate now) {
        Map<String, StatRow> map = new LinkedHashMap<>();
        switch (filter) {
            case CA:
                map.put("Ca 1 (06-08)", new StatRow("Ca 1 (06-08)", 0, 0, 0));
                map.put("Ca 2 (08-10)", new StatRow("Ca 2 (08-10)", 0, 0, 0));
                map.put("Ca 3 (10-12)", new StatRow("Ca 3 (10-12)", 0, 0, 0));
                map.put("Ca 4 (12-14)", new StatRow("Ca 4 (12-14)", 0, 0, 0));
                map.put("Ca 5 (14-16)", new StatRow("Ca 5 (14-16)", 0, 0, 0));
                map.put("Ca 6 (16-18)", new StatRow("Ca 6 (16-18)", 0, 0, 0));
                map.put("Ca 7 (18-20)", new StatRow("Ca 7 (18-20)", 0, 0, 0));
                map.put("Ca 8 (20-22)", new StatRow("Ca 8 (20-22)", 0, 0, 0));
                break;
            case NGAY:
                // Tuần hiện tại T2 → CN
                for (int i = 0; i < 7; i++) {
                    String label = i < 6 ? "T" + (i + 2) : "CN";
                    map.put(label, new StatRow(label, 0, 0, 0));
                }
                break;
            case THANG:
                for (int m = 1; m <= 12; m++) {
                    String label = "T" + m;
                    map.put(label, new StatRow(label, 0, 0, 0));
                }
                break;
            case QUY:
                map.put("Quý I", new StatRow("Quý I", 0, 0, 0));
                map.put("Quý II", new StatRow("Quý II", 0, 0, 0));
                map.put("Quý III", new StatRow("Quý III", 0, 0, 0));
                map.put("Quý IV", new StatRow("Quý IV", 0, 0, 0));
                break;
        }
        return map;
    }

    // =========================================================
    // Xác định key của mỗi hóa đơn theo filter
    // =========================================================
    private String resolveKey(Filter filter, LocalDate date, String maDS,
            List<DATSAN> allDS, LocalDate now, LocalDate startOfWeek) {
        switch (filter) {
            case CA: {
                // Chỉ tính hóa đơn của ngày hôm nay
                if (!date.isEqual(now))
                    return null;
                // Xác định ca qua MaKG của DATSAN liên quan
                int hour = 6; // mặc định ca 1
                if (maDS != null) {
                    for (DATSAN ds : allDS) {
                        if (maDS.equals(ds.getMaDS()) && ds.getMaKG() != null) {
                            hour = maKGToHour(ds.getMaKG());
                            break;
                        }
                    }
                }
                return hourToShift(hour);
            }
            case NGAY: {
                if (date.isBefore(startOfWeek) || date.isAfter(startOfWeek.plusDays(6)))
                    return null;
                int dow = date.getDayOfWeek().getValue(); // 1=Mon … 7=Sun
                return dow == 7 ? "CN" : "T" + (dow + 1);
            }
            case THANG: {
                if (date.getYear() != now.getYear())
                    return null;
                return "T" + date.getMonthValue();
            }
            case QUY: {
                if (date.getYear() != now.getYear())
                    return null;
                int q = (date.getMonthValue() - 1) / 3 + 1;
                return "Quý " + toRoman(q);
            }
            default:
                return null;
        }
    }

    // =========================================================
    // Helpers
    // =========================================================
    private static int maKGToHour(String maKG) {
        switch (maKG) {
            case "KG001":
                return 6;
            case "KG002":
                return 8;
            case "KG003":
                return 10;
            case "KG004":
                return 12;
            case "KG005":
                return 14;
            case "KG006":
                return 16;
            case "KG007":
                return 18;
            case "KG008":
                return 20;
            default:
                // Thử parse nếu MaKG chứa giờ dạng "HH:mm"
                try {
                    return LocalTime.parse(maKG.length() == 5 ? maKG : "06:00").getHour();
                } catch (Exception e) {
                    return 6;
                }
        }
    }

    private static String hourToShift(int hour) {
        if (hour < 8)
            return "Ca 1 (06-08)";
        if (hour < 10)
            return "Ca 2 (08-10)";
        if (hour < 12)
            return "Ca 3 (10-12)";
        if (hour < 14)
            return "Ca 4 (12-14)";
        if (hour < 16)
            return "Ca 5 (14-16)";
        if (hour < 18)
            return "Ca 6 (16-18)";
        if (hour < 20)
            return "Ca 7 (18-20)";
        return "Ca 8 (20-22)";
    }

    private static String toRoman(int q) {
        switch (q) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            default:
                return "IV";
        }
    }

    // =========================================================
    // Dữ liệu cho biểu đồ tròn – sử dụng DB thực
    // =========================================================
    /** Tổng hợp số lần đặt theo tên sân từ DB */
    public Map<String, Integer> getCourtUsageMap() {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (DATSAN ds : datSanDAO.getallDatsans()) {
            if ("DaHuy".equalsIgnoreCase(ds.getTrangThai()))
                continue;
            String name = ds.getTenSan() != null ? ds.getTenSan() : ds.getMaSan();
            if (name != null) {
                map.merge(name, 1, Integer::sum);
            }
        }
        return map;
    }

    /** Phân loại doanh thu theo loại hóa đơn từ DB */
    public Map<String, Long> getServiceRevenueMap() {
        Map<String, Long> map = new LinkedHashMap<>();
        for (HOADON hd : hoaDonDAO.getAllHoaDon()) {
            String tt = hd.getTrangThai();
            if (tt == null || (!tt.equalsIgnoreCase("Da Thanh Toan") && !tt.equalsIgnoreCase("HoanThanh")))
                continue;
            String label;
            if (HOADON.LOAI_DICH_VU.equals(hd.getLoaiHD())) {
                label = "Dịch vụ";
            } else {
                label = "Tiền sân";
            }
            map.merge(label, hd.getThanhTien(), Long::sum);
        }
        return map;
    }

    /** Thống kê số lượt đặt theo khung giờ (peak hours) */
    public Map<String, Integer> getPeakHoursMap() {
        String[] hours = { "06:00", "07:30", "09:00", "10:30", "13:00", "14:30", "16:00", "17:30", "19:00", "20:30" };
        Map<String, Integer> map = new LinkedHashMap<>();
        for (String h : hours)
            map.put(h, 0);

        for (DATSAN ds : datSanDAO.getallDatsans()) {
            if ("DaHuy".equalsIgnoreCase(ds.getTrangThai()))
                continue;
            if (ds.getMaKG() == null)
                continue;
            int h = maKGToHour(ds.getMaKG());
            // Ánh xạ về slot giờ gần nhất trong danh sách
            String slot = nearestSlot(h, hours);
            map.merge(slot, 1, Integer::sum);
        }
        return map;
    }

    private static String nearestSlot(int hour, String[] slots) {
        // Slot dạng "HH:mm" → lấy giờ nguyên
        String best = slots[0];
        int minDiff = Integer.MAX_VALUE;
        for (String s : slots) {
            int slotH = Integer.parseInt(s.split(":")[0]);
            int diff = Math.abs(hour - slotH);
            if (diff < minDiff) {
                minDiff = diff;
                best = s;
            }
        }
        return best;
    }
}
