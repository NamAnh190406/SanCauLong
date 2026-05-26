package Utils;

import DAO.HoaDonDAO;
import Model.HOADON;
import javafx.collections.ObservableList;

public class TestGetAllHoaDon {
    public static void main(String[] args) {
        System.out.println("Bắt đầu gọi getAllHoaDon...");
        try {
            HoaDonDAO dao = new HoaDonDAO();
            ObservableList<HOADON> list = dao.getAllHoaDon();
            System.out.println("Kích thước danh sách lấy được: " + list.size());
            for (HOADON hd : list) {
                System.out.println("- " + hd.getMaHD() + ", MaDS: " + hd.getMaDS() + ", Loai: " + hd.getLoaiHD());
            }
        } catch (Exception e) {
            System.err.println("LỖI NGOẠI LỆ:");
            e.printStackTrace();
        }
    }
}
