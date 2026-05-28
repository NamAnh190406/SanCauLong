package com.mycompany.mavenproject1;

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
import java.sql.*;
import java.util.Optional;

public class QLyQTVController {

    @FXML private TableView<TaiKhoanModel> tableQTV;
    @FXML private TableColumn<TaiKhoanModel, Boolean> colSelect;
    @FXML private TableColumn<TaiKhoanModel, String> colMaTK;
    @FXML private TableColumn<TaiKhoanModel, String> colUsername;
    @FXML private TableColumn<TaiKhoanModel, String> colPassword;
    @FXML private TableColumn<TaiKhoanModel, String> colVaiTro;
    @FXML private TableColumn<TaiKhoanModel, String> colTrangThai;
    @FXML private TextField txtSearch;

    private ObservableList<TaiKhoanModel> listQTV = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colSelect.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));
        colSelect.setEditable(true);
        tableQTV.setEditable(true);

        colMaTK.setCellValueFactory(new PropertyValueFactory<>("maTK"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colVaiTro.setCellValueFactory(new PropertyValueFactory<>("vaiTro"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));

        txtSearch.textProperty().addListener((obj, oldVal, newVal) -> handleTimKiem());

        loadDataFromOracle();
    }

    private void loadDataFromOracle() {
        listQTV.clear();
        String sql = "SELECT Ma_TK, Username, Password, VaiTro, TrangThai " +
                     "FROM TAIKHOAN WHERE LOWER(VaiTro) = 'admin' ORDER BY Ma_TK";

        try (Connection conn = DBContext.KetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                listQTV.add(new TaiKhoanModel(
                        rs.getString("Ma_TK"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("VaiTro"),
                        rs.getString("TrangThai")
                ));
            }
            tableQTV.setItems(listQTV);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTimKiem() {
        String key = txtSearch.getText().trim();
        if (key.isEmpty()) {
            loadDataFromOracle();
            return;
        }
        listQTV.clear();
        String sql = "SELECT Ma_TK, Username, Password, VaiTro, TrangThai FROM TAIKHOAN " +
                     "WHERE LOWER(VaiTro) = 'admin' AND (UPPER(Ma_TK) LIKE UPPER(?) OR UPPER(Username) LIKE UPPER(?)) " +
                     "ORDER BY Ma_TK";

        try (Connection conn = DBContext.KetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + key + "%");
            pstmt.setString(2, "%" + key + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    listQTV.add(new TaiKhoanModel(
                            rs.getString("Ma_TK"),
                            rs.getString("Username"),
                            rs.getString("Password"),
                            rs.getString("VaiTro"),
                            rs.getString("TrangThai")
                    ));
                }
            }
            tableQTV.setItems(listQTV);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleXoaQTV() {
        TaiKhoanModel selectedAcc = null;
        for (TaiKhoanModel tk : listQTV) {
            if (tk.isSelected()) { selectedAcc = tk; break; }
        }

        if (selectedAcc == null) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn tài khoản quản trị cần xóa!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn chắc chắn muốn xóa tài khoản " + selectedAcc.getUsername() + " chứ?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            String sql = "DELETE FROM TAIKHOAN WHERE Ma_TK = ?";
            try (Connection conn = DBContext.KetNoi();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, selectedAcc.getMaTK());
                pstmt.executeUpdate();
                hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Xóa tài khoản quản trị thành công!");
                loadDataFromOracle();
            } catch (SQLException e) {
                hienThongBao(Alert.AlertType.ERROR, "Lỗi Oracle", "Không thể xóa: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleMoThemQTV() {
        chuyenTrang("ThemQTV.fxml");
    }

    @FXML
    private void handleThoat() {
        chuyenTrang("ManHinhChinhQTV.fxml"); 
    }

    private void chuyenTrang(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent view = loader.load();
            Stage stage = (Stage) tableQTV.getScene().getWindow();
            stage.getScene().setRoot(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void hienThongBao(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type, content);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}