package com.example.guidemo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class KhachHangController {

    @FXML private TableView<KHACHHANG> tableKhachHang;
    @FXML private TableColumn<KHACHHANG, Boolean> colSelect;
    @FXML private TableColumn<KHACHHANG, String> colMaKH;
    @FXML private TableColumn<KHACHHANG, String> colHoTen;
    @FXML private TableColumn<KHACHHANG, String> colSDT;
    @FXML private TableColumn<KHACHHANG, String> colEmail;
    @FXML private TableColumn<KHACHHANG, java.util.Date> colNgayDK;
    @FXML private TableColumn<KHACHHANG, String> colHang;
    @FXML private TableColumn<KHACHHANG, Integer> colDiem;
    @FXML private TextField txtSearch;

    private ObservableList<KHACHHANG> listKhachHang = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colSelect.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));
        colSelect.setEditable(true);
        tableKhachHang.setEditable(true);

        colMaKH.setCellValueFactory(new PropertyValueFactory<>("maKH"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colSDT.setCellValueFactory(new PropertyValueFactory<>("sDT"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colNgayDK.setCellValueFactory(new PropertyValueFactory<>("ngayDK"));
        colHang.setCellValueFactory(new PropertyValueFactory<>("hangThanhVien"));
        colDiem.setCellValueFactory(new PropertyValueFactory<>("diemTichLuy"));

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            handleTimKiem();
        });

        loadDataFromOracle();
    }

    private void loadDataFromOracle() {
        listKhachHang.clear();
        String sql = "SELECT MaKH, HoTen, SDT, Email, NgayDK, HangThanhVien, DiemTichLuy FROM KHACHHANG ORDER BY MaKH";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                listKhachHang.add(new KHACHHANG(
                        rs.getString("MaKH"),
                        rs.getString("HoTen"),
                        rs.getString("SDT"),
                        rs.getString("Email"),
                        rs.getDate("NgayDK"),
                        rs.getString("HangThanhVien"),
                        rs.getInt("DiemTichLuy")
                ));
            }
            tableKhachHang.setItems(listKhachHang);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTimKiem() {
        String tuKhoa = txtSearch.getText().trim();
        if (tuKhoa.isEmpty()) {
            loadDataFromOracle();
            return;
        }

        listKhachHang.clear();
        String sql = "SELECT MaKH, HoTen, SDT, Email, NgayDK, HangThanhVien, DiemTichLuy FROM KHACHHANG " +
                "WHERE UPPER(MaKH) LIKE UPPER(?) OR UPPER(HoTen) LIKE UPPER(?) OR SDT LIKE ? ORDER BY MaKH";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String pattern = "%" + tuKhoa + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    listKhachHang.add(new KHACHHANG(
                            rs.getString("MaKH"),
                            rs.getString("HoTen"),
                            rs.getString("SDT"),
                            rs.getString("Email"),
                            rs.getDate("NgayDK"),
                            rs.getString("HangThanhVien"),
                            rs.getInt("DiemTichLuy")
                    ));
                }
            }
            tableKhachHang.setItems(listKhachHang);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleXoaKhachHang() {
        boolean coKhachDuocChon = listKhachHang.stream().anyMatch(KHACHHANG::isSelected);

        if (!coKhachDuocChon) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng tích chọn ít nhất một khách hàng để xóa!");
            return;
        }

        Alert alertConfirm = new Alert(Alert.AlertType.CONFIRMATION);
        alertConfirm.setTitle("Xác nhận xóa");
        alertConfirm.setHeaderText(null);
        alertConfirm.setContentText("Bạn có chắc chắn muốn xóa các khách hàng đã chọn không?");

        ButtonType btnXacNhan = new ButtonType("Xác nhận", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnHuy = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        alertConfirm.getButtonTypes().setAll(btnXacNhan, btnHuy);

        Optional<ButtonType> result = alertConfirm.showAndWait();
        if (result.isPresent() && result.get() == btnXacNhan) {
            String sql = "DELETE FROM KHACHHANG WHERE MaKH = ?";
            int count = 0;

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                for (KHACHHANG kh : listKhachHang) {
                    if (kh.isSelected()) {
                        pstmt.setString(1, kh.getMaKH());
                        pstmt.addBatch();
                        count++;
                    }
                }

                pstmt.executeBatch();
                hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa thành công " + count + " khách hàng!");
                loadDataFromOracle();

            } catch (SQLException e) {
                if (e.getMessage().contains("ORA-02292")) {
                    hienThongBao(Alert.AlertType.ERROR, "Lỗi ràng buộc", "Không thể xóa khách hàng này vì họ đang có lịch sử đặt sân trên hệ thống!");
                } else {
                    hienThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", e.getMessage());
                }
                loadDataFromOracle();
            }
        } else {
            listKhachHang.forEach(kh -> kh.setSelected(false));
            tableKhachHang.refresh();
        }
    }

    @FXML
    private void handleMoThemKhachHang() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ThemKhachHang.fxml"));
            Parent view = loader.load();

            if (TrangChuController.rootPane != null) {
                TrangChuController.rootPane.setCenter(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
            hienThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không thể nạp tệp giao diện ThemKhachHang.fxml!");
        }
    }

    @FXML
    private void handleThoat() {
        if (TrangChuController.rootPane != null && TrangChuController.dashboardContent != null) {
            TrangChuController.rootPane.setCenter(TrangChuController.dashboardContent);
        }
    }

    @FXML
    private void handleMoSuaKhachHang() {
        java.util.List<KHACHHANG> danhSachDuocChon = new java.util.ArrayList<>();
        for (KHACHHANG kh : listKhachHang) {
            if (kh.isSelected()) {
                danhSachDuocChon.add(kh);
            }
        }

        if (danhSachDuocChon.isEmpty()) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng tích chọn một khách hàng cần sửa thông tin!");
            return;
        }
        if (danhSachDuocChon.size() > 1) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Bạn đang chọn " + danhSachDuocChon.size() + " khách hàng. Tính năng sửa chỉ áp dụng cho từng khách hàng một!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SuaKhachHang.fxml"));
            Parent view = loader.load();

            SuaKhachHangController controllerSua = loader.getController();
            controllerSua.setKhachHangBanDau(danhSachDuocChon.get(0));

            if (TrangChuController.rootPane != null) {
                TrangChuController.rootPane.setCenter(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
            hienThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không tìm thấy tệp SuaKhachHang.fxml!");
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
