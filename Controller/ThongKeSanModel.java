package com.mycompany.mavenproject1;

import javafx.beans.property.*;

public class ThongKeSanModel {
    private final StringProperty tenSan = new SimpleStringProperty();
    private final StringProperty loaiSan = new SimpleStringProperty();
    private final IntegerProperty soLuot = new SimpleIntegerProperty();
    private final LongProperty doanhThu = new SimpleLongProperty();

    public ThongKeSanModel(String tenSan, String loaiSan, int soLuot, long doanhThu) {
        this.tenSan.set(tenSan);
        this.loaiSan.set(loaiSan);
        this.soLuot.set(soLuot);
        this.doanhThu.set(doanhThu);
    }

    public StringProperty tenSanProperty() { return tenSan; }
    public StringProperty loaiSanProperty() { return loaiSan; }
    public IntegerProperty soLuotProperty() { return soLuot; }
    public LongProperty doanhThuProperty() { return doanhThu; }
}