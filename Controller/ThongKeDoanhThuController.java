package com.mycompany.mavenproject1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ThongKeDoanhThuController {

    @FXML private DatePicker datePicker;
    @FXML private Label lblTenNgay;
    @FXML private Label lblDoanhThuSan;
    @FXML private Label lblDoanhThuDV;
    @FXML private Label lblSoLuotDat;
    @FXML private Label lblTongCuoi; // Khối tổng kết doanh thu màu xanh cuối trang

    // TableView Thống kê Sân
    @FXML private TableView<ThongKeSanModel> tableSan;
    @FXML private TableColumn<ThongKeSanModel, String> colSanTen;
    @FXML private TableColumn<ThongKeSanModel, String> colSanLoai;
    @FXML private TableColumn<ThongKeSanModel, Integer> colSanLuot;
    @FXML private TableColumn<ThongKeSanModel, Long> colSanTien;

    // TableView Thống kê Dịch vụ
    @FXML private TableView<ThongKeDVModel> tableDV;
    @FXML private TableColumn<ThongKeDVModel, String> colDVTen;
    @FXML private TableColumn<ThongKeDVModel, String> colDVDVT;
    @FXML private TableColumn<ThongKeDVModel, Integer> colDVSL;
    @FXML private TableColumn<ThongKeDVModel, Long> colDVTien;

    // Chi tiết doanh thu hạng mục
    @FXML private Label lblChiTietLuotDat;
    @FXML private Label lblChiTietTienSan;
    @FXML private Label lblChiTietTBSan;
    @FXML private Label lblChiTietLoaiDV;
    @FXML private Label lblChiTietTienDV;
    @FXML private Label lblChiTietTyLeDV;

    // Vùng tổng kết dưới cùng
    @FXML private Label lblTongCuoiSan;
    @FXML private Label lblTongCuoiDV;

    private final ObservableList<ThongKeSanModel> listSan = FXCollections.observableArrayList();
    private final ObservableList<ThongKeDVModel> listDV = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Ánh xạ các cột cho TableView
        colSanTen.setCellValueFactory(cellData -> cellData.getValue().tenSanProperty());
        colSanLoai.setCellValueFactory(cellData -> cellData.getValue().loaiSanProperty());
        colSanLuot.setCellValueFactory(cellData -> cellData.getValue().soLuotProperty().asObject());
        colSanTien.setCellValueFactory(cellData -> cellData.getValue().doanhThuProperty().asObject());
        colDVTen.setCellValueFactory(cellData -> cellData.getValue().tenDVProperty());
        colDVDVT.setCellValueFactory(cellData -> cellData.getValue().dvtProperty());
        colDVSL.setCellValueFactory(cellData -> cellData.getValue().soLuongProperty().asObject());
        colDVTien.setCellValueFactory(cellData -> cellData.getValue().doanhThuProperty().asObject());
        datePicker.setValue(LocalDate.now());
        onDateChanged();
    }

    @FXML
    private void onDateChanged() {
        LocalDate date = datePicker.getValue();
        if (date != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy");
            lblTenNgay.setText(date.format(formatter));
        }
    }

    @FXML
    private void thongKe() {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) return;

        java.sql.Date sqlDate = java.sql.Date.valueOf(selectedDate);

        long tongDoanhThu = 0;
        long tongTienDV = 0; // Sẽ được tính bằng cách cộng dồn các hàm f_TinhTienDV
        long tongTienSan = 0;
        int soLuotDat = 0;
        int soLoaiDV = 0;

        listSan.clear();
        listDV.clear();

        try (Connection conn = DBContext.KetNoi()) {

            // --- PHẦN 1: TÍNH DOANH THU SÂN & SỐ LƯỢT ĐẶT ---
            String sqlTienSan = "SELECT COUNT(hd.MaHoaDon) as SoLuot, SUM(NVL(hd.ThanhTien, 0)) as TongSan " +
                    "FROM HOADON hd " +
                    "JOIN DATSAN ds ON hd.MaDS = ds.MaDS " +
                    "WHERE TRUNC(ds.NgayDat) = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sqlTienSan)) {
                pstmt.setDate(1, sqlDate);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        soLuotDat = rs.getInt("SoLuot");
                        tongTienSan = rs.getLong("TongSan");
                    }
                }
            }


            // --- PHẦN 2: LẤY CHI TIẾT DỊCH VỤ & DÙNG HÀM ORACLE TÍNH TIỀN ---
            // Quét qua các dịch vụ được dùng trong ngày
            String sqlDV = "SELECT ct.MaDV, dv.TenDV, dv.DonViTinh, SUM(ct.SoLuong) as TongSL " +
                    "FROM CTDV ct " +
                    "JOIN DICHVU dv ON ct.MaDV = dv.MaDV " +
                    "JOIN DATSAN ds ON ct.MaDS = ds.MaDS " +
                    "WHERE TRUNC(ds.NgayDat) = ? " +
                    "GROUP BY ct.MaDV, dv.TenDV, dv.DonViTinh " +
                    "ORDER BY TongSL DESC";

            try (PreparedStatement pstmt = conn.prepareStatement(sqlDV)) {
                pstmt.setDate(1, sqlDate);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        soLoaiDV++;
                        String maDV = rs.getString("MaDV");
                        String tenDV = rs.getString("TenDV");
                        String donViTinh = rs.getString("DonViTinh");
                        int tongSL = rs.getInt("TongSL");
                        long doanhThuTungMon = 0;

                        // Gọi hàm f_TinhTienDV(f_MaDv, f_SoLuong) từ Oracle của bạn
                        String sqlCallFunc = "{? = call f_TinhTienDV(?, ?)}";
                        try (CallableStatement cstmt = conn.prepareCall(sqlCallFunc)) {
                            cstmt.registerOutParameter(1, Types.NUMERIC);
                            cstmt.setString(2, maDV);
                            cstmt.setInt(3, tongSL);
                            cstmt.execute();

                            doanhThuTungMon = cstmt.getLong(1);
                        }

                        // Cộng dồn vào tổng doanh thu dịch vụ trong ngày
                        tongTienDV += doanhThuTungMon;

                        // Thêm vào danh sách hiển thị lên bảng TableView dịch vụ
                        listDV.add(new ThongKeDVModel(tenDV, donViTinh, tongSL, doanhThuTungMon));
                    }
                }
            }


            // --- PHẦN 3: LẤY DỮ LIỆU ĐỔ VÀO BẢNG SÂN (tableSan) ---
            String sqlTruyVanSan = "SELECT s.TenSan, s.LoaiSan, COUNT(hd.MaHoaDon) as SoLuot, SUM(hd.ThanhTien) as DoanhThuSan " +
                    "FROM HOADON hd " +
                    "JOIN DATSAN ds ON hd.MaDS = ds.MaDS " +
                    "JOIN SAN s ON ds.MaSan = s.MaSan " +
                    "WHERE TRUNC(ds.NgayDat) = ? " +
                    "GROUP BY s.TenSan, s.LoaiSan " +
                    "ORDER BY SoLuot DESC";

            try (PreparedStatement pstmt = conn.prepareStatement(sqlTruyVanSan)) {
                pstmt.setDate(1, sqlDate);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        listSan.add(new ThongKeSanModel(
                                rs.getString("TenSan"),
                                rs.getString("LoaiSan"),
                                rs.getInt("SoLuot"),
                                rs.getLong("DoanhThuSan")
                        ));
                    }
                }
            }

            // Tính toán Tổng doanh thu cuối cùng dựa trên các nguồn dữ liệu thực tế
            tongDoanhThu = tongTienSan + tongTienDV;

        } catch (SQLException e) {
            System.out.println("Lỗi xử lý thống kê tích hợp hàm Oracle!");
            e.printStackTrace();
        }

        // --- PHẦN 4: HIỂN THỊ DỮ LIỆU LÊN GIAO DIỆN ---
        // Đổ dữ liệu lên các thẻ hiển thị (Cards) đầu trang
        lblDoanhThuSan.setText(String.format("%,d đ", tongTienSan));
        lblDoanhThuDV.setText(String.format("%,d đ", tongTienDV));
        lblSoLuotDat.setText(String.valueOf(soLuotDat));

        // Đổ dữ liệu xuống khối "Chi tiết doanh thu" trung tâm
        lblChiTietLuotDat.setText(soLuotDat + " lượt");
        lblChiTietTienSan.setText(String.format("%,d đ", tongTienSan));
        lblChiTietTienDV.setText(String.format("%,d đ", tongTienDV));
        lblChiTietLoaiDV.setText(soLoaiDV + " loại");

        long trungBinhSan = soLuotDat > 0 ? tongTienSan / soLuotDat : 0;
        lblChiTietTBSan.setText(String.format("%,d đ", trungBinhSan));

        double tyLeDV = tongDoanhThu > 0 ? ((double) tongTienDV / tongDoanhThu) * 100 : 0;
        lblChiTietTyLeDV.setText(String.format("%.1f%%", tyLeDV));

        // Khối tổng kết đậm màu cuối trang
        lblTongCuoi.setText(String.format("%,d đ", tongDoanhThu));
        lblTongCuoiSan.setText(String.format("%,d đ", tongTienSan));
        lblTongCuoiDV.setText(String.format("%,d đ", tongTienDV));

        // Cập nhật lại các bảng TableView
        tableSan.setItems(listSan);
        tableDV.setItems(listDV);
    }

    @FXML
private void goHome() {
    try {
        App.setRoot("ManHinhChinhQTV"); // Tên file FXML của màn hình chính QTV
    } catch (Exception e) {
        System.out.println("Lỗi chuyển về màn hình chính QTV!");
        e.printStackTrace();
    }
}
    
}