package com.example.guidemo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BangGiaController {

    @FXML private TableView<BANGGIA> tableBangGia;
    @FXML private TableColumn<BANGGIA, String> colMaBG;
    @FXML private TableColumn<BANGGIA, String> colMaSan;
    @FXML private TableColumn<BANGGIA, String> colMaKG;
    @FXML private TableColumn<BANGGIA, Long> colDonGia;
    @FXML private TextField txtSuaDonGia;

    private ObservableList<BANGGIA> listBangGia = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Ánh xạ các cột trên giao diện tương ứng thuộc tính Model
        colMaBG.setCellValueFactory(new PropertyValueFactory<>("MaBG"));
        colMaSan.setCellValueFactory(new PropertyValueFactory<>("MaSan"));
        colMaKG.setCellValueFactory(new PropertyValueFactory<>("MaKG"));
        colDonGia.setCellValueFactory(new PropertyValueFactory<>("DonGia"));

        // Lắng nghe sự kiện click dòng trên TableView để tự điền giá cũ vào ô nhập
        tableBangGia.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtSuaDonGia.setText(String.valueOf(newSelection.getDonGia()));
            }
        });

        loadDataFromOracle();
    }

    private void loadDataFromOracle() {
        listBangGia.clear();
        String sql = "SELECT MaBG, DonGia, MaSan, MaKG FROM BANGGIA ORDER BY MaBG";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String maBG = rs.getString("MaBG");
                long donGia = rs.getLong("DonGia");
                String maSan = rs.getString("MaSan");
                String maKG = rs.getString("MaKG");

                listBangGia.add(new BANGGIA(maBG, donGia, maSan, maKG));
            }
            tableBangGia.setItems(listBangGia);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSuaBangGia() {
        // 1. Kiểm tra xem người dùng có chọn dòng nào trên bảng chưa
        BANGGIA selectedBG = tableBangGia.getSelectionModel().getSelectedItem();
        if (selectedBG == null) {
            hienThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một dòng trong bảng giá để sửa!");
            return;
        }

        // 2. Kiểm tra dữ liệu số nhập vào ô
        String strDonGia = txtSuaDonGia.getText().trim();
        if (strDonGia.isEmpty()) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Vui lòng không để trống đơn giá!");
            return;
        }

        try {
            long giaMoi = Long.parseLong(strDonGia);
            if (giaMoi <= 0) {
                hienThongBao(Alert.AlertType.ERROR, "Lỗi ràng buộc", "Đơn giá phải lớn hơn 0 (Khớp CHECK CONSTRAINT Oracle)!");
                return;
            }

            // 3. Thực hiện UPDATE vào Oracle Database
            String sql = "UPDATE BANGGIA SET DonGia = ? WHERE MaBG = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setLong(1, giaMoi);
                pstmt.setString(2, selectedBG.getMaBG());

                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật đơn giá thành công!");
                    txtSuaDonGia.clear();
                    loadDataFromOracle(); // Làm mới lại bảng dữ liệu trên màn hình
                }
            }

        } catch (NumberFormatException e) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi định dạng", "Đơn giá phải là một số nguyên hợp lệ!");
        } catch (SQLException e) {
            e.printStackTrace();
            hienThongBao(Alert.AlertType.ERROR, "Lỗi Database", e.getMessage());
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