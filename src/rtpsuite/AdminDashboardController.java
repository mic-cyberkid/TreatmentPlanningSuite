package rtpsuite;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;


import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import static rtpsuite.FunctionalMethods.md5;
import static rtpsuite.FunctionalMethods.fetchLinacLogs;
import static rtpsuite.FunctionalMethods.fetchDataLogs;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import static rtpsuite.FunctionalMethods.fetchAllLinacLogs;
import static rtpsuite.FunctionalMethods.fetchUsers;
import static rtpsuite.FunctionalMethods.showAlert;


/**
 * FXML Controller class
 *
 * @author Michael Okyere
 */

public class AdminDashboardController implements Initializable {

    @FXML
    private TableView<LinacData> linacLogTable;
    @FXML
    private TableView<DataLog> TreatmentTimeTable;
    @FXML
    private TabPane adminPanel;
    @FXML
    private Tab linacLogTab;
    @FXML
    private Tab treatmentTimeTab;
    @FXML
    private Label addUserinfoLabel;
    @FXML
    private TextField new_username;
    @FXML
    private PasswordField new_password;
    @FXML
    private ChoiceBox<String> roleBox;
    @FXML
    private Tab addUserTab;
    @FXML
    private Tab homeTab;
    @FXML
    private Label welcomeUserLabel;
    @FXML
    private TextField linacSearchField;
    @FXML
    private TextField linacDateField;
    @FXML
    private TextField dataLogSearchField;
    @FXML
    private TextField dataLogDateField;
    @FXML
    private TextField dataLogPatientSearchField;
    @FXML
    private TextField dataLogCalcTypeField;
    @FXML
    private Tab editTab;
    @FXML
    private Tab exportTab;
    @FXML
    private Tab deleteTab;
    @FXML
    private ChoiceBox<String> usernameBox;
    @FXML
    private TextField deleteUsername;
    @FXML
    private Label delUserinfoLabel;
    @FXML
    private VBox edtUsernames;
    @FXML
    private TextField edtUsername;
    @FXML
    private TextField edtPassword;
    
    
    private ObservableList<DataLog> dataLogs;
    private ObservableList<LinacData> linactLogs;
    private ObservableList<User> userList;
    public User selectedUser;
    
    private Connection dbConn;

    public ArrayList<String> usernames;
    public Button btn;
    @FXML
    private ChoiceBox<String> edtroleBox;
    @FXML
    private Label edtUserInfoLabel;
    
    public ArrayList<String> tabs;
    
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // initialize connection
        tabs = new ArrayList<>();
        User user = Session.getUser();
        if (welcomeUserLabel != null && user != null) {
            welcomeUserLabel.setText(user.getUsername());
        }
        usernameBox = new ChoiceBox();
        if(edtroleBox.getItems().isEmpty()){
                boolean addAll = edtroleBox.getItems().addAll("admin","user");
        } 
        adminPanel.getTabs().clear();
        adminPanel.setMaxSize(0, 6);
        adminPanel.setMinSize(0, 1);
        
        adminPanel.getTabs().add(homeTab);
        tabs.add("homeTabe");
        roleBox.getItems().addAll("User","Admin");
        dbConn = DatabaseConnection.getInstance().getConnection();
        usernames = getUsernames();
        usernameBox.getItems().addAll(usernames);
        
        
        
        
        //Fill linac data
        setUpLinacTable();
        
        //setup datalog
        setUpDataLogTable();
        //setup edituser
        setupUsernameBox();
        
             
   
       
    } 
    
    // Method to get all usernames
    public ArrayList<String> getUsernames(){
        ArrayList<String> usernames = new ArrayList<>();
        String query = "SELECT username from users";
        try {
            Statement stmt = DatabaseConnection.getInstance().getConnection().createStatement();
            ResultSet result = stmt.executeQuery(query);
            while(result.next()){
                usernames.add(result.getString("username"));
            }
            
            return usernames;
        } catch (Exception e) {
            return usernames;
        }
    }
    
    
    
    
    // Set up the TableView and filter mechanism
    private void setUpLinacTable(){
        linacSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterLinacTable();
        });
        linacDateField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterLinacTable();
        });
        
        TableColumn<LinacData, String> nameColumn = new TableColumn<>("UserName");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<LinacData, String> dateColumn = new TableColumn<>("Calculation Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("calculation_date"));
        
        TableColumn<LinacData, String> temperatureColumn = new TableColumn<>("Temperature Values");
        temperatureColumn.setCellValueFactory(new PropertyValueFactory<>("temperature_values"));
        
        TableColumn<LinacData, String> pressureColumn = new TableColumn<>("Pressure Values");
        pressureColumn.setCellValueFactory(new PropertyValueFactory<>("pressure_values"));
        
        TableColumn<LinacData, String> neg3Column = new TableColumn<>("neg3_values");
        neg3Column.setCellValueFactory(new PropertyValueFactory<>("neg3_values"));
        
        TableColumn<LinacData, String> zeroColumn = new TableColumn<>("zero_values");
        zeroColumn.setCellValueFactory(new PropertyValueFactory<>("zero_values"));
        
        TableColumn<LinacData, String> pos3Column = new TableColumn<>("pos3_values");
        pos3Column.setCellValueFactory(new PropertyValueFactory<>("pos3_values"));
        
        TableColumn<LinacData, String> negC3Column = new TableColumn<>("negC3_values");
        negC3Column.setCellValueFactory(new PropertyValueFactory<>("negC3"));
        
        TableColumn<LinacData, String> c_zeroColumn = new TableColumn<>("c_zero");
        c_zeroColumn.setCellValueFactory(new PropertyValueFactory<>("c_zero"));
        
        TableColumn<LinacData, String> posC3Column = new TableColumn<>("posC3");
        posC3Column.setCellValueFactory(new PropertyValueFactory<>("posC3"));
        
        TableColumn<LinacData, String> errorColumn = new TableColumn<>("error");
        errorColumn.setCellValueFactory(new PropertyValueFactory<>("error"));
        
        
        
        
        linactLogs = FXCollections.observableArrayList(fetchLinacLogs());
        
       
        linacLogTable.getColumns().addAll(
                nameColumn, dateColumn, temperatureColumn,
                pressureColumn,neg3Column,zeroColumn,
                pos3Column,negC3Column,c_zeroColumn,
                posC3Column,errorColumn
        );
        linacLogTable.setItems(linactLogs);
    }
    
    //Set up Other Calcualtions table and filter mechanism
    private void setUpDataLogTable(){
        
        TableColumn<DataLog, String> nameColumn = new TableColumn<>("UserName");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<DataLog, String> dateColumn = new TableColumn<>("Calculation Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("calculation_date"));
        
        TableColumn<DataLog, String> descriptionColumn = new TableColumn<>("Calculation Values");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("calculation_values"));
        
        TableColumn<DataLog, String> patientColumn = new TableColumn<>("Patient ID");
        patientColumn.setCellValueFactory(new PropertyValueFactory<>("patient_id"));
        
        TableColumn<DataLog, String> calcTypeColumn = new TableColumn<>("Calculation Type");
        calcTypeColumn.setCellValueFactory(new PropertyValueFactory<>("calculation_type"));
        
        
        dataLogs = FXCollections.observableArrayList(fetchDataLogs());
        
        // Custom cell factory for the description column
        descriptionColumn.setCellFactory(col -> new TableCell<DataLog, String>() {
            private final TextArea textArea = new TextArea();

            {
                textArea.setWrapText(true);
                textArea.setEditable(false); // Make it read-only
                textArea.setPrefHeight(100); // Set a preferred height
                textArea.setVisible(false); // Initially hidden
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    textArea.setText(item);
                    setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 15px;");
                    setGraphic(textArea);
                    textArea.setVisible(true);
                }
            }
        });
        
        
        dataLogSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterDataLogTable();
        });
        dataLogDateField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterDataLogTable();
        });
        dataLogPatientSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterDataLogTable();
        });
        
        dataLogCalcTypeField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterDataLogTable();
        });
        
        
        TreatmentTimeTable.getColumns().addAll(nameColumn, patientColumn, dateColumn,calcTypeColumn, descriptionColumn);
        TreatmentTimeTable.setItems(dataLogs);
    }
    
   
    //filter linac table
    private void filterLinacTable() {
        String searchText = linacSearchField.getText().toLowerCase();
        String searchYear = linacDateField.getText().toLowerCase();

        ObservableList<LinacData> filteredList = FXCollections.observableArrayList();

        for (LinacData data : linactLogs) {
            boolean matchesSearch = data.getUsername().toLowerCase().contains(searchText);
            boolean matchesYearFilter = data.getCalculation_date().toLowerCase().contains(searchYear);

            if (matchesSearch && matchesYearFilter) {
                filteredList.add(data);
            }
        }

        linacLogTable.setItems(filteredList);
    }
    
    //filter data log table
    private void filterDataLogTable() {
        String searchText = dataLogSearchField.getText().toLowerCase();
        String searchPatient = dataLogPatientSearchField.getText().toLowerCase();
        String calcType = dataLogCalcTypeField.getText().toLowerCase();
        String searchYear = dataLogDateField.getText().toLowerCase();

        ObservableList<DataLog> filteredList = FXCollections.observableArrayList();

        for (DataLog data : dataLogs) {
            boolean matchesSearch = data.getUsername().toLowerCase().contains(searchText);
            boolean matchesYearFilter = data.getCalculation_date().toLowerCase().contains(searchYear);
            boolean matchesPatient = data.getPatient_id().toLowerCase().contains(searchPatient);
            boolean matchesType = data.getCalculation_type().toLowerCase().contains(calcType);

            if (matchesSearch && matchesYearFilter && matchesType && matchesPatient) {
                filteredList.add(data);
            }
        }

        TreatmentTimeTable.setItems(filteredList);
    }
    
    
    // create Buttons for usernames
    public Button userButtons(String username){
        Button btn = new Button(username);
        btn.setId(username);
        btn.setPrefWidth(205.0);
        btn.setPrefHeight(100);
        btn.setFont(new Font("Courier New", 20));
        btn.setWrapText(true);
        btn.setEffect(new DropShadow());
        //Set buttons event handler as shown below
        btn.setOnAction(eh -> {
                       
            selectedUser = getSelectedUser(username);
            
            if(selectedUser.getRole() == 1){
                edtroleBox.getSelectionModel().select("admin");
            }else{
                edtroleBox.getSelectionModel().select("user");
            }
            
            edtUsername.setText(selectedUser.getUsername());
            
            
        });
        btn.getStyleClass().add("primary-button");
        return btn;
    }
    
    public void setupUsernameBox(){
        edtroleBox.setStyle("-fx-font-family: Courier New; -fx-font-size: 15;");
        userList = FXCollections.observableArrayList(fetchUsers());
        edtUsernames.setSpacing(10);
       
        for(User user : userList){
            edtUsernames.getChildren().add(userButtons(user.getUsername()));
        }
    }
    
    //Get selected user
    public User getSelectedUser(String username){
        for(User user : userList){
            if(user.getUsername().equals(username)){
                return user;
            }
        }
        return null;
    }
    
    @FXML
    private void refreshLinac(ActionEvent event) {
        ObservableList<LinacData> data= FXCollections.observableArrayList(fetchAllLinacLogs());
        linacLogTable.setItems(data);
        

    }
    
    @FXML
    private void refreshTreatmentTime(ActionEvent event) {
        dataLogs = FXCollections.observableArrayList(fetchDataLogs());
        TreatmentTimeTable.setItems(dataLogs);
        

    }
     @FXML
    private void logOutUser(ActionEvent event) {
        //Todo : Clear all session data and load home page
        Session.setUser(null);
        Session.setPatientId(null);
        backToHome(event);
    }
    private void backToHome(ActionEvent event) {
         SceneManager.getInstance().showHome();
    }

    
    @FXML
    private void saveChanges(ActionEvent event) {
        User edtUser = selectedUser;
        String username = edtUsername.getText();
        String password = edtPassword.getText();
        int userRole = 0;
        String role = edtroleBox.getSelectionModel().getSelectedItem();
        if(role.equals("admin")){
            userRole = 1;
        }else{
            userRole = 0;
        }
        if(username != null && password != null && edtroleBox.getSelectionModel().getSelectedItem() != null){
            try{
                String query = "UPDATE users SET username = ?, password = ?, role = ? WHERE user_id = ?";
                PreparedStatement stmt = dbConn.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, md5(password));
                stmt.setInt(3, userRole);
                stmt.setInt(4, edtUser.getID());
                int executeUpdate = stmt.executeUpdate();
                if(executeUpdate == 1){
                    edtUserInfoLabel.setStyle("-fx-text-fill: lime;");
                    edtUserInfoLabel.setText("Successfully Modified.");
                    // Update user list
                    userList = FXCollections.observableArrayList(fetchUsers());
                    return;
                }else{
                    edtUserInfoLabel.setStyle("-fx-text-fill: red;");
                    edtUserInfoLabel.setText("Operation not successful!");
                }
            }catch(SQLException e){
                edtUserInfoLabel.setText("Operation not successful!");
                e.printStackTrace();
                return;
            }
            
        }
        edtUserInfoLabel.setText("Fill all fields.");
    }
    
    
    @FXML
    private void addLinacLogPane(MouseEvent event) {
        
        if(adminPanel.getTabs().contains(linacLogTab) == false){
            adminPanel.getTabs().add(linacLogTab);
            linacLogTab.getContent().setFocusTraversable(true);
        }else{
            adminPanel.getSelectionModel().select(linacLogTab);
        }
   
    }

    
    @FXML
    private void addCalculationsLog(MouseEvent event) {
        
        if(adminPanel.getTabs().contains(treatmentTimeTab) == false){
            adminPanel.getTabs().add(treatmentTimeTab);
            treatmentTimeTab.getContent().setFocusTraversable(true);
        }else{
            adminPanel.getSelectionModel().select(treatmentTimeTab);
        }
    }

    
    @FXML
    private void addRegisterPane(MouseEvent event) {
        if(adminPanel.getTabs().contains(addUserTab) == false){
            adminPanel.getTabs().add(addUserTab);
            addUserTab.getContent().setFocusTraversable(true);
        }else{
            adminPanel.getSelectionModel().select(addUserTab);
        }
    }

    
    @FXML
    private void addEditorPane(MouseEvent event) {
        
        if(adminPanel.getTabs().contains(editTab) == false){
            adminPanel.getTabs().add(editTab);
            editTab.getContent().setFocusTraversable(true);
        }else{
            adminPanel.getSelectionModel().select(editTab);
        }
    }

    @FXML
    private void addDeletePane(MouseEvent event) {
        
        if(adminPanel.getTabs().contains(deleteTab) == false){
            adminPanel.getTabs().add(deleteTab);
            deleteTab.getContent().setFocusTraversable(true);
        }else{
            adminPanel.getSelectionModel().select(deleteTab);
        }
    }

    @FXML
    private void addExportPane(MouseEvent event) {
        if(adminPanel.getTabs().contains(exportTab) == false){
            adminPanel.getTabs().add(exportTab);
            exportTab.getContent().setFocusTraversable(true);
        }else{
            adminPanel.getSelectionModel().select(exportTab);
        }
    }
    
    
    @FXML
    private void addNewUser(MouseEvent event) {
        String username = null, password = null;
        String role = roleBox.getSelectionModel().getSelectedItem();
        int userRole = 0;
        if(!new_password.getText().isEmpty() && !new_username.getText().isEmpty()){
            username = new_username.getText();
            password = new_password.getText();
        }else{
            addUserinfoLabel.setText("Please fill all fields.");
            return;
        }
        if(role == null){
            addUserinfoLabel.setText("Please select user role.");
            return;
        }else{
            if(role.equals("Admin")){
                userRole = 1;
            }else{
                userRole = 0;
            }
        }
        
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try(PreparedStatement stmt = dbConn.prepareStatement(query);){
            stmt.setString(1, username);
            stmt.setString(2, md5(password));
            stmt.setInt(3, userRole);
            int code = stmt.executeUpdate();
            addUserinfoLabel.setStyle("-fx-text-fill: lime;");
            addUserinfoLabel.setText("Successfully added !");
            // update data 
            userList = FXCollections.observableArrayList(fetchUsers());
        }catch(Exception ex){
            addUserinfoLabel.setStyle("-fx-text-fill: red;");
            addUserinfoLabel.setText("Username Already Exists !");
            ex.printStackTrace();
        }
        
    }

    @FXML
    private void backToDashboard(MouseEvent event) {
        
        adminPanel.getSelectionModel().select(homeTab);    
    }


    @FXML
    private void deleteUser(MouseEvent event) {
       String Username = "";
       
       
       if(usernameBox != null && !usernameBox.getSelectionModel().getSelectedItem().isEmpty()){
           Username = usernameBox.getSelectionModel().getSelectedItem();
       }else if(!deleteUsername.getText().isEmpty()){
           Username = deleteUsername.getText();
       }else{
           showAlert(Alert.AlertType.WARNING, "No username specified", "Please set username!");
           return;
       }
       try{
           String query = "DELETE FROM users WHERE username = ?";
           PreparedStatement pstmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
           pstmt.setString(1, Username);
           pstmt.execute();
           delUserinfoLabel.setStyle("-fx-text-fill: lime;");
           delUserinfoLabel.setText("User Deleted !");
           // Update user list
           userList = FXCollections.observableArrayList(fetchUsers());
       }catch(SQLException ex){
           delUserinfoLabel.setStyle("-fx-text-fill: red;");
           delUserinfoLabel.setText("Invalid Username!");
           ex.printStackTrace();
       }
       
    }

    
}

