package photos32.controller;

import java.io.*;
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

        try {
            if (username.equals("admin")) {
                // Load the admin screen
                loadScreen("/photos32/view/AdminHome.fxml", null);
            } else {
                // Load the user screen
                User user = getOrCreateUser(username);
                loadScreen("/photos32/view/UserHome.fxml", user);
            }
        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadScreen(String fxmlPath, User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Scene scene = new Scene(loader.load());

        if (user != null) {
            // Pass user object to UserHomeController
            UserHomeController controller = loader.getController();
            controller.setUser(user);
            controller.setHeader(user);
        }

        Stage stage = (Stage)usernameField.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Photo 32");
    }

    private User getOrCreateUser(String username) {
        try {
            User user = loadUser(username);
            if (user == null) {
                // If the user does not exist, we create a new user
                user = new User(username);
                saveUser(user);
            }
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return new User(username); // Fallback
        }
    }

    // Attempts to load a User object from storage
    private User loadUser(String username) throws IOException, ClassNotFoundException {
        File userFile = new File("data/" + username + ".dat");
        if (userFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(userFile))) {
                return (User)ois.readObject();
            }
        }
        return null; // User doesn't exist
    }

    /* 
    Creates a user.dat file if the user is being saved for the first time.
    Overwrites the user's .dat file if the user has already been saved once.
    */
    private void saveUser(User user) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/" + user.getUsername() + ".dat"))) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}