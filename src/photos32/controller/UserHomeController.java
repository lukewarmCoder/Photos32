package photos32.controller;

import java.io.*;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import photos32.model.Album;
import photos32.model.Photo;
import photos32.model.User;

public class UserHomeController {

    @FXML private FlowPane albumContainer;
    @FXML private Label userHomeHeader;
    @FXML private TextField searchField;
    @FXML private Button signOutButton;

    private User user;

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
                + "Examples:\n"
                + "- Date: 01-01-2023 to 12-31-2023\n"
                + "- Date Format: MM-dd-yyyy to MM-dd-yyyy"
                + "- Single tag: person=John OR location=Paris\n"
                + "- Tag: person=John AND location=Paris");
        alert.showAndWait();
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        System.out.println(query);

        if (query.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            showAlert(alert, "Information", "Empty Search", "Please enter a search term.");
            alert.showAndWait();
            return;
        }
        searchPhotos(query);
    }


    private void searchPhotos(String query) {

        // List to store matching photos
        List<Photo> searchResults = new ArrayList<>();

        // Check if query matches date range search
        if (isDateRangeSearch(query)) {
            System.out.println("isDate Search");
            searchResults = searchByDateRange(query);
        } 
        // Check if query matches tag-based search
        else if (isTagSearch(query)) {
            System.out.println("isTagSearch");
            // searchResults = searchByTags(query);
        }
        // Fallback to filename/caption search
        else {
            // searchResults = searchByNameOrCaption(query);
            System.out.println("is caption search");
        }

        // If no results found
        if (searchResults.isEmpty()) {
            Label noResultsLabel = new Label("No photos found matching the search criteria.");
            noResultsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
            albumContainer.getChildren().add(noResultsLabel);
            return;
        }

        // Display results similar to album view
        // displaySearchResults(searchResults);
    }

    private boolean isDateRangeSearch(String query) {
        return query.matches("\\d{2}-\\d{2}-\\d{4} to \\d{2}-\\d{2}-\\d{4}");
    }

    private boolean isTagSearch(String query) {
        // Check for AND or OR queries with tag-value pairs
        return query.contains(" AND ") || query.contains(" OR ") || 
               query.matches("\\w+=[^\\s]+");
        }

    private List<Photo> searchByDateRange(String query) {
        try {
            String[] parts = query.split(" to ");
            LocalDate startDate = LocalDate.parse(parts[0].replace("from ", ""));
            LocalDate endDate = LocalDate.parse(parts[1]);

            List<Photo> matchingPhotos = new ArrayList<>();
            for (Album album : user.getAlbums()) {
                matchingPhotos.addAll(
                    album.getPhotos().stream()
                        .filter(photo -> {
                            LocalDate photoDate = photo.getDateTime().toLocalDate();
                            return !photoDate.isBefore(startDate) && !photoDate.isAfter(endDate);
                        })
                        .collect(Collectors.toList())
                );
            }
            return matchingPhotos;
        } catch (Exception e) {
            showAlert(new Alert(Alert.AlertType.ERROR), 
                "Search Error", 
                "Invalid Date Format", 
                "Please use format: from YYYY-MM-DD to YYYY-MM-DD"
            );
            return Collections.emptyList();
        }
    }

    // private List<Photo> searchByNameOrCaption(String query) {
    //     List<Photo> matchingPhotos = new ArrayList<>();
    //     for (Album album : user.getAlbums()) {
    //         matchingPhotos.addAll(
    //             album.getPhotos().stream()
    //                 .filter(photo -> 
    //                     photo.getCaption().toLowerCase().contains(query.toLowerCase()) ||
    //                     photo.getFilepath().toLowerCase().contains(query.toLowerCase()))
    //                 .collect(Collectors.toList())
    //         );
    //     }
    //     return matchingPhotos;
    // }

    // private List<Photo> searchByTags(String query) {
    //     List<Photo> matchingPhotos = new ArrayList<>();
    
    //     // Single tag search
    //     if (!query.contains(" AND ") && !query.contains(" OR ")) {
    //         String[] tagParts = query.split("=");
    //         String tagType = tagParts[0];
    //         String tagValue = tagParts[1];
    
    //         for (Album album : user.getAlbums()) {
    //             matchingPhotos.addAll(
    //                 album.getPhotos().stream()
    //                     .filter(photo -> photo.hasTag(tagType, tagValue))
    //                     .collect(Collectors.toList())
    //             );
    //         }
    //     }
    //     // Conjunctive AND search
    //     else if (query.contains(" AND ")) {
    //         String[] tagPairs = query.split(" AND ");
    //         String[] tag1 = tagPairs[0].split("=");
    //         String[] tag2 = tagPairs[1].split("=");
    
    //         for (Album album : user.getAlbums()) {
    //             matchingPhotos.addAll(
    //                 album.getPhotos().stream()
    //                     .filter(photo -> 
    //                         photo.hasTag(tag1[0], tag1[1]) && 
    //                         photo.hasTag(tag2[0], tag2[1]))
    //                     .collect(Collectors.toList())
    //             );
    //         }
    //     }
    //     // Disjunctive OR search
    //     else if (query.contains(" OR ")) {
    //         String[] tagPairs = query.split(" OR ");
    //         String[] tag1 = tagPairs[0].split("=");
    //         String[] tag2 = tagPairs[1].split("=");
    
    //         for (Album album : user.getAlbums()) {
    //             matchingPhotos.addAll(
    //                 album.getPhotos().stream()
    //                     .filter(photo -> 
    //                         photo.hasTag(tag1[0], tag1[1]) || 
    //                         photo.hasTag(tag2[0], tag2[1]))
    //                     .collect(Collectors.toList())
    //             );
    //         }
    //     }
    
    //     return matchingPhotos;
    // }

    // private void displaySearchResults(List<Photo> photos) {
    //     Button createAlbumFromResults = new Button("Create Album from Search Results");
    //     createAlbumFromResults.setOnAction(e -> createAlbumFromSearchResults(photos));
    //     albumContainer.getChildren().add(createAlbumFromResults);
    
    //     for (Photo photo : photos) {
    //         try {
    //             FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos32/view/PhotoCard.fxml"));
    //             VBox photoCard = loader.load();
    
    //             PhotoCardController photoCardController = loader.getController();
    //             photoCardController.setPhoto(photo);
                
    //             albumContainer.getChildren().add(photoCard);
    //         } catch (IOException e) {
    //             e.printStackTrace();
    //         }
    //     }
    // }

    // private void createAlbumFromSearchResults(List<Photo> searchResults) {
    //     // Reuse album creation dialog logic
    //     boolean validInput = false;
    //     String title = null;
        
    //     while (!validInput) {
    //         TextInputDialog dialog = new TextInputDialog();
    //         dialog.setTitle("Create Album from Search Results");
    //         dialog.setHeaderText("Enter a name for the new album:");
    //         dialog.setContentText("Album Title:");
            
    //         Optional<String> result = dialog.showAndWait();
            
    //         if (!result.isPresent()) {
    //             return; // Exit if user cancels
    //         }
            
    //         title = result.get().trim();
            
    //         if (title.isEmpty()) {
    //             Alert alert = new Alert(Alert.AlertType.INFORMATION);
    //             showAlert(alert, "Information", "Error: Invalid Album Name", 
    //                     "Album names cannot be empty!");
    //             alert.showAndWait();
    //             continue;
    //         }
            
    //         if (isDuplicateAlbum(user, title)) {
    //             Alert alert = new Alert(Alert.AlertType.INFORMATION);
    //             showAlert(alert, "Information", "Error: Duplicate Album", 
    //                     "An album with that name already exists!");
    //             alert.showAndWait();
    //             continue;
    //         }
            
    //         validInput = true;
    //     }
        
    //     // Create new album and add search results
    //     Album newAlbum = new Album(title);
    //     newAlbum.getPhotos().addAll(searchResults);
    //     user.getAlbums().add(newAlbum);
        
    //     saveUser();
    //     populateAlbumTiles();
    // }




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
