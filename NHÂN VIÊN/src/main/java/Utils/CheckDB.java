package Utils;

import java.sql.Connection;
import java.sql.ResultSet;

public class CheckDB {
    public static void main(String[] args) {
        Databasehelper db = new Databasehelper();
        try (Connection con = db.createCon()) {
            ResultSet rs = con.getMetaData().getColumns(null, null, "HOADON", null);
            System.out.println("Cột trong bảng HOADON:");
            while (rs.next()) {
                System.out.println("- " + rs.getString("COLUMN_NAME"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
