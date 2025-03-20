package photos32.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import photos32.model.Album;

import java.util.Optional;

    
    public class AlbumCardController {
        @FXML private VBox albumCard;
        @FXML private Label albumTitle;
        
        private Album album;
        private UserHomeController parentController;
        
        public void setAlbumTitle(String title) {
            albumTitle.setText(title);
        }

        public void setParentController(UserHomeController controller) {
            this.parentController = controller;
        }
        
        public void setAlbum(Album album) {
            this.album = album;
            albumTitle.setText(album.getTitle());
        }
        
        @FXML
        public void handleRename() {
            TextInputDialog dialog = new TextInputDialog(albumTitle.getText());
            dialog.setTitle("Rename Album");
            dialog.setHeaderText("Enter new name for the album:");
            dialog.setContentText("Name:");
            
            dialog.showAndWait().ifPresent(newName -> {
                if (!newName.trim().isEmpty()) {
                    albumTitle.setText(newName);
                    
                    if (album != null) {
                        album.setTitle(newName);
                    }
                }
            });

            parentController.saveUser();
            parentController.refresh();
        }
        
        @FXML
        public void handleDelete() {
            parentController.getUser().getAlbums().remove((album));
            parentController.saveUser();
            parentController.refresh();
        }
    }