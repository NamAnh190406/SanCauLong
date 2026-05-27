package Controller;

import Model.THONGBAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Dashboard Controller - Quản lý giao diện chính của hệ thống đặt sân cầu lông
 * 
 * Chức năng:
 * - Quản lý điều hướng giữa các module (Đã tích hợp Caching)
 * - Hiển thị thông báo (Đồng bộ Database) và tìm kiếm
 * - Quản lý cửa sổ (minimize, maximize, close)
 * - Hiển thị lịch đặt sân sắp tới
 */
public class DashboardController implements Initializable {

    // ================= CONSTANTS =================
    private static final int NOTIF_POPUP_WIDTH = 360;
    private static final int NOTIF_POPUP_HEIGHT = 320;
    private static final int NOTIF_POPUP_OFFSET_X = -360;
    private static final int NOTIF_POPUP_OFFSET_Y = 6;

    private static final int SEARCH_POPUP_WIDTH = 300;
    private static final int SEARCH_POPUP_OFFSET_Y = 4;

    private static final String BUTTON_ACTIVE_STYLE = "-fx-background-color: #00c853; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 8; " +
            "-fx-font-weight: bold;";

    private static final String BUTTON_INACTIVE_STYLE = "-fx-background-color: transparent; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: normal;";

    private static final String BUTTON_HOVER_STYLE = "-fx-background-color: rgba(255,255,255,0.12); " +
            "-fx-text-fill: #94a3b8; " +
            "-fx-cursor: hand;";

    private static final String BUTTON_EXIT_STYLE = "-fx-background-color: transparent; " +
            "-fx-text-fill: #94a3b8; " +
            "-fx-cursor: hand; " +
            "-fx-min-width: 46; " +
            "-fx-min-height: 36; " +
            "-fx-background-radius: 0;";

    private static final String BUTTON_CLOSE_HOVER_STYLE = "-fx-background-color: #e81123; " +
            "-fx-text-fill: white; " +
            "-fx-cursor: hand;";

    // ================= FXML INJECTIONS =================
    @FXML
    private HBox titleBar;
    @FXML
    private Button btnMinimize;
    @FXML
    private Button btnMaximize;
    @FXML
    private Button btnClose;

    // Topbar – notification & search
    @FXML
    private StackPane bellPane;
    @FXML
    private Label lblNotifBadge;
    @FXML
    private TextField tfSearch;

    // Center container for navigation
    @FXML
    private VBox centerContainer;

    // Sidebar Buttons
    @FXML
    private Button btnTrangChu;
    @FXML
    private Button btnSanCauLong;
    @FXML
    private Button btnKhachHang;
    @FXML
    private Button btnDichVu;
    @FXML
    private Button btnHoaDon;
    @FXML
    private Button btnThongKe;
    @FXML
    private Button btnCaiDat;
    @FXML
    private VBox vboxLichSapToi;
    @FXML
    private VBox vboxTrangThaiSan;
    @FXML
    private VBox vboxTrangThaiSanList;
    @FXML
    private Label lblNhanVien;
    @FXML
    private Label lblSanTrong;
    @FXML
    private Label lblDangSuDung;
    @FXML
    private Label lblLichHomNay;
    @FXML
    private Label lblDoanhThu;

    // MOCK_SEARCH_DATA removed as we fetch live data



    // ================= STATE =================
    private double dragOffsetX, dragOffsetY;
    private boolean maximized = false;
    private Button currentActiveButton;
    private Node homeContent;

    // BỘ NHỚ ĐỆM CHO NAVIGATION (Tránh load lại FXML)
    private final Map<String, Node> viewCache = new HashMap<>();

    // Popups
    private Popup notifPopup;
    private Popup searchPopup;
    private VBox notifListBox;

    // ================= INIT & ROUTING =================
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            initializeHomeContent();
            updateBadge();
            setupNavigation();
            setDefaultActiveButton();
            renderTrangThaiSan();
            renderLichSapToi();
            startAutoUpdate();
            setupEventListeners();
            System.out.println("✓ Dashboard initialized successfully");
        } catch (Exception e) {
            System.err.println("✗ Error during initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeHomeContent() {
        if (centerContainer != null && centerContainer.getChildren().size() > 1) {
            homeContent = centerContainer.getChildren().get(1);
            System.out.println("✓ Home content saved");
        } else {
            System.err.println("⚠ Could not save home content - centerContainer or children not ready");
        }
    }

    private void setDefaultActiveButton() {
        if (btnTrangChu != null) {
            updateButtonStyle(btnTrangChu);
            currentActiveButton = btnTrangChu;
        }
    }

    private void setupNavigation() {
        if (btnTrangChu != null) {
            btnTrangChu.setOnAction(e -> handleHomeClick());
        }

        if (btnSanCauLong != null)
            btnSanCauLong.setOnAction(e -> handleSidebarClick(btnSanCauLong, "/View/CourtScheduleUI.fxml"));
        if (btnKhachHang != null)
            btnKhachHang.setOnAction(e -> handleSidebarClick(btnKhachHang, "/View/CustomerManagementUI.fxml"));
        if (btnDichVu != null)
            btnDichVu.setOnAction(e -> handleSidebarClick(btnDichVu, "/View/DichVuUI.fxml"));
        if (btnHoaDon != null)
            btnHoaDon.setOnAction(e -> handleSidebarClick(btnHoaDon, "/View/HoaDonUI.fxml"));
        if (btnThongKe != null)
            btnThongKe.setOnAction(e -> handleSidebarClick(btnThongKe, "/View/ThongKeUI.fxml"));
        if (btnCaiDat != null)
            btnCaiDat.setOnAction(e -> handleSidebarClick(btnCaiDat, "/View/CaiDatUI.fxml"));

        if (lblNhanVien != null) {
            lblNhanVien.setOnMouseClicked(e -> navigateTo("/View/ThongTinCaNhanUI.fxml"));
        }
    }

    private void handleHomeClick() {
        updateActiveButton(btnTrangChu);

        if (homeContent != null && centerContainer != null) {
            if (centerContainer.getChildren().size() > 1) {
                centerContainer.getChildren().set(1, homeContent);
            } else {
                centerContainer.getChildren().add(homeContent);
            }
            System.out.println("✓ Returned to home");
        } else {
            System.err.println("⚠ Home content is null, cannot navigate");
        }
    }

    private Node loadFXML(String fxmlPath) throws IOException {
        if (fxmlPath == null || fxmlPath.isEmpty()) {
            throw new IllegalArgumentException("FXML path cannot be null or empty");
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        if (loader.getLocation() == null) {
            throw new IOException("FXML file not found: " + fxmlPath);
        }
        return loader.load();
    }

    /**
     * Điều hướng có sử dụng Cache để tăng hiệu năng và giữ State UI
     */
    private void navigateTo(String fxmlPath) {
        if (centerContainer == null || fxmlPath == null) {
            System.err.println("✗ Navigation failed: centerContainer or fxmlPath is null");
            return;
        }

        try {
            Node node;
            // Kiểm tra xem trang đã có trong Cache chưa
            if (viewCache.containsKey(fxmlPath)
                    && !fxmlPath.equals("/View/ThongKeUI.fxml")
                    && !fxmlPath.equals("/View/HoaDonUI.fxml")
                    && !fxmlPath.equals("/View/CourtScheduleUI.fxml")) {
                node = viewCache.get(fxmlPath);
                System.out.println("✓ Loaded from cache: " + fxmlPath);
            } else {
                node = loadFXML(fxmlPath);
                if (!fxmlPath.equals("/View/ThongKeUI.fxml")
                        && !fxmlPath.equals("/View/HoaDonUI.fxml")
                        && !fxmlPath.equals("/View/CourtScheduleUI.fxml")) {
                    viewCache.put(fxmlPath, node); // Lưu vào Cache
                }
                System.out.println("✓ Parsed FXML: " + fxmlPath);
            }

            VBox.setVgrow(node, Priority.ALWAYS);

            // Thay thế nội dung cũ bằng nội dung mới
            if (centerContainer.getChildren().size() > 1) {
                centerContainer.getChildren().set(1, node);
            } else {
                centerContainer.getChildren().add(node);
            }
        } catch (IOException ex) {
            System.err.println("✗ Error loading view: " + fxmlPath);
            ex.printStackTrace();
        }
    }

    private void handleSidebarClick(Button clickedButton, String fxmlPath) {
        updateActiveButton(clickedButton);
        navigateTo(fxmlPath);
    }

    private void updateActiveButton(Button button) {
        if (currentActiveButton != null) {
            currentActiveButton.setStyle(BUTTON_INACTIVE_STYLE);
        }
        updateButtonStyle(button);
        currentActiveButton = button;
    }

    private void updateButtonStyle(Button button) {
        if (button != null) {
            button.setStyle(BUTTON_ACTIVE_STYLE);
        }
    }

    // ================= NOTIFICATIONS (Đã đồng bộ Database) =================
    private void updateBadge() {
        if (lblNotifBadge == null)
            return;
        try {
            List<THONGBAO> dsThongBao = DAO.ThongBaoDAO.getThongBaoMoiNhat();
            long unread = dsThongBao.stream().filter(n -> !n.isDaDoc()).count();

            lblNotifBadge.setText(String.valueOf(unread));
            lblNotifBadge.setVisible(unread > 0);
            lblNotifBadge.setManaged(unread > 0);
        } catch (Exception e) {
            System.err.println("Lỗi cập nhật số lượng thông báo: " + e.getMessage());
        }
    }

    /**
     * Thêm thông báo trực tiếp vào Database
     */
    public void addNotification(String title, String message, String type) {
        if (title == null || message == null)
            return;

        try {
            // Giả định hàm themThongBao nhận các tham số này.
            // Nếu model THONGBAO cần tham số khác, hãy điều chỉnh cho phù hợp.
            DAO.ThongBaoDAO.themThongBao(title, message, type);

            updateBadge();

            if (notifPopup != null && notifPopup.isShowing()) {
                refreshNotifList();
            }
            System.out.println("✓ Notification saved to DB: " + title);
        } catch (Exception e) {
            System.err.println("Lỗi lưu thông báo vào DB: " + e.getMessage());
        }
    }

    @FXML
    private void onBellClick(MouseEvent e) {
        if (notifPopup == null) {
            buildNotifPopup();
        }

        if (notifPopup.isShowing()) {
            notifPopup.hide();
        } else {
            refreshNotifList();
            var bounds = bellPane.localToScreen(bellPane.getBoundsInLocal());
            notifPopup.show(
                    bellPane.getScene().getWindow(),
                    bounds.getMaxX() + NOTIF_POPUP_OFFSET_X,
                    bounds.getMaxY() + NOTIF_POPUP_OFFSET_Y);
        }
        e.consume();
    }

    private void buildNotifPopup() {
        notifPopup = new Popup();
        notifPopup.setAutoHide(true);

        VBox root = new VBox();
        root.setPrefWidth(NOTIF_POPUP_WIDTH);
        root.setStyle(
                "-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box,rgba(0,0,0,0.18),20,0,0,6); -fx-border-color: #e5e7eb; -fx-border-radius: 12;");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 16, 14, 16));
        header.setStyle("-fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;");

        VBox titleBox = new VBox(3);
        Label hTitle = new Label("🔔 Thông báo");
        hTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        Label unreadLbl = new Label();
        unreadLbl.setStyle("-fx-font-size: 11; -fx-text-fill: #6b7280;");
        titleBox.getChildren().addAll(hTitle, unreadLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button markAllBtn = new Button("✓ Đọc tất cả");
        markAllBtn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #16a34a; -fx-cursor: hand; -fx-border-color: #16a34a; -fx-border-radius: 6;");
        markAllBtn.setOnAction(e -> {
            try {
                DAO.ThongBaoDAO.danhDauDaDocTatCa(); // Cập nhật trực tiếp xuống DB
                refreshNotifList();
                updateBadge();
            } catch (Exception ex) {
                System.err.println("Lỗi khi đánh dấu đọc tất cả: " + ex.getMessage());
            }
        });

        header.getChildren().addAll(titleBox, spacer, markAllBtn);

        notifListBox = new VBox();
        ScrollPane scroll = new ScrollPane(notifListBox);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(NOTIF_POPUP_HEIGHT);
        scroll.setStyle("-fx-background-color: white; -fx-background: white; -fx-border-color: transparent;");

        Button viewAll = new Button("Xem tất cả thông báo →");
        viewAll.setStyle("-fx-background-color: transparent; -fx-text-fill: #16a34a; -fx-cursor: hand;");
        HBox footer = new HBox(viewAll);
        footer.setAlignment(Pos.CENTER);
        footer.setStyle("-fx-border-color: #f3f4f6; -fx-border-width: 1 0 0 0; -fx-padding: 10;");

        notifListBox.getProperties().put("unreadLabel", unreadLbl);
        root.getChildren().addAll(header, scroll, footer);
        notifPopup.getContent().add(root);
    }

    private void refreshNotifList() {
        if (notifListBox == null)
            return;
        notifListBox.getChildren().clear();

        List<THONGBAO> dsThongBao = DAO.ThongBaoDAO.getThongBaoMoiNhat();

        long unread = dsThongBao.stream().filter(n -> !n.isDaDoc()).count();

        lblNotifBadge.setText(String.valueOf(unread));
        lblNotifBadge.setVisible(unread > 0);
        lblNotifBadge.setManaged(unread > 0);

        Label ul = (Label) notifListBox.getProperties().get("unreadLabel");
        if (ul != null)
            ul.setText(unread + " thông báo chưa đọc");

        for (THONGBAO n : dsThongBao) {
            notifListBox.getChildren().add(makeNotifItem(n));
        }
    }

    private HBox makeNotifItem(THONGBAO n) {
        HBox row = new HBox(12);
        row.setPadding(new Insets(12, 16, 12, 16));

        row.setStyle(n.isDaDoc()
                ? "-fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0; -fx-cursor: hand;"
                : "-fx-background-color: #f0fdf4; -fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0; -fx-cursor: hand;");

        VBox content = new VBox(3);
        HBox.setHgrow(content, Priority.ALWAYS);

        Label titleLbl = new Label(n.getTieuDe());
        titleLbl.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

        Label msg = new Label(n.getNoiDung());
        msg.setWrapText(true);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        Label time = new Label(sdf.format(n.getThoiGian()));
        time.setStyle("-fx-text-fill: #9ca3af;");

        content.getChildren().addAll(titleLbl, msg, time);
        row.getChildren().add(content);

        row.setOnMouseClicked(e -> {
            if (!n.isDaDoc()) {
                DAO.ThongBaoDAO.danhDauDaDoc(n.getMaTB());
                refreshNotifList();
            }
        });

        return row;
    }

    // ================= SEARCH =================
    @FXML
    private void onSearchKeyReleased(KeyEvent e) {
        String term = tfSearch.getText().trim().toLowerCase();

        if (term.isEmpty()) {
            if (searchPopup != null) {
                searchPopup.hide();
            }
            return;
        }

        if (searchPopup == null) {
            searchPopup = new Popup();
            searchPopup.setAutoHide(true);
        }
        searchPopup.getContent().clear();
        searchPopup.getContent().add(buildSearchPanel(term));

        if (!searchPopup.isShowing()) {
            var bounds = tfSearch.localToScreen(tfSearch.getBoundsInLocal());
            searchPopup.show(
                    tfSearch.getScene().getWindow(),
                    bounds.getMinX(),
                    bounds.getMaxY() + SEARCH_POPUP_OFFSET_Y);
        }
    }

    private VBox buildSearchPanel(String term) {
        VBox root = new VBox();
        root.setPrefWidth(SEARCH_POPUP_WIDTH);
        root.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box,rgba(0,0,0,0.15),16,0,0,5);");

        HBox header = new HBox();
        header.setPadding(new Insets(10, 14, 10, 14));
        header.getChildren().add(new Label("Kết quả cho \"" + term + "\""));
        root.getChildren().add(header);

        boolean hasAny = false;
        DAO.DatSanDAO dsDAO = new DAO.DatSanDAO();
        javafx.collections.ObservableList<Model.DATSAN> datsans = dsDAO.getallDatsans();
        
        int count = 0;
        for (Model.DATSAN ds : datsans) {
            boolean match = false;
            if (ds.getTenKH() != null && ds.getTenKH().toLowerCase().contains(term)) match = true;
            if (ds.getSdtKH() != null && ds.getSdtKH().contains(term)) match = true;
            if (ds.getMaDS() != null && ds.getMaDS().toLowerCase().contains(term)) match = true;
            if (ds.getMaHD() != null && ds.getMaHD().toLowerCase().contains(term)) match = true;

            if (match) {
                hasAny = true;
                String info = (ds.getTenKH() != null ? ds.getTenKH() : "Khách hàng") 
                            + (ds.getSdtKH() != null ? " - " + ds.getSdtKH() : "") 
                            + " (Sân: " + (ds.getTenSan() != null ? ds.getTenSan() : ds.getMaSan()) + ")";
                
                Button btn = new Button(info);
                btn.setMaxWidth(Double.MAX_VALUE);
                btn.setAlignment(Pos.CENTER_LEFT);
                btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 8 14;");
                btn.setOnAction(e -> {
                    tfSearch.setText("");
                    searchPopup.hide();
                    // Điều hướng đến trang Sân cầu lông (CourtSchedule)
                    updateActiveButton(btnSanCauLong);
                    navigateTo("/View/CourtScheduleUI.fxml");
                });
                root.getChildren().add(btn);
                
                count++;
                if (count >= 5) break;
            }
        }

        if (!hasAny) {
            Label none = new Label("Không tìm thấy kết quả");
            none.setPadding(new Insets(14));
            none.setStyle("-fx-text-fill: #9ca3af; -fx-padding: 8 12;");
            root.getChildren().add(none);
        }
        return root;
    }

    // ================= PROFILE & LOGOUT =================
    @FXML
    private void onProfileClick(MouseEvent e) {
        Popup popup = new Popup();
        popup.setAutoHide(true);

        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box,rgba(0,0,0,0.15),16,0,0,5);");
        card.setAlignment(Pos.CENTER);

        String displayName = LuuThongTinDangNhap.hoTen != null ? LuuThongTinDangNhap.hoTen : "Nhân viên";
        if (LuuThongTinDangNhap.chucVu != null) {
            displayName += " - " + LuuThongTinDangNhap.chucVu;
        }
        Label name = new Label(displayName);
        name.setStyle("-fx-font-weight: bold;");

        Button logout = new Button("Đăng xuất");
        logout.setStyle("-fx-background-color: #fef2f2; -fx-text-fill: #ef4444; -fx-cursor: hand;");
        logout.setOnAction(ev -> {
            popup.hide();
            viewCache.clear(); // Xóa sạch bộ nhớ đệm UI khi đăng xuất

            Stage stage = getStage();
            if (stage != null) {
                stage.close();
                // Tại đây bạn có thể gọi màn hình Đăng nhập (LoginUI) lên lại
            }
        });

        card.getChildren().addAll(name, new Separator(), logout);
        popup.getContent().add(card);

        var bounds = bellPane.localToScreen(bellPane.getBoundsInLocal());
        popup.show(bellPane.getScene().getWindow(), bounds.getMaxX() + 20, bounds.getMaxY() - 10);
    }

    // ================= WINDOW MANAGEMENT =================
    @FXML
    private void onTitleBarPressed(MouseEvent e) {
        Stage stage = getStage();
        if (stage != null) {
            dragOffsetX = e.getScreenX() - stage.getX();
            dragOffsetY = e.getScreenY() - stage.getY();
        }
    }

    @FXML
    private void onTitleBarDragged(MouseEvent e) {
        Stage stage = getStage();
        if (stage != null && !maximized) {
            stage.setX(e.getScreenX() - dragOffsetX);
            stage.setY(e.getScreenY() - dragOffsetY);
        }
    }

    @FXML
    private void onMinimize() {
        Stage stage = getStage();
        if (stage != null) {
            stage.setIconified(true);
        }
    }

    private double normalX, normalY, normalWidth, normalHeight;

    @FXML
    private void onMaximize() {
        Stage s = getStage();
        if (s == null) return;
        
        maximized = !maximized;
        if (maximized) {
            // Lưu lại kích thước cũ
            normalX = s.getX();
            normalY = s.getY();
            normalWidth = s.getWidth();
            normalHeight = s.getHeight();
            
            // Lấy kích thước màn hình không bao gồm taskbar
            javafx.geometry.Rectangle2D bounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            s.setX(bounds.getMinX());
            s.setY(bounds.getMinY());
            s.setWidth(bounds.getWidth());
            s.setHeight(bounds.getHeight());
            btnMaximize.setText("\u2750");
        } else {
            // Khôi phục kích thước cũ
            s.setX(normalX);
            s.setY(normalY);
            s.setWidth(normalWidth);
            s.setHeight(normalHeight);
            btnMaximize.setText("\u25A1");
        }
    }

    @FXML
    private void onClose() {
        cleanup();
        Stage stage = getStage();
        if (stage != null) {
            stage.close();
        }
    }

    @FXML
    private void onWinBtnHover(MouseEvent e) {
        Button btn = (Button) e.getSource();
        btn.setStyle(BUTTON_HOVER_STYLE);
    }

    @FXML
    private void onCloseBtnHover(MouseEvent e) {
        Button btn = (Button) e.getSource();
        btn.setStyle(BUTTON_CLOSE_HOVER_STYLE);
    }

    @FXML
    private void onWinBtnExit(MouseEvent e) {
        Button btn = (Button) e.getSource();
        btn.setStyle(BUTTON_EXIT_STYLE);
    }

    // ================= MODAL DIALOGS =================
    // ================= MODAL DIALOGS =================
    @FXML
    private void onDatSanClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ThemDatSan.fxml"));
            Parent root = loader.load();

            // LẤY CONTROLLER CỦA POPUP VÀ TRUYỀN CALLBACK
            // (Đảm bảo bạn đã import Controller.ThemDatSanController ở đầu file)
            ThemDatSanController controller = loader.getController();
            controller.setCallback(() -> {
                renderLichSapToi();       // Cập nhật lịch sắp tới
                renderTrangThaiSan();     // Cập nhật trạng thái sân
                updateBadge();            // Cập nhật chuông thông báo
                viewCache.remove("/View/CourtScheduleUI.fxml"); // Xóa cache trang sân để load lại
            });

            Stage stage = new Stage();
            stage.setTitle("Thêm Đặt Sân Mới");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            System.err.println("Error opening booking dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onXemLichClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/CourtScheduleUI.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Lịch đặt sân");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            renderLichSapToi();
            updateBadge();
        } catch (Exception e) {
            System.err.println("Error opening booking dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onThemDichVuClick() {
        handleSidebarClick(btnDichVu, "/View/DichVuUI.fxml");
    }

    @FXML
    private void onQuanLyKhachHangClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/CustomerManagementUI.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Quản Lý Khách Hàng");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            System.err.println("✗ Error opening customer dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getTimeFromMaKG(String maKG) {
        if (maKG == null) return "00:00";
        switch (maKG) {
            case "KG001": return "06:00";
            case "KG002": return "07:30";
            case "KG003": return "09:00";
            case "KG004": return "10:30";
            case "KG005": return "13:00";
            case "KG006": return "14:30";
            case "KG007": return "16:00";
            case "KG008": return "17:30";
            case "KG009": return "19:00";
            case "KG010": return "20:30";
            default: return "00:00";
        }
    }

    private java.time.LocalTime parseTime(String timeStr) {
        try {
            return java.time.LocalTime.parse(timeStr);
        } catch (Exception e) {
            return java.time.LocalTime.MIN;
        }
    }

    private void renderTrangThaiSan() {
        if (vboxTrangThaiSanList == null) return;
        vboxTrangThaiSanList.getChildren().clear();

        java.util.List<Model.SAN> allCourts = Model.SAN.getDanhSachSanTuDB();
        DAO.DatSanDAO dsDAO = new DAO.DatSanDAO();
        javafx.collections.ObservableList<Model.DATSAN> allBookings = dsDAO.getallDatsans();
        
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalTime now = java.time.LocalTime.now();

        int sanTrong = 0;
        int dangSuDung = 0;

        for (Model.SAN san : allCourts) {
            Model.DATSAN currentBooking = null;
            Model.DATSAN nextBooking = null;

            for (Model.DATSAN ds : allBookings) {
                if (ds.getMaSan() != null && ds.getMaSan().equals(san.MaSan) && today.equals(ds.getNgayDat())) {
                    if ("DaHuy".equalsIgnoreCase(ds.getTrangThai())) continue;
                    
                    String startTimeStr = getTimeFromMaKG(ds.getMaKG());
                    java.time.LocalTime startTime = parseTime(startTimeStr);
                    java.time.LocalTime endTime = startTime.plusMinutes(90);

                    if (now.isAfter(startTime) && now.isBefore(endTime)) {
                        currentBooking = ds;
                    } else if (startTime.isAfter(now)) {
                        if (nextBooking == null || startTime.isBefore(parseTime(getTimeFromMaKG(nextBooking.getMaKG())))) {
                            nextBooking = ds;
                        }
                    }
                }
            }

            HBox row = new HBox();
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8; -fx-padding: 12;");
            
            javafx.scene.shape.Circle circle = new javafx.scene.shape.Circle(6.0);
            VBox leftCol = new VBox();
            leftCol.setStyle("-fx-padding: 0 0 0 12;");
            Label lblSan = new Label(san.TenSan != null ? san.TenSan : san.MaSan);
            lblSan.setStyle("-fx-text-fill: #111827;");
            
            Label lblSubLeft = new Label("");
            lblSubLeft.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            VBox rightCol = new VBox();
            rightCol.setAlignment(Pos.CENTER_RIGHT);
            Label lblStatus = new Label("");
            lblStatus.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 12px;");
            Label lblSubRight = new Label("");
            lblSubRight.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");

            if ("BaoTri".equalsIgnoreCase(san.TrangThai)) {
                circle.setFill(javafx.scene.paint.Color.valueOf("#eab308"));
                lblStatus.setText("Bảo trì");
            } else if (currentBooking != null) {
                circle.setFill(javafx.scene.paint.Color.valueOf("#ef4444"));
                lblSubLeft.setText(currentBooking.getTenKH() != null ? currentBooking.getTenKH() : "Khách hàng");
                lblStatus.setText("Đang sử dụng");
                if (nextBooking != null) {
                    lblSubRight.setText("Tiếp: " + getTimeFromMaKG(nextBooking.getMaKG()));
                }
                dangSuDung++;
            } else {
                circle.setFill(javafx.scene.paint.Color.valueOf("#22c55e"));
                lblStatus.setText("Trống");
                if (nextBooking != null) {
                    lblSubRight.setText("Tiếp: " + getTimeFromMaKG(nextBooking.getMaKG()));
                }
                sanTrong++;
            }

            leftCol.getChildren().addAll(lblSan);
            if (!lblSubLeft.getText().isEmpty()) leftCol.getChildren().add(lblSubLeft);
            
            rightCol.getChildren().add(lblStatus);
            if (!lblSubRight.getText().isEmpty()) rightCol.getChildren().add(lblSubRight);

            row.getChildren().addAll(circle, leftCol, spacer, rightCol);
            vboxTrangThaiSanList.getChildren().add(row);
        }

        if (lblSanTrong != null) lblSanTrong.setText(sanTrong + "/" + allCourts.size());
        if (lblDangSuDung != null) lblDangSuDung.setText(dangSuDung + "/" + allCourts.size());
    }

    private void renderLichSapToi() {
        if (vboxLichSapToi == null) {
            System.err.println(" vboxLichSapToi is null");
            return;
        }

        vboxLichSapToi.getChildren().clear();

        DAO.DatSanDAO dsDAO = new DAO.DatSanDAO();
        javafx.collections.ObservableList<Model.DATSAN> allBookings = dsDAO.getallDatsans();
        
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalTime now = java.time.LocalTime.now();

        java.util.List<Model.DATSAN> upcoming = new java.util.ArrayList<>();
        int todayTotal = 0;

        for (Model.DATSAN ds : allBookings) {
            if (today.equals(ds.getNgayDat()) && !"DaHuy".equalsIgnoreCase(ds.getTrangThai())) {
                todayTotal++;
                String timeStr = getTimeFromMaKG(ds.getMaKG());
                java.time.LocalTime t = parseTime(timeStr);
                if (t.plusMinutes(90).isAfter(now)) {
                    upcoming.add(ds);
                }
            }
        }

        upcoming.sort(java.util.Comparator.comparing(ds -> parseTime(getTimeFromMaKG(ds.getMaKG()))));

        if (upcoming.isEmpty()) {
            Label lblTrong = new Label("Chưa có lịch đặt nào sắp tới.");
            lblTrong.setStyle("-fx-text-fill: #9ca3af; -fx-font-style: italic; -fx-padding: 10 0;");
            vboxLichSapToi.getChildren().add(lblTrong);
        } else {
            for (Model.DATSAN ds : upcoming) {
                String gio = getTimeFromMaKG(ds.getMaKG());
                String tenKhach = ds.getTenKH() != null ? ds.getTenKH() : "Khách hàng";
                String tenSan = ds.getTenSan() != null ? ds.getTenSan() : (ds.getMaSan() != null ? ds.getMaSan() : "Sân");
                String thoiLuong = "1.5h";

                String[] arr = { gio, tenKhach, tenSan, thoiLuong };
                vboxLichSapToi.getChildren().add(createScheduleRow(arr));
            }
        }

        if (lblLichHomNay != null) lblLichHomNay.setText(String.valueOf(todayTotal));
        
        try {
            DAO.HoaDonDAO hdDAO = new DAO.HoaDonDAO();
            Long doanhThu = hdDAO.getTongDoanhThuByNgay(today);
            if (doanhThu == null) doanhThu = 0L;
            
            if (lblDoanhThu != null) {
                if (doanhThu >= 1000000) {
                    double dtM = doanhThu / 1000000.0;
                    lblDoanhThu.setText(String.format("%.1fM", dtM));
                } else if (doanhThu >= 1000) {
                    double dtK = doanhThu / 1000.0;
                    lblDoanhThu.setText(String.format("%.0fK", dtK));
                } else {
                    lblDoanhThu.setText(String.valueOf(doanhThu));
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi tính doanh thu: " + e.getMessage());
        }
    }

    private HBox createScheduleRow(String[] lich) {
        HBox row = new HBox();
        row.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8; -fx-cursor: hand;");
        row.setPadding(new Insets(12));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(16);

        Label lblGio = new Label(lich[0]);
        lblGio.setStyle(
                "-fx-background-color: #dcfce7; -fx-text-fill: #16a34a; -fx-padding: 8 12; -fx-background-radius: 8; -fx-font-weight: bold; -fx-font-size: 13px;");

        VBox centerInfo = new VBox(4);
        Label lblTenKhach = new Label(lich[1]);
        lblTenKhach.setStyle("-fx-font-weight: bold; -fx-text-fill: #1f2937; -fx-font-size: 13px;");

        Label lblTenSan = new Label(lich[2]);
        lblTenSan.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11px;");

        centerInfo.getChildren().addAll(lblTenKhach, lblTenSan);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblThoiLuong = new Label(lich[3]);
        lblThoiLuong.setStyle(
                "-fx-background-color: #dbeafe; -fx-text-fill: #1d4ed8; -fx-padding: 4 8; -fx-background-radius: 4; -fx-font-size: 11px; -fx-font-weight: bold;");

        row.setOnMouseEntered(
                e -> row.setStyle("-fx-background-color: #f3f4f6; -fx-background-radius: 8; -fx-cursor: hand;"));
        row.setOnMouseExited(
                e -> row.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8; -fx-cursor: hand;"));

        row.getChildren().addAll(lblGio, centerInfo, spacer, lblThoiLuong);
        return row;
    }

    // ================= UTILITY METHODS =================
    private Stage getStage() {
        if (titleBar != null && titleBar.getScene() != null) {
            return (Stage) titleBar.getScene().getWindow();
        }
        return null;
    }

    private void cleanup() {
        try {
            if (notifPopup != null) {
                notifPopup.hide();
            }
            if (searchPopup != null) {
                searchPopup.hide();
            }
            viewCache.clear();
            System.out.println("✓ Cleanup completed");
        } catch (Exception e) {
            System.err.println("⚠ Error during cleanup: " + e.getMessage());
        }
    }

    private void startAutoUpdate() {
        // Cài đặt chu kỳ quét: Ví dụ 10 giây 1 lần
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
            try {
                // 1. Cập nhật lại số lượng chuông thông báo
                updateBadge();

                // 2. Nếu người dùng đang mở bảng thông báo thì làm mới nội dung bên trong
                if (notifPopup != null && notifPopup.isShowing()) {
                    refreshNotifList();
                }

                renderTrangThaiSan();
                renderLichSapToi();

            } catch (Exception e) {
                System.err.println("Lỗi khi tự động cập nhật dữ liệu: " + e.getMessage());
            }
        }));

        // Cho vòng lặp chạy vô hạn
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void setupEventListeners() {
        EventBus.subscribe(eventType -> {
            if (EventBus.EVENT_INVOICE_PAID.equals(eventType) || 
                EventBus.EVENT_INVOICE_CREATED.equals(eventType) || 
                EventBus.EVENT_SERVICE_ORDER_CREATED.equals(eventType)) {
                
                System.out.println("🔔 DashboardController received: " + eventType);
                javafx.application.Platform.runLater(() -> {
                    renderTrangThaiSan();
                    renderLichSapToi(); // Updates revenue and upcoming schedule
                });
            }
        });
    }
}