/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Utils.Databasehelper;
import Model.DichVu;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Hi
 */
public class DichVuDAO {
    private Connection con;
    private Databasehelper connectDB;

    public DichVuDAO() throws SQLException {
        connectDB = new Databasehelper();
        con = connectDB.createCon();
    }

    public ObservableList<DichVu> getAllDichVu() {
        ObservableList<DichVu> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM DICHVU ORDER BY MADV";
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String ma = rs.getString("MaDV");
                String ten = rs.getString("TenDV");

                String loai = "Khác";
                try {
                    loai = rs.getString("LoaiDV");
                } catch (Exception e) {
                    try {
                        loai = rs.getString("DonViTinh");
                    } catch (Exception e2) {
                    }
                }

                long gia = 0;
                try {
                    gia = rs.getLong("DonGia");
                } catch (Exception e) {
                    try {
                        gia = rs.getLong("GiaBan");
                    } catch (Exception e2) {
                    }
                }

                int sl = 0;
                try {
                    sl = rs.getInt("SoLuongTon");
                } catch (Exception e) {
                    try {
                        sl = rs.getInt("SLTonkho");
                    } catch (Exception e2) {
                    }
                }

                String status = "Hoạt động";
                try {
                    status = rs.getString("TrangThai");
                } catch (Exception e) {
                }

                DichVu dv = new DichVu(ma, ten, loai, gia, sl, status);
                list.add(dv);
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Loi lay du lieu" + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public boolean addDichVu(DichVu dv) {
        String sql = "Insert into DICHVU(MaDV, TENDV, GIABAN, DONVITINH, SLTONKHO) values(?,?,?,?,?)";
        try {
            if (getDichVuByMa(dv.getMaDV()) != null) {
                System.out.println("Mã dịch vụ đã tồn tại!");
                return false;
            }
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, dv.getMaDV().trim());
            pstmt.setString(2, dv.getTenDV().trim());
            pstmt.setLong(3, dv.getGiaBan());
            pstmt.setString(4, dv.getLoaiDV() != null ? dv.getLoaiDV().trim() : "Khác");
            pstmt.setInt(5, dv.getSoLuongTon());

            int result = pstmt.executeUpdate();
            pstmt.close();
            System.out.println("Thêm dịch vụ thành công!");
            return result > 0;
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Lỗi thêm dịch vụ: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDichVu(DichVu dv) {
        String sql = "UPDATE DICHVU SET TENDV = ?, GIABAN = ?, DONVITINH = ?, SLTONKHO = ? WHERE MADV = ?";
        try {
            PreparedStatement pstmt = con.prepareCall(sql);
            pstmt.setString(1, dv.getTenDV().trim());
            pstmt.setLong(2, dv.getGiaBan());
            pstmt.setString(3, dv.getLoaiDV() != null ? dv.getLoaiDV().trim() : "Khác");
            pstmt.setInt(4, dv.getSoLuongTon());
            pstmt.setString(5, dv.getMaDV().trim());

            int result = pstmt.executeUpdate();
            pstmt.close();
            System.out.println("Sửa dịch vụ thành công");
            return result > 0;
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Lỗi sửa dịch vụ: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletaDichVu(String maDV) {
        String sql = "DELETE FROM DICHVU WHERE MADV = ?";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maDV.trim());

            int result = pstmt.executeUpdate();
            pstmt.close();

            System.out.println("Xóa dịch vụ thành công");
            return result > 0;
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Lỗi xóa dịch vụ: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

    }

    public DichVu getDichVuByMa(String maDV) {
        String sql = "SELECT * FROM DICHVU WHERE MADV = ?";

        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maDV.trim());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String ma = rs.getString("MaDV");
                String ten = rs.getString("TenDV");

                String loai = "Khác";
                try {
                    loai = rs.getString("LoaiDV");
                } catch (Exception e) {
                    try {
                        loai = rs.getString("DonViTinh");
                    } catch (Exception e2) {
                    }
                }

                long gia = 0;
                try {
                    gia = rs.getLong("DonGia");
                } catch (Exception e) {
                    try {
                        gia = rs.getLong("GiaBan");
                    } catch (Exception e2) {
                    }
                }

                int sl = 0;
                try {
                    sl = rs.getInt("SoLuongTon");
                } catch (Exception e) {
                    try {
                        sl = rs.getInt("SLTonkho");
                    } catch (Exception e2) {
                    }
                }

                String status = "Hoạt động";
                try {
                    status = rs.getString("TrangThai");
                } catch (Exception e) {
                }

                return new DichVu(ma, ten, loai, gia, sl, status);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi tìm kiếm: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void closeCon() throws SQLException {
        connectDB.closeCon(con);
    }
}
