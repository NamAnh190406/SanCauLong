package com.mycompany.mavenproject1;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.*;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

/**
 * Controller class for KhachHangNV.fxml.
 * Version: 1.3.1 - Removed Edit Customer feature.
 */
public class KhachHangNVController implements Initializable {

    // ── Table ────────────────────────────────────────────────────
    @FXML private TableView<KhachHangRow>            tableCustomers;
    @FXML private TableColumn<KhachHangRow, Boolean> colCheckbox;
    @FXML private TableColumn<KhachHangRow, String>  colId;
    @FXML private TableColumn<KhachHangRow, String>  colName;
    @FXML private TableColumn<KhachHangRow, String>  colPhone;
    @FXML private TableColumn<KhachHangRow, String>  colEmail;
    @FXML private TableColumn<KhachHangRow, Number>  colTotalBookings;

    // ── Header ───────────────────────────────────────────────────
    @FXML private TextField txtSearch;
    @FXML private Button    btnDeleteSelected;
    @FXML private Button    btnCancelSelection;
    @FXML private HBox      actionButtonsBox;

    // ── Footer ───────────────────────────────────────────────────
    @FXML private Label lblTotalCount;
    @FXML private HBox  selectedCountBox;
    @FXML private Label lblSelectedCount;

    // ── Data ─────────────────────────────────────────────────────
    private final ObservableList<KhachHangRow> masterList = FXCollections.observableArrayList();
    private FilteredList<KhachHangRow>         filteredList;
    private boolean selectionMode = false;

    // ─────────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        setupSearch();
        setupButtons();
        loadData();
    }

    // ═══════════════════════════════════════════════════════════════
    //  TABLE SETUP
    // ═══════════════════════════════════════════════════════════════
    private void setupTable() {
        tableCustomers.setEditable(true);

        colCheckbox.setCellValueFactory(cd -> cd.getValue().selectedProperty());
        colCheckbox.setCellFactory(CheckBoxTableCell.forTableColumn(colCheckbox));
        colCheckbox.setEditable(true);

        colId           .setCellValueFactory(cd -> cd.getValue().maKHProperty());
        colName         .setCellValueFactory(cd -> cd.getValue().hoTenProperty());
        colPhone        .setCellValueFactory(cd -> cd.getValue().sdtProperty());
        colEmail        .setCellValueFactory(cd -> cd.getValue().emailProperty());
        colTotalBookings.setCellValueFactory(cd -> cd.getValue().soLanDatProperty());

        filteredList = new FilteredList<>(masterList, p -> true);
        tableCustomers.setItems(filteredList);
    }

    // ═══════════════════════════════════════════════════════════════
    //  SEARCH
    // ═══════════════════════════════════════════════════════════════
    private void setupSearch() {
        txtSearch.textProperty().addListener((ob, o, kw) -> {
            String lower = kw.trim().toLowerCase();
            filteredList.setPredicate(r -> {
                if (lower.isEmpty()) return true;
                return r.getHoTen().toLowerCase().contains(lower)
                    || r.getSdt()  .toLowerCase().contains(lower)
                    || r.getEmail().toLowerCase().contains(lower)
                    || r.getMaKH() .toLowerCase().contains(lower);
            });
            lblTotalCount.setText(String.valueOf(filteredList.size()));
        });
    }

    // ═══════════════════════════════════════════════════════════════
    //  BUTTONS
    // ═══════════════════════════════════════════════════════════════
    private void setupButtons() {
        btnDeleteSelected .setOnAction(e -> deleteSelected());
        btnCancelSelection.setOnAction(e -> exitSelectionMode());
    }

    // ═══════════════════════════════════════════════════════════════
    //  LOAD DATA  ← SỬA: thêm dấu phẩy + sửa GROUP BY
    // ═══════════════════════════════════════════════════════════════
    private void loadData() {
        masterList.clear();
        String sql =
            "SELECT kh.MaKH, kh.HoTen, kh.SDT, kh.Email, " +
            "       kh.HangThanhVien, kh.DiemTichLuy, " +
            "       COUNT(ds.MaDS) AS SoLanDat " +
            "FROM KHACHHANG kh " +
            "LEFT JOIN DATSAN ds ON kh.MaKH = ds.MaKH " +
            "GROUP BY kh.MaKH, kh.HoTen, kh.SDT, kh.Email, " +
            "         kh.HangThanhVien, kh.DiemTichLuy " +
            "ORDER BY kh.HoTen";

        try (Connection conn = DBContext.KetNoi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                KhachHangRow row = new KhachHangRow(
                    rs.getString("MaKH"),
                    rs.getString("HoTen"),
                    rs.getString("SDT")   != null ? rs.getString("SDT")   : "",
                    rs.getString("Email") != null ? rs.getString("Email") : "",
                    rs.getInt("SoLanDat")
                );
                row.selectedProperty().addListener((ob, o, n) -> updateSelectionUI());
                masterList.add(row);
            }
        } catch (SQLException e) {
            System.err.println("loadData error: " + e.getMessage());
        }

        lblTotalCount.setText(String.valueOf(masterList.size()));
    }

    // ═══════════════════════════════════════════════════════════════
    //  XÓA NHIỀU
    // ═══════════════════════════════════════════════════════════════
    private void deleteSelected() {
        long count = masterList.stream().filter(KhachHangRow::isSelected).count();
        if (count == 0) { showAlert("Thông báo", "Chưa chọn khách hàng nào!"); return; }

        Alert cf = new Alert(Alert.AlertType.CONFIRMATION,
            "Xóa " + count + " khách hàng đã chọn?", ButtonType.OK, ButtonType.CANCEL);
        cf.setHeaderText(null);
        cf.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            String sql = "DELETE FROM KHACHHANG WHERE MaKH = ?";
            try (Connection conn = DBContext.KetNoi();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                for (KhachHangRow r : masterList) {
                    if (r.isSelected()) { ps.setString(1, r.getMaKH()); ps.addBatch(); }
                }
                ps.executeBatch();
                exitSelectionMode();
                loadData();
            } catch (SQLException e) {
                showAlert("Lỗi", "Không thể xóa: " + e.getMessage());
            }
        });
    }

    // ═══════════════════════════════════════════════════════════════
    //  SELECTION MODE UI
    // ═══════════════════════════════════════════════════════════════
    private void updateSelectionUI() {
        long selected = masterList.stream().filter(KhachHangRow::isSelected).count();
        if (selected > 0 && !selectionMode) enterSelectionMode();
        if (selected == 0 && selectionMode)  exitSelectionMode();
        lblSelectedCount.setText(String.valueOf(selected));
    }

    private void enterSelectionMode() {
        selectionMode = true;
        btnDeleteSelected .setVisible(true);  btnDeleteSelected .setManaged(true);
        btnCancelSelection.setVisible(true);  btnCancelSelection.setManaged(true);
        selectedCountBox  .setVisible(true);  selectedCountBox  .setManaged(true);
    }

    private void exitSelectionMode() {
        selectionMode = false;
        masterList.forEach(r -> r.setSelected(false));
        btnDeleteSelected .setVisible(false); btnDeleteSelected .setManaged(false);
        btnCancelSelection.setVisible(false); btnCancelSelection.setManaged(false);
        selectedCountBox  .setVisible(false); selectedCountBox  .setManaged(false);
    }

    // ═══════════════════════════════════════════════════════════════
    //  THOÁT
    // ═══════════════════════════════════════════════════════════════
    @FXML private void handleThoat()  { navigateBack(); }
    @FXML private void onThoatClick() { navigateBack(); }

    private void navigateBack() {
        try { App.setRoot("ManHinhChinhNV"); } catch (Exception e) { e.printStackTrace(); }
    }

    // ═══════════════════════════════════════════════════════════════
    //  HELPERS
    // ═══════════════════════════════════════════════════════════════
    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }

    // ═══════════════════════════════════════════════════════════════
    //  ROW MODEL
    // ═══════════════════════════════════════════════════════════════
    public static class KhachHangRow {
        private final SimpleBooleanProperty selected  = new SimpleBooleanProperty(false);
        private final SimpleStringProperty  maKH      = new SimpleStringProperty();
        private final SimpleStringProperty  hoTen     = new SimpleStringProperty();
        private final SimpleStringProperty  sdt       = new SimpleStringProperty();
        private final SimpleStringProperty  email     = new SimpleStringProperty();
        private final SimpleIntegerProperty soLanDat  = new SimpleIntegerProperty();

        public KhachHangRow(String maKH, String hoTen, String sdt, String email, int soLanDat) {
            this.maKH    .set(maKH);
            this.hoTen   .set(hoTen);
            this.sdt     .set(sdt);
            this.email   .set(email);
            this.soLanDat.set(soLanDat);
        }

        public SimpleBooleanProperty selectedProperty()  { return selected; }
        public boolean isSelected()                      { return selected.get(); }
        public void    setSelected(boolean v)            { selected.set(v); }

        public SimpleStringProperty  maKHProperty()     { return maKH; }
        public SimpleStringProperty  hoTenProperty()    { return hoTen; }
        public SimpleStringProperty  sdtProperty()      { return sdt; }
        public SimpleStringProperty  emailProperty()    { return email; }
        public SimpleIntegerProperty soLanDatProperty() { return soLanDat; }

        public String getMaKH()  { return maKH.get(); }
        public String getHoTen() { return hoTen.get(); }
        public String getSdt()   { return sdt.get(); }
        public String getEmail() { return email.get(); }
    }
}
