package photos32.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import photos32.model.User;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            errorLabel.setText("Username cannot be empty!");
            return;
        }

        if (username.equals("admin")) {
            loadAdminScreen();
        } else {
            User user = loadOrCreateUser(username);
            loadUserScreen(user);
        }
    }

    private void loadAdminScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos32/view/AdminHome.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Admin Panel");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUserScreen(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos32/view/UserHome.fxml"));
            Scene scene = new Scene(loader.load());

            // Pass user object to UserHomeController
            UserHomeController controller = loader.getController();
            controller.setUser(user);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("User Home - ");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private User loadOrCreateUser(String username) {
        File userFile = new File("data/" + username + ".dat");
        if (userFile.exists()) {
            // Deserialize existing user
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(userFile))) {
                return (User) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                errorLabel.setText("Error loading user data.");
                return new User(username); // fallback
            }
        } else {
            // Create new user
            User newUser = new User(username);
            saveUser(newUser);
            return newUser;
        }
    }

    private void saveUser(User user) {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdir();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/" + user.getUsername() + ".dat"))) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}