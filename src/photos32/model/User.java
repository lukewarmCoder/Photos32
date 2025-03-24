package photos32.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    String username;
    List<Album> albums;

    public User(String username) {
        this.username = username;
        this.albums = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public List<Album> getAlbums() {
        return albums;
    }


    public Album getAlbumFromTitle(String title) {
        for (Album album : this.getAlbums()) {
            if (album.getTitle().equals(title)) return album;
        }
        return null;
    }
}
