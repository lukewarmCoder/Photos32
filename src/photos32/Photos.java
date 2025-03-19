package photos32;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class Photos extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/photos32/view/login.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("MVC Example");
        stage.show();
    }

    public static void main(String[] args) {
        System.out.println("hoi");
        launch(args);
    }
}