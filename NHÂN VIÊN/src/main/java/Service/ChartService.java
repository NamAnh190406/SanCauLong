package Service;

import Model.StatRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import java.util.List;
import java.util.Map;

public class ChartService {

    // ---------------------------------------------------------------- Bar / Line series

    public XYChart.Series<String, Number> createRevenueBarSeries(List<StatRow> data) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");
        for (StatRow r : data) {
            series.getData().add(new XYChart.Data<>(r.name(), r.revenue() / 1000.0));
        }
        series.getData().forEach(d -> d.nodeProperty().addListener((obs, o, n) -> {
            if (n != null) {
                n.setStyle("-fx-bar-fill: #10b981;");
            }
        }));
        return series;
    }

    public XYChart.Series<String, Number> createBookingsLineSeries(List<StatRow> data) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Lượt đặt");
        for (StatRow r : data) {
            series.getData().add(new XYChart.Data<>(r.name(), r.bookings()));
        }
        return series;
    }

    // ---------------------------------------------------------------- Pie charts (no-arg – default mock data)

    public ObservableList<PieChart.Data> getCourtUsageData() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        data.add(new PieChart.Data("Sân 1  35%", 35));
        data.add(new PieChart.Data("Sân 2  28%", 28));
        data.add(new PieChart.Data("Sân 3  22%", 22));
        data.add(new PieChart.Data("Sân 4  15%", 15));
        return data;
    }

    public ObservableList<PieChart.Data> getServiceRevenueData() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        data.add(new PieChart.Data("Đặt sân  60%",    60));
        data.add(new PieChart.Data("Dịch vụ  25%",    25));
        data.add(new PieChart.Data("Khuyến mãi  15%", 15));
        return data;
    }

    public XYChart.Series<String, Number> createPeakHoursSeries() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Lượt đặt");
        String[] hours = {"06:00","07:30","09:00","10:30","13:00","14:30","16:00","17:30","19:00","20:30"};
        int[]    vals  = {  2,      4,      3,       1,      5,      8,     12,     15,      10,      6  };
        for (int i = 0; i < hours.length; i++) {
            series.getData().add(new XYChart.Data<>(hours[i], vals[i]));
        }
        series.getData().forEach(d -> d.nodeProperty().addListener((obs, o, n) -> {
            if (n != null) {
                n.setStyle("-fx-bar-fill: #f59e0b;");
            }
        }));
        return series;
    }

    // ---------------------------------------------------------------- Map-based overloads

    public ObservableList<PieChart.Data> getCourtUsageData(Map<String, Integer> usageCounts) {
        ObservableList<PieChart.Data> courtData = FXCollections.observableArrayList();
        int total = usageCounts.values().stream().mapToInt(Integer::intValue).sum();
        if (total == 0) total = 1;
        for (Map.Entry<String, Integer> entry : usageCounts.entrySet()) {
            double percent = Math.round((entry.getValue() * 100.0) / total);
            courtData.add(new PieChart.Data(entry.getKey() + " " + percent + "%", entry.getValue()));
        }
        return courtData;
    }

    public ObservableList<PieChart.Data> getServiceRevenueData(Map<String, Long> revMap) {
        ObservableList<PieChart.Data> svcData = FXCollections.observableArrayList();
        long total = revMap.values().stream().mapToLong(Long::longValue).sum();
        if (total == 0) total = 1;
        for (Map.Entry<String, Long> entry : revMap.entrySet()) {
            double percent = Math.round((entry.getValue() * 100.0) / total);
            svcData.add(new PieChart.Data(entry.getKey() + " " + percent + "%", entry.getValue()));
        }
        return svcData;
    }

    public XYChart.Series<String, Number> createPeakHoursSeries(Map<String, Integer> peakMap) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Lượt đặt");
        String[] hours = {"06:00","07:30","09:00","10:30","13:00","14:30","16:00","17:30","19:00","20:30"};
        for (String h : hours) {
            series.getData().add(new XYChart.Data<>(h, peakMap.getOrDefault(h, 0)));
        }
        series.getData().forEach(d -> d.nodeProperty().addListener((obs, o, n) -> {
            if (n != null) {
                n.setStyle("-fx-bar-fill: #f59e0b;");
            }
        }));
        return series;
    }
}