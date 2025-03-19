package photos32.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            errorLabel.setText("Username cannot be empty!");
            return;
        }

        // Basic logic: If admin → go to admin subsystem, else → go to user subsystem
        if (username.equals("admin")) {
            loadAdminScreen();
        } else {
            loadUserHomeScreen(username);
        }
    }

    private void loadAdminScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos32/view/Admin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Panel");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUserHomeScreen(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos32/view/UserHome.fxml"));
            Parent root = loader.load();
            // You can pass the username to UserHomeController here if needed
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("User Home");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}