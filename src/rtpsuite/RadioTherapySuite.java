package rtpsuite;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author Michael Okyere
 */
public class RadioTherapySuite extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
        Image icon = new Image(getClass().getResourceAsStream("../images/basicmedia.jpg"));
        primaryStage.setTitle("Treatment Time Calculator");
        primaryStage.getIcons().add(icon);
        SceneManager.initialize(primaryStage);
        SceneManager.getInstance().showHome();  // start with Home screen
        primaryStage.show();
        primaryStage.setFullScreen(true);
        primaryStage.setOnCloseRequest(event -> {
            DatabaseConnection.getInstance().close();
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        // Initialize jdbc
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        launch(args);
    }

}
