package photos32.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import photos32.model.User;
import photos32.service.AlertUtil;

/**
 * Controller class for the admin view.
 * Handles UI interactions related to manipulating users.
 * Connected to AdminHome.fxml.
 */
public class AdminHomeController  {

    @FXML private Button signOutButton;
    @FXML private ToggleButton toggleUsersButton;
    @FXML private VBox userListContainer;

    private List<User> users;

    /**
     * When the controller is initialized, immediately reference all users from memory.
     */
    @FXML
    public void initialize() {
        users = loadUsers();
    }

    /**
     * Toggles the visibility of the user list when the toggle button is clicked.
     * <p>
     * Updates the button text accordingly and either displays or clears the user list.
     */
    @FXML
    private void handleToggleUsers() {

        if (toggleUsersButton.isSelected()) {
            showUsers();
            toggleUsersButton.setText("Hide Users");
        } else {
            userListContainer.getChildren().clear();
            toggleUsersButton.setText("Show Users");
        }
    }

    /**
     * Displays the list of users in the UI by creating and adding user rows.
     */
    private void showUsers() {
        userListContainer.getChildren().clear();

        for (User user : users) {
            HBox userRow = createUserRow(user);
            userListContainer.getChildren().add(userRow);
        }
    }

    /**
     * Creates a UI row for a given user, including a label and delete button.
     *
     * @param user the user to represent in the UI row
     * @return a formatted {@link HBox} containing the user's info and a delete button
     */
    private HBox createUserRow(User user) {
        HBox row = new HBox(10);
        row.setStyle("-fx-padding: 10; -fx-background-color: #FFFFFF; -fx-border-color: #CCCCCC; -fx-alignment: CENTER_LEFT;");

        // Username label on the left
        Label username = new Label(user.getUsername());
        username.setStyle("-fx-pref-width: 200;");

        // Flexible spacer to push the button to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        // Delete button on the right
        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> handleDeleteUser(user));

        // Add all components to the row
        row.getChildren().addAll(username, spacer, deleteBtn);

        return row;
    }

    /**
     * Loads user objects from the local "data/" directory.
     * <p>
     * Only files ending in ".dat" are considered valid user files.
     *
     * @return a list of {@link User} objects
     */
    private List<User> loadUsers() {
        List<User> users = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("data/"))) {
            for (Path path : stream) {
                if (path.toString().endsWith(".dat")) {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                        User user = (User) ois.readObject();
                        users.add(user);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Handles deleting a user after confirmation.
     * <p>
     * Deletes the user's file, reloads the user list, and updates the UI if needed.
     *
     * @param user the {@link User} to delete
     */
    private void handleDeleteUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        AlertUtil.showAlert(alert, "Confirmation", null, "Deleting this user will remove all of its data.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Create the file path for the selected user (based on their username)
            Path userFile = Paths.get("data/", user.getUsername() + ".dat");

            try {

                // Delete the user file
                Files.deleteIfExists(userFile);

                // Reload the users list
                users = loadUsers();

                // Refresh the UI
                if (toggleUsersButton.isSelected()) {
                    showUsers();
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles adding a new user via dialog input.
     * <p>
     * Prompts for username, validates input, checks for duplicates, and saves the new user.
     */
    @FXML 
    private void handleAddUser() {
        boolean validInput = false;
        String username = null;
        
        while (!validInput) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("New User");
            dialog.setHeaderText("Enter a username for the new user:");
            dialog.setContentText("Username:");
            
            Optional<String> result = dialog.showAndWait();
            
            // If admin clicks Cancel/Close
            if (!result.isPresent()) {
                return; // Exit the method entirely
            }
    
            username = result.get().trim();
            
            // Check if the username is empty
            if (username.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                AlertUtil.showAlert(alert, "Information", "Error: Invalid Username", 
                        "Usernames cannot be empty!");
                alert.showAndWait();
                continue; // Go back to the start of the loop
            }
            
            // Check for duplicate titles
            if (isDuplicateUser(username)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                AlertUtil.showAlert(alert, "Information", "Error: Duplicate User", 
                        "That username is taken!");
                alert.showAndWait();
                continue; // Go back to the start of the loop
            }
            
            // If we reach here, the input is valid
            validInput = true;
        }

        User user = new User(username);
        saveUser(user);
        users = loadUsers();
        
        if (toggleUsersButton.isSelected()) {
            showUsers();
        }
    }

    /**
     * Checks whether the given username is already in use.
     *
     * @param username the username to check
     * @return true if the username already exists; false otherwise
     */
    private boolean isDuplicateUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) return true;
        }
        return false;
    }

    /**
     * Handles the sign-out process with user confirmation.
     * <p>
     * If confirmed, it navigates the user back to the login screen.
     */
    @FXML
    private void handleSignOut() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        AlertUtil.showAlert(alert, "Confirmation", null, "Are you sure you want to sign out?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos32/view/Login.fxml"));
                Scene scene = new Scene(loader.load());

                Stage stage = (Stage)signOutButton.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Photos32 Login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves the current user data to a file.
     * The user's data is serialized and stored in a file named after their username.
     * 
     * @throws IOException if an error occurs during file writing.
     */
    private void saveUser(User user) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/" + user.getUsername() + ".dat"))) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
