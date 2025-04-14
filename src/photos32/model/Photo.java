package photos32.model;

import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a photo with metadata such as filepath, caption, date/time of creation, and associated tags.
 * Provides functionality to add and retrieve tags, check if a photo has a specific tag, and manage photo details.
 */
public class Photo implements Serializable {
    private String filepath;
    private String caption;
    private LocalDateTime dateTime;
    private List<Tag> tags;
    
    /**
     * Constructs a Photo object with the given filepath.
     * The caption is set to an empty string, and the dateTime is set to the file's last modified time.
     * 
     * @param filepath The path to the photo file.
     */
    public Photo(String filepath) {
        this.filepath = filepath;
        this.caption = "";
        this.dateTime = getFileLastModifiedTime(filepath);
        this.tags = new ArrayList<>();
    }

    /**
     * Returns the filepath of the photo.
     * 
     * @return The filepath of the photo.
     */
    public String getFilepath() {
        return filepath;
    }
    
    /**
     * Sets the filepath of the photo.
     * 
     * @param filepath The new filepath of the photo.
     */
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
    
    /**
     * Returns the caption of the photo.
     * 
     * @return The caption of the photo.
     */
    public String getCaption() {
        return caption;
    }
    
    /**
     * Sets the caption of the photo.
     * 
     * @param caption The new caption of the photo.
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }
    
    /**
     * Returns the date and time the photo was taken or last modified.
     * 
     * @return The date and time the photo was taken or last modified.
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Returns the date (without the time) of when the photo was taken or last modified.
     * 
     * @return The date when the photo was taken or last modified.
     */
    public LocalDate getDate() {
        return dateTime.toLocalDate();
    }
    
     /**
     * Sets the date and time of when the photo was taken or last modified.
     * 
     * @param dateTime The new date and time for the photo.
     */
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    
    /**
     * Returns the list of tags associated with the photo.
     * 
     * @return The list of tags associated with the photo.
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * Retrieves the last modified time of the file at the given filepath as a LocalDateTime.
     * 
     * @param filepath The path to the photo file.
     * @return The last modified time of the photo file as a LocalDateTime.
     */
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
    
    /**
     * Adds a new tag to the photo. Throws an exception if the tag type doesn't allow multiple values
     * and the tag type already exists for the photo.
     * 
     * @param newTag The new tag to add to the photo.
     * @throws IllegalArgumentException If the tag type doesn't allow multiple values and the tag type already exists.
     */
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

    /**
     * Checks if the photo has a specific tag with the given name and value.
     * 
     * @param tagName The name of the tag.
     * @param tagValue The value of the tag.
     * @return True if the photo has the specified tag, false otherwise.
     */
    public boolean hasTag(String tagName, String tagValue) {
        for (Tag tag : tags) {
            if (tag.getName().equals(tagName) && tag.getValue().equals(tagValue)) return true;
        }
        return false;
    }

    /**
     * Returns a string representation of the photo. If the caption is not empty, it returns the caption; 
     * otherwise, it returns the filepath.
     * 
     * @return The string representation of the photo (caption or filepath).
     */
    @Override
    public String toString() {
        return caption.isEmpty() ? filepath : caption;
    }

    /**
     * Compares this photo to another object for equality. Two photos are considered equal if they have 
     * the same filepath, caption, date/time, and tags.
     * 
     * @param o The object to compare this photo to.
     * @return True if the photos are equal, false otherwise.
     */
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
