package rtpsuite;

/**
 *
 * @author Michael Okyere
 */
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javafx.scene.Node;

import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;

public class FunctionalMethods {

    // md5 hash
    public static String md5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] byteData = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : byteData) {
                sb.append(String.format("%02x", b));

            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void updateUserInDatabase(User user) {
        String updateQuery = "UPDATE users SET password = ?, role = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
            pstmt.setString(1, user.getPassword());
            pstmt.setInt(1, user.getRole());
            pstmt.setString(3, user.getUsername());

            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load LINAC DATA
    public static ArrayList<LinacData> fetchLinacLogs() {
        try (Statement stmt = DatabaseConnection.getInstance().getConnection().createStatement()) {
            ArrayList<LinacData> dataLogs = new ArrayList<>();
            ResultSet result = stmt.executeQuery("select * from qa_table order by qa_date desc");
            while (result.next()) {
                dataLogs.add(new LinacData(
                        result.getString("username"),
                        result.getString("temperature_values"),
                        result.getString("pressure_values"),
                        result.getString("neg3_values"),
                        result.getString("zero_values"),
                        result.getString("pos3_values"),
                        result.getString("negC3"),
                        result.getString("c_zero"),
                        result.getString("posC3"),
                        result.getString("error"),
                        result.getString("qa_date")
                ));

            }
            return dataLogs;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList();

        }
    }

    // Load Data LOG
     public static ArrayList<DataLog> fetchDataLogs() {
        try (Statement stmt = DatabaseConnection.getInstance().getConnection().createStatement()) {
            ArrayList<DataLog> dataLogs = new ArrayList<>();
            ResultSet result = stmt.executeQuery("select * from datalog");
            while (result.next()) {
                String username = result.getString("username");
                String calculation_values = result.getString("calculation_values");
                dataLogs.add(new DataLog(result.getString("username"),
                        result.getString("patient_id"),
                        result.getString("calculation_type"),
                        result.getString("calculation_values"),
                        result.getString("calculation_date"))
                );

            }
            return dataLogs;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList();

        }
    }

    // Load user DATA
    public static ArrayList<User> fetchUsers() {
        try (Statement stmt = DatabaseConnection.getInstance().getConnection().createStatement()) {
            ArrayList<User> userList = new ArrayList<>();
            ResultSet result = stmt.executeQuery("select * from users");
            while (result.next()) {
                userList.add(new User(result.getString("username"), result.getString("password"),
                        result.getInt("role"), result.getInt("user_id")));

            }
            return userList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList();

        }
    }
    
    //Fetch all logs
        // Fetch User Calculation History
    public static ArrayList<DataLog> fetchAllLogs(){
        try{ 
            ArrayList<DataLog> dataLogs = new ArrayList<>();
           
            PreparedStatement dataLogstmt = DatabaseConnection.getInstance().getConnection().prepareStatement("select * from datalog");
            
            ResultSet dataLogResult = dataLogstmt.executeQuery();
            
            PreparedStatement linacLogstmt = DatabaseConnection.getInstance().getConnection().prepareStatement("select * from qa_table");
            
            ResultSet linacLogResult = linacLogstmt.executeQuery();
            
            while(dataLogResult.next()){
               
                dataLogs.add(new DataLog(dataLogResult.getString("username"), 
                        dataLogResult.getString("patient_id"), 
                        dataLogResult.getString("calculation_type"),
                        dataLogResult.getString("calculation_values"),
                        dataLogResult.getString("calculation_date"))
                );
                
            }
            
            while(linacLogResult.next()){
                 StringBuilder CalculationValues = new StringBuilder();
                            // Final answers
                CalculationValues.append("Temperature T1,T2 = ").append(linacLogResult.getString("temperature_values")).append("\n");
                CalculationValues.append("Pressure P1,P2 = ").append(linacLogResult.getString("pressure_values")).append("\n");
                
                CalculationValues.append("Neg31, Neg32 = ").append(linacLogResult.getString("neg3_values")).append("\n");
                
                CalculationValues.append("Zero_1,Zero_2 = ").append(linacLogResult.getString("zero_values")).append("\n");
                CalculationValues.append("Pos3_1, Pos3_2 = ").append(linacLogResult.getString("pos3_values")).append("\n");
                CalculationValues.append("C3 = ").append(linacLogResult.getString("posC3")).append("\n");
                CalculationValues.append("Co = ").append(linacLogResult.getString("c_zero")).append("\n");
                CalculationValues.append("Cneg = ").append(linacLogResult.getString("negC3")).append("\n");         
                CalculationValues.append("Error = ").append(linacLogResult.getString("error")).append("% \n");    
                dataLogs.add(new DataLog(linacLogResult.getString("username"), 
                        "None", 
                        "Qualitity Assurance",
                        CalculationValues.toString(),
                        linacLogResult.getString("qa_date"))
                );
                
            }
            return dataLogs;
        }catch(Exception e){
            e.printStackTrace();
            return new ArrayList();
            
            
        }
    }

    // Fetch User Calculation History
    public static ArrayList<DataLog> fetchUserLogs(String username) {
        try {
            ArrayList<DataLog> dataLogs = new ArrayList<>();

            PreparedStatement dataLogstmt = DatabaseConnection.getInstance().getConnection().prepareStatement("select * from datalog where username = ?");
            dataLogstmt.setString(1, username);
            ResultSet dataLogResult = dataLogstmt.executeQuery();

            PreparedStatement linacLogstmt = DatabaseConnection.getInstance().getConnection().prepareStatement("select * from qa_table where username = ?");
            linacLogstmt.setString(1, username);
            ResultSet linacLogResult = linacLogstmt.executeQuery();

            while (dataLogResult.next()) {

                dataLogs.add(new DataLog(dataLogResult.getString("username"),
                        dataLogResult.getString("patient_id"),
                        dataLogResult.getString("calculation_type"),
                        dataLogResult.getString("calculation_values"),
                        dataLogResult.getString("calculation_date"))
                );

            }

            while (linacLogResult.next()) {
                StringBuilder CalculationValues = new StringBuilder();
                // Final answers
                CalculationValues.append("Temperature T1,T2 = ").append(linacLogResult.getString("temperature_values")).append("\n");
                CalculationValues.append("Pressure P1,P2 = ").append(linacLogResult.getString("pressure_values")).append("\n");

                CalculationValues.append("Neg31, Neg32 = ").append(linacLogResult.getString("neg3_values")).append("\n");

                CalculationValues.append("Zero_1,Zero_2 = ").append(linacLogResult.getString("zero_values")).append("\n");
                CalculationValues.append("Pos3_1, Pos3_2 = ").append(linacLogResult.getString("pos3_values")).append("\n");
                CalculationValues.append("C3 = ").append(linacLogResult.getString("posC3")).append("\n");
                CalculationValues.append("Co = ").append(linacLogResult.getString("c_zero")).append("\n");
                CalculationValues.append("Cneg = ").append(linacLogResult.getString("negC3")).append("\n");
                CalculationValues.append("Error = ").append(linacLogResult.getString("error")).append("% \n");
                dataLogs.add(new DataLog(linacLogResult.getString("username"),
                        "None",
                        "Qualitity Assurance",
                        CalculationValues.toString(),
                        linacLogResult.getString("qa_date"))
                );

            }
            return dataLogs;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList();

        }
    }

    // Fetch all logs [ Coul reuse the function above but I enjoy typing plenty (o-*) haha
    // Fetch User Calculation History
    public static ArrayList<LinacData> fetchAllLinacLogs() {
        try {
            ArrayList<LinacData> dataLogs = new ArrayList<>();

            PreparedStatement linacLogstmt = DatabaseConnection.getInstance().getConnection().prepareStatement("select * from qa_table");

            ResultSet linacLogResult = linacLogstmt.executeQuery();

            while (linacLogResult.next()) {

                dataLogs.add(new LinacData(
                        linacLogResult.getString("username"),
                        linacLogResult.getString("temperature_values"),
                        linacLogResult.getString("pressure_values"),
                        linacLogResult.getString("neg3_values"),
                        linacLogResult.getString("zero_values"),
                        linacLogResult.getString("pos3_values"),
                        linacLogResult.getString("negC3"),
                        linacLogResult.getString("c_zero"),
                        linacLogResult.getString("posC3"),
                        linacLogResult.getString("error"),
                        linacLogResult.getString("qa_date")
                ));

            }
            return dataLogs;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList();

        }
    }

    //Export data
    public static void printData(Node node) {
        // Create a PrinterJob
        PrinterJob printerJob = PrinterJob.createPrinterJob();

        if (printerJob != null && printerJob.showPrintDialog(node.getScene().getWindow())) {
            // Print the TableView
            /*
            double scaleX = printerJob.getJobSettings().getPageLayout().getPrintableWidth()/ node.getBoundsInParent().getWidth();
            double scaleY = printerJob.getJobSettings().getPageLayout().getPrintableHeight() / node.getBoundsInParent().getHeight();
            node.setScaleX(scaleX);
            node.setScaleY(scaleY);
             */

            boolean success = printerJob.printPage(node);
            if (success) {
                printerJob.endJob(); // End the job if printing was successful
            }

            // node.setScaleX(1.0);
            // node.setScaleY(1.0);
        }
    }

    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
