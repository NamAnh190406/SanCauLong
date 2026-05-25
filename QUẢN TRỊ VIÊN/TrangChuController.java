package com.example.guidemo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class TrangChuController {

    @FXML private BorderPane mainBorderPane;

    public static BorderPane rootPane;
    public static javafx.scene.Node dashboardContent;

    @FXML
    public void initialize() {
        rootPane = mainBorderPane;
        dashboardContent = mainBorderPane.getCenter();
    }

    @FXML
    private void handleMoQuanLyNhanVien() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("QLyNhanVien.fxml"));
            Parent quanLyNhanVienView = loader.load();

            mainBorderPane.setCenter(quanLyNhanVienView);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Không tìm thấy file QLyNhanVien.fxml!");
        }
    }

    @FXML
    private void handleMoQuanLyBangGia() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("QLyBangGia.fxml"));
            Parent quanLyBangGiaView = loader.load();

            mainBorderPane.setCenter(quanLyBangGiaView);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Không tìm thấy file QLyBangGia.fxml!");
        }
    }

    @FXML
    private void handleMoQuanLyDichVu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("QLyDichVu.fxml"));
            Parent quanLyDichVuView = loader.load();

            mainBorderPane.setCenter(quanLyDichVuView);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Lỗi: Không tìm thấy tệp tin QLyDichVu.fxml!");
        }
    }

    @FXML
    private void handleMoQuanLyKhachHang() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("QLyKhachHang.fxml"));
            Parent view = loader.load();

            mainBorderPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Không tìm thấy file QLyKhachHang.fxml!");
        }
    }

    @FXML
    private void handleMoQuanLyKhuyenMai() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("QLyKhuyenMai.fxml"));
            Parent view = loader.load();

            if (rootPane != null) {
                rootPane.setCenter(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi hệ thống");
            alert.setHeaderText(null);
            alert.setContentText("Không thể tải giao diện QLyKhuyenMai.fxml! Chi tiết: " + e.getMessage());
            alert.showAndWait();
        }
    }
    @FXML
    private void handleMoQuanLyDanhGia() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("QLyDanhGia.fxml"));
            Parent view = loader.load();
            if (rootPane != null) {
                rootPane.setCenter(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleMoQuanLySanVaKhungGio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("QLySanVaKhungGio.fxml"));
            Parent view = loader.load();

            if (rootPane != null) {
                rootPane.setCenter(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi hệ thống");
            alert.setHeaderText(null);
            alert.setContentText("Không thể tải giao diện QLySanVaKhungGio.fxml! Chi tiết: " + e.getMessage());
            alert.showAndWait();
        }
    }
    @FXML
    private void handleMoQuanLyDatSan() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("QLyDatSan.fxml"));
            Parent view = loader.load();
            if (rootPane != null) {
                rootPane.setCenter(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleMoQuanLyQTV() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("QLyQTV.fxml"));
            Parent view = loader.load();
            if (rootPane != null) {
                rootPane.setCenter(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMoThongKeDoanhThu() {
        try {

            Parent view = FXMLLoader.load(getClass().getResource("ThongKeDoanhThu.fxml"));


            if (rootPane != null) {
                rootPane.setCenter(view);
            } else {
                System.out.println("Lỗi: rootPane đang bị null, chưa thể gán giao diện Thống Kê!");
            }
        } catch (IOException e) {
            System.out.println("Không tìm thấy hoặc lỗi cấu trúc file ThongKeDoanhThu.fxml!");
            e.printStackTrace();
        }
    }
}
