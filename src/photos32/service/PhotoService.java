package photos32.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import photos32.model.Album;

public class PhotoService {

    /**
     * Displays a dialog to select one or more destination albums.
     * 
     * @param userAlbums all available user albums.
     * @param currAlbum the album the photo is currently in
     * 
     * @return a list of selected album titles, or an empty list if none selected
     */
    public static List<String> showAlbumSelectionDialog(List<Album> userAlbums, String currAlbum) {
        Dialog<List<String>> dialog = createDialog(userAlbums, currAlbum);
        Optional<List<String>> result = dialog.showAndWait();
        return result.orElse(Collections.emptyList());
    }

    /**
     * Creates the album selection dialog.
     *
     * @param userAlbums all available user albums.
     * @param currAlbum the album the photo is currently in.
     * 
     * @return the dialog instance
     */
    public static Dialog<List<String>> createDialog(List<Album> userAlbums, String currAlbum) {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Select Destination Album(s)");
    
        ListView<String> albumListView = createAlbumListView(userAlbums, currAlbum);
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

    /**
     * Creates a list view of albums to display in the album selection dialog.
     *
     * @param userAlbums all available user albums.
     * @param currAlbum the album the photo is currently in.
     * 
     * @return the {@link ListView} of album titles
     */
    public static ListView<String> createAlbumListView(List<Album> userAlbums, String currAlbum) {
        ListView<String> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    
        for (Album album : userAlbums) {
            if (!album.getTitle().equals(currAlbum)) {
                listView.getItems().add(album.getTitle());
            }
        }
    
        return listView;
    }
}
