package rtpsuite;

/**
 *
 * @author Michael Okyere
 */
public class LinacData {
    String username, temperature_values, pressure_values, neg3_values,zero_values, pos3_values,negC3,c_zero,posC3,error;
    String calculation_date;

    public String getCalculation_date() {
        return calculation_date;
    }

    public void setCalculation_date(String calculation_date) {
        this.calculation_date = calculation_date;
    }
    public String getTemperature_values() {
        return temperature_values;
    }

    public String getPressure_values() {
        return pressure_values;
    }

    public String getNeg3_values() {
        return neg3_values;
    }

    public String getZero_values() {
        return zero_values;
    }

    public String getPos3_values() {
        return pos3_values;
    }

    public String getNegC3() {
        return negC3;
    }

    public String getC_zero() {
        return c_zero;
    }

    public String getPosC3() {
        return posC3;
    }

    public String getError() {
        return error;
    }

    public LinacData(String username, String temperature_values, String pressure_values, 
            String neg3_values, String zero_values,
            String pos3_values, String negC3,
            String c_zero, String posC3, String error, String date) {
        this.username = username;
        this.temperature_values = temperature_values;
        this.pressure_values = pressure_values;
        this.neg3_values = neg3_values;
        this.zero_values = zero_values;
        this.pos3_values = pos3_values;
        this.negC3 = negC3;
        this.c_zero = c_zero;
        this.posC3 = posC3;
        this.error = error;
        this.calculation_date = date;
                
    }

    public String getUsername() {
        return username;
    }


  
    
}
