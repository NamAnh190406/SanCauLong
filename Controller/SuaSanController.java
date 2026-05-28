package com.mycompany.mavenproject1;

import com.mycompany.mavenproject1.model.SAN;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SuaSanController {

    @FXML private TextField txtMaSan;
    @FXML private TextField txtTenSan;
    @FXML private ChoiceBox<String> choiceLoaiSan;
    @FXML private TextField txtLoaiMatSan;
    @FXML private ChoiceBox<String> choiceKhongGian;
    @FXML private Spinner<Integer> spinnerSLNguoiChoi;
    @FXML private TextField txtGiaThue;
    @FXML private ChoiceBox<String> choiceTrangThai;
    @FXML private TextField txtDiaChi;
    @FXML private TextArea txtMoTa;

    private SAN sanDangSua;

    @FXML
    public void initialize() {
        choiceLoaiSan.setItems(FXCollections.observableArrayList("Đơn", "Đôi"));
        choiceKhongGian.setItems(FXCollections.observableArrayList("NhàCầu", "NgoàiTrời"));
        choiceTrangThai.setItems(FXCollections.observableArrayList("HoatDong", "BaoDuong", "Dong"));

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 2);
        spinnerSLNguoiChoi.setValueFactory(valueFactory);
    }

    public void setSanBanDau(SAN san) {
        if (san == null) return;
        this.sanDangSua = san;

        txtMaSan.setText(san.getMaSan());
        txtMaSan.setEditable(false);
        txtTenSan.setText(san.getTenSan());
        choiceLoaiSan.setValue(san.getLoaiSan());
        txtLoaiMatSan.setText(san.getLoaiMatSan());
        choiceKhongGian.setValue(san.getKhongGian());
        spinnerSLNguoiChoi.getValueFactory().setValue(san.getSlNguoiChoi());
        txtGiaThue.setText(String.valueOf(san.getGiaThueTheoGio()));
        choiceTrangThai.setValue(san.getTrangThai());
        txtDiaChi.setText(san.getDiaChi() != null ? san.getDiaChi() : "");
        if (txtMoTa != null) txtMoTa.setText(san.getMoTa() != null ? san.getMoTa() : "");
    }

    @FXML
    private void handleLuuCapNhat() {
        String tenSan = txtTenSan.getText() != null ? txtTenSan.getText().trim() : "";
        String loaiMatSan = txtLoaiMatSan.getText() != null ? txtLoaiMatSan.getText().trim() : "";
        String giaThueStr = txtGiaThue.getText() != null ? txtGiaThue.getText().trim() : "";
        String diaChi = txtDiaChi.getText() != null ? txtDiaChi.getText().trim() : "";
        String moTa = (txtMoTa != null && txtMoTa.getText() != null) ? txtMoTa.getText().trim() : "";

        if (tenSan.isEmpty() || loaiMatSan.isEmpty() || giaThueStr.isEmpty() || diaChi.isEmpty()) {
            hienThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ các thông tin bắt buộc!");
            return;
        }

        long giaThue;
        try {
            giaThue = Long.parseLong(giaThueStr);
            if (giaThue <= 0) {
                hienThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Giá thuê phải là số lớn hơn 0!");
                return;
            }
        } catch (NumberFormatException e) {
            hienThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Giá thuê phải là một số nguyên hợp lệ!");
            return;
        }

        String sql = "UPDATE SAN SET TenSan = ?, LoaiSan = ?, LoaiMatSan = ?, KhongGian = ?, " +
                "SLNguoiChoi = ?, GiaThueTheoGio = ?, TrangThai = ?, MoTa = ?, DiaChi = ? WHERE MaSan = ?";

        try (Connection conn = DBContext.KetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tenSan);
            pstmt.setString(2, choiceLoaiSan.getValue() != null ? choiceLoaiSan.getValue() : "Đơn");
            pstmt.setString(3, loaiMatSan);
            pstmt.setString(4, choiceKhongGian.getValue());
            pstmt.setInt(5, spinnerSLNguoiChoi.getValue());
            pstmt.setLong(6, giaThue);
            pstmt.setString(7, choiceTrangThai.getValue() != null ? choiceTrangThai.getValue() : "HoatDong");
            pstmt.setString(8, moTa);
            pstmt.setString(9, diaChi);
            pstmt.setString(10, sanDangSua.getMaSan());

            int result = pstmt.executeUpdate();
            if (result > 0) {
                hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật thông tin sân thành công!");
                QuayLaiDanhSachSan();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            hienThongBao(Alert.AlertType.ERROR, "Lỗi Database", "Không thể cập nhật. Lỗi Oracle: " + e.getMessage());
        }
    }

    @FXML
    private void handleHuyBo() {
        QuayLaiDanhSachSan();
    }

    private void QuayLaiDanhSachSan() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("QLySan.fxml"));
            Parent view = loader.load();
            Stage stage = (Stage) txtMaSan.getScene().getWindow();
            stage.getScene().setRoot(view);
        } catch (IOException e) {
            e.printStackTrace();
            hienThongBao(Alert.AlertType.ERROR, "Lỗi", "Không thể quay lại màn hình QLySan.fxml!");
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