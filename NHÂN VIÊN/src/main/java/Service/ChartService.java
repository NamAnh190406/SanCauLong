package Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import Controller.ThongKeController;
import java.util.List;

public class ChartService {

    private static final String[][] COURT_USAGE = {
            { "Sân 1", "45" }, { "Sân 2", "38" }, { "Sân 3", "42" },
            { "Sân 4", "35" }, { "Sân 5", "28" }, { "Sân 6", "40" }
    };
    private static final String[][] SERVICE_REV = {
            { "Tiền sân", "75" }, { "Đồ uống", "12" }, { "Thuê vợt", "8" }, { "Cầu lông", "5" }
    };
    private static final int[] PEAK = { 3, 5, 4, 6, 5, 4, 3, 4, 5, 6, 7, 8, 10, 12, 11, 9 };
    private static final String[] PEAK_HOURS = { "6h", "7h", "8h", "9h", "10h", "11h", "12h", "13h", "14h", "15h",
            "16h", "17h", "18h", "19h", "20h", "21h" };

    public XYChart.Series<String, Number> createRevenueBarSeries(List<ThongKeController.StatRow> data) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");
        for (ThongKeController.StatRow r : data) {
            series.getData().add(new XYChart.Data<>(r.name(), r.revenue() / 1000.0));
        }
        
        series.getData().forEach(d -> d.nodeProperty().addListener((obs, o, n) -> {
            if (n != null) {
                n.setStyle("-fx-bar-fill: #10b981;");
            }
        }));
        return series;
    }

    public XYChart.Series<String, Number> createBookingsLineSeries(List<ThongKeController.StatRow> data) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Lượt đặt");
        for (ThongKeController.StatRow r : data) {
            series.getData().add(new XYChart.Data<>(r.name(), r.bookings()));
        }
        return series;
    }

    public ObservableList<PieChart.Data> getCourtUsageData() {
        ObservableList<PieChart.Data> courtData = FXCollections.observableArrayList();
        for (String[] entry : COURT_USAGE) {
            courtData.add(new PieChart.Data(entry[0] + " " + entry[1] + "%", Double.parseDouble(entry[1])));
        }
        return courtData;
    }

    public ObservableList<PieChart.Data> getServiceRevenueData() {
        ObservableList<PieChart.Data> svcData = FXCollections.observableArrayList();
        for (String[] entry : SERVICE_REV) {
            svcData.add(new PieChart.Data(entry[0] + " " + entry[1] + "%", Double.parseDouble(entry[1])));
        }
        return svcData;
    }

    public XYChart.Series<String, Number> createPeakHoursSeries() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Lượt đặt");
        for (int i = 0; i < PEAK_HOURS.length; i++) {
            series.getData().add(new XYChart.Data<>(PEAK_HOURS[i], PEAK[i]));
        }
        
        series.getData().forEach(d -> d.nodeProperty().addListener((obs, o, n) -> {
            if (n != null) {
                n.setStyle("-fx-bar-fill: #f59e0b;");
            }
        }));
        return series;
    }
}