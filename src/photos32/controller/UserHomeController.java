package photos32.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert.AlertType;


import photos32.model.Album;
import photos32.model.User;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Optional;

public class UserHomeController {

    @FXML private FlowPane albumContainer;
    @FXML private Button newAlbumButton;

    private User user;

    public void setUser(User user) {
        this.user = user;
        populateAlbumTiles();
    }

    public User getUser() {
        return user;
    }

    public void refresh() {
        populateAlbumTiles();
    }

    private void populateAlbumTiles() {
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
        // Create a dialog for the user to enter the title
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Album");
        dialog.setHeaderText("Enter the title for your new album:");
        dialog.setContentText("Title:");

        // Get the result of the dialog (user input)
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String albumTitle = result.get().trim();

            // Check for duplicate album titles
            for (Album album : user.getAlbums()) {
                if (album.getTitle().equalsIgnoreCase(albumTitle)) {
                    System.out.println("Album with this title already exists!");
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Information");
                    alert.setHeaderText("Error: Duplicate Album");
                    alert.setContentText("An album with that name already exists!");
                    alert.showAndWait();
                    return;
                }
            }

            Album newAlbum = new Album(albumTitle);
            user.getAlbums().add(newAlbum);
            saveUser();
            populateAlbumTiles();
        }
    }


    @FXML
    private void handleDeleteAlbum(Album album) {
        user.getAlbums().remove(album);
        saveUser();
        populateAlbumTiles();
    }

    @FXML
    private void handleRenameAlbum(Album album) {
        TextInputDialog dialog = new TextInputDialog(album.getTitle());
        dialog.setTitle("Rename Album");
        dialog.setHeaderText("Enter a new title for the album:");
        dialog.setContentText("New title:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String newTitle = result.get().trim();

            // Check for duplicate
            for (Album other : user.getAlbums()) {
                if (other != album && other.getTitle().equalsIgnoreCase(newTitle)) {
                    System.out.println("Another album already has this title!");
                    return;
                }
            }

            album.setTitle(newTitle);
            saveUser();
        }
    }

    @FXML
    private void handleOpenAlbum() {
        System.out.println("hi");
    }

    void saveUser() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/" + user.getUsername() + ".dat"))) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML private Button signOutButton;

    @FXML
    private void handleSignOut() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirm Sign Out");
    alert.setHeaderText(null);
    alert.setContentText("Are you sure you want to sign out?");

    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos32/view/Login.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) signOutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Photo32 Login");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }

}
