package photos32.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    String username;
    List<Album> albums;
    List<TagType> tagTypes;

    public User(String username) {
        this.username = username;
        this.albums = new ArrayList<>();

        this.tagTypes = new ArrayList<>();
        tagTypes.add(new TagType("location", false));
        tagTypes.add(new TagType("person", true));
        tagTypes.add(new TagType("event", false));
    }

    public String getUsername() {
        return username;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public List<TagType> getTagTypes() {
        return tagTypes;
    }


    public Album getAlbumFromTitle(String title) {
        for (Album album : this.getAlbums()) {
            if (album.getTitle().equals(title)) return album;
        }
        return null;
    }
}
