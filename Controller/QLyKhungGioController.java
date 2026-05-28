package com.mycompany.mavenproject1;

import com.mycompany.mavenproject1.model.KHUNGGIO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QLyKhungGioController {

    @FXML private TableView<KHUNGGIO> tableKhungGio;
    @FXML private TableColumn<KHUNGGIO, Boolean> colSelect;
    @FXML private TableColumn<KHUNGGIO, String> colMaKG;
    @FXML private TableColumn<KHUNGGIO, String> colGioBD;
    @FXML private TableColumn<KHUNGGIO, String> colGioKT;
    @FXML private TextField txtSearch;

    private ObservableList<KHUNGGIO> listKhungGio = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colSelect.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));
        colSelect.setEditable(true);
        tableKhungGio.setEditable(true);

        colMaKG.setCellValueFactory(new PropertyValueFactory<>("maKG"));
        colGioBD.setCellValueFactory(new PropertyValueFactory<>("gioBDStr"));
        colGioKT.setCellValueFactory(new PropertyValueFactory<>("gioKTStr"));

        txtSearch.textProperty().addListener((obj, oldVal, newVal) -> handleTimKiem());

        loadDataFromOracle();
    }

    private void loadDataFromOracle() {
        listKhungGio.clear();
        String sql = "SELECT MaKG, GioBD, GioKT FROM KHUNGGIO ORDER BY MaKG";
        try (Connection conn = DBContext.KetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                listKhungGio.add(new KHUNGGIO(
                        rs.getString("MaKG"),
                        rs.getTimestamp("GioBD"),
                        rs.getTimestamp("GioKT")
                ));
            }
            tableKhungGio.setItems(listKhungGio);
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
        listKhungGio.clear();
        String sql = "SELECT MaKG, GioBD, GioKT FROM KHUNGGIO WHERE UPPER(MaKG) LIKE UPPER(?) ORDER BY MaKG";
        try (Connection conn = DBContext.KetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + tuKhoa + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    listKhungGio.add(new KHUNGGIO(
                            rs.getString("MaKG"),
                            rs.getTimestamp("GioBD"),
                            rs.getTimestamp("GioKT")
                    ));
                }
            }
            tableKhungGio.setItems(listKhungGio);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleXoaKG() {
        List<KHUNGGIO> dsChon = new ArrayList<>();
        for (KHUNGGIO kg : listKhungGio) {
            if (kg.isSelected()) dsChon.add(kg);
        }

        if (dsChon.isEmpty()) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn ít nhất một khung giờ để xóa!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bạn chắc chắn muốn xóa " + dsChon.size() + " khung giờ đã chọn chứ?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            String sql = "DELETE FROM KHUNGGIO WHERE MaKG = ?";
            try (Connection conn = DBContext.KetNoi();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (KHUNGGIO kg : dsChon) {
                    pstmt.setString(1, kg.getMaKG());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Xóa khung giờ thành công!");
                loadDataFromOracle();
            } catch (SQLException e) {
                hienThongBao(Alert.AlertType.ERROR, "Lỗi", "Khung giờ này đã được sử dụng ở lịch đặt sân, không thể xóa: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleMoThemKG() {
        chuyenTrang("ThemKhungGio.fxml");
    }

   @FXML
private void handleMoSuaKG() {
    List<KHUNGGIO> dsChon = new ArrayList<>();
    for (KHUNGGIO kg : listKhungGio) {
        if (kg.isSelected()) dsChon.add(kg);
    }
    if (dsChon.isEmpty() || dsChon.size() > 1) {
        hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn duy nhất 1 khung giờ để sửa!");
        return;
    }

    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SuaKhungGio.fxml"));
        Parent view = loader.load();
        SuaKhungGioController ctrl = loader.getController();
        ctrl.setKhungGioBanDau(dsChon.get(0));
        Stage stage = (Stage) tableKhungGio.getScene().getWindow();
        stage.getScene().setRoot(view);  // ✅ đã dùng setRoot
    } catch (IOException e) {
        e.printStackTrace();
    }
}
    
    @FXML
    private void handleThoat() {
        chuyenTrang("QLySanVaKG.fxml");
    }

    private void chuyenTrang(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent view = loader.load();
            Stage stage = (Stage) tableKhungGio.getScene().getWindow();
            stage.getScene().setRoot(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void hienThongBao(Alert.AlertType type, String t, String c) {
        Alert a = new Alert(type, c);
        a.setTitle(t);
        a.setHeaderText(null);
        a.showAndWait();
    }
}