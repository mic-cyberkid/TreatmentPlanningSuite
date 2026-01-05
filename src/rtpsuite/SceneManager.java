package rtpsuite;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Central navigation manager for the Oncology Physics QA & Treatment Time Calculator app.
 * Ensures smooth fullscreen transitions without resizing/flickering.
 */
public class SceneManager {

    private static SceneManager instance;

    private final Stage primaryStage;
    private final Scene scene;
    private final Map<String, String> fxmlPaths = new HashMap<>();

    private SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.scene = new Scene(new Parent() {}); // empty root initially
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        // Optional: disable ESC key exit in clinical/kiosk environment
        // primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        // Register all FXML paths here – easy to maintain
        registerFXML("Home", "Home.fxml");
        registerFXML("UserLogin", "UserLogin.fxml");
        registerFXML("AdminLogin", "AdminLogin.fxml");
        registerFXML("LoadPage", "LoadPage.fxml");
        registerFXML("AdminDashboard", "AdminDashboard.fxml");
        // add more as needed...
    }

    public static void initialize(Stage primaryStage) {
        if (instance == null) {
            instance = new SceneManager(primaryStage);
        }
    }

    public static SceneManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SceneManager not initialized. Call initialize(Stage) first.");
        }
        return instance;
    }

    private void registerFXML(String key, String fxmlFile) {
        fxmlPaths.put(key, fxmlFile);
    }

    /**
     * Load and display an FXML view with optional fade transition.
     */
    public void switchTo(String viewKey) {
        switchTo(viewKey, true);
    }

    public void switchTo(String viewKey, boolean withFade) {
        String fxmlPath = fxmlPaths.get(viewKey);
        if (fxmlPath == null) {
            throw new IllegalArgumentException("Unknown view key: " + viewKey);
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newRoot = loader.load();

            if (withFade) {
                applyFadeTransition(newRoot);
            } else {
                setRootImmediate(newRoot);
            }

        } catch (IOException e) {
            e.printStackTrace();
            // In production you might show an alert
        }
    }

    /**
     * Direct root swap without animation – useful for instant changes.
     */
    public void setRootImmediate(Parent newRoot) {
        scene.setRoot(newRoot);
        newRoot.applyCss();
        newRoot.layout();
        // Ensure fullscreen persists
        primaryStage.setFullScreen(true);
    }

    private void applyFadeTransition(Parent newRoot) {
        newRoot.setOpacity(0);

        setRootImmediate(newRoot);

        FadeTransition fade = new FadeTransition(Duration.millis(300), newRoot);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }

    /**
     * Convenience methods for common screens
     */
    public void showHome()           { switchTo("Home", true); }
    public void showUserLogin()      { switchTo("UserLogin", true); }
    public void showAdminLogin()     { switchTo("AdminLogin", true); }
    public void showCalculationPage(){ switchTo("LoadPage", true); }
    public void showAdminDashboard() { switchTo("AdminDashboard", true); }

    // Add more shortcuts as needed...
}