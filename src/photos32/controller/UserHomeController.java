package photos32.controller;

import java.io.*;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import photos32.controller.FilterController.FilterCriteria;
import photos32.controller.FilterController.TagFilter;
import photos32.model.Album;
import photos32.model.Photo;
import photos32.model.Tag;
import photos32.model.TagType;
import photos32.model.User;
import photos32.service.AlertUtil;
import photos32.service.DataStore;

/**
 * Controller class for the user home screen.
 * Handles UI interactions related to album management, searching, and filtering photos.
 * Connected to UserHome.fxml.
 */
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

    /**
     * Sets the header label on the user home screen to display a welcome message
     * using the provided user's username.
     *
     * @param user The user whose username will be displayed in the header.
     */
    public void setHeader(User user) {
        userHomeHeader.setText("Welcome, " + user.getUsername());
    }

    /**
     * Sets the current user for this controller and populates the album tiles
     * with that user's albums.
     *
     * @param user The user to associate with this controller.
     */
    public void setUser(User user) {
        this.user = user;
        populateAlbumTiles();
    }

    /**
     * Returns the user associated with this controller.
     *
     * @return The current user.
     */
    public User getUser() {
        return user;
    } 

    /**
     * Returns the currently applied filter criteria for searching or filtering photos.
     *
     * @return The current {@link FilterCriteria} object, or null if no filters are applied.
     */
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
     * Instantiates a new album and adds it the user's list
     * @param user
     * @param title
     */
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
            stage.setTitle("Photos32");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Filters the list of photos based on search text, tag filters, and date range filters.
     * 
     * @return a list of photos that match the given search criteria
     */
    private List<Photo> filterPhotos() {

        List<Photo> searchResults = new ArrayList<>();
        List<Photo> allPhotos = user.getAllPhotos();
        
        // If no search text and no filter criteria, return all photos
        if ((searchField.getText() == null || searchField.getText().trim().isEmpty()) && 
            isFilterCriteriaEmpty(currentFilterCriteria)) {
            return allPhotos;
        }
        
        // First filter by caption if search field has text
        List<Photo> captionFilteredPhotos = new ArrayList<>();
        if (searchField.getText() != null && !searchField.getText().trim().isEmpty()) {
            String searchText = searchField.getText().toLowerCase().trim();
            for (Photo photo : allPhotos) {
                if (photo.getCaption() != null && photo.getCaption().toLowerCase().contains(searchText)) {
                    captionFilteredPhotos.add(photo);
                }
            }
        } else {
            // If no search text, start with all photos
            captionFilteredPhotos.addAll(allPhotos);
        }
        
        // No filter criteria, just return caption-filtered results
        if (isFilterCriteriaEmpty(currentFilterCriteria)) {
            System.out.println("no filter criteria, return caption results");
            return captionFilteredPhotos;
        }
        
        // Apply tag filters
        List<TagFilter> tagFilters = currentFilterCriteria.getTagFilters();
        boolean hasTagFilters = tagFilters != null && !tagFilters.isEmpty() && 
                            tagFilters.stream().anyMatch(tf -> tf.getValue() != null && !tf.getValue().isEmpty());
        
        // Apply date range filters
        LocalDate startDate = currentFilterCriteria.getStartDate();
        LocalDate endDate = currentFilterCriteria.getEndDate();
        boolean hasDateFilter = startDate != null || endDate != null;

        // If there are no tag filters, only apply date filter
        if (!hasTagFilters && hasDateFilter) {
            for (Photo photo : captionFilteredPhotos) {
                if (isWithinDateRange(photo, startDate, endDate)) {
                    searchResults.add(photo);
                }
            }
            return searchResults;
        }
        
        // If there are no date filters, only apply tag filters
        if (hasTagFilters && !hasDateFilter) {
            System.out.println("only searching by tag");
            return applyTagFilters(captionFilteredPhotos, tagFilters, currentFilterCriteria.getLogicalOperator());
        }
        
        // Apply both tag filters and date filter
        List<Photo> tagFilteredPhotos = applyTagFilters(captionFilteredPhotos, tagFilters, currentFilterCriteria.getLogicalOperator());
        
        for (Photo photo : tagFilteredPhotos) {
            if (isWithinDateRange(photo, startDate, endDate)) {
                searchResults.add(photo);
            }
        }
        
        return searchResults;
    }

    /**
     * Checks whether the provided filter criteria object is empty.
     * A filter is considered empty if it has no tag filters or date range filters set.
     * 
     * @param criteria the filter criteria to check
     * @return true if the filter criteria is empty; false otherwise
     */
    private boolean isFilterCriteriaEmpty(FilterCriteria criteria) {
        if (criteria == null) return true;
        
        // Check tag filters
        List<TagFilter> tagFilters = criteria.getTagFilters();
        boolean hasTagFilters = tagFilters != null && !tagFilters.isEmpty() && 
                            tagFilters.stream().anyMatch(tf -> tf.getValue() != null && !tf.getValue().isEmpty());
        
        // Check date range
        boolean hasDateFilter = criteria.getStartDate() != null || criteria.getEndDate() != null;
        
        return !hasTagFilters && !hasDateFilter;
    }

    /**
     * Applies tag-based filters to a list of photos using the given logical operator (AND/OR).
     * Supports up to two tag filters.
     * 
     * @param photos the list of photos to filter
     * @param tagFilters the list of tag filters to apply
     * @param logicalOperator the logical operator ("AND" or "OR") used to combine tag filters
     * @return a list of photos that match the tag filter criteria
     */
    private List<Photo> applyTagFilters(List<Photo> photos, List<TagFilter> tagFilters, String logicalOperator) {
        List<Photo> filteredPhotos = new ArrayList<>();
        
        // Check if we only have one tag filter
        if (tagFilters.size() == 1 || tagFilters.get(1).getValue() == null || tagFilters.get(1).getValue().isEmpty()) {
            TagFilter filter = tagFilters.get(0);
            // Only apply if the filter has a value
            if (filter.getValue() != null && !filter.getValue().isEmpty()) {
                for (Photo photo : photos) {
                    if (photoMatchesTagFilter(photo, filter)) {
                        filteredPhotos.add(photo);
                    }
                }
            } else {
                // No valid tag filter, return original list
                return photos;
            }
        } else {
            // We have two tag filters
            TagFilter filter1 = tagFilters.get(0);
            TagFilter filter2 = tagFilters.get(1);
            
            // Apply based on logical operator
            if ("AND".equalsIgnoreCase(logicalOperator)) {
                for (Photo photo : photos) {
                    if (photoMatchesTagFilter(photo, filter1) && photoMatchesTagFilter(photo, filter2)) {
                        filteredPhotos.add(photo);
                    }
                }
            } else if ("OR".equalsIgnoreCase(logicalOperator)) {
                for (Photo photo : photos) {
                    if (photoMatchesTagFilter(photo, filter1) || photoMatchesTagFilter(photo, filter2)) {
                        filteredPhotos.add(photo);
                    }
                }
            } else {
                // No logical operator specified but we have two filters
                // Default to OR behavior
                for (Photo photo : photos) {
                    if (photoMatchesTagFilter(photo, filter1) || photoMatchesTagFilter(photo, filter2)) {
                        filteredPhotos.add(photo);
                    }
                }
            }
        }
        
        return filteredPhotos;
    }
    
    /**
     * Determines if a photo contains a tag that matches the provided tag filter.
     * 
     * @param photo the photo to check
     * @param filter the tag filter to match against
     * @return true if the photo has a tag that matches the filter; false otherwise
     */
    private boolean photoMatchesTagFilter(Photo photo, TagFilter filter) {
        for (Tag tag : photo.getTags()) {
            if (tag.getName().equalsIgnoreCase(filter.getName()) && 
                tag.getValue().equalsIgnoreCase(filter.getValue())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a photo's date falls within the specified date range.
     * 
     * @param photo the photo whose date is being checked
     * @param startDate the start date of the range (inclusive); can be null
     * @param endDate the end date of the range (inclusive); can be null
     * @return true if the photo's date is within the specified range; false otherwise
     */
    private boolean isWithinDateRange(Photo photo, LocalDate startDate, LocalDate endDate) {
        LocalDate photoDate = photo.getDate();
        
        if (startDate != null && endDate != null) {
            return !photoDate.isBefore(startDate) && !photoDate.isAfter(endDate);
        } else if (startDate != null) {
            return !photoDate.isBefore(startDate);
        } else if (endDate != null) {
            return !photoDate.isAfter(endDate);
        }
        
        return true; // No date filters
    }

    /**
     * Handles the search button action. Filters photos based on the current search text and filters,
     * removes duplicates, and displays the results in a new window.
     */
    @FXML
    private void handleSearch() {
        List<Photo> searchResults = filterPhotos();
        
        // Remove duplicate photos
        Set<String> seenPaths = new HashSet<>();
        List<Photo> uniquePhotos = new ArrayList<>();
        for (Photo photo : searchResults) {
            String path = photo.getFilepath(); // or another unique field
            if (seenPaths.add(path)) {
                uniquePhotos.add(photo);
            }
        }
        searchResults.clear();
        searchResults.addAll(uniquePhotos);
        
        try {
            FXMLLoader loader = 
                new FXMLLoader(getClass().getResource("/photos32/view/SearchResults.fxml"));
            Parent root = loader.load();

            SearchResultsPopupController controller = loader.getController();
            controller.setSearchResults(searchResults);
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setWidth(700);
            stage.setHeight(500);
            stage.setMinWidth(700);
            stage.setMinHeight(500);
            stage.initOwner(albumContainer.getScene().getWindow());
            stage.setTitle("Search Results");
            stage.setScene(new Scene(root));
            stage.show();

            controller.populatePhotoTiles();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
                stage.setTitle("Photos32 Login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
