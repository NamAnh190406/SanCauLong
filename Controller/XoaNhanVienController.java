package com.mycompany.mavenproject1;

import com.mycompany.mavenproject1.model.NHANVIEN;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class XoaNhanVienController {

    @FXML private TableView<NHANVIEN> tableNhanVien;
    @FXML private TableColumn<NHANVIEN, Boolean> colSelect;
    @FXML private TableColumn<NHANVIEN, String> colMaNV;
    @FXML private TableColumn<NHANVIEN, String> colHoTen;
    @FXML private TableColumn<NHANVIEN, String> colSDT;
    @FXML private TableColumn<NHANVIEN, String> colChucVu;
    @FXML private TableColumn<NHANVIEN, String> colCaLamViec;
    @FXML private TextField txtSearch;
    private ObservableList<NHANVIEN> listNhanVien = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colSelect.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));
        colSelect.setEditable(true);
        tableNhanVien.setEditable(true);

        colMaNV.setCellValueFactory(new PropertyValueFactory<>("MaNV"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("HoTen"));
        colSDT.setCellValueFactory(new PropertyValueFactory<>("SDT"));
        colChucVu.setCellValueFactory(new PropertyValueFactory<>("ChucVu"));
        colCaLamViec.setCellValueFactory(new PropertyValueFactory<>("CaLamViec"));

        loadDataFromOracle();
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            handleTimKiem();
        });
    }

    private void loadDataFromOracle() {
        listNhanVien.clear();
        String sql = "SELECT Ma_NV, Hoten_nv, SDT, ChucVu, CaLamViec, Ma_TK FROM NHAN_VIEN";

        try (Connection conn = DBContext.KetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String ma = rs.getString("Ma_NV");
                String ten = rs.getString("Hoten_nv");
                String sdt = rs.getString("SDT");
                String chucvu = rs.getString("ChucVu");
                String ca = rs.getString("CaLamViec");
                String matk = rs.getString("Ma_TK");

                NHANVIEN nv = new NHANVIEN(ma, ten, sdt, chucvu, ca, matk);
                listNhanVien.add(nv);
            }
            tableNhanVien.setItems(listNhanVien);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

   @FXML
private void handleThoat() {
    try {
        App.setRoot("ManHinhChinhQTV");
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    @FXML
    private void handleMoThemNhanVien() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ThemNV.fxml"));
            Parent themNhanVienView = loader.load();
            tableNhanVien.getScene().setRoot(themNhanVienView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleXoaNhanVien() {
        boolean coNguoiDuocChon = false;
        for (NHANVIEN nv : listNhanVien) {
            if (nv.isSelected()) {
                coNguoiDuocChon = true;
                break;
            }
        }

        if (!coNguoiDuocChon) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng tích chọn ít nhất một nhân viên để xóa!");
            return;
        }

        Alert alertConfirm = new Alert(Alert.AlertType.CONFIRMATION);
        alertConfirm.setTitle("Xác nhận xóa");
        alertConfirm.setHeaderText(null);
        alertConfirm.setContentText("Bạn có chắc chắn muốn xóa những nhân viên đã chọn không?");

        ButtonType btnXacNhan = new ButtonType("Xác nhận", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnHuy = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        alertConfirm.getButtonTypes().setAll(btnXacNhan, btnHuy);

        Optional<ButtonType> result = alertConfirm.showAndWait();

        if (result.isPresent() && result.get() == btnXacNhan) {
            String sql = "DELETE FROM NHAN_VIEN WHERE Ma_NV = ?";
            int soLuongDaXoa = 0;

            try (Connection conn = DBContext.KetNoi();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                for (NHANVIEN nv : listNhanVien) {
                    if (nv.isSelected()) {
                        pstmt.setString(1, nv.getMaNV());
                        pstmt.addBatch();
                        soLuongDaXoa++;
                    }
                }

                pstmt.executeBatch();
                hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa thành công " + soLuongDaXoa + " nhân viên!");
                loadDataFromOracle();

            } catch (SQLException e) {
                e.printStackTrace();
                hienThongBao(Alert.AlertType.ERROR, "Lỗi Oracle Database", "Không thể xóa nhân viên! Chi tiết:\n" + e.getMessage());
            }

        } else {
            for (NHANVIEN nv : listNhanVien) {
                if (nv.isSelected()) {
                    nv.setSelected(false);
                }
            }
            tableNhanVien.refresh();
        }
    }

    private void hienThongBao(Alert.AlertType type, String tieuDe, String noiDung) {
        Alert alert = new Alert(type);
        alert.setTitle(tieuDe);
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }

    @FXML
    private void handleTimKiem() {
        String tuKhoa = txtSearch.getText().trim();

        if (tuKhoa.isEmpty()) {
            loadDataFromOracle();
            return;
        }

        listNhanVien.clear();

        String sql = "SELECT Ma_NV, Hoten_nv, SDT, ChucVu, CaLamViec, Ma_TK " +
                "FROM NHAN_VIEN " +
                "WHERE UPPER(Ma_NV) LIKE UPPER(?) OR UPPER(Hoten_nv) LIKE UPPER(?) " +
                "ORDER BY Ma_NV";

        try (Connection conn = DBContext.KetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + tuKhoa + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String ma = rs.getString("Ma_NV");
                    String ten = rs.getString("Hoten_nv");
                    String sdt = rs.getString("SDT");
                    String chucvu = rs.getString("ChucVu");
                    String ca = rs.getString("CaLamViec");
                    String matk = rs.getString("Ma_TK");

                    NHANVIEN nv = new NHANVIEN(ma, ten, sdt, chucvu, ca, matk);
                    listNhanVien.add(nv);
                }
            }

            tableNhanVien.setItems(listNhanVien);
            System.out.println("Đã tìm kiếm xong với từ khóa: " + tuKhoa);

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi hệ thống");
            alert.setHeaderText(null);
            alert.setContentText("Lỗi tìm kiếm nhân viên: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleMoSuaNhanVien() {
        java.util.List<NHANVIEN> nvDuocChon = new java.util.ArrayList<>();
        for (NHANVIEN nv : listNhanVien) {
            if (nv.isSelected()) {
                nvDuocChon.add(nv);
            }
        }

        if (nvDuocChon.isEmpty()) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng tích chọn một nhân viên để sửa!");
            return;
        }
        if (nvDuocChon.size() > 1) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Bạn chỉ được chọn duy nhất một nhân viên để sửa thông tin!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SuaNhanVien.fxml"));
            Parent view = loader.load();

            SuaNhanVienController controllerSua = loader.getController();
            controllerSua.setNhanVienBanDau(nvDuocChon.get(0));

            tableNhanVien.getScene().setRoot(view);
        } catch (IOException e) {
            e.printStackTrace();
            hienThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không tìm thấy tệp SuaNhanVien.fxml!");
        }
    }
}
