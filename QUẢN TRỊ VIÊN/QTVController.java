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
import java.util.Optional;

public class QTVController {

    @FXML private TableView<TAIKHOANModel> tableQTV; // Dùng lại TAIKHOANModel đã tạo ở bước trước
    @FXML private TableColumn<TAIKHOANModel, Boolean> colSelect;
    @FXML private TableColumn<TAIKHOANModel, String> colMaTK;
    @FXML private TableColumn<TAIKHOANModel, String> colUsername;
    @FXML private TableColumn<TAIKHOANModel, String> colPassword;
    @FXML private TableColumn<TAIKHOANModel, String> colVaiTro;
    @FXML private TableColumn<TAIKHOANModel, String> colTrangThai;
    @FXML private TextField txtSearch;

    private ObservableList<TAIKHOANModel> listQTV = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colSelect.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));
        colSelect.setEditable(true);
        tableQTV.setEditable(true);

        // Ánh xạ khớp với các thuộc tính trong TAIKHOANModel
        colMaTK.setCellValueFactory(new PropertyValueFactory<>("maTK"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colVaiTro.setCellValueFactory(new PropertyValueFactory<>("vaiTro"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        txtSearch.textProperty().addListener((obj, oldVal, newVal) -> handleTimKiem());

        loadDataFromOracle();
    }

    private void loadDataFromOracle() {
        listQTV.clear();
        // 🌟 CHỈ LỌC CHỮ 'admin', BỎ 'nhanvien' ĐI
        String sql = "SELECT Ma_TK, Username, Password, VaiTro, TrangThai " +
                "FROM TAIKHOAN " +
                "WHERE LOWER(VaiTro) = 'admin' " +
                "ORDER BY Ma_TK";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                listQTV.add(new TAIKHOANModel(
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
        // 🌟 ĐỒNG BỘ: CHỈ LỌC LOWER(VaiTro) = 'admin' KHI TÌM KIẾM
        String sql = "SELECT Ma_TK, Username, Password, VaiTro, TrangThai FROM TAIKHOAN " +
                "WHERE LOWER(VaiTro) = 'admin' AND (UPPER(Ma_TK) LIKE UPPER(?) OR UPPER(Username) LIKE UPPER(?)) " +
                "ORDER BY Ma_TK";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + key + "%");
            pstmt.setString(2, "%" + key + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    listQTV.add(new TAIKHOANModel(
                            rs.getString("Ma_TK"), rs.getString("Username"), rs.getString("Password"), rs.getString("VaiTro"), rs.getString("TrangThai")
                    ));
                }
            }
            tableQTV.setItems(listQTV);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleXoaQTV() {
        TAIKHOANModel selectedAcc = null;
        for (TAIKHOANModel tk : listQTV) {
            if (tk.isSelected()) { selectedAcc = tk; break; }
        }

        if (selectedAcc == null) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn tài khoản quản trị cần xóa!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn chắc chắn muốn xóa tài khoản " + selectedAcc.getUsername() + " chứ?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            // XÓA TRỰC TIẾP TRÊN BẢNG TAIKHOAN
            String sql = "DELETE FROM TAIKHOAN WHERE Ma_TK = ?";
            try (Connection conn = DBConnection.getConnection();
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
        chuyenTrangInCenter("ThemQTV.fxml");
    }

    @FXML
    private void handleThoat() {
        // Trở về trang quản lý chính hoặc trang chủ admin mà vẫn giữ sidebar
        chuyenTrangInCenter("TrangChuAdmin.fxml");
    }

    // Hàm phụ trợ nạp giao diện vào vùng CENTER của Sidebar tổng
    private void chuyenTrangInCenter(String fxmlFile) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlFile));

            // Kiểm tra nếu rootPane của TrangChuController tồn tại thì nạp vào vùng giữa
            if (TrangChuController.rootPane != null) {
                TrangChuController.rootPane.setCenter(view);
            } else {
                // Phương án dự phòng nếu không tìm thấy rootPane static
                tableQTV.getScene().setRoot(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Parent loadView(String fxmlFile) {
        try {
            return FXMLLoader.load(getClass().getResource(fxmlFile));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void hienThongBao(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type, content);
        alert.setTitle(title); alert.setHeaderText(null); alert.showAndWait();
    }
}