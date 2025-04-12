package photos32.controller;

import java.io.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import photos32.controller.FilterController.FilterCriteria;
import photos32.controller.FilterController.TagFilter;
import photos32.model.Album;
import photos32.model.Photo;
import photos32.model.TagType;
import photos32.model.User;
import photos32.service.AlertUtil;
import photos32.service.DataStore;

public class UserHomeController {

    @FXML private Label userHomeHeader;
    @FXML private Button signOutButton;
    @FXML private FlowPane albumContainer;

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button filterButton;
    @FXML private Button resetFilterButton;

    private User user;
    private FilterCriteria currentFilterCriteria;


    public void setHeader(User user) {
        userHomeHeader.setText("Welcome, " + user.getUsername());
    }

    public void setUser(User user) {
        this.user = user;
        populateAlbumTiles();
    }

    public User getUser() {
        return user;
    } 

    public FilterCriteria getCurrentFilterCriteria() {
        return currentFilterCriteria;
    }

    /**
     * Load and display available albums in UserHome.fxml
     * Removes existing album cards and repopulates them. 
     */
    public void populateAlbumTiles() {
        // First, remove all present album cards
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

    /**
     * Displays a dialog for user input, validates the album title,
     * and creates a new album if valid.
     * 
     * This method is triggered by an FXML button event.
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
            if (isDuplicateAlbum(user, title)) {
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
        
        createAlbum(user, title);
        DataStore.saveUser(user);
        populateAlbumTiles();
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

    private void createAlbum(User user, String title) {
        Album newAlbum = new Album(title);
        user.getAlbums().add(newAlbum);
    }

    /**
     * Opens an album and switches to the album view.
     * Loads the album view UI, initializes the controller with the selected album and user, 
     * and updates the UI elements.
     * 
     * @param album The album to be opened.
     */
    public void openAlbum(Album album) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos32/view/AlbumView.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Get the controller and pass necessary data
            AlbumViewController controller = loader.getController();
            controller.setParentController(this);
            controller.setAlbum(album);
            controller.setUser(user);  // Pass the current user
            
            // Update the UI elements
            controller.setHeader();
            controller.populatePhotoTiles();
            
            // Display the scene
            Stage stage = (Stage)albumContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Photo32");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {

        System.out.println("Filter results: ");
        for (TagFilter tagFilter : currentFilterCriteria.getTagFilters()) {
            System.out.println(tagFilter.getName() + " = " + tagFilter.getValue());
        }
        System.out.println(currentFilterCriteria.getLogicalOperator());
        System.out.println(currentFilterCriteria.getStartDate());
        System.out.println(currentFilterCriteria.getEndDate());

        // TODO: Implement search logic and display photos on a new stage


        // Creating a PhotoCard
        List<Photo> searchResultPhotos = new ArrayList<>(); 
        
        //Loop through albums and photos
        for (Album album : user.getAlbums()){
            for (Photo photo : album.getPhotos()){
                boolean matchesCriteria = true;
                //Checking tag filters 
                for (TagFilter tagFilter : currentTagFilterCriteria.getTagFilters()){
                    if (!photo.hasTag(tagFilter.getName(), tagFilter.getValue())){
                        matchesCriteria = false;
                        if (currentFilterCriteria.getLogicalOperator().equals("AND")){
                            break;
                        }
                    } else if (currentFilterCriteria.getLogicalOperator().equals("OR")){
                        matchesCriteria = true;
                        break;
                    }
                }

                //checking date range
                if (matchesCriteria){
                    if (photo.getDate().isBefore(currentFilterCriteria.getStartDate()) ||
                        photo.getDate().isAfter(currentFilterCriteria.getEndDate())){
                        matchesCriteria = false;
                    }
                }

                //adding photo to list if matches criteria
                if (matchesCriteria){
                    searchResultPhotos.add(photo);
                }
            }
        }
        
        for (Photo photo : searchResultPhotos) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos32/view/PhotoCard.fxml"));
                StackPane photoCard = loader.load();

                // Assign the associated album with the photo
                AlbumViewController albumView = new AlbumViewController();
                for (Album album : user.getAlbums()) {
                    for (Photo p : album.getPhotos()) {
                        if (p.equals(photo)) albumView.setAlbum(album);
                    }
                }
                albumView.setUser(user);
                albumView.setParentController(this);
    
                PhotoCardController photoCardController = loader.getController();
                photoCardController.setPhoto(photo);
                photoCardController.setParentController(albumView); // Pass reference to parent
                
                // At this point, you can add the photo cards to the pop up window.
                // photoContainer.getChildren().add(photoCard);
                try {
                    FXMLLoader loader = new
                        FXMLLoader(getClass().getResource("/photo32/view/SearchResultsPopup.fxml"));
                    Parent root = loader.load();

                    SearchResultsPopupController controller = loader.getController();
                    controller.setSearchResultPhotos(searchResultPhotos);

                    Stage stage = new Stage();
                    stage.setTitle("Search Results");
                    stage.setScene(new Scene(root));
                    stage.show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // try {
        //     FXMLLoader loader = 
        //         new FXMLLoader(getClass().getResource("/photos32/view/SearchResultsPopup.fxml"));
        //     Parent root = loader.load();

        //     SearchResultsPopupController controller = loader.getController();
        //     controller.setSearchResults(searchresults);
        //     controller.setParentController(this);

        //     Stage stage = new Stage();
        //     stage.initModality(Modality.WINDOW_MODAL);
        //     stage.initOwner(albumContainer.getScene().getWindow());
        //     stage.setTitle("Search Results");
        //     stage.setScene(new Scene(root));
        //     stage.show();

        //     controller.populatePhotoTiles();

        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }

    /**
     * Opens the filter window to allow users to apply tag-based search filters.
     * Blocks input to other windows while the filter window is open.
     * 
     * This method is triggered by an FXML button event.
     */
    @FXML
    private void handleFilter() {
        try {
            // Load the filter window FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos32/view/FilterWindow.fxml"));
            Parent root = loader.load();
            
            FilterController filterController = loader.getController();
            
            // Get available tag names
            List<String> availableTagNames = new ArrayList<>();
            for (TagType tagType : user.getTagTypes()) {
                availableTagNames.add(tagType.getName());
            }
            
            Stage filterStage = new Stage();
            filterStage.setTitle("Filter Photos");
            filterStage.setHeight(350);
            filterStage.setWidth(550);
            filterStage.setMinHeight(350);
            filterStage.setMinWidth(550);
            filterStage.initModality(Modality.APPLICATION_MODAL); // Block input to other windows
            filterStage.initOwner(filterButton.getScene().getWindow());
            
            // Setup the controller with existing criteria (if any)
            filterController.setup(this, availableTagNames, filterStage, currentFilterCriteria);
            
            Scene scene = new Scene(root);
            filterStage.setScene(scene);
            filterStage.showAndWait();
            
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            AlertUtil.showAlert(alert, null, "Error opening filter window", null);
            e.printStackTrace();
        }
    }

    /**
    * Handles the action of resetting any active filters.
    * <p>
    * This method is typically triggered by a UI event (e.g., clicking the reset filter button).
    * It clears the current filter criteria and hides the reset button.
    */
    @FXML
    private void handleResetFilter() {
       // Clear any active filters
        currentFilterCriteria = null;
        resetFilterButton.setVisible(false);
    }

    /**
     * Applies the given filter criteria to the view.
     * 
     * If the provided criteria contain valid filters, they are stored and the reset button is made visible.
     * If the criteria are empty or null, the filters are reset.
     *
     * @param criteria the {@link FilterController.FilterCriteria} object containing the filter conditions
     */
    public void applyFilterCriteria(FilterController.FilterCriteria criteria) {
        if (criteria != null && criteria.hasFilters()) {
            // Store the current filter criteria
            this.currentFilterCriteria = criteria;
            
            // Show reset filter button since we now have active filters
            resetFilterButton.setVisible(true);
        
        } else {
            // If criteria is empty, just reset filters
            handleResetFilter();
        }
    }

    /**
     * Handles user sign-out by prompting for confirmation.
     * If the user confirms, loads the login screen and switches the scene.
     * 
     * This method is triggered by an FXML button event.
     */
    @FXML
    private void handleSignOut() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        AlertUtil.showAlert(alert, "Confirmation", null, "Are you sure you want to sign out?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos32/view/Login.fxml"));
                Scene scene = new Scene(loader.load());

                Stage stage = (Stage)signOutButton.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Photo32 Login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
