package rtpsuite;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class DataLogger {
	String calculationType;
	String userName;
	String patientId;
	String dateTime;
	String values;

        LinacData linac_data;

    public DataLogger(LinacData linac_data) {
        this.linac_data = linac_data;
    }

	public String getCalculationType() {
		return calculationType;
	}
	public String getUserName() {
		return userName;
	}
	public String getPatientId() {
		return patientId;
	}
	public String getDateTime() {
		return dateTime;
	}
	public String getValues() {
		return values;
	}
	
	
	public DataLogger(String userName, String patientId, String calculationType, String values) {
		super();
		this.calculationType = calculationType;
		this.userName = userName;
		this.patientId = patientId;
		this.values = values;
	}
        
        public void LogQA(){
            User user = Session.getUser();

            //TODO : Modify query to update neccessary fields
            String sql = """
                         INSERT INTO qa_table (username, temperature_values, pressure_values, neg3_values,zero_values, pos3_values,negC3,c_zero,posC3,error)
                          VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""";
            try{
                Connection dbConn = DatabaseConnection.getInstance().getConnection(); 
                PreparedStatement pstmt = dbConn.prepareStatement(sql);
                pstmt.setString(1, linac_data.getUsername()); // User performing the calculation
                pstmt.setString(2, linac_data.getTemperature_values()); // Associated patient
                pstmt.setString(3, linac_data.getPressure_values()); //Calculation summary 
                pstmt.setString(4, linac_data.getNeg3_values()); 
                pstmt.setString(5, linac_data.getZero_values()); //Calculation summary 
                pstmt.setString(6, linac_data.getPos3_values()); //Calculation summary 
                pstmt.setString(7, linac_data.getNegC3()); //Calculation summary 
                pstmt.setString(8, linac_data.getC_zero()); //Calculation summary 
                pstmt.setString(9, linac_data.getPosC3()); //Calculation summary 
                pstmt.setString(10, linac_data.getError()); //Calculation summary 
                
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

	
	public void LogData() {
		// use defined dbCon instead of creating new one
		
		User user = Session.getUser();
		
		String sql = "INSERT INTO datalog (username, patient_id, calculation_type, calculation_values, calculation_date) VALUES (?, ?, ?, ?, ?)";
                try{
                    Connection dbConn = DatabaseConnection.getInstance().getConnection();
                    PreparedStatement pstmt = dbConn.prepareStatement(sql); 
                    pstmt.setString(1, user.getUsername()); // User performing the calculation
                    pstmt.setString(2, this.patientId); // Associated patient
                    pstmt.setString(3, this.calculationType); // Calculation type
                    pstmt.setString(4,this.values); //Calculation summary 
                    pstmt.setString(5, LocalDateTime.now().toString()); // Timestamp
                    pstmt.executeUpdate();
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
		
	}
	

}
