package photos32.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import photos32.model.Album;
import photos32.model.Photo;
import photos32.model.Tag;
import photos32.model.TagType;

public class PhotoViewController {

    @FXML private StackPane imageContainer;
    @FXML private ImageView photoImageView;
    @FXML private Label caption;
    @FXML private Label dateTimeLabel;
    @FXML private ListView<Tag> tagListView;
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

        // Set up context menu for deleting tags
        tagListView.setCellFactory(param -> {
            ListCell<Tag> cell = new ListCell<>() {
                @Override
                protected void updateItem(Tag item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item.toString());
                }
            };

            // Context menu for deleting tags
            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteItem = new MenuItem("Delete Tag");

            deleteItem.setOnAction(event -> {
                Tag selectedTag = cell.getItem();
                if (selectedTag != null) {
                    removeTag(selectedTag);
                }
            });
            contextMenu.getItems().add(deleteItem);

            cell.setContextMenu(contextMenu);
            return cell;
        });
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
        updateTagListView();
    }

    private void updateTagListView() {
        if (photo != null) {
            // Clear existing items and add all tags from the current photo
            tagListView.getItems().clear();
            
            for (Tag tag : photo.getTags()) {
                // Add the tag name to the ListView
                tagListView.getItems().add(tag);
            }
        }   
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
        if (parentController == null || parentController.getAlbum() == null) return;

        List<Photo> photos = parentController.getAlbum().getPhotos();
        if (photos.isEmpty()) return;

        // Find current photo's index
        int currentIndex = photos.indexOf(photo);
        
        // Navigate to previous photo (wrapping around to the end if at the start)
        int previousIndex = (currentIndex - 1 + photos.size()) % photos.size();
        Photo previousPhoto = photos.get(previousIndex);

        // Update photo and display
        this.photo = previousPhoto;
        displayPhoto();
        updateTagListView();
    }

    @FXML
    private void handleNextPhoto() {
        if (parentController == null || parentController.getAlbum() == null) return;

        List<Photo> photos = parentController.getAlbum().getPhotos();
        if (photos.isEmpty()) return;

        // Find current photo's index
        int currentIndex = photos.indexOf(photo);
        
        // Navigate to next photo (wrapping around to the start if at the end)
        int nextIndex = (currentIndex + 1) % photos.size();
        Photo nextPhoto = photos.get(nextIndex);

        // Update photo and display
        this.photo = nextPhoto;
        displayPhoto();
        updateTagListView();
    }

    

    @FXML
    private void handleAddTag() {
        List<TagType> existingTagTypes = parentController.getUser().getTagTypes();
        
        Dialog<Tag> dialog = new Dialog<>();
        dialog.setTitle("Add Tag");
        dialog.setHeaderText("Select a Tag Type");
    
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
    
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
    
        // Combo box for tag types
        ComboBox<Object> tagTypeComboBox = new ComboBox<>();
        tagTypeComboBox.getItems().addAll(existingTagTypes);
        tagTypeComboBox.getItems().add("Custom");
        tagTypeComboBox.setPromptText("Choose Tag Type");
    
        // New tag type fields
        TextField newTagTypeField = new TextField();
        newTagTypeField.setPromptText("New Tag Type");
        newTagTypeField.setVisible(false);
    
        // Checkbox for multiple values
        CheckBox multipleValuesCheckBox = new CheckBox("Allow Multiple Values");
        multipleValuesCheckBox.setVisible(false);
    
        TextField tagValueField = new TextField();
        tagValueField.setPromptText("Tag Value");
    
        // Listener to show/hide new tag type field and multiple values checkbox
        tagTypeComboBox.setOnAction(e -> {
            if (tagTypeComboBox.getValue().equals("Custom")) {
                newTagTypeField.setVisible(true);
                multipleValuesCheckBox.setVisible(true);
            } else {
                newTagTypeField.setVisible(false);
                newTagTypeField.clear();
                multipleValuesCheckBox.setVisible(false);
                multipleValuesCheckBox.setSelected(false);
            }
        });
    
        // Layout grid
        grid.add(new Label("Tag Type:"), 0, 0);
        grid.add(tagTypeComboBox, 1, 0);
        grid.add(new Label("New Tag Type:"), 0, 1);
        grid.add(newTagTypeField, 1, 1);
        grid.add(multipleValuesCheckBox, 1, 2);
        grid.add(new Label("Tag Value:"), 0, 3);
        grid.add(tagValueField, 1, 3);
    
        dialog.getDialogPane().setContent(grid);
    
        // Enable/disable add button based on input
        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);
    
        // Validation
        tagValueField.textProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(newValue.trim().isEmpty() || 
                (tagTypeComboBox.getValue().equals("Custom") && 
                 (newTagTypeField.getText().trim().isEmpty())));
        });
    
        // Validation for new tag type field
        newTagTypeField.textProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(tagValueField.getText().trim().isEmpty() || 
                (tagTypeComboBox.getValue().equals("Custom") && newValue.trim().isEmpty()));
        });
    
        // Convert the result to a Tag when dialog is closed
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                TagType selectedTagType;
                
                // Determine tag type
                if (tagTypeComboBox.getValue().equals("Custom")) {
                    // Creating new tag type
                    String newTagTypeName = newTagTypeField.getText().trim();
                    
                    // Check if tag type already exists
                    if (existingTagTypes.stream()
                        .anyMatch(tt -> tt.getName().equalsIgnoreCase(newTagTypeName))) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("A tag type with this name already exists.");
                        alert.showAndWait();
                        return null;
                    }
                    
                    // Create new tag type with multiple values option
                    selectedTagType = new TagType(newTagTypeName, multipleValuesCheckBox.isSelected());
                    parentController.getUser().getTagTypes().add(selectedTagType);
                } else {
                    // Using existing tag type
                    selectedTagType = (TagType)tagTypeComboBox.getValue();
                }
    
                // Create and return tag
                return new Tag(
                    selectedTagType.getName(), 
                    tagValueField.getText().trim(), 
                    selectedTagType
                );
            }
            return null;
        });
    
        // Show dialog and process result
        Optional<Tag> result = dialog.showAndWait();
        result.ifPresent(tag -> {
            try {
                // Add tag to photo
                photo.addTag(tag);
                
                // Update UI
                updateTagListView();
                
                // Save user data
                parentController.saveUser();
            } catch (IllegalArgumentException e) {
                // Show error alert
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Tag Addition Error");
                alert.setHeaderText(null);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        });
    }

    private void removeTag(Tag tag) {
        if (photo != null) {
            // Remove the tag from the photo
            photo.getTags().remove(tag);
            
            // Update the ListView
            updateTagListView();
            parentController.saveUser();
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
