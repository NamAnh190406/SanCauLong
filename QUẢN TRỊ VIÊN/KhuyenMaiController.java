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
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KhuyenMaiController {

    @FXML private TableView<KHUYENMAI> tableKhuyenMai;
    @FXML private TableColumn<KHUYENMAI, Boolean> colSelect;
    @FXML private TableColumn<KHUYENMAI, String> colMaKM;
    @FXML private TableColumn<KHUYENMAI, String> colTenKM;
    @FXML private TableColumn<KHUYENMAI, Double> colPhanTram;
    @FXML private TableColumn<KHUYENMAI, Long> colGiaTriToiDa;
    @FXML private TableColumn<KHUYENMAI, LocalDate> colNgayBD;
    @FXML private TableColumn<KHUYENMAI, LocalDate> colNgayKT;
    @FXML private TextField txtSearch;

    private ObservableList<KHUYENMAI> listKhuyenMai = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Khởi tạo cột Checkbox chọn hàng loạt
        colSelect.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));
        colSelect.setEditable(true);
        tableKhuyenMai.setEditable(true);

        // Ánh xạ dữ liệu các cột
        colMaKM.setCellValueFactory(new PropertyValueFactory<>("maKM"));
        colTenKM.setCellValueFactory(new PropertyValueFactory<>("tenKM"));
        colPhanTram.setCellValueFactory(new PropertyValueFactory<>("phanTramGiam"));
        colGiaTriToiDa.setCellValueFactory(new PropertyValueFactory<>("giaTriToiDa"));
        colNgayBD.setCellValueFactory(new PropertyValueFactory<>("ngayBatDau"));
        colNgayKT.setCellValueFactory(new PropertyValueFactory<>("ngayKetThuc"));

        // Lọc Realtime khi gõ phím
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            handleTimKiem();
        });

        loadDataFromOracle();
    }

    private void loadDataFromOracle() {
        listKhuyenMai.clear();
        String sql = "SELECT MaKM, TenKM, PhanTramGG, GTriToiDa, NgayBD, NgayKT FROM KHUYENMAI ORDER BY MaKM";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Date d1 = rs.getDate("NgayBD");
                Date d2 = rs.getDate("NgayKT");

                KHUYENMAI km = new KHUYENMAI(
                        rs.getString("MaKM"),
                        rs.getString("TenKM"),
                        rs.getDouble("PhanTramGG"),
                        rs.getLong("GTriToiDa"),
                        d1 != null ? d1.toLocalDate() : null,
                        d2 != null ? d2.toLocalDate() : null
                );
                listKhuyenMai.add(km);
            }
            tableKhuyenMai.setItems(listKhuyenMai);
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

        listKhuyenMai.clear();
        String sql = "SELECT MaKM, TenKM, PhanTramGG, GTriToiDa, NgayBD, NgayKT FROM KHUYENMAI " +
                "WHERE UPPER(MaKM) LIKE UPPER(?) OR UPPER(TenKM) LIKE UPPER(?) ORDER BY MaKM";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String pattern = "%" + tuKhoa + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Date d1 = rs.getDate("NgayBD");
                    Date d2 = rs.getDate("NgayKT");
                    listKhuyenMai.add(new KHUYENMAI(
                            rs.getString("MaKM"),
                            rs.getString("TenKM"),
                            rs.getDouble("PhanTramGG"),
                            rs.getLong("GTriToiDa"),
                            d1 != null ? d1.toLocalDate() : null,
                            d2 != null ? d2.toLocalDate() : null
                    ));
                }
            }
            tableKhuyenMai.setItems(listKhuyenMai);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleXoaKhuyenMai() {
        List<KHUYENMAI> dsChon = new ArrayList<>();
        for (KHUYENMAI km : listKhuyenMai) {
            if (km.isSelected()) dsChon.add(km);
        }

        if (dsChon.isEmpty()) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn ít nhất một mã khuyến mãi để xóa!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc chắn muốn xóa " + dsChon.size() + " chương trình khuyến mãi đã chọn?");

        ButtonType btnOk = new ButtonType("Xác nhận", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnOk, btnCancel);

        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent() && res.get() == btnOk) {
            String sql = "DELETE FROM KHUYENMAI WHERE MaKM = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                for (KHUYENMAI km : dsChon) {
                    pstmt.setString(1, km.getMaKM());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa thành công chương trình khuyến mãi!");
                loadDataFromOracle();
            } catch (SQLException e) {
                if (e.getMessage().contains("ORA-02292")) {
                    hienThongBao(Alert.AlertType.ERROR, "Lỗi ràng buộc", "Không thể xóa vì chương trình này đã được áp dụng cho các hóa đơn đặt sân!");
                } else {
                    hienThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", e.getMessage());
                }
                loadDataFromOracle();
            }
        } else {
            listKhuyenMai.forEach(km -> km.setSelected(false));
            tableKhuyenMai.refresh();
        }
    }

    @FXML
    private void handleMoSuaKhuyenMai() {
        List<KHUYENMAI> dsChon = new ArrayList<>();
        for (KHUYENMAI km : listKhuyenMai) {
            if (km.isSelected()) dsChon.add(km);
        }

        if (dsChon.isEmpty()) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng tích chọn chương trình khuyến mãi cần sửa!");
            return;
        }
        if (dsChon.size() > 1) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Chỉ được chọn duy nhất một chương trình khuyến mãi để chỉnh sửa!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SuaKhuyenMai.fxml"));
            Parent view = loader.load();

            SuaKhuyenMaiController controller = loader.getController();
            controller.setKhuyenMaiBanDau(dsChon.get(0));

            if (TrangChuController.rootPane != null) {
                TrangChuController.rootPane.setCenter(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
            hienThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không tìm thấy tệp SuaKhuyenMai.fxml!");
        }
    }

    @FXML
    private void handleMoThemKhuyenMai() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ThemKhuyenMai.fxml"));
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