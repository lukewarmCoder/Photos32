package photos32.controller;

import java.io.*;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import photos32.model.Album;
import photos32.model.User;

public class UserHomeController {

    @FXML private FlowPane albumContainer;
    @FXML private Label userHomeHeader;
    @FXML private TextField searchField;
    @FXML private Button signOutButton;
    @FXML private Button helpButton;

    private User user;
    private FilterPopUp filterPopup;
    private Button filterButton;
    private Button resetFilterButton;
    private HBox searchHBox;

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
    public void initialize() {
        // Find the HBox containing the search elements
        searchHBox = (HBox) searchField.getParent();
        
        // Create the filter button
        filterButton = new Button("Filter");
        filterButton.setOnAction(event -> showFilterPopup());
        
        // Create the reset filter button (initially not visible)
        resetFilterButton = new Button("Reset Filter");
        resetFilterButton.setVisible(false);
        resetFilterButton.setOnAction(event -> resetFilter());
        
        // Add buttons to the search HBox
        searchHBox.getChildren().add(searchHBox.getChildren().indexOf(helpButton), filterButton);
        searchHBox.getChildren().add(searchHBox.getChildren().indexOf(helpButton), resetFilterButton);
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
    private void handleHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("How to Use Search");
        alert.setContentText("Enter a date range, tags, or captions to find photos.\n\n"
                + "Search Formats:\n"
                + "- Date Range: MM-dd-yyyy to MM-dd-yyyy\n" // 01-01-2023 to 12-31-2023
                + "- Single Tag: person=John\n"
                + "- Tag Conjuction: person=John AND location=Paris\n"
                + "- Tag Disjunction: person=John OR location=Paris\n\n"
                + "Queries not following the above formats will assume to be captions");
        alert.showAndWait();
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();

        // Check if filter is applied
        if (filterPopup != null && filterPopup.isFilterApplied()) {
            Map<String, List<String>> tags = filterPopup.getSelectedTags();
            LocalDate startDate = filterPopup.getStartDate();
            LocalDate endDate = filterPopup.getEndDate();
            
            // Log filter parameters for debugging
            System.out.println("Search query: " + query);
            System.out.println("Filtering by tags: " + tags);
            System.out.println("Date range: " + startDate + " to " + endDate);
            
            // Implement search logic with filters and with/without a query here
            // searchPhotosWithFilters(query, tags, startDate, endDate);
        } else {
            // Regular search without filters
            System.out.println("Search query: " + query);
            // searchPhotos(query);
        }
        
    }

    private void showFilterPopup() {
        if (filterPopup == null) {
            filterPopup = new FilterPopUp(searchField.getScene().getWindow());
        }
        
        filterPopup.show();
        
        // If filters were applied, show the reset button
        if (filterPopup.isFilterApplied()) {
            resetFilterButton.setVisible(true);
        }
    }
    
    private void resetFilter() {
        if (filterPopup != null) {
            filterPopup.clearFilters();
            resetFilterButton.setVisible(false);
        }
    }


    private void searchPhotos(String query) {


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
