package photos32;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class Photos extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/photos32/view/Login.fxml"));
        stage.setScene(new Scene(root));
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setTitle("Photo32 App - Login");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}