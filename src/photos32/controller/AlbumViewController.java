package photos32.controller;

import java.io.*;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import photos32.model.Album;
import photos32.model.Photo;
import photos32.model.User;

public class AlbumViewController {

    @FXML private FlowPane photoContainer;
    @FXML private Label albumViewHeader;
    @FXML private Button backButton;

    private Album album;
    private User user;

    public void setAlbum(Album album) {
        this.album = album;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Album getAlbum() {
        return album;
    }

    public void setHeader() {
        albumViewHeader.setText(album.getTitle());
    }

    // Populate the album view with photo tiles
    public void populatePhotoTiles() {
        // First, remove all present photo cards
        photoContainer.getChildren().clear();

        for (Photo photo : album.getPhotos()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos32/view/PhotoCard.fxml"));
                StackPane photoCard = loader.load();
    
                PhotoCardController photoCardController = loader.getController();
                photoCardController.setPhoto(photo);
                photoCardController.setParentController(this); // Pass reference to parent
                
                photoContainer.getChildren().add(photoCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void handleCreatePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );
        
        Stage stage = (Stage)backButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            // Intermediary confirmation window with file path
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            showAlert(confirmDialog, "Confirm Selection", "Confirm Image Selection", 
                "File: " + selectedFile.getAbsolutePath());

            Optional<ButtonType> confirmation = confirmDialog.showAndWait();
            if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                // Create new photo object
                Photo newPhoto = new Photo(selectedFile.getAbsolutePath());
                
                // Ask for caption
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Add Caption");
                dialog.setHeaderText("Would you like to add a caption to this photo?");
                dialog.setContentText("Caption (optional):");
                
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    String caption = result.get().trim();
                    if (!caption.isEmpty()) {
                        newPhoto.setCaption(caption);
                    }
                }
                
                // Add to album
                album.getPhotos().add(newPhoto);
                
                // Save changes
                saveUser();
                populatePhotoTiles();
            } else {
                // User cancelled the confirmation dialog
                System.out.println("File selection cancelled.");
            }
        }
    }

    public void openPhoto(Photo photo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos32/view/PhotoView.fxml"));
            Scene scene = new Scene(loader.load());
            
            PhotoViewController controller = loader.getController();
            controller.setPhoto(photo);
            controller.setParentController(this);
            controller.displayPhoto();

            // Display the scene
            Stage stage = (Stage)photoContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Photo32");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos32/view/UserHome.fxml"));
            Scene scene = new Scene(loader.load());

            UserHomeController controller = loader.getController();
            controller.setUser(user);
            controller.setHeader(user);

            Stage stage = (Stage)backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Photo32");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This method is called by PhotoCardController to remove a photo
    public void removePhoto(Photo photo) {
        album.getPhotos().remove(photo);
        saveUser();
    }

    // Save changes to the user object (which contains the album)
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
