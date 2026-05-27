package DAO;

import Utils.Databasehelper;
import Model.DATSAN;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatSanDAO {
    private final Databasehelper dbHelper;

    public DatSanDAO() {
        dbHelper = new Databasehelper();
    }

    // =========================================================================
    // 1. LẤY TOÀN BỘ DANH SÁCH ĐẶT SÂN (Sử dụng INNER JOIN hiển thị chữ lên UI)
    // =========================================================================
    public ObservableList<DATSAN> getallDatsans() {
        ObservableList<DATSAN> list = FXCollections.observableArrayList();

        String sql = "SELECT ds.MaDS, ds.MaKH, ds.MaNV, ds.MaHD, ds.MaSan, ds.MaKG, ds.NgayDat, ds.TongTienTamTinh, ds.TrangThai, "
                +
                "kh.HoTen AS TenKhachHang, s.TenSan, " +
                "TO_CHAR(kg.GioBD, 'HH24:MI') || ' - ' || TO_CHAR(kg.GioKT, 'HH24:MI') AS CbxKhungGio " +
                "FROM DATSAN ds " +
                "LEFT JOIN KHACHHANG kh ON ds.MaKH = kh.MaKH " +
                "LEFT JOIN SAN s ON ds.MaSan = s.MaSan " +
                "LEFT JOIN KHUNGGIO kg ON ds.MaKG = kg.MaKG " +
                "ORDER BY ds.NgayDat DESC, ds.MaDS DESC";

        try (Connection conn = dbHelper.createCon();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Sử dụng Constructor 7 tham số mặc định ban đầu của bạn
                DATSAN ds = new DATSAN(
                        rs.getString("MaDS"),
                        rs.getString("MaKH"),
                        rs.getString("MaNV"),
                        rs.getString("MaHD"),
                        rs.getDate("NgayDat") != null ? rs.getDate("NgayDat").toLocalDate() : null,
                        rs.getLong("TongTienTamTinh"),
                        rs.getString("TrangThai"));

                // Gán thêm dữ liệu mã gốc (đề phòng cần dùng cho xử lý ẩn)
                try {
                    ds.setMaSan(rs.getString("MaSan"));
                } catch (Exception e) {
                }
                try {
                    ds.setMaKG(rs.getString("MaKG"));
                } catch (Exception e) {
                }

                // Gán thêm dữ liệu chữ (Để TableView hiển thị thông qua thuộc tính mở rộng)
                try {
                    ds.setTenKH(rs.getString("TenKhachHang"));
                } catch (Exception e) {
                }
                try {
                    ds.setTenSan(rs.getString("TenSan"));
                } catch (Exception e) {
                }
                try {
                    ds.setKhungGio(rs.getString("CbxKhungGio"));
                } catch (Exception e) {
                }

                list.add(ds);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy toàn bộ danh sách đặt sân: " + e.getMessage());
        }
        return list;
    }

    // =========================================================================
    // 2. LẤY DANH SÁCH ĐẶT SÂN THEO MÃ KHÁCH HÀNG
    // =========================================================================
    public ObservableList<DATSAN> getbangDatsans(String maKH) {
        ObservableList<DATSAN> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM DATSAN WHERE MaKH = ? ORDER BY MaDS";

        try (Connection conn = dbHelper.createCon();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maKH);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DATSAN ds = new DATSAN(
                            rs.getString("MaDS"),
                            rs.getString("MaKH"),
                            rs.getString("MaNV"),
                            rs.getString("MaHD"),
                            rs.getDate("NgayDat") != null ? rs.getDate("NgayDat").toLocalDate() : null,
                            rs.getLong("TongTienTamTinh"),
                            rs.getString("TrangThai"));
                    try {
                        ds.setMaSan(rs.getString("MaSan"));
                    } catch (Exception e) {
                    }
                    try {
                        ds.setMaKG(rs.getString("MaKG"));
                    } catch (Exception e) {
                    }
                    list.add(ds);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy thông tin đặt sân theo khách hàng: " + e.getMessage());
        }
        return list;
    }

    // =========================================================================
    // 3. LẤY THÔNG TIN ĐẶT SÂN CHI TIẾT THEO MÃ PHIẾU ĐẶT SÂN
    // =========================================================================
    public DATSAN getDatSanByMa(String maDS) {
        DATSAN ds = null;
        String sql = "SELECT ds.MaDS, ds.MaKH, ds.MaNV, ds.MaHD, ds.MaSan, ds.MaKG, ds.NgayDat, ds.TongTienTamTinh, ds.TrangThai, "
                +
                "kh.HoTen AS TenKhachHang, kh.Sdt AS SoDienThoai, s.TenSan, " +
                "TO_CHAR(kg.GioBD, 'HH24:MI') || ' - ' || TO_CHAR(kg.GioKT, 'HH24:MI') AS CbxKhungGio " +
                "FROM DATSAN ds " +
                "LEFT JOIN KHACHHANG kh ON ds.MaKH = kh.MaKH " +
                "LEFT JOIN SAN s ON ds.MaSan = s.MaSan " +
                "LEFT JOIN KHUNGGIO kg ON ds.MaKG = kg.MaKG " +
                "WHERE ds.MaDS = ?";

        try (Connection conn = dbHelper.createCon();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maDS);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ds = new DATSAN(
                            rs.getString("MaDS"),
                            rs.getString("MaKH"),
                            rs.getString("MaNV"),
                            rs.getString("MaHD"),
                            rs.getDate("NgayDat") != null ? rs.getDate("NgayDat").toLocalDate() : null,
                            rs.getLong("TongTienTamTinh"),
                            rs.getString("TrangThai"));
                    try {
                        ds.setMaSan(rs.getString("MaSan"));
                    } catch (Exception e) {
                    }
                    try {
                        ds.setMaKG(rs.getString("MaKG"));
                    } catch (Exception e) {
                    }
                    try {
                        ds.setTenKH(rs.getString("TenKhachHang"));
                    } catch (Exception e) {
                    }
                    try {
                        ds.setTenSan(rs.getString("TenSan"));
                    } catch (Exception e) {
                    }
                    try {
                        ds.setKhungGio(rs.getString("CbxKhungGio"));
                    } catch (Exception e) {
                    }
                    try {
                        ds.setSdtKH(rs.getString("SoDienThoai"));
                    } catch (Exception e) {
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy thông tin đặt sân theo mã: " + e.getMessage());
        }
        return ds;
    }

    // =========================================================================
    // 4. HÀM THÊM ĐẶT SÂN MỚI (Gọi Procedure xử lý xếp hàng khóa đồng thời)
    // =========================================================================
    public boolean addDatSan(DATSAN ds) throws Exception {
        String sql = "{call PROC_DatSan(?, ?, ?, ?, ?)}";

        try (Connection conn = dbHelper.createCon();
                CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, ds.getMaDS());
            cstmt.setString(2, ds.getMaKH());
            cstmt.setString(3, ds.getMaSan());
            cstmt.setString(4, ds.getMaKG());
            cstmt.setDate(5, java.sql.Date.valueOf(ds.getNgayDat()));

            cstmt.execute();
            return true;

        } catch (SQLException e) {
            // Ném ngược Exception ra ngoài để UI Controller bắt được mã lỗi ORA-20001
            throw e;
        }
    }

    // =========================================================================
    // 4b. INSERT ĐẶT SÂN TRỰC TIẾP (không qua Procedure)
    // Lưu đủ TongTienTamTinh và TrangThai ngay lập tức.
    // =========================================================================
    public boolean insertDatSanDirect(DATSAN ds) throws Exception {
        // ================================================================
        // Kiểm tra xung đột đặt sân (SELECT FOR UPDATE NOWAIT)
        // Nếu cùng sân + cùng khung giờ + cùng ngày → báo lỗi ngay lập tức
        // dù có 2 request đến đồng thời → Oracle lock đảm bảo chỉ 1 request thành công
        // ================================================================
        String checkSql = "SELECT MaDS FROM DATSAN " +
                "WHERE MaSan = ? AND MaKG = ? AND NgayDat = ? " +
                "AND TrangThai NOT IN ('DaHuy', 'Cancelled') " +
                "FOR UPDATE NOWAIT";

        String insertSql = "INSERT INTO DATSAN (MaDS, MaKH, MaSan, MaKG, NgayDat, TongTienTamTinh, TrangThai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbHelper.createCon()) {
            conn.setAutoCommit(false);
            try {
                // Bước 1: Kiểm tra + khoá dòng xung đột
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setString(1, ds.getMaSan());
                    checkStmt.setString(2, ds.getMaKG());
                    checkStmt.setDate(3, java.sql.Date.valueOf(ds.getNgayDat()));
                    try (java.sql.ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next()) {
                            conn.rollback();
                            // Ném lỗi giống ORA-20001 để controller hiển thị đúng thông báo
                            throw new java.sql.SQLException(
                                    "ORA-20001: San nay da duoc dat trong khung gio nay roi!");
                        }
                    }
                }

                // Bước 2: INSERT an toàn (đã lock xong)
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, ds.getMaDS());
                    pstmt.setString(2, ds.getMaKH());
                    pstmt.setString(3, ds.getMaSan());
                    pstmt.setString(4, ds.getMaKG());
                    pstmt.setDate(5, java.sql.Date.valueOf(ds.getNgayDat()));
                    pstmt.setLong(6, ds.getTongTienTamTinh());
                    pstmt.setString(7, ds.getTrangThai() != null ? ds.getTrangThai() : "ChoDuyet");

                    int rows = pstmt.executeUpdate();
                    if (rows > 0) {
                        conn.commit();
                        System.out.println("✓ Da luu dat san: " + ds.getMaDS());
                        return true;
                    }
                    conn.rollback();
                    return false;
                }
            } catch (java.sql.SQLException e) {
                conn.rollback();
                // ORA-00054 = resource busy (NOWAIT bị block) → trùng lịch
                if (e.getErrorCode() == 54 || (e.getMessage() != null && e.getMessage().contains("ORA-00054"))) {
                    throw new java.sql.SQLException(
                            "ORA-20001: San dang duoc xu ly boi nguoi khac, vui long thu lai!");
                }
                throw e;
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Loi insertDatSanDirect: " + e.getMessage());
            throw e;
        }
    }

    // =========================================================================
    // 4c. TÌM HOẶC TẠO MỚI KHÁCH HÀNG THEO SĐT
    // Trả về MaKH thực trong DB để đảm bảo FK hợp lệ khi thêm DATSAN.
    // =========================================================================
    public String upsertKhachVangLai(String tenKH, String sdt) {
        // Tìm xem SĐT đã tồn tại chưa
        String selectSql = "SELECT MaKH FROM KHACHHANG WHERE Sdt = ?";
        try (Connection conn = dbHelper.createCon();
                PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
            pstmt.setString(1, sdt);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String maKH = rs.getString("MaKH");
                    System.out.println("\u2713 Tim thay khach cu: " + maKH + " - " + tenKH);
                    return maKH;
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi tim khach hang: " + e.getMessage());
        }

        // Chưa có → tạo mới
        String maKHMoi = "KH" + (System.currentTimeMillis() % 1000000);
        String insertSql = "INSERT INTO KHACHHANG (MaKH, HoTen, Sdt) VALUES (?, ?, ?)";
        try (Connection conn = dbHelper.createCon();
                PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setString(1, maKHMoi);
            pstmt.setString(2, tenKH);
            pstmt.setString(3, sdt);
            pstmt.executeUpdate();
            System.out.println("\u2713 Da tao khach hang moi: " + maKHMoi + " - " + tenKH);
            return maKHMoi;
        } catch (SQLException e) {
            System.err.println("Loi tao khach hang moi: " + e.getMessage());
            return null;
        }
    }

    // =========================================================================
    // 5. CẬP NHẬT THÔNG TIN ĐẶT SÂN (Giao dịch an toàn Transaction)
    // =========================================================================
    public boolean updateDatSan(DATSAN ds) {
        String sql = "UPDATE DATSAN SET MaKH=?, MaNV=?, MaHD=?, NgayDat=?, TrangThai=?, TongTienTamTinh=? WHERE MaDS=?";
        boolean updateThanhCong = false;

        try (Connection conn = dbHelper.createCon()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, ds.getMaKH());
                pstmt.setString(2, ds.getMaNV());
                pstmt.setString(3, ds.getMaHD());

                if (ds.getNgayDat() != null) {
                    pstmt.setDate(4, java.sql.Date.valueOf(ds.getNgayDat()));
                } else {
                    pstmt.setNull(4, java.sql.Types.DATE);
                }

                pstmt.setString(5, ds.getTrangThai());
                pstmt.setLong(6, ds.getTongTienTamTinh());
                pstmt.setString(7, ds.getMaDS());

                int result = pstmt.executeUpdate();
                conn.commit();
                updateThanhCong = (result > 0);
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Lỗi thực thi / Rollback cập nhật đặt sân: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
            return false;
        }

        if (updateThanhCong) {
            taoThongBaoHeThong("Thay đổi thông tin sân",
                    "Phiếu đặt sân mã " + ds.getMaDS() + " vừa được nhân viên cập nhật.", "warning");
            return true;
        }
        return false;
    }

    // =========================================================================
    // 6. XÓA PHIẾU ĐẶT SÂN KHỎI HỆ THỐNG
    // =========================================================================
    public boolean deleteDatSan(String maDS) {
        String sql = "DELETE FROM DATSAN WHERE MaDS = ?";
        boolean deleteThanhCong = false;

        try (Connection conn = dbHelper.createCon()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, maDS);

                int result = pstmt.executeUpdate();
                conn.commit();
                deleteThanhCong = (result > 0);
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Lỗi thực thi / Rollback xóa đặt sân: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
            return false;
        }

        if (deleteThanhCong) {
            taoThongBaoHeThong("Xóa phiếu đặt sân", "Phiếu đặt sân mã " + maDS + " đã bị xóa khỏi hệ thống.",
                    "warning");
            return true;
        }
        return false;
    }

    // =========================================================================
    // 7. HỦY ĐẶT SÂN (Chuyển trạng thái sang 'Da Huy')
    // =========================================================================
    public boolean huyDatSan(DATSAN ds) {
        String sql = "UPDATE DATSAN SET TrangThai = ? WHERE MaDS = ?";
        boolean huyThanhCong = false;

        try (Connection conn = dbHelper.createCon()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "DaHuy");
                pstmt.setString(2, ds.getMaDS());

                int result = pstmt.executeUpdate();
                conn.commit();
                huyThanhCong = (result > 0);
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Lỗi thực thi / Rollback hủy đơn đặt sân: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
            return false;
        }

        if (huyThanhCong) {
            taoThongBaoHeThong("Khách hủy sân",
                    "Lịch đặt sân mã " + ds.getMaDS() + " đã bị chuyển sang trạng thái Đã Hủy.", "warning");
            return true;
        }
        return false;
    }

    // =========================================================================
    // 8. XÁC NHẬN THANH TOÁN THÀNH CÔNG
    // =========================================================================
    public void xacNhanThanhToan(DATSAN ds) {
        String sql = "UPDATE DATSAN SET TrangThai = ? WHERE MaDS = ?";
        boolean thanhToanThanhCong = false;

        try (Connection conn = dbHelper.createCon()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "HoanThanh"); // Điều chỉnh chuỗi khớp với CHECK CONSTRAINT của bạn ('HoanThanh')
                pstmt.setString(2, ds.getMaDS());

                int result = pstmt.executeUpdate();
                conn.commit();
                thanhToanThanhCong = (result > 0);
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Lỗi thực thi / Rollback xác nhận thanh toán: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        }

        if (thanhToanThanhCong) {
            taoThongBaoHeThong("Thanh toán thành công", "Phiếu đặt sân mã " + ds.getMaDS() + " đã hoàn tất thanh toán.",
                    "success");
        }
    }

    // =========================================================================
    // 9. TÍNH TỔNG THÀNH TIỀN DỊCH VỤ (Đã sửa đổi bảng CTDV)
    // =========================================================================
    public long TinhThanhTien(String maDS) {
        String sql = "SELECT COALESCE(SUM(ThanhTien), 0) as TongTienDV FROM CTDV WHERE MaDS = ?";
        try (Connection conn = dbHelper.createCon();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maDS);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("TongTienDV");
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi tính tổng thành tiền dịch vụ từ bảng CTDV: " + e.getMessage());
        }
        return 0;
    }

    // =========================================================================
    // 10. HÀM PHỤ: TỰ ĐỘNG PHÁT THÔNG BÁO ĐẾN CHUÔNG HỆ THỐNG
    // =========================================================================
    private void taoThongBaoHeThong(String tieuDe, String noiDung, String loai) {
        String thongBaoSql = "INSERT INTO THONGBAO (TieuDe, NoiDung, Loai) VALUES (?, ?, ?)";
        try (Connection conn = dbHelper.createCon()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtTB = conn.prepareStatement(thongBaoSql)) {
                pstmtTB.setString(1, tieuDe);
                pstmtTB.setString(2, noiDung);
                pstmtTB.setString(3, loai);
                pstmtTB.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Lỗi tạo thông báo hệ thống: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối tạo thông báo: " + e.getMessage());
        }
    }
}