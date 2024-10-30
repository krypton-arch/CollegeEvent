package college;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionTest {
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/user_management";
        String jdbcUser = "root";
        String jdbcPass = "root";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
            if (connection != null) {
                System.out.println("Connected to the database!");
            }
        } catch (SQLException e) {
            System.out.println("Unable to connect to the database. Error: " + e.getMessage());
        }
    }
}
