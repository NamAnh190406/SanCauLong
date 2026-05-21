package com.example.guidemo;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ThemSanController {

    @FXML private TextField txtMaSan;
    @FXML private TextField txtTenSan;
    @FXML private ChoiceBox<String> choiceLoaiSan;
    @FXML private TextField txtLoaiMatSan;
    @FXML private ChoiceBox<String> choiceKhongGian;
    @FXML private Spinner<Integer> spinnerSLNguoiChoi;
    @FXML private TextField txtGiaThue;
    @FXML private ChoiceBox<String> choiceTrangThai;
    @FXML private TextField txtDiaChi;
    @FXML private TextArea txtMoTa;

    @FXML
    public void initialize() {
        choiceLoaiSan.setItems(FXCollections.observableArrayList("Đơn", "Đôi"));
        choiceLoaiSan.setValue("Đơn");

        choiceKhongGian.setItems(FXCollections.observableArrayList("NhàCầu", "NgoàiTrời"));
        choiceKhongGian.setValue("NhàCầu");

        choiceTrangThai.setItems(FXCollections.observableArrayList("HoatDong", "BaoDuong", "Dong"));
        choiceTrangThai.setValue("HoatDong");

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 2);
        spinnerSLNguoiChoi.setValueFactory(valueFactory);
    }
    @FXML
    private void handleLuuThemMoi() {
        String maSan = txtMaSan.getText().trim();
        String tenSan = txtTenSan.getText().trim();
        String loaiMatSan = txtLoaiMatSan.getText().trim();
        String giaThueStr = txtGiaThue.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String moTa = txtMoTa.getText().trim();

        // 1. Validation: Kiểm tra không được bỏ trống các trường bắt buộc (NOT NULL bên Oracle)
        if (maSan.isEmpty() || tenSan.isEmpty() || loaiMatSan.isEmpty() || giaThueStr.isEmpty() || diaChi.isEmpty()) {
            hienThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ các thông tin bắt buộc (*)");
            return;
        }

        // 2. Validation: Kiểm tra trùng Khóa chính (Primary Key - MaSan)
        if (kiemTraTrungMaSan(maSan)) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi trùng mã", "Mã sân '" + maSan + "' đã tồn tại trên hệ thống! Vui lòng nhập mã khác.");
            return;
        }

        // 3. Validation: Kiểm tra định dạng số và ràng buộc > 0 (chk_san_gia)
        long giaThue;
        try {
            giaThue = Long.parseLong(giaThueStr);
            if (giaThue <= 0) {
                hienThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Giá thuê theo giờ phải là số lớn hơn 0!");
                return;
            }
        } catch (NumberFormatException e) {
            hienThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Giá thuê phải nhập vào một số nguyên hợp lệ!");
            return;
        }

        // 4. Thực hiện INSERT lệnh SQL vào bảng SAN của Oracle
        String sql = "INSERT INTO SAN (MaSan, TenSan, LoaiSan, LoaiMatSan, KhongGian, SLNguoiChoi, GiaThueTheoGio, TrangThai, MoTa, DiaChi) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maSan);
            pstmt.setString(2, tenSan);
            pstmt.setString(3, choiceLoaiSan.getValue());
            pstmt.setString(4, loaiMatSan);
            pstmt.setString(5, choiceKhongGian.getValue());
            pstmt.setInt(6, spinnerSLNguoiChoi.getValue());
            pstmt.setLong(7, giaThue);
            pstmt.setString(8, choiceTrangThai.getValue());
            pstmt.setString(9, moTa);
            pstmt.setString(10, diaChi);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Thêm mới sân bóng thành công!");
                quayLaiDanhSachSan(); // Thêm xong tự động quay về trang bảng danh sách chính
            }
        } catch (SQLException e) {
            e.printStackTrace();
            hienThongBao(Alert.AlertType.ERROR, "Lỗi Database", "Không thể thêm sân bóng mới. Lỗi Oracle: " + e.getMessage());
        }
    }

    @FXML
    private void handleHuyBo() {
        quayLaiDanhSachSan();
    }

    /**
     * Hàm phụ trợ kiểm tra xem mã sân đã tồn tại trong DB Oracle chưa
     */
    private boolean kiemTraTrungMaSan(String maSan) {
        String sql = "SELECT COUNT(*) FROM SAN WHERE MaSan = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maSan);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void quayLaiDanhSachSan() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("QLySan.fxml"));
            Parent view = loader.load();
            if (TrangChuController.rootPane != null) {
                TrangChuController.rootPane.setCenter(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
            hienThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không thể quay trở lại màn hình QLySan.fxml!");
        }
    }

    private void hienThongBao(Alert.AlertType type, String tieuDe, String noiDung) {
        Alert alert = new Alert(type);
        alert.setTitle(tieuDe);
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }
}