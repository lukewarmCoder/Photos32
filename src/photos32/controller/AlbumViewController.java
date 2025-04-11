package photos32.controller;

import java.io.*;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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

    private UserHomeController parentController;
    private Album album;
    private User user;

    public void setParentController(UserHomeController parentController) {
        this.parentController = parentController;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserHomeController getParentController() {
        return parentController;
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

   /**
     * Populates the photo container with photo tiles representing each photo in the current album.
     * <p>
     * Loads FXML components for each photo and sets their corresponding data and parent controller reference.
     */
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
    
    /**
     * Handles the "Add Photo" action by presenting a dialog to choose between stock photos and local file selection.
     */
    @FXML
    private void handleCreatePhoto() {
        // Create initial popup with two options
        Alert optionDialog = new Alert(Alert.AlertType.CONFIRMATION);
        optionDialog.setTitle("Add Photo");
        optionDialog.setHeaderText("Choose Photo Source");
        optionDialog.setContentText("Please select how you would like to add a photo:");

        ButtonType stockPhotoButton = new ButtonType("Choose Stock Photo");
        ButtonType fileChooserButton = new ButtonType("Choose From Files");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        optionDialog.getButtonTypes().setAll(stockPhotoButton, fileChooserButton, cancelButton);

        // Show dialog and handle result
        Optional<ButtonType> result = optionDialog.showAndWait();

        if (result.isPresent()) {
            if (result.get() == stockPhotoButton) {
                handleStockPhotoSelection();
            } else if (result.get() == fileChooserButton) {
                handleFileSelection();
            }
            // If cancel, do nothing
        }
    }

    /**
     * Displays a dialog to allow the user to select a stock photo from a predefined directory.
     * <p>
     * Adds the selected photo to the album after confirmation.
     */
    private void handleStockPhotoSelection() {
        // Define your stock photos directory
        String stockPhotosDir = "resources/"; // Adjust this path to your project structure
        File stockDir = new File(stockPhotosDir);
        
        // Get list of stock photos
        File[] stockPhotos = stockDir.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".jpg") || 
            name.toLowerCase().endsWith(".jpeg") || 
            name.toLowerCase().endsWith(".png") ||
            name.toLowerCase().endsWith(".gif") ||
            name.toLowerCase().endsWith(".bmp"));
        
        if (stockPhotos == null || stockPhotos.length == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No Stock Photos Found");
            alert.setContentText("No stock photos were found in the directory: " + stockPhotosDir);
            alert.showAndWait();
            return;
        }

        // Create a dialog
        Dialog<File> dialog = new Dialog<>();
        dialog.setTitle("Choose Stock Photo");
        dialog.setHeaderText("Select a stock photo to add to your album");
        
        // Set the button types
        ButtonType selectButtonType = new ButtonType("Select", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(selectButtonType, ButtonType.CANCEL);
        
        // Create the list view with stock photo names
        ListView<String> listView = new ListView<>();
        ObservableList<String> items = FXCollections.observableArrayList();
        
        // Add file names to the list
        for (File file : stockPhotos) {
            items.add(file.getName());
        }
        
        listView.setItems(items);
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setPrefHeight(300);
        listView.setPrefWidth(400);
        
        // Only enable the confirm button when a selection is made
        Node selectButton = dialog.getDialogPane().lookupButton(selectButtonType);
        selectButton.setDisable(true);
        
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectButton.setDisable(newValue == null);
        });
        
        dialog.getDialogPane().setContent(listView);
        
        // Convert the result to a file when the select button is clicked
        dialog.setResultConverter(dialogButton -> {
            int selectedIndex = listView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                return stockPhotos[selectedIndex];
            }
            return null;
        });
        
        // Show the dialog and process the result
        Optional<File> result = dialog.showAndWait();
        result.ifPresent(this::checkAndAddPhoto);
    }

    /**
     * Opens a file chooser for selecting an image file from the local file system.
     * <p>
     * Adds the photo to the album upon confirmation.
     */
    private void handleFileSelection() {
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
            parentController.showAlert(confirmDialog, "Confirm Selection", "Confirm Image Selection", 
                "File: " + selectedFile.getAbsolutePath());

            Optional<ButtonType> confirmation = confirmDialog.showAndWait();
            if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                checkAndAddPhoto(selectedFile);
            } else {
                // User cancelled the confirmation dialog
                System.out.println("File selection cancelled.");
            }
        }
    }

    /**
     * Validates the selected image file and adds it to the current album.
     * <p>
     * Checks for duplicates in the same or other albums, prompts for caption, and refreshes the photo view.
     *
     * @param selectedFile the image file to add
     */
    private void checkAndAddPhoto(File selectedFile) {
        // Check if a photo with the same filepath already exists in the current album...
        for (Photo photo : album.getPhotos()) {
            if (photo.getFilepath().equals(selectedFile.getAbsolutePath())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Duplicate photo");
                alert.setContentText("The photo you selected already exists in this album.");
                alert.showAndWait();
                return;
            }
        }

        // Check if a photo with the same filepath already exists in another album...
        for (Album a : user.getAlbums()) {
            for (Photo photo : a.getPhotos()) {
                if (photo.getFilepath().equals(selectedFile.getAbsolutePath())) {
                    Alert duplicatePhotoAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    parentController.showAlert(duplicatePhotoAlert, "Duplicate Photo Alert", "", 
                        "The photo you selected already exists in the following album: '" + a.getTitle() + 
                        "'. Proceeding will copy all photo data from that album to the current album.");

                    Optional<ButtonType> confirmOptional = duplicatePhotoAlert.showAndWait();
                    if (confirmOptional.isPresent() && confirmOptional.get() == ButtonType.OK) {
                        album.getPhotos().add(photo);
                        // Save changes
                        parentController.saveUser();
                        populatePhotoTiles();
                        return;
                    } else {
                        return;
                    }
                }
            }
        }

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
        parentController.saveUser();
        populatePhotoTiles();
    }

    /**
     * Opens the detailed photo view for the given photo.
     *
     * @param photo the {@link Photo} to display
     */
    public void openPhoto(Photo photo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos32/view/PhotoView.fxml"));
            Scene scene = new Scene(loader.load());
            
            PhotoViewController controller = loader.getController();
            controller.setParentController(this);
            controller.setPhoto(photo);
            controller.displayPhoto();

            // Display the scene
            Stage stage = (Stage)photoContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Photo32");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles returning to the user home screen.
     * <p>
     * Loads the user home view and passes the current user data. 
     */
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

    /**
     * Removes the given photo from the current album and saves the updated user data.
     *
     * @param photo the {@link Photo} to remove
     */
    public void removePhoto(Photo photo) {
        album.getPhotos().remove(photo);
        parentController.saveUser();
    }
}
