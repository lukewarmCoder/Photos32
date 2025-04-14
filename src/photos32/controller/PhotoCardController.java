package photos32.controller;

import java.io.File;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

import photos32.model.Album;
import photos32.model.Photo;
import photos32.service.DataStore;
import photos32.service.PhotoService;

/**
 * Controller class for the photo cards.
 * Handles UI interactions related to buttons on the photo card.
 * Connected to PhotoCard.fxml.
 */
public class PhotoCardController {

    @FXML private StackPane photoCard;
    @FXML private ImageView thumbnail; 
    @FXML private MenuButton dropdownMenu;
    @FXML private Label caption;

    private AlbumViewController parentController;
    private boolean isSearchResult;
    private Photo photo;

    /**
     * Initializes the photo card UI.
     * <p>
     * Adds hover animations, sets rounded corners on the thumbnail, and sets up
     * click listeners for viewing the photo.
     */
    @FXML
    private void initialize() {

        // Add hover effects
        photoCard.setOnMouseEntered(event -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), photoCard);
            photoCard.setCursor(javafx.scene.Cursor.HAND);
            scale.setToX(1.02);
            scale.setToY(1.02);
            scale.play();
        });
        
        photoCard.setOnMouseExited(event -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), photoCard);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });

        // Make the corners rounded
        Rectangle clip = new Rectangle(thumbnail.getFitWidth(), thumbnail.getFitHeight());
        clip.setArcWidth(10);  // Adjust for how round you want it
        clip.setArcHeight(10);
        thumbnail.setClip(clip);

        // Make the thumbnail and card clickable to view photo
        thumbnail.setOnMouseClicked(event -> handleViewPhoto());
        caption.setOnMouseClicked(event -> handleViewPhoto());
    }

    /**
     * Sets the parent controller for this controller.
     * 
     * @param controller The AlbumViewController that acts as the parent controller.
     */
    public void setParentController(AlbumViewController controller) {
        this.parentController = controller;
    }

    /**
     * Sets whether this view is displaying search results.
     * 
     * @param isSearchResult A boolean indicating if this is a search result view.
     */
    public void setIsSearchResult(boolean isSearchResult) {
        this.isSearchResult = isSearchResult;
    }

    /**
     * Sets the photo associated with this card and updates the thumbnail and caption.
     *
     * @param photo the {@link Photo} to display
     */
    public void setPhoto(Photo photo) {
        this.photo = photo;
        
        // Set the image thumbnail
        try {
            File file = new File(photo.getFilepath());
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                thumbnail.setImage(image);

               // "Cover" behavior:
                double imageRatio = image.getWidth() / image.getHeight();
                double viewRatio = 230.0 / 230.0;

                double cropWidth = image.getWidth();
                double cropHeight = image.getHeight();

                if (imageRatio > viewRatio) {
                    // Image is wider -> crop width
                    cropWidth = image.getHeight() * viewRatio;
                } else {
                    // Image is taller -> crop height
                    cropHeight = image.getWidth() / viewRatio;
                }

                double x = (image.getWidth() - cropWidth) / 2;
                double y = (image.getHeight() - cropHeight) / 2;

                thumbnail.setViewport(new javafx.geometry.Rectangle2D(x, y, cropWidth, cropHeight));
                thumbnail.setFitWidth(230);
                thumbnail.setFitHeight(230);
                thumbnail.setPreserveRatio(false); // disable ratio because viewport handles it now
                // Optional: add a subtle border
                thumbnail.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 4, 0, 0, 0);");
            } else {
                // Set a placeholder image if file doesn't exist
                Image placeholder = new Image(getClass().getResourceAsStream("/photos32/data/stockphotos/placeholder.png"));
                thumbnail.setImage(placeholder);
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            // Set placeholder on error
            try {
                Image placeholder = new Image(getClass().getResourceAsStream("/photos32/data/stockphotos/placeholder.png"));
                thumbnail.setImage(placeholder);
            } catch (Exception ex) {
                // If even placeholder fails, just log it
                System.err.println("Error loading placeholder: " + ex.getMessage());
            }
        }
        
        // Set the caption
        if (photo.getCaption() != null && !photo.getCaption().isEmpty()) {
            caption.setText(photo.getCaption());
        } else {
            caption.setText("No caption");
        }
    }

    @FXML
    private void handleViewPhoto() {
        parentController.openPhoto(photo, isSearchResult);
    }

    /**
     * Opens a dialog to edit the photo's caption.
     */
    @FXML
    private void handleEditCaption() {
        // Dialog for editing caption
        TextInputDialog dialog = new TextInputDialog(photo.getCaption());
        dialog.setTitle("Edit Photo");
        dialog.setHeaderText("Edit photo caption");
        dialog.setContentText("Caption:");
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newCaption = result.get().trim();
            photo.setCaption(newCaption);
            updateCaptionDisplay();

            // Save changes
            DataStore.saveUser(parentController.getUser());
        }
    }

    private void updateCaptionDisplay() {
        if (photo.getCaption() != null && !photo.getCaption().isEmpty()) {
            caption.setText(photo.getCaption());
        } else {
            caption.setText("No caption");
        }
    }

    /**
     * Opens an album selection dialog and copies the photo to the selected albums.
     */
    @FXML
    private void handlePhotoCopy() {
        List<String> selectedAlbums = PhotoService.showAlbumSelectionDialog(
            parentController.getUser().getAlbums(), parentController.getAlbum().getTitle());
        if (selectedAlbums.isEmpty()) return;

        // Make sure none of the selected albums already have the current photo in it.
        for (String albumTitle : selectedAlbums) {
            for (Photo existingPhoto : parentController.getUser().getAlbumFromTitle(albumTitle).getPhotos()) {
                if (existingPhoto.getFilepath().equals(photo.getFilepath())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error: Invalid copy");
                    alert.setHeaderText("Duplicate photo");
                    alert.setContentText("This photo already exists in the following album: '" + albumTitle + "'");
                    alert.showAndWait();
                    return;
                }
            }
        }

        for (String albumTitle : selectedAlbums) {
            Album destAlbum = parentController.getUser().getAlbumFromTitle(albumTitle);
            if (destAlbum != null) {
                destAlbum.getPhotos().add(photo);
            }
        }
        DataStore.saveUser(parentController.getUser());
    }

    /**
     * Moves the photo to selected albums and removes it from the current album.
     */
    @FXML
    public void handlePhotoMove() {
        List<String> selectedAlbums = PhotoService.showAlbumSelectionDialog(
            parentController.getUser().getAlbums(), parentController.getAlbum().getTitle());
        if (selectedAlbums.isEmpty()) return;

        // Make sure none of the selected albums already have the current photo in it.
        for (String albumTitle : selectedAlbums) {
            for (Photo existingPhoto : parentController.getUser().getAlbumFromTitle(albumTitle).getPhotos()) {
                if (existingPhoto.getFilepath().equals(photo.getFilepath())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error: Invalid move");
                    alert.setHeaderText("Duplicate photo");
                    alert.setContentText("This photo already exists in the following album: '" + albumTitle + "'");
                    alert.showAndWait();
                    return;
                }
            }
        }

        for (String albumTitle : selectedAlbums) {
            Album destAlbum = parentController.getUser().getAlbumFromTitle(albumTitle);
            if (destAlbum != null) {
                destAlbum.getPhotos().add(photo);
            }
        }
        parentController.getAlbum().getPhotos().remove(photo);
        DataStore.saveUser(parentController.getUser());
        parentController.populatePhotoTiles();
    }


    /**
     * Handles deletion of the photo after user confirmation.
     */
    @FXML
    private void handleDeletePhoto() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete Photo");
        alert.setContentText("Are you sure you want to delete this photo?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Remove from data model
            parentController.removePhoto(photo);
            
            // Update UI (handled by AlbumViewController)
            parentController.populatePhotoTiles();

            if (isSearchResult) {
                parentController.getSearchResultsController().populatePhotoTiles();
            }
        }
    }
}