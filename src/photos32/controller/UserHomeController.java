package photos32.controller;

import java.io.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Optional;

import photos32.model.Album;
import photos32.model.User;

public class UserHomeController {

    @FXML private FlowPane albumContainer;
    @FXML private Label userHomeHeader;
    @FXML private Button signOutButton;

    private User user;

    public void setHeader(User user) {
        userHomeHeader.setText("Welcome, " + user.getUsername());
    }

    public void setUser(User user) {
        this.user = user;
        populateAlbumTiles();
    }

    public User getUser() {
        return user;
    }

    // Append the album cards for each user to the album container in UserHome.fxml
    public void populateAlbumTiles() {
        // First, remove all present album cards
        albumContainer.getChildren().clear();

        for (Album album : user.getAlbums()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos32/view/AlbumCard.fxml"));
                VBox albumCard = loader.load();
    
                AlbumCardController albumCardController = loader.getController();
                albumCardController.setAlbum(album);
                albumCardController.setParentController(this); // Pass reference to parent
                
                albumContainer.getChildren().add(albumCard);
    
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void handleCreateAlbum() {
        boolean validInput = false;
        String title = null;
        
        while (!validInput) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("New Album");
            dialog.setHeaderText("Enter the title for your new album:");
            dialog.setContentText("Title:");
            
            Optional<String> result = dialog.showAndWait();
            
            // If user clicks Cancel/Close
            if (!result.isPresent()) {
                return; // Exit the method entirely
            }
            
            title = result.get().trim();
            
            // Check if the title is empty
            if (title.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                showAlert(alert, "Information", "Error: Invalid Album Name", 
                        "Album names cannot be empty!");
                alert.showAndWait();
                continue; // Go back to the start of the loop
            }
            
            // Check for duplicate titles
            if (isDuplicateAlbum(user, title)) {
                // if (album.getTitle().equals(newTitle)) break;

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                showAlert(alert, "Information", "Error: Duplicate Album", 
                        "An album with that name already exists!");
                alert.showAndWait();
                continue; // Go back to the start of the loop
            }
            
            // If we reach here, the input is valid
            validInput = true;
        }
        
        createAlbum(user, title);
        saveUser();
        populateAlbumTiles();
    }

    // Check for duplicate album titles
    private boolean isDuplicateAlbum(User user, String title) {
        for (Album album : user.getAlbums()) {
            if (album.getTitle().equalsIgnoreCase(title)) {
                return true;
            }
        }
        return false;
    }

    private void createAlbum(User user, String title) {
        Album newAlbum = new Album(title);
        user.getAlbums().add(newAlbum);
    }


    @FXML
    private void handleOpenAlbum() {
        System.out.println("hi");
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

    public void saveUser() {
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
