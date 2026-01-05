package rtpsuite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.scene.control.Alert;
import static rtpsuite.FunctionalMethods.showAlert;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private final String url = "jdbc:sqlite:src/db/radiotherapysuite_db.db"; // Adjust the path as necessary

    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(url);
            
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error Connecting To Database.",e.getMessage());
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url);
                showAlert(Alert.AlertType.INFORMATION, "Connected To Database.","Re-established connection to SQLite.");
                
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.INFORMATION, "Connection To Database.","Error re-establishing connection:" + e.getMessage());
        }
        return connection;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error.","Error closing connection: " + e.getMessage());
                
            }
        }
    }
}
