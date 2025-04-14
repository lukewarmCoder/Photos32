package photos32;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;


/**
 * The main class for the Photo32 application. 
 * This class extends the JavaFX Application class and initializes the main window of the app.
 */
public class Photos extends Application {

    /**
     * Starts the JavaFX application and sets up the main stage (window) for the application.
     * Loads the login screen and configures the window size and title.
     * 
     * @param stage The primary stage for this application, onto which the scene is set.
     * @throws Exception If an error occurs during the loading of the FXML file.
     */
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/photos32/view/Login.fxml"));
        stage.setScene(new Scene(root));
        stage.setWidth(825);
        stage.setHeight(600);
        stage.setMinWidth(825);
        stage.setMinHeight(600);
        stage.setTitle("Photos32 App - Login");
        stage.show();
    }

    /**
     * The main entry point for the Photos32 application.
     * Launches the JavaFX application.
     * 
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        launch(args);
    }
}