package photos32.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;

import photos32.model.User;
import photos32.model.Album;

import javafx.animation.ScaleTransition;
import javafx.util.Duration;

import java.util.Optional;

    
public class AlbumCardController {
    @FXML private VBox albumCard;
    @FXML private Label albumTitle;
    @FXML private Label infoText;
    
    private Album album;
    private UserHomeController parentController;
    
    public void setAlbumTitle(String title) {
        albumTitle.setText(title);
    }

    public void setParentController(UserHomeController controller) {
        this.parentController = controller;
    }
    
    public void setAlbum(Album album) {
        this.album = album;
        albumTitle.setText(album.getTitle());
    }

    @FXML
    public void initialize() {
        // Make the entire card clickable
        albumCard.setOnMouseClicked(event -> handleAlbumClick());

        // Add hover effects
        albumCard.setOnMouseEntered(event -> {
            albumCard.setCursor(javafx.scene.Cursor.HAND);
            
            // Optional scale effect
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), albumCard);
            scale.setToX(1.02);
            scale.setToY(1.02);
            scale.play();
        });
        
        albumCard.setOnMouseExited(event -> {
            albumCard.setStyle(albumCard.getStyle().replace("-fx-background-color: #f0f8ff;", ""));
            
            // Reset scale
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), albumCard);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
    }

    private void handleAlbumClick() {
        parentController.openAlbum(album);
    }
    
    @FXML
    public void handleRename() {

        boolean validInput = false;
        String newTitle = null;
        
        while (!validInput) {
            TextInputDialog dialog = new TextInputDialog(albumTitle.getText());
            dialog.setTitle("Rename Album");
            dialog.setHeaderText("Enter new name for the album:");
            dialog.setContentText("Name:");
            
            Optional<String> result = dialog.showAndWait();
            
            // If user clicks Cancel/Close
            if (!result.isPresent()) {
                return; // Exit the method entirely
            }
            
            newTitle = result.get().trim();
            
            // Check if the title is empty
            if (newTitle.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                showAlert(alert, "Information", "Error: Invalid Album Name", 
                        "Album names cannot be empty!");
                alert.showAndWait();
                continue; // Go back to the start of the loop
            }
            
            // Check for duplicate titles
            if (isDuplicateAlbum(parentController.getUser(), newTitle)) {
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
        
        // At this point, newTitle is guaranteed to be valid
        album.setTitle(newTitle);
        albumTitle.setText(newTitle);
        
        parentController.saveUser();
        parentController.populateAlbumTiles();
    }
    
    @FXML
    public void handleDelete() {
        // Create a confirmation alert before deleting the album
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        showAlert(alert, "Confirm Deletion", "Are you sure you want to delete this album?", 
            "This action cannot be undone.");

        // Show the alert and wait for the user's response
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // If the user confirms, proceed with deletion
            parentController.getUser().getAlbums().remove(album);
            parentController.saveUser();
            parentController.populateAlbumTiles();
        }
        // If the user cancels, nothing happens and the album is not deleted
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

    private void showAlert(Alert alert, String title, String header, String content) {
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
    }
}