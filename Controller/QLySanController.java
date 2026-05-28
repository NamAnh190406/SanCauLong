package com.mycompany.mavenproject1;

import com.mycompany.mavenproject1.model.SAN;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QLySanController {

    @FXML private TableView<SAN> tableSan;
    @FXML private TableColumn<SAN, Boolean> colSelect;
    @FXML private TableColumn<SAN, String> colMaSan;
    @FXML private TableColumn<SAN, String> colTenSan;
    @FXML private TableColumn<SAN, String> colLoaiSan;
    @FXML private TableColumn<SAN, String> colLoaiMatSan;
    @FXML private TableColumn<SAN, String> colKhongGian;
    @FXML private TableColumn<SAN, Integer> colSLNguoiChoi;
    @FXML private TableColumn<SAN, Long> colGiaThue;
    @FXML private TableColumn<SAN, String> colTrangThai;
    @FXML private TableColumn<SAN, String> colDiaChi;
    @FXML private TextField txtSearch;

    private ObservableList<SAN> listSan = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colSelect.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));
        colSelect.setEditable(true);
        tableSan.setEditable(true);

        colMaSan.setCellValueFactory(new PropertyValueFactory<>("maSan"));
        colTenSan.setCellValueFactory(new PropertyValueFactory<>("tenSan"));
        colLoaiSan.setCellValueFactory(new PropertyValueFactory<>("loaiSan"));
        colLoaiMatSan.setCellValueFactory(new PropertyValueFactory<>("loaiMatSan"));
        colKhongGian.setCellValueFactory(new PropertyValueFactory<>("khongGian"));
        colSLNguoiChoi.setCellValueFactory(new PropertyValueFactory<>("slNguoiChoi"));
        colGiaThue.setCellValueFactory(new PropertyValueFactory<>("giaThueTheoGio"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colDiaChi.setCellValueFactory(new PropertyValueFactory<>("diaChi"));

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> handleTimKiem());

        loadDataFromOracle();
    }

    private void loadDataFromOracle() {
        listSan.clear();
        String sql = "SELECT MaSan, TenSan, LoaiSan, LoaiMatSan, KhongGian, SLNguoiChoi, GiaThueTheoGio, TrangThai, MoTa, DiaChi FROM SAN ORDER BY MaSan";

        try (Connection conn = DBContext.KetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                listSan.add(new SAN(
                        rs.getString("MaSan"),
                        rs.getString("TenSan"),
                        rs.getString("LoaiSan"),
                        rs.getString("LoaiMatSan"),
                        rs.getString("KhongGian"),
                        rs.getInt("SLNguoiChoi"),
                        rs.getLong("GiaThueTheoGio"),
                        rs.getString("TrangThai"),
                        rs.getString("MoTa"),
                        rs.getString("DiaChi")
                ));
            }
            tableSan.setItems(listSan);
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

        listSan.clear();
        String sql = "SELECT MaSan, TenSan, LoaiSan, LoaiMatSan, KhongGian, SLNguoiChoi, GiaThueTheoGio, TrangThai, MoTa, DiaChi FROM SAN " +
                "WHERE UPPER(MaSan) LIKE UPPER(?) OR UPPER(TenSan) LIKE UPPER(?) OR UPPER(LoaiSan) LIKE UPPER(?) ORDER BY MaSan";

        try (Connection conn = DBContext.KetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String pattern = "%" + tuKhoa + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    listSan.add(new SAN(
                            rs.getString("MaSan"),
                            rs.getString("TenSan"),
                            rs.getString("LoaiSan"),
                            rs.getString("LoaiMatSan"),
                            rs.getString("KhongGian"),
                            rs.getInt("SLNguoiChoi"),
                            rs.getLong("GiaThueTheoGio"),
                            rs.getString("TrangThai"),
                            rs.getString("MoTa"),
                            rs.getString("DiaChi")
                    ));
                }
            }
            tableSan.setItems(listSan);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleXoaSan() {
        List<SAN> dsChon = new ArrayList<>();
        for (SAN s : listSan) {
            if (s.isSelected()) dsChon.add(s);
        }

        if (dsChon.isEmpty()) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn ít nhất một sân để xóa!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText(null);
        alert.setContentText("Bạn chắc chắn muốn xóa " + dsChon.size() + " sân đã chọn chứ?");

        ButtonType btnOk = new ButtonType("Xác nhận", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnOk, btnCancel);

        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent() && res.get() == btnOk) {
            String sql = "DELETE FROM SAN WHERE MaSan = ?";
            try (Connection conn = DBContext.KetNoi();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                for (SAN s : dsChon) {
                    pstmt.setString(1, s.getMaSan());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa dữ liệu sân thành công!");
                loadDataFromOracle();
            } catch (SQLException e) {
                hienThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không thể xóa. Sân có thể đã dính dữ liệu hóa đơn/lịch đặt: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleMoThemSan() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ThemSan.fxml"));
            Parent view = loader.load();
            Stage stage = (Stage) tableSan.getScene().getWindow();
            stage.getScene().setRoot(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMoSuaSan() {
        List<SAN> dsChon = new ArrayList<>();
        for (SAN s : listSan) {
            if (s.isSelected()) dsChon.add(s);
        }

        if (dsChon.isEmpty()) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn một sân để cập nhật!");
            return;
        }
        if (dsChon.size() > 1) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Hệ thống chỉ hỗ trợ sửa thông tin một sân tại một thời điểm!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SuaSan.fxml"));
            Parent view = loader.load();

            SuaSanController controller = loader.getController();
            controller.setSanBanDau(dsChon.get(0));

            Stage stage = (Stage) tableSan.getScene().getWindow();
            stage.getScene().setRoot(view);
        } catch (IOException e) {
            e.printStackTrace();
            hienThongBao(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy tệp tin form SuaSan.fxml!");
        }
    }

    @FXML
    private void handleThoat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("QLySanVaKG.fxml"));
            Parent view = loader.load();
            Stage stage = (Stage) tableSan.getScene().getWindow();
            stage.getScene().setRoot(view);
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