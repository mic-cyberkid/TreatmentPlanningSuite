package rtpsuite;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import static rtpsuite.FunctionalMethods.md5;

/**
 * FXML Controller class
 *
 * @author Michael Okyere
 */
public class UserLoginController implements Initializable {

    @FXML
    private TextField regular_username;
    @FXML
    private PasswordField regular_password;
    @FXML
    private Label infoLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void logonCalculationPage(ActionEvent event) {
        String uname = regular_username.getText();
        String password = regular_password.getText();
        if (!uname.isEmpty() && !password.isEmpty()) {
            try {
                Connection loginCon = DatabaseConnection.getInstance().getConnection();
                String query = "SELECT * FROM users where username = ? AND password = ?";
                PreparedStatement stmt = loginCon.prepareStatement(query);
                stmt.setString(1, uname);
                stmt.setString(2, md5(password));
                ResultSet result = stmt.executeQuery();
                boolean userExists = result.next();
                if (userExists) {
                    int uid = result.getInt("user_id");
                    String username = result.getString("username");
                    int role = result.getInt("role");
                    User authenticatedUser = new User(username, role, uid);
                    Session.setUser(authenticatedUser);
                    infoLabel.setStyle("-fx-text-fill: lime;");
                    infoLabel.setText("Login Successful !");

                    SceneManager.getInstance().showCalculationPage();

                } else {
                    infoLabel.setText("Wrong password or username.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Prompt user to enter all fields
            infoLabel.setText("Please fill all input fields.");

        }
    }

    @FXML
    private void backToHome(ActionEvent event) {
        SceneManager.getInstance().showHome();
    }

    private void openCalculationPage(ActionEvent event) {
        SceneManager.getInstance().showCalculationPage();
    }

}
