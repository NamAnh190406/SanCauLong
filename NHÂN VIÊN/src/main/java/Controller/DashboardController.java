package Controller;

import Model.THONGBAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;

/**
 * Dashboard Controller - Quản lý giao diện chính của hệ thống đặt sân cầu lông
 * 
 * Chức năng:
 * - Quản lý điều hướng giữa các module
 * - Hiển thị thông báo và tìm kiếm
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
    
    private static final String BUTTON_ACTIVE_STYLE = 
        "-fx-background-color: #00c853; " +
        "-fx-text-fill: white; " +
        "-fx-background-radius: 8; " +
        "-fx-font-weight: bold;";
    
    private static final String BUTTON_INACTIVE_STYLE = 
        "-fx-background-color: transparent; " +
        "-fx-text-fill: white; " +
        "-fx-font-weight: normal;";
    
    private static final String BUTTON_HOVER_STYLE = 
        "-fx-background-color: rgba(255,255,255,0.12); " +
        "-fx-text-fill: #94a3b8; " +
        "-fx-cursor: hand;";
    
    private static final String BUTTON_EXIT_STYLE = 
        "-fx-background-color: transparent; " +
        "-fx-text-fill: #94a3b8; " +
        "-fx-cursor: hand; " +
        "-fx-min-width: 46; " +
        "-fx-min-height: 36; " +
        "-fx-background-radius: 0;";
    
    private static final String BUTTON_CLOSE_HOVER_STYLE = 
        "-fx-background-color: #e81123; " +
        "-fx-text-fill: white; " +
        "-fx-cursor: hand;";

    // ================= FXML INJECTIONS =================
    @FXML private HBox titleBar;
    @FXML private Button btnMinimize;
    @FXML private Button btnMaximize;
    @FXML private Button btnClose;

    // Topbar – notification & search
    @FXML private StackPane bellPane;
    @FXML private Label lblNotifBadge;
    @FXML private TextField tfSearch;

    // Center container for navigation
    @FXML private VBox centerContainer;
    
    // Sidebar Buttons
    @FXML private Button btnTrangChu;
    @FXML private Button btnSanCauLong;
    @FXML private Button btnKhachHang;
    @FXML private Button btnHoaDon;
    @FXML private Button btnThongKe;
    @FXML private Button btnThanhToan;
    @FXML private Button btnCaiDat;
    @FXML private VBox vboxLichSapToi;
    @FXML private VBox vboxTrangThaiSan;
    @FXML private Label lblNhanVien;

    // ================= MOCK DATA =================
    private static class Notif {
        int id;
        String title, message, time, type;
        boolean read;

        Notif(int id, String title, String msg, String time, String type, boolean read) {
            this.id = id;
            this.title = title;
            this.message = msg;
            this.time = time;
            this.type = type;
            this.read = read;
        }
    }

    private final List<Notif> notifications = new ArrayList<>(List.of(
            new Notif(1, "Đặt sân mới", "Nguyễn Văn A đã đặt Sân 1 lúc 14:00", "5 phút trước", "info", false),
            new Notif(2, "Thanh toán thành công", "Hóa đơn HD001 đã được thanh toán", "10 phút trước", "success", false),
            new Notif(3, "Sân cần bảo trì", "Sân 5 cần kiểm tra và bảo trì", "1 giờ trước", "warning", true),
            new Notif(4, "Khách hàng mới", "Trần Thị B đã đăng ký thành viên", "2 giờ trước", "info", true)
    ));

    private static final String[][] SEARCH_DATA = {
            { "customer", "Nguyễn Văn A - 0901234567" },
            { "customer", "Trần Thị B - 0907654321" },
            { "invoice", "HD001 - Nguyễn Văn A" },
            { "court", "Sân 1 - 14:00 - Nguyễn Văn A" },
    };

    // Mock schedule data
    private static final String[][] MOCK_SCHEDULE = {
        {"14:00", "Lê Văn C", "Sân 1", "2h"},
        {"15:30", "Phạm Thị D", "Sân 2", "1h"},
        {"18:00", "Hoàng Văn E", "Sân 3", "1.5h"},
        {"19:00", "CLB Cầu Lông", "Sân VIP 1", "3h"}
    };

    // ================= STATE =================
    private double dragOffsetX, dragOffsetY;
    private boolean maximized = false;
    private Button currentActiveButton;
    private Node homeContent; // Biến lưu lại ruột của Trang Chủ để tránh load lại FXML

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
            renderLichSapToi();
            
            System.out.println("✓ Dashboard initialized successfully");
        } catch (Exception e) {
            System.err.println("✗ Error during initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Khởi tạo nội dung Trang Chủ để tránh load lại FXML
     */
    private void initializeHomeContent() {
        if (centerContainer != null && centerContainer.getChildren().size() > 1) {
            homeContent = centerContainer.getChildren().get(1);
            System.out.println("✓ Home content saved");
        } else {
            System.err.println("⚠ Could not save home content - centerContainer or children not ready");
        }
    }

    /**
     * Đặt nút Trang Chủ là nút mặc định khi mở ứng dụng
     */
    private void setDefaultActiveButton() {
        if (btnTrangChu != null) {
            updateButtonStyle(btnTrangChu);
            currentActiveButton = btnTrangChu;
        }
    }

    private void setupNavigation() {
        // Nút Trang Chủ: Phục hồi lại ruột gốc đã lưu
        if (btnTrangChu != null) {
            btnTrangChu.setOnAction(e -> handleHomeClick());
        }

        // Các nút khác: Dùng hàm navigateTo bình thường
        if (btnSanCauLong != null) 
            btnSanCauLong.setOnAction(e -> handleSidebarClick(btnSanCauLong, "/View/SanBadmintonUI.fxml"));
        if (btnKhachHang != null) 
            btnKhachHang.setOnAction(e -> handleSidebarClick(btnKhachHang, "/View/CustomerManagementUI.fxml"));
        if (btnHoaDon != null) 
            btnHoaDon.setOnAction(e -> handleSidebarClick(btnHoaDon, "/View/HoaDonUI.fxml"));
        if (btnThongKe != null) 
            btnThongKe.setOnAction(e -> handleSidebarClick(btnThongKe, "/View/ThongKeUI.fxml"));
        if (btnThanhToan != null) 
            btnThanhToan.setOnAction(e -> handleSidebarClick(btnThanhToan, "/View/ThanhToanUI.fxml"));
        if (btnCaiDat != null) 
            btnCaiDat.setOnAction(e -> handleSidebarClick(btnCaiDat, "/View/CaiDatUI.fxml"));
        
        // FIX: Use setOnMouseClicked for Label instead of setOnAction
        if (lblNhanVien != null) {
            lblNhanVien.setOnMouseClicked(e -> navigateTo("/View/ThongTinCaNhanUI.fxml"));
        }
    }

    /**
     * Xử lý click vào nút Trang Chủ
     */
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

    /**
     * Tải tệp FXML từ đường dẫn
     */
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
     * Điều hướng đến một view mới
     */
    private void navigateTo(String fxmlPath) {
        if (centerContainer == null || fxmlPath == null) {
            System.err.println("✗ Navigation failed: centerContainer or fxmlPath is null");
            return;
        }

        try {
            Node node = loadFXML(fxmlPath);
            VBox.setVgrow(node, Priority.ALWAYS);
            
            // Thay thế nội dung cũ bằng nội dung mới
            if (centerContainer.getChildren().size() > 1) {
                centerContainer.getChildren().set(1, node);
            } else {
                centerContainer.getChildren().add(node);
            }
            
            System.out.println("✓ Navigated to: " + fxmlPath);
        } catch (IOException ex) {
            System.err.println("✗ Error loading view: " + fxmlPath);
            ex.printStackTrace();
        }
    }

    /**
     * Xử lý click vào nút Sidebar
     */
    private void handleSidebarClick(Button clickedButton, String fxmlPath) {
        updateActiveButton(clickedButton);
        navigateTo(fxmlPath);
    }

    /**
     * Cập nhật button được active (highlight màu xanh)
     */
    private void updateActiveButton(Button button) {
        if (currentActiveButton != null) {
            currentActiveButton.setStyle(BUTTON_INACTIVE_STYLE);
        }
        updateButtonStyle(button);
        currentActiveButton = button;
    }

    /**
     * Cập nhật style cho button active
     */
    private void updateButtonStyle(Button button) {
        if (button != null) {
            button.setStyle(BUTTON_ACTIVE_STYLE);
        }
    }

    // ================= NOTIFICATIONS =================
    /**
     * Cập nhật badge số thông báo chưa đọc
     */
    private void updateBadge() {
        if (lblNotifBadge == null) return;
    try {
        List<Model.THONGBAO> dsThongBao = DAO.ThongBaoDAO.getThongBaoMoiNhat();
        long unread = dsThongBao.stream().filter(n -> !n.isDaDoc()).count();
        
        // Hiển thị lên số lượng lên chuông
        lblNotifBadge.setText(String.valueOf(unread));
        lblNotifBadge.setVisible(unread > 0);
        lblNotifBadge.setManaged(unread > 0);
    } catch (Exception e) {
        System.err.println("Lỗi cập nhật số lượng thông báo: " + e.getMessage());
    }
    }

    /**
     * Thêm thông báo mới vào hệ thống
     */
    public void addNotification(String title, String message, String type) {
        if (title == null || message == null || type == null) {
            System.err.println("✗ Notification parameters cannot be null");
            return;
        }
        
        Notif notif = new Notif(
            notifications.size() + 1,
            title,
            message,
            "vừa xong",
            type,
            false
        );
        notifications.add(0, notif);
        updateBadge();
        
        if (notifPopup != null && notifPopup.isShowing()) {
            refreshNotifList();
        }
        
        System.out.println("✓ Notification added: " + title);
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
                bounds.getMaxY() + NOTIF_POPUP_OFFSET_Y
            );
        }
        e.consume();
    }

    private void buildNotifPopup() {
        notifPopup = new Popup();
        notifPopup.setAutoHide(true);

        VBox root = new VBox();
        root.setPrefWidth(NOTIF_POPUP_WIDTH);
        root.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box,rgba(0,0,0,0.18),20,0,0,6); -fx-border-color: #e5e7eb; -fx-border-radius: 12;");

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
        markAllBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #16a34a; -fx-cursor: hand; -fx-border-color: #16a34a; -fx-border-radius: 6;");
        markAllBtn.setOnAction(e -> {
            notifications.forEach(n -> n.read = true);
            refreshNotifList();
            updateBadge();
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
        if (notifListBox == null) return;
        notifListBox.getChildren().clear();

        // Gọi DAO để lấy dữ liệu thật từ DB
        List<Model.THONGBAO> dsThongBao = DAO.ThongBaoDAO.getThongBaoMoiNhat();

        long unread = dsThongBao.stream().filter(n -> !n.isDaDoc()).count();

        // Cập nhật số đếm trên biểu tượng chuông
        lblNotifBadge.setText(String.valueOf(unread));
        lblNotifBadge.setVisible(unread > 0);
        lblNotifBadge.setManaged(unread > 0);

        Label ul = (Label) notifListBox.getProperties().get("unreadLabel");
        if (ul != null) ul.setText(unread + " thông báo chưa đọc");

        // Hiển thị từng thông báo
        for (Model.THONGBAO n : dsThongBao) {
            notifListBox.getChildren().add(makeNotifItem(n));
        }
    }

    private HBox makeNotifItem(THONGBAO n) {
       HBox row = new HBox(12);
    row.setPadding(new Insets(12, 16, 12, 16));
    
    // Đổi màu nền nếu chưa đọc
    row.setStyle(n.isDaDoc() 
        ? "-fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0; -fx-cursor: hand;" 
        : "-fx-background-color: #f0fdf4; -fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0; -fx-cursor: hand;");

    VBox content = new VBox(3);
    HBox.setHgrow(content, Priority.ALWAYS);

    Label titleLbl = new Label(n.getTieuDe());
    titleLbl.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
    
    Label msg = new Label(n.getNoiDung());
    msg.setWrapText(true);
    
    // Format thời gian hiển thị cho đẹp
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
    Label time = new Label(sdf.format(n.getThoiGian()));
    time.setStyle("-fx-text-fill: #9ca3af;");

    content.getChildren().addAll(titleLbl, msg, time);
    row.getChildren().add(content);

    // Xử lý sự kiện click vào 1 thông báo
    row.setOnMouseClicked(e -> {
        if (!n.isDaDoc()) {
            DAO.ThongBaoDAO.danhDauDaDoc(n.getMaTB()); // Update Database
            refreshNotifList(); // Load lại danh sách UI
        }
        
        // Bạn có thể thêm logic ở đây: Nếu click vào "Đặt sân mới", thì chuyển hướng trang sang SanCauLongUI
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
                bounds.getMaxY() + SEARCH_POPUP_OFFSET_Y
            );
        }
    }

    private VBox buildSearchPanel(String term) {
        VBox root = new VBox();
        root.setPrefWidth(SEARCH_POPUP_WIDTH);
        root.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box,rgba(0,0,0,0.15),16,0,0,5);");

        HBox header = new HBox();
        header.setPadding(new Insets(10, 14, 10, 14));
        header.getChildren().add(new Label("Kết quả cho \"" + term + "\""));
        root.getChildren().add(header);

        boolean hasAny = false;
        for (String[] row : SEARCH_DATA) {
            if (row[1].toLowerCase().contains(term)) {
                hasAny = true;
                Button btn = new Button(row[1]);
                btn.setMaxWidth(Double.MAX_VALUE);
                btn.setAlignment(Pos.CENTER_LEFT);
                btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 8 14;");
                btn.setOnAction(e -> {
                    tfSearch.setText("");
                    searchPopup.hide();
                });
                root.getChildren().add(btn);
            }
        }

        if (!hasAny) {
            Label none = new Label("Không tìm thấy kết quả");
            none.setPadding(new Insets(14));
            root.getChildren().add(none);
        }
        return root;
    }

    // ================= PROFILE =================
    @FXML
    private void onProfileClick(MouseEvent e) {
        Popup popup = new Popup();
        popup.setAutoHide(true);

        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box,rgba(0,0,0,0.15),16,0,0,5);");
        card.setAlignment(Pos.CENTER);

        Label name = new Label("Nhân viên");
        name.setStyle("-fx-font-weight: bold;");
        
        Button logout = new Button("Đăng xuất");
        logout.setStyle("-fx-background-color: #fef2f2; -fx-text-fill: #ef4444; -fx-cursor: hand;");
        logout.setOnAction(ev -> {
            popup.hide();
            Stage stage = getStage();
            if (stage != null) {
                stage.close();
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

    @FXML
    private void onMaximize() {
        Stage s = getStage();
        if (s == null) return;
        maximized = !maximized;
        s.setMaximized(maximized);
        btnMaximize.setText(maximized ? "\u2750" : "\u25A1");
    }

    @FXML
    private void onClose() {
        cleanup();
        Stage stage = getStage();
        if (stage != null) {
            stage.close();
        }
    }

    // FIX: Use complete style strings instead of replace()
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
    @FXML
    private void onDatSanClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ThemDatSan.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Thêm Đặt Sân Mới");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.showAndWait();

            renderLichSapToi();
            updateBadge();
        } catch (Exception e) {
            System.err.println("✗ Error opening booking dialog: " + e.getMessage());
            e.printStackTrace();
        }
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

    // ================= SCHEDULE RENDERING =================
    private void renderLichSapToi() {
        if (vboxLichSapToi == null) {
            System.err.println(" vboxLichSapToi is null");
            return;
        }
        
        vboxLichSapToi.getChildren().clear();

        if (MOCK_SCHEDULE == null || MOCK_SCHEDULE.length == 0) {
            Label lblTrong = new Label("Chưa có lịch đặt nào sắp tới.");
            lblTrong.setStyle("-fx-text-fill: #9ca3af; -fx-font-style: italic; -fx-padding: 10 0;");
            vboxLichSapToi.getChildren().add(lblTrong);
            return;
        }

        for (String[] lich : MOCK_SCHEDULE) {
            vboxLichSapToi.getChildren().add(createScheduleRow(lich));
        }
    }

    /**
     * Tạo một dòng lịch đặt sân
     */
    private HBox createScheduleRow(String[] lich) {
        HBox row = new HBox();
        row.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8; -fx-cursor: hand;");
        row.setPadding(new Insets(12));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(16);

        Label lblGio = new Label(lich[0]);
        lblGio.setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #16a34a; -fx-padding: 8 12; -fx-background-radius: 8; -fx-font-weight: bold; -fx-font-size: 13px;");

        VBox centerInfo = new VBox(4);
        Label lblTenKhach = new Label(lich[1]);
        lblTenKhach.setStyle("-fx-font-weight: bold; -fx-text-fill: #1f2937; -fx-font-size: 13px;");
        
        Label lblTenSan = new Label(lich[2]);
        lblTenSan.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11px;");
        
        centerInfo.getChildren().addAll(lblTenKhach, lblTenSan);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblThoiLuong = new Label(lich[3]);
        lblThoiLuong.setStyle("-fx-background-color: #dbeafe; -fx-text-fill: #1d4ed8; -fx-padding: 4 8; -fx-background-radius: 4; -fx-font-size: 11px; -fx-font-weight: bold;");

        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #f3f4f6; -fx-background-radius: 8; -fx-cursor: hand;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8; -fx-cursor: hand;"));

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

    /**
     * Dọn dẹp resources khi đóng ứng dụng
     */
    private void cleanup() {
        try {
            if (notifPopup != null) {
                notifPopup.hide();
            }
            if (searchPopup != null) {
                searchPopup.hide();
            }
            System.out.println("✓ Cleanup completed");
        } catch (Exception e) {
            System.err.println("⚠ Error during cleanup: " + e.getMessage());
        }
    }
}