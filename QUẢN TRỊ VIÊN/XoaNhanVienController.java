package com.example.guidemo;

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
        // Cấu hình cột chọn thành Checkbox
        colSelect.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));
        colSelect.setEditable(true);
        tableNhanVien.setEditable(true);

        // Ánh xạ các cột còn lại
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

        try (Connection conn = DBConnection.getConnection();
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
        if (TrangChuController.rootPane != null && TrangChuController.dashboardContent != null) {
            TrangChuController.rootPane.setCenter(TrangChuController.dashboardContent);
        }
    }

    @FXML
    private void handleMoThemNhanVien() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ThemNhanVien.fxml"));
            Parent themNhanVienView = loader.load();

            BorderPane mainPane = (BorderPane) tableNhanVien.getScene().lookup("#mainBorderPane");
            if (mainPane != null) {
                mainPane.setCenter(themNhanVienView);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleXoaNhanVien() {
        // 1. Kiểm tra xem người dùng đã tích chọn ai chưa
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

        // 2. Hiện popup xác nhận có 2 nút Xác nhận và Hủy
        Alert alertConfirm = new Alert(Alert.AlertType.CONFIRMATION);
        alertConfirm.setTitle("Xác nhận xóa");
        alertConfirm.setHeaderText(null);
        alertConfirm.setContentText("Bạn có chắc chắn muốn xóa những nhân viên đã chọn không?");

        ButtonType btnXacNhan = new ButtonType("Xác nhận", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnHuy = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        alertConfirm.getButtonTypes().setAll(btnXacNhan, btnHuy);

        Optional<ButtonType> result = alertConfirm.showAndWait();

        // 3. Xử lý kết quả bấm nút
        if (result.isPresent() && result.get() == btnXacNhan) {
            // Trường hợp: Xác nhận xóa
            String sql = "DELETE FROM NHAN_VIEN WHERE Ma_NV = ?";
            int soLuongDaXoa = 0;

            try (Connection conn = DBConnection.getConnection();
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
                loadDataFromOracle(); // Load lại bảng sạch sẽ

            } catch (SQLException e) {
                e.printStackTrace();
                hienThongBao(Alert.AlertType.ERROR, "Lỗi Oracle Database", "Không thể xóa nhân viên! Chi tiết:\n" + e.getMessage());
            }

        } else {
            // Trường hợp: Bấm Hủy -> Bỏ tích chọn toàn bộ, đưa về trạng thái đầu
            for (NHANVIEN nv : listNhanVien) {
                if (nv.isSelected()) {
                    nv.setSelected(false);
                }
            }
            tableNhanVien.refresh(); // Vẽ lại giao diện bảng trống checkbox
        }
    }

    // Hàm tiện ích hiển thị thông báo nhanh
    private void hienThongBao(Alert.AlertType type, String tieuDe, String noiDung) {
        Alert alert = new Alert(type);
        alert.setTitle(tieuDe);
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }

    @FXML
    private void handleTimKiem() {
        String tuKhoa = txtSearch.getText().trim(); // Lấy nội dung người dùng nhập vào ô search

        // 🌟 Nếu ô tìm kiếm trống rỗng, ta tải lại toàn bộ danh sách nhân viên ban đầu
        if (tuKhoa.isEmpty()) {
            loadDataFromOracle();
            return;
        }

        // Xóa danh sách cũ trên giao diện trước khi nạp kết quả tìm kiếm mới
        listNhanVien.clear();

        // Câu lệnh SQL tìm kiếm: Tìm kiếm theo mã hoặc tên chứa từ khóa (Dùng UPPER để tìm kiếm không phân biệt hoa thường)
        String sql = "SELECT Ma_NV, Hoten_nv, SDT, ChucVu, CaLamViec, Ma_TK " +
                "FROM NHAN_VIEN " +
                "WHERE UPPER(Ma_NV) LIKE UPPER(?) OR UPPER(Hoten_nv) LIKE UPPER(?) " +
                "ORDER BY Ma_NV";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Gán từ khóa tìm kiếm vào 2 dấu hỏi chấm (thêm dấu % để tìm kiếm tương đối)
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

            // Đổ kết quả tìm kiếm mới tìm được lên TableView
            tableNhanVien.setItems(listNhanVien);

            // Thêm một thông báo nhỏ dưới Console dễ theo dõi khi debug
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
        // 1. Quét tìm những nhân viên đang được tích chọn Checkbox
        java.util.List<NHANVIEN> nvDuocChon = new java.util.ArrayList<>();
        for (NHANVIEN nv : listNhanVien) { // Tên list chứa dữ liệu Table của bạn
            if (nv.isSelected()) { // Hàm isSelected() của cột Checkbox
                nvDuocChon.add(nv);
            }
        }

        // 2. Kiểm tra tính hợp lệ
        if (nvDuocChon.isEmpty()) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng tích chọn một nhân viên để sửa!");
            return;
        }
        if (nvDuocChon.size() > 1) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Bạn chỉ được chọn duy nhất một nhân viên để sửa thông tin!");
            return;
        }

        // 3. Tiến hành chuyển vùng main content sang form Sửa và chuyển dữ liệu đi kèm
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SuaNhanVien.fxml"));
            Parent view = loader.load();

            // Gọi controller của form Sửa để nạp thông tin nhân viên cũ lên các ô text
            SuaNhanVienController controllerSua = loader.getController();
            controllerSua.setNhanVienBanDau(nvDuocChon.get(0));

            // Hoán đổi vùng Center của trang chủ
            if (TrangChuController.rootPane != null) {
                TrangChuController.rootPane.setCenter(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
            hienThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không tìm thấy tệp SuaNhanVien.fxml!");
        }
    }
}