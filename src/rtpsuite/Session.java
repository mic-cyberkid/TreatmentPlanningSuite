package rtpsuite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Session {
    private static User activeUser;
    private static Connection dbConnection;
    private static String patient_id;

    
    public static User getUser() {
        return activeUser;
    }

    public static void setUser(User authenticatedUser) {
        Session.activeUser = authenticatedUser;
    }

    public static Connection getDbConnection() {
		return dbConnection;
    }

    public static void setDbConnection(Connection dbConn) {
		Session.dbConnection = dbConn;
    }
    
    public static void setPatientId(String ID) {
		Session.patient_id = ID;
    }
    
    public static String getPatientId() {
		return patient_id;
    }
    
}