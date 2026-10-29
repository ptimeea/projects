import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = getEnv("DB_URL", "jdbc:postgresql://localhost:5432/airline_reservation_database");
    private static final String USER = getEnv("DB_USER", "postgres");
    private static final String PASSWORD = getEnv("DB_PASSWORD", "postgres");

    private static String getEnv(String key, String fallback) {
        String value = System.getenv(key);
        return (value == null || value.isEmpty()) ? fallback : value;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Test conexiunea
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Conexiune realizata cu succes!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
