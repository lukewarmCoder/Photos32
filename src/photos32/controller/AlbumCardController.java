package photos32.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;

import photos32.model.User;
import photos32.service.AlertUtil;
import photos32.service.DataStore;
import photos32.model.Album;
import photos32.model.Photo;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

import java.util.Optional;

/**
 * Controller class for the album cards.
 * Handles UI interactions related to buttons on the album card.
 * Connected to AlbumCard.fxml.
 */
public class AlbumCardController {
    @FXML private VBox albumCard;
    @FXML private Label albumTitle;
    @FXML private Label dateRangeLabel;
    @FXML private Label numOfPhotos;
    
    private Album album;
    private UserHomeController parentController;
    
    /**
     * Sets the title of the album.
     * 
     * @param title The title to set for the album.
     */
    public void setAlbumTitle(String title) {
        albumTitle.setText(title);
    }

    /**
     * Sets the parent controller for this controller.
     * 
     * @param controller The UserHomeController that acts as the parent controller.
     */
    public void setParentController(UserHomeController controller) {
        this.parentController = controller;
    }
    
    /**
     * Sets the album associated with this controller and updates the UI with its title, photo count, and date range.
     *
     * @param album the {@link Album} to display
     */
    public void setAlbum(Album album) {
        this.album = album;
        albumTitle.setText(album.getTitle());
        numOfPhotos.setText(String.valueOf(album.getPhotoCount()));

        // Apply date range
        if (album.getPhotoCount() > 0) {

            Photo earliestPhoto = album.getPhotos().get(0);
            Photo latestPhoto = album.getPhotos().get(0);

            for (Photo photo : album.getPhotos()) {
                if (photo.getDateTime().isBefore(earliestPhoto.getDateTime())) {
                    earliestPhoto = photo;
                }
                if (photo.getDateTime().isAfter(latestPhoto.getDateTime())) {
                    latestPhoto = photo;
                }
            }

            // Format the dates
            String formattedStartDate = earliestPhoto.getDateTime().format(
                java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")
            );
            String formattedEndDate = latestPhoto.getDateTime().format(
                java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")
            );

            // Set the date range label
            dateRangeLabel.setText(formattedStartDate + " - " + formattedEndDate);
        }
    }   

    /**
     * Initializes the album card controller.
     * <p>
     * Adds mouse click and hover effects to the album card UI component.
     */
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

    /**
     * Handles the album card click event by opening the associated album.
     */
    private void handleAlbumClick() {
        parentController.openAlbum(album);
    }

    /**
     * Handles the "Rename Album" action.
     * <p>
     * Prompts the user for a new name, validates it, and updates the album title.
     */
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
                AlertUtil.showAlert(alert, "Information", "Error: Invalid Album Name", 
                        "Album names cannot be empty!");
                alert.showAndWait();
                continue; // Go back to the start of the loop
            }
            
            // Check for duplicate titles
            if (isDuplicateAlbum(parentController.getUser(), newTitle)) {
                // if (album.getTitle().equals(newTitle)) break;

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                AlertUtil.showAlert(alert, "Information", "Error: Duplicate Album", 
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
        
        DataStore.saveUser(parentController.getUser());
        parentController.populateAlbumTiles();
    }

    /**
     * Handles the "Delete Album" action.
     * <p>
     * Prompts the user for confirmation and removes the album if confirmed.
     */
    @FXML
    public void handleDelete() {
        // Create a confirmation alert before deleting the album
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        AlertUtil.showAlert(alert, "Confirm Deletion", "Are you sure you want to delete this album?", 
            "This action cannot be undone.");

        // Show the alert and wait for the user's response
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // If the user confirms, proceed with deletion
            parentController.getUser().getAlbums().remove(album);
            DataStore.saveUser(parentController.getUser());
            parentController.populateAlbumTiles();
        }
        // If the user cancels, nothing happens and the album is not deleted
    }

    /**
     * Checks if an album with the given title already exists for the user.
     *
     * @param user the {@link User} to check against
     * @param title the album title to check
     * @return true if a duplicate title exists, false otherwise
     */
    private boolean isDuplicateAlbum(User user, String title) {
        for (Album album : user.getAlbums()) {
            if (album.getTitle().equalsIgnoreCase(title)) {
                return true;
            }
        }
        return false;
    }
}