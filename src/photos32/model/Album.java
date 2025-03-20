package photos32.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Album implements Serializable {
    private String title;
    private List<Photo> photos;

    public Album(String title) {
        this.title = title;
        photos = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPhotoCount() {
        return photos.size();
    }

    // public Date getEarliestDate() { /* logic here */ }
    // public Date getLatestDate() { /* logic here */ }
    
}
