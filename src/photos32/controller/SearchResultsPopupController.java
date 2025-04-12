package photos32.controller;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import photos32.model.Album;
import photos32.model.Photo;

public class SearchResultsPopupController {

    @FXML private FlowPane photoFlowPane;   

    private UserHomeController parentController;
    private List<Photo> searchResults;

    public void setSearchResults(List<Photo> searchResults) {
        this.searchResults = searchResults;
    }

    public void setParentController(UserHomeController parentController) {
        this.parentController = parentController;
    }
    
    @FXML
    private void handleCreateAlbum() {
        System.out.println("Album created!");
    }

    @FXML
    private void handleBack() {
        System.out.println("we back");
    }

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
