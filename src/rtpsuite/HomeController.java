package rtpsuite;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;

public class HomeController implements Initializable {

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

    }

    @FXML
    void openAdminPage(ActionEvent event) {

        SceneManager.getInstance().showAdminLogin();
    }

    

    @FXML
    void openUserLogin(ActionEvent event) {

        SceneManager.getInstance().showUserLogin();

    }

}
