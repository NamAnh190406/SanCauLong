package com.mycompany.mavenproject1;

import javafx.beans.property.*;

public class ThongKeDVModel {
    private final StringProperty tenDV = new SimpleStringProperty();
    private final StringProperty dvt = new SimpleStringProperty();
    private final IntegerProperty soLuong = new SimpleIntegerProperty();
    private final LongProperty doanhThu = new SimpleLongProperty();

    public ThongKeDVModel(String tenDV, String dvt, int soLuong, long doanhThu) {
        this.tenDV.set(tenDV);
        this.dvt.set(dvt);
        this.soLuong.set(soLuong);
        this.doanhThu.set(doanhThu);
    }

    public StringProperty tenDVProperty() { return tenDV; }
    public StringProperty dvtProperty() { return dvt; }
    public IntegerProperty soLuongProperty() { return soLuong; }
    public LongProperty doanhThuProperty() { return doanhThu; }
}