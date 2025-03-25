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

public class AdminHomeController  {

    @FXML private Button signOutButton;
    @FXML private ToggleButton toggleUsersButton;
    @FXML private VBox userListContainer;

    private List<User> users;

    @FXML
    public void initialize() {
        users = loadUsers();
    }

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

    private void showUsers() {
        userListContainer.getChildren().clear();

        for (User user : users) {
            HBox userRow = createUserRow(user);
            userListContainer.getChildren().add(userRow);
        }
    }

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

    private void handleDeleteUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        showAlert(alert, "Confirmation", null, "Deleting this user will remove all of its data.");

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

                // // Delete the user file
                // Files.deleteIfExists(userFile);

                // // Create a list to collect nodes that should be removed
                // List<Node> nodesToRemove = new ArrayList<>();

                // // Iterate over the children and find the node to remove
                // for (Node node : userListContainer.getChildren()) {
                //     if (node instanceof HBox) {
                //         HBox hbox = (HBox) node;
                //         for (Node child : hbox.getChildren()) {
                //             if (child instanceof Label) {
                //                 Label label = (Label) child;
                //                 if (label.getText().equals(user.getUsername())) {
                //                     nodesToRemove.add(node);  // Add the node to the removal list
                //                     break;  // Stop looking once the user is found
                //                 }
                //             }
                //         }
                //     }
                // }

                // // After the iteration, remove all collected nodes
                // userListContainer.getChildren().removeAll(nodesToRemove);
                
                // // Remove the user from the UI
                // List<User> users = loadUsers();
                // users.remove(user);
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


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
                showAlert(alert, "Information", "Error: Invalid Username", 
                        "Usernames cannot be empty!");
                alert.showAndWait();
                continue; // Go back to the start of the loop
            }
            
            // Check for duplicate titles
            if (isDuplicateUser(username)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                showAlert(alert, "Information", "Error: Duplicate User", 
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

    private boolean isDuplicateUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) return true;
        }
        return false;
    }


    @FXML
    private void handleSignOut() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        showAlert(alert, "Confirmation", null, "Are you sure you want to sign out?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos32/view/Login.fxml"));
                Scene scene = new Scene(loader.load());

                Stage stage = (Stage)signOutButton.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Photo32 Login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveUser(User user) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/" + user.getUsername() + ".dat"))) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert alert, String title, String header, String content) {
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
    }
}
