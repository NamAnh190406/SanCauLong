package com.mycompany.mavenproject1;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class TaiKhoanModel {
    private String maTK;
    private String username;
    private String password;
    private String vaiTro;
    private String trangThai;
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    public TaiKhoanModel(String maTK, String username, String password, String vaiTro, String trangThai) {
        this.maTK = maTK;
        this.username = username;
        this.password = password;
        this.vaiTro = vaiTro;
        this.trangThai = trangThai;
    }

    public String getMaTK() { return maTK; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getVaiTro() { return vaiTro; }
    public String getTrangThai() { return trangThai; }

    public BooleanProperty selectedProperty() { return selected; }
    public boolean isSelected() { return selected.get(); }
    public void setSelected(boolean selected) { this.selected.set(selected); }
}