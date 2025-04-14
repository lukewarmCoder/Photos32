package photos32.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user in the system, including their username, albums, and tag types.
 * This class is Serializable, allowing user data to be saved and loaded from a file.
 */
public class User implements Serializable {
    String username;
    List<Album> albums;
    List<TagType> tagTypes;

    /**
     * Constructs a new User with the given username.
     * Initializes albums as an empty list and sets up default tag types.
     * 
     * @param username The username of the user.
     */
    public User(String username) {
        this.username = username;
        this.albums = new ArrayList<>();

        this.tagTypes = new ArrayList<>();
        tagTypes.add(new TagType("location", false));
        tagTypes.add(new TagType("person", true));
        tagTypes.add(new TagType("event", false));
    }

    /**
     * Returns the username of the user.
     * 
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the list of albums associated with the user.
     * 
     * @return A list of the user's albums.
     */
    public List<Album> getAlbums() {
        return albums;
    }

    /**
     * Returns the list of tag types that the user can apply to photos.
     * 
     * @return A list of tag types.
     */
    public List<TagType> getTagTypes() {
        return tagTypes;
    }

    /**
     * Retrieves an album by its title.
     * 
     * @param title The title of the album to search for.
     * @return The album with the matching title, or null if no such album exists.
     */
    public Album getAlbumFromTitle(String title) {
        for (Album album : this.getAlbums()) {
            if (album.getTitle().equals(title)) return album;
        }
        return null;
    }

    /**
     * Retrieves all photos from all albums associated with the user.
     * 
     * @return A list of all photos across the user's albums.
     */
    public List<Photo> getAllPhotos() {
        List<Photo> allPhotos = new ArrayList<>();
        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                allPhotos.add(photo);
            }
        }
        return allPhotos;
    }
}
