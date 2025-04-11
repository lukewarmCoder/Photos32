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

    /**
     * Handles the login button press.
     * <p>
     * Validates the entered username. If the username is "admin", it loads the admin interface.
     * Otherwise, it loads or creates a user and launches the user interface.
     */
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

    /**
     * Loads the specified FXML screen and, if applicable, passes the user to the controller.
     *
     * @param fxmlPath the path to the FXML file
     * @param user     the user to pass to the controller (null for admin)
     * @throws IOException if the FXML file cannot be loaded
     */
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

    /**
     * Retrieves an existing user from storage, or creates a new one if not found.
     *
     * @param username the username to look up
     * @return the {@link User} object associated with the username
     */
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

    /**
     * Loads a user object from disk if it exists.
     *
     * @param username the username of the user to load
     * @return the loaded {@link User} object, or null if not found
     * @throws IOException            if an I/O error occurs
     * @throws ClassNotFoundException if the class of the serialized object cannot be found
     */
    private User loadUser(String username) throws IOException, ClassNotFoundException {
        File userFile = new File("data/" + username + ".dat");
        if (userFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(userFile))) {
                return (User)ois.readObject();
            }
        }
        return null; // User doesn't exist 
    }

    private void saveUser(User user) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/" + user.getUsername() + ".dat"))) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}