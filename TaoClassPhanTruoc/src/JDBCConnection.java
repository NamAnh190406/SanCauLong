import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCConnection {
    public static void main(String[] args) {
        // 1. Thông số kết nối (Thay đổi theo máy của bạn)
        String url = "jdbc:oracle:thin:@localhost:1521:XE"; // xe là SID
        String username = "C##SANCAULONG";
        String password = "123";

        try {
            // 2. Đăng ký Driver (Với bản ojdbc mới có thể bỏ qua bước này)
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // 3. Thiết lập kết nối
            Connection conn = DriverManager.getConnection(url, username, password);

            if (conn != null) {
                System.out.println("Kết nối đến Oracle thành công!");
                // Bạn có thể tiếp tục thực thi các câu lệnh SQL ở đây
            }

        } catch (ClassNotFoundException e) {
            System.out.println("Không tìm thấy Driver JDBC!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối hoặc sai thông số!");
            e.printStackTrace();
        }
    }
}
