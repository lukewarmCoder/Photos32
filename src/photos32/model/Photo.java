package photos32.model;

import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class Photo implements Serializable {
    private String filepath;
    private String caption;
    private LocalDateTime dateTime;
    private List<Tag> tags;
    
    public Photo(String filepath) {
        this.filepath = filepath;
        this.caption = "";
        this.dateTime = getFileLastModifiedTime(filepath);
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
    
    public List<Tag> getTags() {
        return tags;
    }

    private LocalDateTime getFileLastModifiedTime(String filepath) {
        try {
            File file = new File(filepath);
            if (!file.exists()) return LocalDateTime.now(); // Fallback if file doesn't exist
            
            // Convert file's last modified time to LocalDateTime
            return Instant.ofEpochMilli(file.lastModified())
                          .atZone(ZoneId.systemDefault())
                          .toLocalDateTime();
        } catch (Exception e) {
            e.printStackTrace();
            return LocalDateTime.now(); // Fallback in case of an error
        }
    }
    
    public void addTag(Tag newTag) {
        // Check if the tag type allows multiple values
        if (!newTag.getTagType().isAllowMultipleValues()) {
            // Check if this tag type already exists for the photo
            if (tags.stream().anyMatch(existingTag -> 
                existingTag.getTagType().getName().equalsIgnoreCase(newTag.getTagType().getName()))) {
                throw new IllegalArgumentException("This tag type does not allow multiple values.");
            }
        }
        
        // If validation passes, add the tag
        tags.add(newTag);
    }

    public boolean hasTag(String tagName, String tagValue) {
        for (Tag tag : tags) {
            if (tag.getName().equals(tagName) && tag.getValue().equals(tagValue)) return true;
        }
        return false;
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
