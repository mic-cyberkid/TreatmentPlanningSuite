package rtpsuite;

public class Patient {
	
	String Name;
	int patient_id;

	
	public Patient(String name, int patient_id) {
		super();
		Name = name;
		this.patient_id = patient_id;
	}
	
	
	private String getName() {
		return Name;
	}
	private int getPatient_id() {
		return patient_id;
	}
	
	
	

}
