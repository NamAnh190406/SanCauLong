package Controller;

import Service.ChartService; // Import ChartService của bạn
import java.net.URL;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;

public class ThongKeController implements Initializable {

    // ================= FXML INJECTIONS =================
    @FXML private Label lblFilterLabel;
    @FXML private Button btnCa, btnNgay, btnThang, btnQuy;
    
    @FXML private Label lblTotalRevenue, lblTotalBookings, lblTotalCustomers, lblAvgRevenue;
    @FXML private Label lblChartRevTitle, lblChartBkTitle;
    
    @FXML private BarChart<String, Number> chartRevenue;
    @FXML private LineChart<String, Number> chartBookings;
    @FXML private PieChart chartCourtUsage;
    @FXML private PieChart chartServiceRevenue;
    @FXML private BarChart<String, Number> chartPeakHours;

    @FXML private TableView<StatRow> tableDetail; // Đổi StatModel thành StatRow cho đồng nhất
    @FXML private TableColumn<StatRow, String> colPeriod;
    @FXML private TableColumn<StatRow, String> colRevenue;
    @FXML private TableColumn<StatRow, String> colBookings;
    @FXML private TableColumn<StatRow, String> colCustomers;
    @FXML private TableColumn<StatRow, String> colAvg;

    private final NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    private Button[] filterButtons;
    
    // Gọi class xử lý Biểu đồ
    private final ChartService chartService = new ChartService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        filterButtons = new Button[]{btnCa, btnNgay, btnThang, btnQuy};
        setupTable();
        loadDataTheoNgay();
    }

    // ... (Giữ nguyên các hàm sự kiện OnFilter() và SetActiveFilter() của bạn) ...

    private void setupTable() {
        colPeriod.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name()));
        colRevenue.setCellValueFactory(c -> new SimpleStringProperty(currencyFormat.format(c.getValue().revenue()) + "đ"));
        colBookings.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().bookings())));
        colCustomers.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().customers())));
        colAvg.setCellValueFactory(c -> {
            long avg = c.getValue().bookings() > 0 ? c.getValue().revenue() / c.getValue().bookings() : 0;
            return new SimpleStringProperty(currencyFormat.format(avg) + "đ");
        });
    }

    private void loadDataTheoNgay() {
        // 1. Cập nhật số liệu Header
        lblTotalRevenue.setText("2.150.000đ");
        lblTotalBookings.setText("24");
        lblTotalCustomers.setText("18");
        lblAvgRevenue.setText("89.500đ");

        // 2. Tạo Mock Data (Sau này sẽ gọi từ class DB của Oracle)
        List<StatRow> mockData = Arrays.asList(
            new StatRow("T2", 500000, 5, 4),
            new StatRow("T3", 750000, 8, 6),
            new StatRow("T4", 400000, 4, 3),
            new StatRow("T5", 1200000, 12, 10),
            new StatRow("T6", 900000, 10, 8),
            new StatRow("T7", 2150000, 24, 18),
            new StatRow("CN", 2500000, 30, 25)
        );

        // 3. Giao việc vẽ biểu đồ cho ChartService
        chartRevenue.getData().clear();
        chartRevenue.getData().add(chartService.createRevenueBarSeries(mockData));

        chartBookings.getData().clear();
        chartBookings.getData().add(chartService.createBookingsLineSeries(mockData));

        chartCourtUsage.setData(chartService.getCourtUsageData());
        chartServiceRevenue.setData(chartService.getServiceRevenueData());

        chartPeakHours.getData().clear();
        chartPeakHours.getData().add(chartService.createPeakHoursSeries());

        // 4. Đổ dữ liệu vào bảng
        tableDetail.setItems(FXCollections.observableArrayList(mockData));
    }
    public static class StatRow {
        private String name;
        private long revenue;
        private int bookings;
        private int customers;

        // Hàm khởi tạo (Constructor) để nhận dữ liệu khi gọi new StatRow(...)
        public StatRow(String name, long revenue, int bookings, int customers) {
            this.name = name;
            this.revenue = revenue;
            this.bookings = bookings;
            this.customers = customers;
        }

        // Các hàm Getter (Lấy dữ liệu ra)
        public String name() { return name; }
        public long revenue() { return revenue; }
        public int bookings() { return bookings; }
        public int customers() { return customers; }
    }
}