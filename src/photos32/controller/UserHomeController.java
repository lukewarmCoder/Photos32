package photos32.controller;

import java.io.*;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import photos32.controller.FilterController.FilterCriteria;
import photos32.controller.FilterController.TagFilter;
import photos32.model.Album;
import photos32.model.TagType;
import photos32.model.User;

public class UserHomeController {

    @FXML private Label userHomeHeader;
    @FXML private Button signOutButton;
    @FXML private FlowPane albumContainer;

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button filterButton;
    @FXML private Button resetFilterButton;

    private User user;

    private Map<String, String> tagFilters = new HashMap<>();
    private String logicOperator = null;
    private LocalDate fromDate = null;
    private LocalDate toDate = null;
    private boolean filtersActive = false;

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

    @FXML
    public void initialize() {
        
    }

    // Append the album cards for each user to the album container in UserHome.fxml
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
                showAlert(alert, "Information", "Error: Invalid Album Name", 
                        "Album names cannot be empty!");
                alert.showAndWait();
                continue; // Go back to the start of the loop
            }
            
            // Check for duplicate titles
            if (isDuplicateAlbum(user, title)) {
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
        
        createAlbum(user, title);
        saveUser();
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

    public void openAlbum(Album album) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos32/view/AlbumView.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Get the controller and pass necessary data
            AlbumViewController controller = loader.getController();
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
            System.out.println(tagFilter.getName());
        }
        System.out.println(currentFilterCriteria.getLogicalOperator());
        System.out.println(currentFilterCriteria.getStartDate());
        System.out.println(currentFilterCriteria.getEndDate());
    }

    @FXML
    private void handleFilter() {
        try {
            // Load the filter window FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos32/view/FilterWindow.fxml"));
            Parent root = loader.load();
            
            // Get controller and setup with available tag names
            FilterController filterController = loader.getController();
            
            // Get available tag names
            List<String> availableTagNames = new ArrayList<>();
            for (TagType tagType : user.getTagTypes()) {
                availableTagNames.add(tagType.getName());
            }
            
            // Create new stage for filter window
            Stage filterStage = new Stage();
            filterStage.setTitle("Filter Photos");
            filterStage.setHeight(400);
            filterStage.setWidth(550);
            filterStage.setMinHeight(400);
            filterStage.setMinWidth(550);
            filterStage.initModality(Modality.APPLICATION_MODAL); // Block input to other windows
            filterStage.initOwner(filterButton.getScene().getWindow());
            
            // Setup the controller with existing criteria (if any)
            filterController.setup(this, availableTagNames, filterStage, currentFilterCriteria);
            
            // Set scene and show the window
            Scene scene = new Scene(root);
            filterStage.setScene(scene);
            filterStage.showAndWait();
            
        } catch (IOException e) {
            // showErrorAlert("Error opening filter window", e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleResetFilter() {
       // Clear any active filters
        currentFilterCriteria = null;
        
        // Update UI to reflect no active filters
        resetFilterButton.setVisible(false);
        
        // Refresh the displayed albums/photos based on no filters
        // refreshDisplay();
    }

    /**
     * Apply the filter criteria passed from the FilterController
     */
    public void applyFilterCriteria(FilterController.FilterCriteria criteria) {
        System.out.println(criteria);
        if (criteria != null && criteria.hasFilters()) {
            // Store the current filter criteria
            this.currentFilterCriteria = criteria;
            
            // Show reset filter button since we now have active filters
            resetFilterButton.setVisible(true);
            
            // Refresh the display with filters applied
            // refreshDisplay();
        } else {
            // If criteria is empty, just reset filters
            handleResetFilter();
        }
    }

    @FXML
    private void handleSignOut() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        showAlert(alert, "Confirmation", null, "Are you sure you want to sign out?");

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

    public void saveUser() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/" + user.getUsername() + ".dat"))) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert alert, String title, String header, String content) {
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
    }

}
