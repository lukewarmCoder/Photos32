package photos32.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an album containing a list of photos. 
 * Each album has a title and a collection of photos.
 */
public class Album implements Serializable {
    private String title;
    private List<Photo> photos;

    /**
     * Constructs an Album with the specified title. 
     * Initializes the photo list as an empty list.
     * 
     * @param title The title of the album.
     */
    public Album(String title) {
        this.title = title;
        photos = new ArrayList<>();
    }

    /**
     * Returns the title of the album.
     * 
     * @return The title of the album.
     */
    public String getTitle() {
        return title;
    } 

    /**
     * Returns the list of photos in the album.
     * 
     * @return The list of photos in the album.
     */
    public List<Photo> getPhotos() {
        return photos;
    }

    /**
     * Sets the title of the album.
     * 
     * @param title The new title of the album.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the number of photos in the album.
     * 
     * @return The number of photos in the album.
     */
    public int getPhotoCount() {
        return photos.size();
    }
    
}
