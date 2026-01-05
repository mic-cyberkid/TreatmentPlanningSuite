package rtpsuite;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDateTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import static rtpsuite.FunctionalMethods.md5;

/**
 * FXML Controller class
 *
 * @author OverComer
 */
public class AdminLoginController implements Initializable {

    @FXML
    private TextField admin_username;
    @FXML
    private PasswordField admin_password;
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
    private void logonAdminPage(ActionEvent event) {
        String uname = admin_username.getText();
        String password = admin_password.getText();
        if (!uname.isEmpty() && !password.isEmpty()) {
            try {
                Connection loginCon = DatabaseConnection.getInstance().getConnection();
                String query = "SELECT * FROM users where username = ? AND password = ? AND role= ?";
                PreparedStatement stmt = loginCon.prepareStatement(query);
                stmt.setString(1, uname);
                stmt.setString(2, md5(password));
                stmt.setInt(3, 1);
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
                    openAdminPage(event);

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

    private void openAdminPage(ActionEvent event) {
        SceneManager.getInstance().showAdminDashboard();
    }

    @FXML
    private void backToHome(ActionEvent event) {
        SceneManager.getInstance().showHome();
    }

}
