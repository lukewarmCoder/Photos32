package photos32.controller;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

import photos32.model.Photo;

public class PhotoCardController {

    @FXML private StackPane photoCard;
    @FXML private ImageView thumbnail; 
    @FXML private MenuButton dropdownMenu;
    @FXML private Label caption;
   
    // @FXML private Label dateTimeLabel;

    private Photo photo;
    private AlbumViewController parentController;

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
        // dateTimeLabel.setOnMouseClicked(event -> handleViewPhoto());
    }

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
                Image placeholder = new Image(getClass().getResourceAsStream("/photos32/resources/placeholder.png"));
                thumbnail.setImage(placeholder);
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            // Set placeholder on error
            try {
                Image placeholder = new Image(getClass().getResourceAsStream("/photos32/resources/placeholder.png"));
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
        
        // Set the date/time
        // if (photo.getDateTime() != null) {
        //     // Format as MM/dd/yyyy HH:mm
        //     String formattedDate = photo.getDateTime().format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"));
        //     dateTimeLabel.setText(formattedDate);
        // } else {
        //     dateTimeLabel.setText("No date available");
        // }
    }



    public void setParentController(AlbumViewController controller) {
        this.parentController = controller;
    }

    @FXML
    private void handleViewPhoto() {
        // try {
        //     FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos32/view/PhotoView.fxml"));
        //     Scene scene = new Scene(loader.load());
            
        //     PhotoViewController controller = loader.getController();
        //     controller.setPhoto(photo);
        //     controller.setParentController(parentController);
        //     controller.displayPhoto();
            
        //     Stage stage = new Stage();
        //     stage.setScene(scene);
        //     stage.setTitle("View Photo");
        //     stage.setResizable(true);
        //     stage.show();
        // } catch (IOException e) {
        //     e.printStackTrace();
        //     showAlert(Alert.AlertType.ERROR, "Error", null, "Could not open photo viewer: " + e.getMessage());
        // }
    }

    @FXML
    private void handleEditCaption() {
        // // Dialog for editing caption
        // TextInputDialog dialog = new TextInputDialog(photo.getCaption());
        // dialog.setTitle("Edit Photo");
        // dialog.setHeaderText("Edit photo caption");
        // dialog.setContentText("Caption:");
        
        // Optional<String> result = dialog.showAndWait();
        // if (result.isPresent()) {
        //     photo.setCaption(result.get().trim());
        //     captionLabel.setText(photo.getCaption().isEmpty() ? "No caption" : photo.getCaption());
            
        //     // Save changes
        //     parentController.saveUser();
        // }
    }

    @FXML
    private void handlePhotoCopy() {


    }

    @FXML
    private void handlePhotoMove() {

    }

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
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
}
