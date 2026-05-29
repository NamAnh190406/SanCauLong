package Controller;

import Service.ChartService;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;

public class ThongKeController implements Initializable {

    @FXML private Label lblFilterLabel;
    @FXML private Button btnCa, btnNgay, btnThang, btnQuy;
    @FXML private Label lblTotalRevenue, lblTotalBookings, lblTotalCustomers, lblAvgRevenue;
    @FXML private Label lblChartRevTitle, lblChartBkTitle;
    @FXML private BarChart<String, Number> chartRevenue;
    @FXML private LineChart<String, Number> chartBookings;
    @FXML private PieChart chartCourtUsage;
    @FXML private PieChart chartServiceRevenue;
    @FXML private BarChart<String, Number> chartPeakHours;
    @FXML private TableView<StatRow> tableDetail;
    @FXML private TableColumn<StatRow, String> colPeriod;
    @FXML private TableColumn<StatRow, String> colRevenue;
    @FXML private TableColumn<StatRow, String> colBookings;
    @FXML private TableColumn<StatRow, String> colCustomers;
    @FXML private TableColumn<StatRow, String> colAvg;

    private final NumberFormat fmt = NumberFormat.getInstance(new Locale("vi", "VN"));
    private Button[] filterButtons;
    private final ChartService chartService = new ChartService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        filterButtons = new Button[]{btnCa, btnNgay, btnThang, btnQuy};
        setupTable();
        setActiveFilter(btnNgay, "Theo Ngày");
        loadDataTheoNgay();
    }

    @FXML private void onFilterCa()    { setActiveFilter(btnCa,    "Theo Ca");    loadDataTheoNgay(); }
    @FXML private void onFilterNgay()  { setActiveFilter(btnNgay,  "Theo Ngày");  loadDataTheoNgay(); }
    @FXML private void onFilterThang() { setActiveFilter(btnThang, "Theo Tháng"); loadDataTheoThang(); }
    @FXML private void onFilterQuy()   { setActiveFilter(btnQuy,   "Theo Quý");   loadDataTheoQuy(); }
    @FXML private void onExportPDF()   {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Chức năng xuất báo cáo PDF đang phát triển!");
        alert.showAndWait();
    }

    private void setActiveFilter(Button active, String label) {
        String activeStyle = "-fx-background-color: white; -fx-text-fill: #16a34a; " +
                "-fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 5 14;";
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: #6b7280; " +
                "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 5 14;";
        for (Button btn : filterButtons) {
            btn.setStyle(btn == active ? activeStyle : inactiveStyle);
        }
        if (lblFilterLabel != null) lblFilterLabel.setText(label);
    }

    private void setupTable() {
        colPeriod.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name()));
        colRevenue.setCellValueFactory(c -> new SimpleStringProperty(fmt.format(c.getValue().revenue()) + "đ"));
        colBookings.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().bookings())));
        colCustomers.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().customers())));
        colAvg.setCellValueFactory(c -> {
            long avg = c.getValue().bookings() > 0 ? c.getValue().revenue() / c.getValue().bookings() : 0;
            return new SimpleStringProperty(fmt.format(avg) + "đ");
        });
    }

    private void loadDataTheoNgay() {
        lblChartRevTitle.setText("Doanh Thu Theo Ngày");
        lblChartBkTitle.setText("Lượt Đặt Sân Theo Ngày");
        lblTotalRevenue.setText(fmt.format(2150000) + "đ");
        lblTotalBookings.setText("24");
        lblTotalCustomers.setText("18");
        lblAvgRevenue.setText(fmt.format(89500) + "đ");

        List<StatRow> data = Arrays.asList(
            new StatRow("T2", 500000, 5, 4),
            new StatRow("T3", 750000, 8, 6),
            new StatRow("T4", 400000, 4, 3),
            new StatRow("T5", 1200000, 12, 10),
            new StatRow("T6", 900000, 10, 8),
            new StatRow("T7", 2150000, 24, 18),
            new StatRow("CN", 2500000, 30, 25)
        );
        renderCharts(data);
    }

    private void loadDataTheoThang() {
        lblChartRevTitle.setText("Doanh Thu Theo Tháng");
        lblChartBkTitle.setText("Lượt Đặt Sân Theo Tháng");
        lblTotalRevenue.setText(fmt.format(45000000) + "đ");
        lblTotalBookings.setText("320");
        lblTotalCustomers.setText("85");
        lblAvgRevenue.setText(fmt.format(140625) + "đ");

        List<StatRow> data = Arrays.asList(
            new StatRow("T1", 3200000, 22, 15),
            new StatRow("T2", 2800000, 18, 12),
            new StatRow("T3", 4100000, 28, 20),
            new StatRow("T4", 3900000, 26, 18),
            new StatRow("T5", 5200000, 35, 28),
            new StatRow("T6", 4800000, 32, 25),
            new StatRow("T7", 6100000, 42, 35),
            new StatRow("T8", 5800000, 38, 30),
            new StatRow("T9", 4200000, 28, 22),
            new StatRow("T10", 3800000, 25, 18),
            new StatRow("T11", 5600000, 37, 29),
            new StatRow("T12", 5500000, 39, 32)
        );
        renderCharts(data);
    }

    private void loadDataTheoQuy() {
        lblChartRevTitle.setText("Doanh Thu Theo Quý");
        lblChartBkTitle.setText("Lượt Đặt Sân Theo Quý");
        lblTotalRevenue.setText(fmt.format(180000000) + "đ");
        lblTotalBookings.setText("1250");
        lblTotalCustomers.setText("180");
        lblAvgRevenue.setText(fmt.format(144000) + "đ");

        List<StatRow> data = Arrays.asList(
            new StatRow("Quý 1", 40000000, 280, 95),
            new StatRow("Quý 2", 45000000, 320, 110),
            new StatRow("Quý 3", 52000000, 380, 130),
            new StatRow("Quý 4", 43000000, 270, 90)
        );
        renderCharts(data);
    }

    private void renderCharts(List<StatRow> data) {
        chartRevenue.getData().clear();
        chartRevenue.getData().add(chartService.createRevenueBarSeries(data));

        chartBookings.getData().clear();
        chartBookings.getData().add(chartService.createBookingsLineSeries(data));

        chartCourtUsage.setData(chartService.getCourtUsageData());
        chartServiceRevenue.setData(chartService.getServiceRevenueData());

        chartPeakHours.getData().clear();
        chartPeakHours.getData().add(chartService.createPeakHoursSeries());

        tableDetail.setItems(FXCollections.observableArrayList(data));
    }

    public static class StatRow {
        private final String name;
        private final long revenue;
        private final int bookings;
        private final int customers;

        public StatRow(String name, long revenue, int bookings, int customers) {
            this.name = name;
            this.revenue = revenue;
            this.bookings = bookings;
            this.customers = customers;
        }

        public String name() { return name; }
        public long revenue() { return revenue; }
        public int bookings() { return bookings; }
        public int customers() { return customers; }
    }
}