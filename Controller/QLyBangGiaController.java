package com.mycompany.mavenproject1;

import com.mycompany.mavenproject1.model.BANGGIA;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;

public class QLyBangGiaController {

    @FXML private TableView<BANGGIA> tableBangGia;
    @FXML private TableColumn<BANGGIA, String> colMaBG;
    @FXML private TableColumn<BANGGIA, String> colMaSan;
    @FXML private TableColumn<BANGGIA, String> colMaKG;
    @FXML private TableColumn<BANGGIA, Long> colDonGia;
    @FXML private TextField txtSuaDonGia;

    private ObservableList<BANGGIA> listBangGia = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colMaBG.setCellValueFactory(new PropertyValueFactory<>("MaBG"));
        colMaSan.setCellValueFactory(new PropertyValueFactory<>("MaSan"));
        colMaKG.setCellValueFactory(new PropertyValueFactory<>("MaKG"));
        colDonGia.setCellValueFactory(new PropertyValueFactory<>("DonGia"));

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

        try (Connection conn = DBContext.KetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                listBangGia.add(new BANGGIA(
                        rs.getString("MaBG"),
                        rs.getLong("DonGia"),
                        rs.getString("MaSan"),
                        rs.getString("MaKG")
                ));
            }
            tableBangGia.setItems(listBangGia);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSuaBangGia() {
        BANGGIA selectedBG = tableBangGia.getSelectionModel().getSelectedItem();
        if (selectedBG == null) {
            hienThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một dòng trong bảng giá để sửa!");
            return;
        }

        String strDonGia = txtSuaDonGia.getText().trim();
        if (strDonGia.isEmpty()) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Vui lòng không để trống đơn giá!");
            return;
        }

        try {
            long giaMoi = Long.parseLong(strDonGia);
            if (giaMoi <= 0) {
                hienThongBao(Alert.AlertType.ERROR, "Lỗi ràng buộc", "Đơn giá phải lớn hơn 0!");
                return;
            }

            String sql = "UPDATE BANGGIA SET DonGia = ? WHERE MaBG = ?";
            try (Connection conn = DBContext.KetNoi();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setLong(1, giaMoi);
                pstmt.setString(2, selectedBG.getMaBG());

                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật đơn giá thành công!");
                    txtSuaDonGia.clear();
                    loadDataFromOracle();
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
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ManHinhChinhQTV.fxml"));
        Parent v = loader.load();
        Stage stage = (Stage) tableBangGia.getScene().getWindow();
        stage.getScene().setRoot(v);
    } catch (IOException e) {
        e.printStackTrace();
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