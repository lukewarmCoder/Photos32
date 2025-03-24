package photos32.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Photo implements Serializable {
    private String filepath;
    private String caption;
    private LocalDateTime dateTime;
    private List<String> tags;
    
    public Photo(String filepath) {
        this.filepath = filepath;
        this.caption = "";
        this.dateTime = LocalDateTime.now();
        this.tags = new ArrayList<>();
    }

    // Getters and setters
    public String getFilepath() {
        return filepath;
    }
    
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
    
    public String getCaption() {
        return caption;
    }
    
    public void setCaption(String caption) {
        this.caption = caption;
    }
    
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void addTag(String tag) {
        tags.add(tag);
    }
    
    public void removeTag(String tag) {
        tags.remove(tag);
    }

    @Override
    public String toString() {
        return caption.isEmpty() ? filepath : caption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Photo photo = (Photo) o;
        
        // Compare based on filepath, caption, dateTime, and tags
        return filepath.equals(photo.filepath) && 
            caption.equals(photo.caption) && 
            dateTime.equals(photo.dateTime) && 
            tags.equals(photo.tags);
    }

}
