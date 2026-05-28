package com.mycompany.mavenproject1;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class ManHinhChinhController implements Initializable {

    @FXML private Label lblHoTen;
    @FXML private Label lblDiem;
    @FXML private Label lblHang;
    @FXML private Label lblTenKH;
    @FXML private Label lblMaKH;
    @FXML private Label lblHangSidebar;
    @FXML private Label lblDiemSidebar;
    @FXML private Label lblTrangChu;
    @FXML private Label lblCaiDat;
    @FXML private Label lblConLai;
    @FXML private ProgressBar progressBar;
    @FXML private AnchorPane sidebar;

    private boolean sidebarDangMo = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        sidebar.setTranslateX(-251);
        sidebar.setMouseTransparent(true);

         sidebar.sceneProperty().addListener((obs, oldScene, newScene) -> {
        if (newScene != null) {
            sidebar.setTranslateX(-251);
            sidebar.setMouseTransparent(true);
        }
    });

        
        
        
        lblHoTen.setText(LuuThongTinDangNhap.hoTen + "!");
        lblDiem.setText(String.valueOf(LuuThongTinDangNhap.diemTichLuy));
        lblHang.setText(LuuThongTinDangNhap.hangThanhVien);
        lblTenKH.setText(LuuThongTinDangNhap.hoTen);
        lblMaKH.setText("Mã: " + LuuThongTinDangNhap.maKH);
        lblHangSidebar.setText("Hạng: " + LuuThongTinDangNhap.hangThanhVien);
        lblDiemSidebar.setText(LuuThongTinDangNhap.diemTichLuy + " điểm");

        int diem = LuuThongTinDangNhap.diemTichLuy;
        int diemToiDa;
        if      (diem <= 100) diemToiDa = 100;
        else if (diem <= 200) diemToiDa = 200;
        else if (diem <= 300) diemToiDa = 300;
        else                  diemToiDa = diem;

        progressBar.setProgress((double) diem / diemToiDa);

        int diemConLai = diemToiDa - diem;
        lblConLai.setText(diem >= 301
                ? "Bạn đã đạt hạng cao nhất!"
                : "Còn " + diemConLai + " điểm đến hạng tiếp theo");

        setActiveMenu(lblTrangChu);
    }

    private void setActiveMenu(Label selected) {
        lblTrangChu.getStyleClass().remove("menu-item-active");
        lblCaiDat.getStyleClass().remove("menu-item-active");
        selected.getStyleClass().add("menu-item-active");
    }

    private void goTo(String fxml) {
        try {
            App.setRoot(fxml);
        } catch (IOException e) {
            System.err.println("Lỗi chuyển màn hình " + fxml + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void toggleSidebar(MouseEvent event) {
        event.consume();
        TranslateTransition slide = new TranslateTransition(Duration.millis(300), sidebar);
        if (!sidebarDangMo) {
            slide.setToX(0);
            sidebarDangMo = true;
            sidebar.setMouseTransparent(false); // ✅ Mở sidebar → cho phép click
        } else {
            slide.setToX(-251);
            sidebarDangMo = false;
            sidebar.setMouseTransparent(true);  // ✅ Đóng sidebar → không chặn click
        }
        slide.play();
    }

    @FXML
    private void handleTrangChu(MouseEvent event) {
        event.consume();
        setActiveMenu(lblTrangChu);
        goTo("ManHinhChinh");
    }

    @FXML
    private void handleCaiDat(MouseEvent event) {
        event.consume();
        setActiveMenu(lblCaiDat);
        goTo("CaiDat");
    }

    @FXML
    private void handleDangXuat(MouseEvent event) {
        event.consume();
        LuuThongTinDangNhap.hoTen         = null;
        LuuThongTinDangNhap.maKH          = null;
        LuuThongTinDangNhap.diemTichLuy   = 0;
        LuuThongTinDangNhap.hangThanhVien = null;
        goTo("primary");
    }

    @FXML
    private void handleBangGia(MouseEvent event) {
        event.consume();
        goTo("BangGia");
    }

    @FXML
    private void handleDatSan(MouseEvent event) {
        event.consume();
        goTo("DatSan");
    }

    @FXML
    private void handleDatDichVu(MouseEvent event) {
        event.consume();
        goTo("ChonDichVu");
    }

    @FXML
    private void handleLichSu(MouseEvent event) {
        event.consume();
        goTo("LichSuDS");
    }

    @FXML
    private void handleThongTinSan(MouseEvent event) {
        event.consume();
        goTo("ThongTinSan");
    }
}