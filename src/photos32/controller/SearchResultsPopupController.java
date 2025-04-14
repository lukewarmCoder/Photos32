package photos32.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import photos32.model.Album;
import photos32.model.Photo;
import photos32.model.User;
import photos32.service.AlertUtil;
import photos32.service.DataStore;

/**
 * Controller class for the search results pop up window.
 * Handles UI interactions related to displaying and maniulating search results.
 * Connected to SearchResults.fxml.
 */
public class SearchResultsPopupController {

    @FXML private FlowPane photoFlowPane;   
    @FXML private Button backButton;

    private UserHomeController parentController;
    private List<Photo> searchResults;

    /**
     * Sets the list of photos to display as the current search results.
     * 
     * @param searchResults
     */
    public void setSearchResults(List<Photo> searchResults) {
        this.searchResults = searchResults;
    }

    /**
     * Returns the current list of search result photos.
     * 
     * @return the list of Photo objects from the latest search
     */
    public List<Photo> getSearchResults() {
        return searchResults;
    }

    /**
     * Sets the parent controller (UserHomeController) for this controller.
     * 
     * @param parentController the parent UserHomeController
     */
    public void setParentController(UserHomeController parentController) {
        this.parentController = parentController;
    }
    
    /**
     * Displays a dialog for user input, validates the album title,
     * and creates a new album if valid.
     */
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
                AlertUtil.showAlert(alert, "Information", "Error: Invalid Album Name", 
                        "Album names cannot be empty!");
                alert.showAndWait();
                continue; // Go back to the start of the loop
            }
            
            // Check for duplicate titles
            if (isDuplicateAlbum(parentController.getUser(), title)) {
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
        
        createAlbum(parentController.getUser(), title);
        DataStore.saveUser(parentController.getUser());
        parentController.populateAlbumTiles();
    }

    /**
     * Finds duplicate albums.
     * 
     * @param user
     * @param title
     * @return true if an album with the same name already exists, false otherwise.
     */
    private boolean isDuplicateAlbum(User user, String title) {
        for (Album album : user.getAlbums()) {
            if (album.getTitle().equalsIgnoreCase(title)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Instantiates a new album with the search results and adds it the user's list.
     * 
     * @param user
     * @param title
     */
    private void createAlbum(User user, String title) {
        Album newAlbum = new Album(title);
        for (Photo photo : searchResults) {
            newAlbum.getPhotos().add(photo);
        }
        user.getAlbums().add(newAlbum);
    }

    /**
     * Closes the search results pop up window.
     */
    @FXML
    private void handleBack() {
        Stage stage = (Stage)backButton.getScene().getWindow();
        stage.close();
    }   

    /**
     * Populates the photo container with photo tiles representing each photo in the search results.
     * <p>
     * Loads FXML components for each photo and sets their corresponding data and parent controller reference.
     */
    public void populatePhotoTiles() {
        // First, remove all present photo cards
        photoFlowPane.getChildren().clear();

        for (Photo photo : searchResults) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos32/view/PhotoCard.fxml"));
                StackPane photoCard = loader.load();

                // Assign the associated album with the photo
                FXMLLoader parentLoader = new FXMLLoader(getClass().getResource("/photos32/view/AlbumView.fxml"));
                parentLoader.load();
                AlbumViewController albumView = parentLoader.getController();

                for (Album album : parentController.getUser().getAlbums()) {
                    for (Photo p : album.getPhotos()) {
                        if (p.equals(photo)) albumView.setAlbum(album);
                    }
                }
                albumView.setUser(parentController.getUser());
                albumView.setParentController(parentController);
                albumView.setSearchResultFlowPane(photoFlowPane);
                albumView.setSearchController(this);

                PhotoCardController photoCardController = loader.getController();
                photoCardController.setPhoto(photo);
                photoCardController.setParentController(albumView); // Pass reference to parent
                photoCardController.setIsSearchResult(true);

                photoFlowPane.getChildren().add(photoCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}