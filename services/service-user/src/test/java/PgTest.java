import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PgTest {
    public static void main(String[] args) {

        String url = "jdbc:postgresql://localhost:5432/testdb";
        String user = "postgres";
        String password = "123456";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            if (conn != null) {
                System.out.println("连接成功！");
            } else {
                System.out.println("连接失败！");
            }
        } catch (SQLException e) {
            System.out.println("数据库连接异常！");
            e.printStackTrace();
        }
    }
}
