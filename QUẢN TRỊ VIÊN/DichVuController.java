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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DichVuController {

    @FXML private TableView<DICHVU> tableDichVu;
    @FXML private TableColumn<DICHVU, Boolean> colSelect;
    @FXML private TableColumn<DICHVU, String> colMaDV;
    @FXML private TableColumn<DICHVU, String> colTenDV;
    @FXML private TableColumn<DICHVU, String> colDonViTinh;
    @FXML private TableColumn<DICHVU, Long> colGiaBan;
    @FXML private TableColumn<DICHVU, Integer> colSoLuongTon;
    @FXML private TableColumn<DICHVU, String> colTrangThai;
    @FXML private TextField txtSearch;

    private ObservableList<DICHVU> listDichVu = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colSelect.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));
        colSelect.setEditable(true);
        tableDichVu.setEditable(true);

        colMaDV.setCellValueFactory(new PropertyValueFactory<>("maDV"));
        colTenDV.setCellValueFactory(new PropertyValueFactory<>("tenDV"));
        colDonViTinh.setCellValueFactory(new PropertyValueFactory<>("donViTinh")); // Đã sửa từ loaiDV
        colGiaBan.setCellValueFactory(new PropertyValueFactory<>("giaBan"));
        colSoLuongTon.setCellValueFactory(new PropertyValueFactory<>("slTonKho"));   // Đã sửa từ soLuongTon
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> handleTimKiem());

        loadDataFromOracle();
    }

    private void loadDataFromOracle() {
        listDichVu.clear();
        String sql = "SELECT MaDV, TenDV, DonViTinh, GiaBan, SLTonkho FROM DICHVU ORDER BY MaDV";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                listDichVu.add(new DICHVU(
                        rs.getString("MaDV"),
                        rs.getString("TenDV"),
                        rs.getString("DonViTinh"),
                        rs.getLong("GiaBan"),
                        rs.getInt("SLTonkho") // Map chuẩn cột Oracle vào Constructor
                ));
            }
            tableDichVu.setItems(listDichVu);
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

        listDichVu.clear();
        String sql = "SELECT MaDV, TenDV, DonViTinh, GiaBan, SLTonkho FROM DICHVU " +
                "WHERE UPPER(MaDV) LIKE UPPER(?) OR UPPER(TenDV) LIKE UPPER(?) ORDER BY MaDV";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String pattern = "%" + tuKhoa + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    listDichVu.add(new DICHVU(
                            rs.getString("MaDV"),
                            rs.getString("TenDV"),
                            rs.getString("DonViTinh"),
                            rs.getLong("GiaBan"),
                            rs.getInt("SLTonkho")
                    ));
                }
            }
            tableDichVu.setItems(listDichVu);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMoSuaDichVu() {
        List<DICHVU> dsChon = new ArrayList<>();
        for (DICHVU dv : listDichVu) {
            if (dv.isSelected()) dsChon.add(dv);
        }

        if (dsChon.isEmpty()) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng tích chọn một dịch vụ cần sửa!");
            return;
        }
        if (dsChon.size() > 1) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Chỉ được chọn duy nhất một dịch vụ để sửa!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SuaDichVu.fxml"));
            Parent view = loader.load();

            SuaDichVuController controllerSua = loader.getController();
            controllerSua.setDichVuBanDau(dsChon.get(0)); // Truyền dữ liệu sang form sửa

            if (TrangChuController.rootPane != null) {
                TrangChuController.rootPane.setCenter(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
            hienThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không tìm thấy tệp SuaDichVu.fxml!");
        }
    }

    @FXML
    private void handleXoaDichVu() {
        List<DICHVU> dsChon = new ArrayList<>();
        for (DICHVU dv : listDichVu) {
            if (dv.isSelected()) dsChon.add(dv);
        }

        if (dsChon.isEmpty()) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn ít nhất một dịch vụ để xóa!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc muốn xóa " + dsChon.size() + " dịch vụ đã chọn?");

        ButtonType btnOk = new ButtonType("Xác nhận", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnOk, btnCancel);

        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent() && res.get() == btnOk) {
            String sql = "DELETE FROM DICHVU WHERE MaDV = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                for (DICHVU dv : dsChon) {
                    pstmt.setString(1, dv.getMaDV());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa dịch vụ thành công!");
                loadDataFromOracle();
            } catch (SQLException e) {
                hienThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không thể xóa. Chi tiết: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleMoThemDichVu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ThemDichVu.fxml"));
            Parent view = loader.load();
            if (TrangChuController.rootPane != null) {
                TrangChuController.rootPane.setCenter(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleThoat() {
        if (TrangChuController.rootPane != null && TrangChuController.dashboardContent != null) {
            TrangChuController.rootPane.setCenter(TrangChuController.dashboardContent);
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