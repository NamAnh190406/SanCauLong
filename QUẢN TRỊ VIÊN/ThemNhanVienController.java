package com.example.guidemo;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ThemNhanVienController {

    @FXML private TextField txtMaNV;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtSDT;
    @FXML private ComboBox<String> cbChucVu;

    @FXML private ToggleGroup groupCaLamViec;
    @FXML private RadioButton rdCa1;
    @FXML private RadioButton rdCa2;
    @FXML private RadioButton rdCa3;

    @FXML
    public void initialize() {
        // Đổ dữ liệu vào ô chức vụ ComboBox
        cbChucVu.setItems(FXCollections.observableArrayList(
                "Quản lý", "Thu Ngân", "Bảo Vệ", "Kỹ Thuật","Lễ Tân"
        ));
    }

    @FXML
    private void handleLuuNhanVien() {
        // 1. LẤY DỮ LIỆU TỪ CÁC Ô NHẬP TRÊN GIAO DIỆN
        String maNV = txtMaNV.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSDT.getText().trim();
        String chucVu = cbChucVu.getValue();

        // Đọc dữ liệu từ RadioButton để chuyển đổi thành Ca1, Ca2, Ca3 theo Oracle constraint
        String caLamViec = "";
        if (groupCaLamViec.getSelectedToggle() != null) {
            RadioButton selectedRadio = (RadioButton) groupCaLamViec.getSelectedToggle();
            if (selectedRadio == rdCa1) caLamViec = "Ca1";
            else if (selectedRadio == rdCa2) caLamViec = "Ca2";
            else if (selectedRadio == rdCa3) caLamViec = "Ca3";
        }

        // 2. KIỂM TRA CÁC RÀNG BUỘC CHẶT CHẼ TRƯỚC KHI LƯU
        if (maNV.isEmpty() || hoTen.isEmpty() || sdt.isEmpty() || chucVu == null || caLamViec.isEmpty()) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Vui lòng nhập đầy đủ thông tin có dấu (*)");
            return;
        }

        // Kiểm tra mã nhân viên không chứa ký tự đặc biệt
        if (!maNV.matches("^[a-zA-Z0-9]+$")) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi định dạng", "Mã nhân viên chỉ được chứa chữ và số!");
            return;
        }

        // Kiểm tra số điện thoại (9-11 số, khớp 100% với chk_nv_sdt của Oracle)
        if (!sdt.matches("^[0-9]{9,11}$")) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi định dạng", "Số điện thoại không hợp lệ! (Phải từ 9 đến 11 chữ số)");
            return;
        }

        // 3. THỰC THI LỆNH KẾT NỐI VÀ LƯU VÀO DATABASE ORACLE CỦA BẠN
        String sql = "INSERT INTO NHAN_VIEN (Ma_NV, Hoten_nv, SDT, ChucVu, CaLamViec) VALUES (?, ?, ?, ?, ?)";

        // Sử dụng try-with-resources để tự động đóng kết nối sau khi chạy xong, tránh rò rỉ bộ nhớ
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Nhét dữ liệu an toàn vào các dấu hỏi chấm (?) tránh lỗi SQL Injection
            pstmt.setString(1, maNV);
            pstmt.setString(2, hoTen);
            pstmt.setString(3, sdt);
            pstmt.setString(4, chucVu);
            pstmt.setString(5, caLamViec);

            // Chạy lệnh thực thi chèn dòng mới vào bảng
            int rowsInserted = pstmt.executeUpdate();

            if (rowsInserted > 0) {
                hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm nhân viên mới vào cơ sở dữ liệu!");

                // Sau khi thêm thành công, xóa sạch dữ liệu trên các ô để người dùng nhập tiếp người mới
                lamMoiForm();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Nếu trùng khóa chính (Ma_NV) hoặc trùng SDT (UNIQUE), Oracle sẽ ném lỗi về đây
            hienThongBao(Alert.AlertType.ERROR, "Lỗi Oracle Database",
                    "Không thể thêm nhân viên! Chi tiết lỗi:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleThoat() {
        try {
            // 🌟 ĐIỀU HƯỚNG MỚI: Nạp lại file danh sách quản lý nhân viên
            FXMLLoader loader = new FXMLLoader(getClass().getResource("QLyNhanVien.fxml"));
            Parent quanLyView = loader.load();

            // Đẩy file danh sách vào vùng Center của khung tổng
            if (TrangChuController.rootPane != null) {
                TrangChuController.rootPane.setCenter(quanLyView);
            } else {
                System.out.println("Lỗi hệ thống: Không tìm thấy mainBorderPane!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Lỗi: Không thể tải file QLyNhanVien.fxml!");
        }
    }

    // Hàm phụ dọn dẹp các ô nhập sau khi thêm thành công
    private void lamMoiForm() {
        txtMaNV.clear();
        txtHoTen.clear();
        txtSDT.clear();
        cbChucVu.setValue(null);
        if (groupCaLamViec.getSelectedToggle() != null) {
            groupCaLamViec.getSelectedToggle().setSelected(false);
        }
    }

    // Hàm tiện ích hiển thị hộp thoại thông báo nhanh
    private void hienThongBao(Alert.AlertType type, String tieuDe, String noiDung) {
        Alert alert = new Alert(type);
        alert.setTitle(tieuDe);
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }
}