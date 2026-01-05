package rtpsuite;

/**
 *
 * @author Michael Okyere
 */
public class DataLog {
    String username, patient_id, calculation_type, calculation_values, calculation_date;

    public String getUsername() {
        return username;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public String getCalculation_type() {
        return calculation_type;
    }

    public String getCalculation_values() {
        return calculation_values;
    }

    public String getCalculation_date() {
        return calculation_date;
    }

    public DataLog(String username, String patient_id, String calculation_type, String calculation_values, String calculation_date) {
        this.username = username;
        this.patient_id = patient_id;
        this.calculation_type = calculation_type;
        this.calculation_values = calculation_values;
        this.calculation_date = calculation_date;
    }
}
