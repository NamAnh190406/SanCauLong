package Controller;



import Service.StatisticsService;
import Service.ChartService;
import Model.StatRow;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Enhanced Statistics Controller
 * - Service Layer Pattern
 * - Observable Properties
 * - Async Loading
 * - Better Error Handling
 */
public class ThongKeController implements Initializable {

    // ================================================================ ENUMS &
    // CONSTANTS
    public enum Filter {
        CA("Theo Ca"),
        NGAY("Theo Ngày"),
        THANG("Theo Tháng"),
        QUY("Theo Quý");

        private final String label;

        Filter(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private static final Locale VI = new Locale("vi", "VN");
    private static final NumberFormat NF = NumberFormat.getIntegerInstance(VI);

    // ================================================================ FXML
    // COMPONENTS
    @FXML
    private Label lblFilterLabel, lblTotalRevenue, lblTotalBookings,
            lblTotalCustomers, lblAvgRevenue;
    @FXML
    private Label lblChartRevTitle, lblChartBkTitle;

    @FXML
    private Button btnCa, btnNgay, btnThang, btnQuy;

    @FXML
    private BarChart<String, Number> chartRevenue, chartPeakHours;
    @FXML
    private LineChart<String, Number> chartBookings;
    @FXML
    private PieChart chartCourtUsage, chartServiceRevenue;

    @FXML
    private CategoryAxis revXAxis, bkXAxis, peakXAxis;
    @FXML
    private NumberAxis revYAxis, bkYAxis, peakYAxis;

    @FXML
    private TableView<StatRow> tableDetail;
    @FXML
    private TableColumn<StatRow, String> colPeriod, colRevenue, colBookings, colCustomers, colAvg;

    @FXML
    private ProgressIndicator loadingIndicator;

    // ================================================================ PROPERTIES
    // (Observable)
    private final ObjectProperty<Filter> currentFilterProperty = new SimpleObjectProperty<>(Filter.NGAY);
    private final ListProperty<StatRow> dataProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final BooleanProperty loadingProperty = new SimpleBooleanProperty(false);
    private final ObjectProperty<StatSummary> summaryProperty = new SimpleObjectProperty<>();

    // ================================================================ SERVICES
    private StatisticsService statService;
    private ChartService chartService;

    // ================================================================
    // INITIALIZATION
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Khá»Ÿi táº¡o services
        this.statService = new StatisticsService();
        this.chartService = new ChartService();

        // Setup table
        setupTable();

        // Setup Pie Charts
        setupPieCharts();

        // Setup Peak Hours Chart
        setupPeakChart();

        // Binding for loading indicator
        if (loadingIndicator != null) {
            loadingIndicator.visibleProperty().bind(loadingProperty);
        }

        // Listen to filter changes
        currentFilterProperty.addListener((obs, oldFilter, newFilter) -> {
            applyFilter(newFilter);
        });

        // Listen to data changes
        dataProperty.addListener((obs, oldData, newData) -> {
            if (newData != null) {
                refreshAllCharts(newData);
                refreshTable(newData);
            }
        });

        // Initialize with default filter
        applyFilter(Filter.NGAY);

        setupEventListeners();
    }

    private void setupEventListeners() {
        Controller.EventBus.subscribe(eventType -> {
            if (Controller.EventBus.EVENT_INVOICE_PAID.equals(eventType) || 
                Controller.EventBus.EVENT_INVOICE_CREATED.equals(eventType) || 
                Controller.EventBus.EVENT_SERVICE_ORDER_CREATED.equals(eventType)) {
                
                System.out.println("🔔 ThongKeController received: " + eventType);
                javafx.application.Platform.runLater(() -> {
                    // Re-apply current filter to refresh data
                    applyFilter(currentFilterProperty.get());
                });
            }
        });
    }

    // ================================================================ FILTER
    // HANDLING
    @FXML
    private void onFilterCa() {
        currentFilterProperty.set(Filter.CA);
    }

    @FXML
    private void onFilterNgay() {
        currentFilterProperty.set(Filter.NGAY);
    }

    @FXML
    private void onFilterThang() {
        currentFilterProperty.set(Filter.THANG);
    }

    @FXML
    private void onFilterQuy() {
        currentFilterProperty.set(Filter.QUY);
    }

    private void applyFilter(Filter filter) {
        loadingProperty.set(true);

        Task<List<StatRow>> task = new Task<>() {
            @Override
            protected List<StatRow> call() throws Exception {
                Thread.sleep(300);
                return statService.getStatsByFilter(filter);
            }
        };

        task.setOnSucceeded(e -> {
            List<StatRow> data = task.getValue();
            dataProperty.set(FXCollections.observableArrayList(data));

            updateFilterUI(filter);

            StatSummary summary = calculateSummary(data);
            summaryProperty.set(summary);
            updateSummaryLabels(summary);

            loadingProperty.set(false);
        });

        task.setOnFailed(e -> {
            showError("Lỗi tải dữ liệu", "Không thể tải dữ liệu: " + task.getException().getMessage());
            loadingProperty.set(false);
        });

        new Thread(task).start();
    }

    private void updateFilterUI(Filter filter) {
        String active = "-fx-background-color: #16a34a; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 7 16; -fx-cursor: hand;";
        String inactive = "-fx-background-color: transparent; -fx-text-fill: #374151; "
                + "-fx-background-radius: 8; -fx-padding: 7 16; -fx-cursor: hand;";

        List<Button> buttons = Arrays.asList(btnCa, btnNgay, btnThang, btnQuy);
        List<Filter> filters = Arrays.asList(Filter.CA, Filter.NGAY, Filter.THANG, Filter.QUY);

        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setStyle(filters.get(i) == filter ? active : inactive);
        }

        // Cáº­p nháº­t labels
        if (lblFilterLabel != null)
            lblFilterLabel.setText(filter.getLabel());
        if (lblChartRevTitle != null)
            lblChartRevTitle.setText("Doanh Thu " + filter.getLabel());
        if (lblChartBkTitle != null)
            lblChartBkTitle.setText("Lượt Đặt Sân" + filter.getLabel());
    }

    private StatSummary calculateSummary(List<StatRow> data) {
        long totalRev = data.stream().mapToLong(StatRow::revenue).sum();
        int totalBk = data.stream().mapToInt(StatRow::bookings).sum();
        int totalCust = data.stream().mapToInt(StatRow::customers).sum();
        long avgRev = data.isEmpty() ? 0 : totalRev / data.size();

        return new StatSummary(totalRev, totalBk, totalCust, avgRev);
    }

    private void updateSummaryLabels(StatSummary summary) {
        if (lblTotalRevenue != null)
            lblTotalRevenue.setText(formatCurrency(summary.totalRevenue()) + "đ");
        if (lblTotalBookings != null)
            lblTotalBookings.setText(String.valueOf(summary.totalBookings()));
        if (lblTotalCustomers != null)
            lblTotalCustomers.setText(String.valueOf(summary.totalCustomers()));
        if (lblAvgRevenue != null)
            lblAvgRevenue.setText(formatCurrency(summary.avgRevenue()) + "đ");
    }

    // ================================================================ CHART
    // UPDATES
    private void refreshAllCharts(List<StatRow> data) {
        refreshBarChart(data);
        refreshLineChart(data);
    }

    private void refreshBarChart(List<StatRow> data) {
        if (chartRevenue == null)
            return;

        XYChart.Series<String, Number> series = chartService.createRevenueBarSeries(data);
        chartRevenue.getData().setAll(series);

        if (revYAxis != null)
            revYAxis.setLabel("(nghìn đ)");
    }

    private void refreshLineChart(List<StatRow> data) {
        if (chartBookings == null)
            return;

        XYChart.Series<String, Number> series = chartService.createBookingsLineSeries(data);
        chartBookings.getData().setAll(series);
    }

    private void setupPieCharts() {
        if (chartCourtUsage != null) {
            chartCourtUsage.setData(chartService.getCourtUsageData(statService.getCourtUsageMap()));
        }

        if (chartServiceRevenue != null) {
            chartServiceRevenue.setData(chartService.getServiceRevenueData(statService.getServiceRevenueMap()));
        }
    }

    private void setupPeakChart() {
        if (chartPeakHours != null) {
            XYChart.Series<String, Number> series = chartService.createPeakHoursSeries(statService.getPeakHoursMap());
            chartPeakHours.getData().setAll(series);
        }
    }

    // ================================================================ TABLE
    private void setupTable() {
        if (tableDetail == null)
            return;

        colPeriod.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name()));
        colRevenue.setCellValueFactory(c -> new SimpleStringProperty(NF.format(c.getValue().revenue()) + "đ"));
        colBookings.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().bookings())));
        colCustomers.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().customers())));
        colAvg.setCellValueFactory(c -> new SimpleStringProperty(
                NF.format(c.getValue().revenue() / Math.max(1, c.getValue().bookings())) + "đ"));
        colRevenue.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle(empty ? "" : "-fx-text-fill: #16a34a; -fx-font-weight: bold;");
            }
        });
    }

    private void refreshTable(List<StatRow> data) {
        if (tableDetail == null)
            return;

        ObservableList<StatRow> rows = FXCollections.observableArrayList(data);
        if (!data.isEmpty() && summaryProperty.get() != null) {
            StatSummary summary = summaryProperty.get();
            rows.add(new StatRow("Tổng cộng", summary.totalRevenue(),
                    summary.totalBookings(), summary.totalCustomers()));
        }

        tableDetail.setItems(rows);

        tableDetail.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(StatRow item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null && item.name().equals("Tổng cộng")) {
                    setStyle("-fx-background-color: #dcfce7;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    // ================================================================ PDF EXPORT
    @FXML
    private void onExportPDF() {
        Filter filter = currentFilterProperty.get();
        List<StatRow> data = dataProperty.get();
        StatSummary summary = summaryProperty.get();

        if (data == null || summary == null) {
            showWarning("Cảnh báo", "Không có dữ liệu để xuất");
            return;
        }

        Task<File> exportTask = new Task<>() {
            @Override
            protected File call() throws Exception {
                return generatePDF(filter, data, summary);
            }
        };

        exportTask.setOnSucceeded(e -> {
            File exportedFile = exportTask.getValue();
            showSuccess("Xuất PDF thành công",
                    "File được lưu tại: " + exportedFile.getAbsolutePath());
        });

        exportTask.setOnFailed(e -> {
            showError("Lỗi xuất PDF",
                    "Chi tiết: " + exportTask.getException().getMessage());
        });

        loadingProperty.set(true);
        new Thread(exportTask).start();
    }

    private File generatePDF(Filter filter, List<StatRow> data, StatSummary summary) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            // Sử dụng font Arial hỗ trợ tiếng Việt
            PDType0Font font = PDType0Font.load(doc, new File("C:/Windows/Fonts/arial.ttf"));
            PDType0Font fontBold = PDType0Font.load(doc, new File("C:/Windows/Fonts/arialbd.ttf"));

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float W = page.getMediaBox().getWidth();
                float margin = 40;
                float y = 780;

                // Title
                cs.setFont(fontBold, 18);
                cs.beginText();
                cs.newLineAtOffset(margin, y);
                cs.showText("BÁO CÁO THỐNG KÊ - " + filter.getLabel().toUpperCase());
                cs.endText();
                y -= 22;

                // Date
                cs.setFont(font, 10);
                cs.beginText();
                cs.newLineAtOffset(margin, y);
                cs.showText("Hệ thống quản lý sân cầu lông | Ngày: " +
                        LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                cs.endText();
                y -= 24;

                cs.setStrokingColor(0.2f, 0.7f, 0.4f);
                cs.setLineWidth(1.5f);
                cs.moveTo(margin, y);
                cs.lineTo(W - margin, y);
                cs.stroke();
                y -= 16;

                cs.setFont(fontBold, 12);
                cs.beginText();
                cs.newLineAtOffset(margin, y);
                cs.showText("Tổng quan");
                cs.endText();
                y -= 16;

                cs.setFont(font, 10);
                String[] summaryLines = {
                        "Tổng doanh thu : " + NF.format(summary.totalRevenue()) + " VND",
                        "Lượt đặt sân   : " + summary.totalBookings(),
                        "Khách hàng    : " + summary.totalCustomers(),
                        "Trung bình : " + NF.format(summary.avgRevenue()) + " VND"
                };

                for (String line : summaryLines) {
                    cs.beginText();
                    cs.newLineAtOffset(margin + 10, y);
                    cs.showText(line);
                    cs.endText();
                    y -= 14;
                }
                y -= 10;

                // Table section
                cs.setFont(fontBold, 11);
                cs.beginText();
                cs.newLineAtOffset(margin, y);
                cs.showText("CHI TIẾT");
                cs.endText();
                y -= 16;

                float[] colWidths = { 150, 120, 70, 90, 110 };
                String[] headers = { "Khoảng thời gian", "Doanh thu", "Lượt đặt", "Khách hàng", "TB/lượt" };
                cs.setFont(fontBold, 10);
                drawTableRow(cs, margin, y, colWidths, headers, true);
                y -= 18;

                cs.setFont(font, 9);
                for (StatRow r : data) {
                    String[] row = {
                            r.name(),
                            NF.format(r.revenue()) + "đ",
                            String.valueOf(r.bookings()),
                            String.valueOf(r.customers()),
                            NF.format(r.revenue() / Math.max(1, r.bookings())) + "đ"
                    };
                    drawTableRow(cs, margin, y, colWidths, row, false);
                    y -= 16;
                }

                // Total row
                String[] totalRow = {
                        "TỔNG CỘNG",
                        NF.format(summary.totalRevenue()) + "đ",
                        String.valueOf(summary.totalBookings()),
                        String.valueOf(summary.totalCustomers()),
                        NF.format(summary.avgRevenue()) + "đ"
                };
                cs.setFont(fontBold, 9);
                drawTableRow(cs, margin, y, colWidths, totalRow, false);
                y -= 24;

                // Footer
                cs.setFont(font, 8);
                cs.beginText();
                cs.newLineAtOffset(margin, 30);
                cs.showText("Trang 1/1 | Quản lý sân cầu lông");
                cs.endText();
            }

            // Save file
            String fileName = "bao-cao-" + filter.name().toLowerCase()
                    + "-" + System.currentTimeMillis() + ".pdf";
            File downloadDir = new File(System.getProperty("user.home"), "Downloads");
            downloadDir.mkdirs();
            File outputFile = new File(downloadDir, fileName);
            doc.save(outputFile);

            loadingProperty.set(false);
            return outputFile;
        }
    }

    private void drawTableRow(PDPageContentStream cs, float x, float y,
            float[] colWidths, String[] cells, boolean bold) throws IOException {
        float cx = x;
        for (int i = 0; i < cells.length; i++) {
            cs.beginText();
            cs.newLineAtOffset(cx + 4, y);
            cs.showText(cells[i]);
            cs.endText();
            cx += colWidths[i];
        }
    }

    // ================================================================ HELPERS
    private static String formatCurrency(long value) {
        if (value >= 1_000_000)
            return String.format("%.1fM", value / 1_000_000.0);
        if (value >= 1_000)
            return String.format("%dK", value / 1_000);
        return String.valueOf(value);
    }

    private void showSuccess(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void showWarning(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static class StatSummary {
        private final long totalRevenue;
        private final int totalBookings;
        private final int totalCustomers;
        private final long avgRevenue;

        public StatSummary(long totalRevenue, int totalBookings, int totalCustomers, long avgRevenue) {
            this.totalRevenue = totalRevenue;
            this.totalBookings = totalBookings;
            this.totalCustomers = totalCustomers;
            this.avgRevenue = avgRevenue;
        }

        public long totalRevenue() {
            return totalRevenue;
        }

        public int totalBookings() {
            return totalBookings;
        }

        public int totalCustomers() {
            return totalCustomers;
        }

        public long avgRevenue() {
            return avgRevenue;
        }
    }
}
