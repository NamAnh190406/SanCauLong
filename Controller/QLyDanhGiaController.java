package com.mycompany.mavenproject1;

import com.mycompany.mavenproject1.model.DANHGIASAN;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class QLyDanhGiaController {

    @FXML private TableView<DANHGIASAN> tableDanhGia;
    @FXML private TableColumn<DANHGIASAN, Boolean> colSelect;
    @FXML private TableColumn<DANHGIASAN, String> colMaDG;
    @FXML private TableColumn<DANHGIASAN, String> colMaKH;
    @FXML private TableColumn<DANHGIASAN, String> colMaSan;
    @FXML private TableColumn<DANHGIASAN, Integer> colDiemDG;
    @FXML private TableColumn<DANHGIASAN, String> colNhanXet;
    @FXML private TableColumn<DANHGIASAN, Timestamp> colThoiDiem;
    @FXML private TextField txtSearch;

    private ObservableList<DANHGIASAN> listDanhGia = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colSelect.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));
        colSelect.setEditable(true);
        tableDanhGia.setEditable(true);

        colMaDG.setCellValueFactory(new PropertyValueFactory<>("maDanhGia"));
        colMaKH.setCellValueFactory(new PropertyValueFactory<>("maKH"));
        colMaSan.setCellValueFactory(new PropertyValueFactory<>("maSan"));
        colDiemDG.setCellValueFactory(new PropertyValueFactory<>("diemDG"));
        colNhanXet.setCellValueFactory(new PropertyValueFactory<>("nhanXet"));
        colThoiDiem.setCellValueFactory(new PropertyValueFactory<>("thoiDiemDanhGia"));

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> handleTimKiem());

        loadDataFromOracle();
    }

    private void loadDataFromOracle() {
        listDanhGia.clear();
        String sql = "SELECT MaDanhGia, DiemDG, NhanXet, ThoiDiemDanhGia, MaKH, MaSan FROM DANHGIASAN ORDER BY ThoiDiemDanhGia DESC";

        try (Connection conn = DBContext.KetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                listDanhGia.add(new DANHGIASAN(
                        rs.getString("MaDanhGia"),
                        rs.getInt("DiemDG"),
                        rs.getString("NhanXet"),
                        rs.getTimestamp("ThoiDiemDanhGia"),
                        rs.getString("MaKH"),
                        rs.getString("MaSan")
                ));
            }
            tableDanhGia.setItems(listDanhGia);
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

        listDanhGia.clear();
        String sql = "SELECT MaDanhGia, DiemDG, NhanXet, ThoiDiemDanhGia, MaKH, MaSan FROM DANHGIASAN " +
                "WHERE UPPER(MaDanhGia) LIKE UPPER(?) OR UPPER(MaKH) LIKE UPPER(?) OR UPPER(MaSan) LIKE UPPER(?) " +
                "ORDER BY ThoiDiemDanhGia DESC";

        try (Connection conn = DBContext.KetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String pattern = "%" + tuKhoa + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    listDanhGia.add(new DANHGIASAN(
                            rs.getString("MaDanhGia"),
                            rs.getInt("DiemDG"),
                            rs.getString("NhanXet"),
                            rs.getTimestamp("ThoiDiemDanhGia"),
                            rs.getString("MaKH"),
                            rs.getString("MaSan")
                    ));
                }
            }
            tableDanhGia.setItems(listDanhGia);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleXoaDanhGia() {
        List<DANHGIASAN> dsChon = new ArrayList<>();
        for (DANHGIASAN dg : listDanhGia) {
            if (dg.isSelected()) dsChon.add(dg);
        }

        if (dsChon.isEmpty()) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng tích chọn ít nhất một phản hồi đánh giá để gỡ bỏ!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận gỡ bỏ");
        alert.setHeaderText(null);
        alert.setContentText("Hệ thống sẽ tiến hành xóa vĩnh viễn " + dsChon.size() + " đánh giá đã chọn. Xác nhận?");

        ButtonType btnOk = new ButtonType("Xóa vĩnh viễn", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnOk, btnCancel);

        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent() && res.get() == btnOk) {
            String sql = "DELETE FROM DANHGIASAN WHERE MaDanhGia = ?";
            try (Connection conn = DBContext.KetNoi();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                for (DANHGIASAN dg : dsChon) {
                    pstmt.setString(1, dg.getMaDanhGia());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã gỡ bỏ các đánh giá được chọn!");
                loadDataFromOracle();
            } catch (SQLException e) {
                hienThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", e.getMessage());
            }
        }
    }

   @FXML
private void handleThoat() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ManHinhChinhQTV.fxml"));
        Parent view = loader.load();
        txtSearch.getScene().setRoot(view);
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