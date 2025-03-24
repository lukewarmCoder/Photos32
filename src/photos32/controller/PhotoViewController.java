package photos32.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import photos32.model.Album;
import photos32.model.Photo;

public class PhotoViewController {

    @FXML private StackPane imageContainer;
    @FXML private ImageView photoImageView;
    @FXML private Label caption;
    @FXML private Label dateTimeLabel;
    @FXML private ListView<String> tagListView;
    @FXML private Button backButton;
    @FXML private Button previousPhotoButton;
    @FXML private Button nextPhotoButton;

    private Photo photo;
    private AlbumViewController parentController;

    @FXML
    public void initialize() {
        // Bind the ImageView to resize with the container
        if (photoImageView != null && imageContainer != null) {
            photoImageView.fitWidthProperty().bind(imageContainer.widthProperty().subtract(20));
            photoImageView.fitHeightProperty().bind(imageContainer.heightProperty().subtract(20));
            photoImageView.setPreserveRatio(true);
        }
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public void setParentController(AlbumViewController controller) {
        this.parentController = controller;
    }

    public void displayPhoto() {
        // Load and display full-size image
        try {
            File file = new File(photo.getFilepath());
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                photoImageView.setImage(image);
            } else {
                // Set a placeholder image
                Image placeholder = new Image(getClass().getResourceAsStream("/photos32/resources/placeholder.png"));
                photoImageView.setImage(placeholder);
                showAlert(Alert.AlertType.WARNING, "Warning", null, 
                          "Original image file could not be found: " + photo.getFilepath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", null, 
                     "Error displaying image: " + e.getMessage());
        }

        // Set caption
        updateCaptionDisplay();
        
        // Set date/time
        if (photo.getDateTime() != null) {
            String formattedDate = photo.getDateTime().format(
                java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm:ss")
            );
            dateTimeLabel.setText(formattedDate);
        } else {
            dateTimeLabel.setText("No date available");
        }
    }

    private void updateCaptionDisplay() {
        if (photo.getCaption() != null && !photo.getCaption().isEmpty()) {
            caption.setText(photo.getCaption());
        } else {
            caption.setText("No caption");
        }
    }

    @FXML
    private void handleEditCaption() {
        TextInputDialog dialog = new TextInputDialog(photo.getCaption());
        dialog.setTitle("Edit Caption");
        dialog.setHeaderText("Edit photo caption");
        dialog.setContentText("Caption:");
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newCaption = result.get().trim();
            photo.setCaption(newCaption);
            updateCaptionDisplay();
            
            // Save changes
            parentController.saveUser();
        }
    }

    @FXML
    private void handlePhotoCopy() {
        PhotoCardController controller = new PhotoCardController();
        List<String> selectedAlbums = showAlbumSelectionDialog();
        if (selectedAlbums.isEmpty()) return;

        System.out.println("Copy to: " + selectedAlbums);

        for (String albumTitle : selectedAlbums) {
            Album destAlbum = parentController.getUser().getAlbumFromTitle(albumTitle);
            if (destAlbum != null) {
                destAlbum.getPhotos().add(photo);
            }
        }
        parentController.saveUser();
    }

    @FXML
    private void handlePhotoMove() {
        List<String> selectedAlbums = showAlbumSelectionDialog();
        if (selectedAlbums.isEmpty()) return;

        System.out.println("Move to: " + selectedAlbums);

        for (String albumTitle : selectedAlbums) {
            Album destAlbum = parentController.getUser().getAlbumFromTitle(albumTitle);
            if (destAlbum != null) {
                destAlbum.getPhotos().add(photo);
            }
        }
        parentController.getAlbum().getPhotos().remove(photo);
        parentController.saveUser();
        parentController.populatePhotoTiles();

        handleBackToAlbumView();
    }

    private List<String> showAlbumSelectionDialog() {
        Dialog<List<String>> dialog = createDialog();
        Optional<List<String>> result = dialog.showAndWait();
        return result.orElse(Collections.emptyList());
    }

    private Dialog<List<String>> createDialog() {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Select Destination Album(s)");
    
        ListView<String> albumListView = createAlbumListView();
        dialog.getDialogPane().setContent(albumListView);
    
        dialog.getDialogPane().getButtonTypes().addAll(
            new ButtonType("OK", ButtonBar.ButtonData.OK_DONE),
            ButtonType.CANCEL
        );
    
        dialog.setResultConverter(button -> {
            if (button.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                return new ArrayList<>(albumListView.getSelectionModel().getSelectedItems());
            }
            return null;
        });
    
        return dialog;
    }

    private ListView<String> createAlbumListView() {
        ListView<String> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    
        for (Album album : parentController.getUser().getAlbums()) {
            if (!album.getTitle().equals(parentController.getAlbum().getTitle())) {
                listView.getItems().add(album.getTitle());
            }
        }
        return listView;
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
            handleBackToAlbumView();
        }
    }

    @FXML
    private void handleBackToAlbumView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos32/view/AlbumView.fxml"));
            Scene scene = new Scene(loader.load());

            AlbumViewController controller = loader.getController();
            controller.setAlbum(parentController.getAlbum());
            controller.setUser(parentController.getUser());
            controller.setHeader();
            controller.populatePhotoTiles();

            Stage stage = (Stage)backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Photo32");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    

    @FXML private void handlePreviousPhoto() {
        
    }

    @FXML
    private void handleNextPhoto() {
        
    }


    @FXML
    private void handleCreateTag() {

    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
