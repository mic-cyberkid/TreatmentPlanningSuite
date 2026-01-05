package rtpsuite;

public class User {

	String username;
        String password;

    
	int role;
	int ID;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
	
	
	public String getUsername() {
		return username;
	}
	
	public int getRole() {
		return role;
	}
	
	public int getID() {
		return ID;
	}
        
        public String getPassword() {
            return password;
        }
	
	
	
	public User(String username, int role, int iD) {
		super();
		this.username = username;
		this.role = role;
		ID = iD;
	}
        
        public User(String username, String password, int role, int iD) {
		super();
		this.username = username;
                this.password = password;
		this.role = role;
		ID = iD;
	}
        
        public User(){
            
        }
        
	
	
	
}
