package photos32.model;

import java.io.Serializable;

/**
 * Represents a tag associated with a photo, consisting of a name, a value, and a tag type.
 * Tags can be used to categorize photos by specific attributes such as location, person, or event.
 */
public class Tag implements Serializable {
    private String name;
    private String value;
    private TagType tagType;

    /**
     * Constructs a new Tag with the specified name, value, and tag type.
     * 
     * @param name The name of the tag (e.g., "location", "person").
     * @param value The value associated with the tag (e.g., "Paris", "John Doe").
     * @param tagType The type of tag (e.g., whether it allows multiple values).
     */
    public Tag(String name, String value, TagType tagType) {
        this.name = name.trim();
        this.value = value.trim();
        this.tagType = tagType;
    }

    /**
     * Constructs a new Tag with the specified name and value, defaulting to a single-value tag type.
     * 
     * @param name The name of the tag (e.g., "location", "person").
     * @param value The value associated with the tag (e.g., "Paris", "John Doe").
     */
    public Tag(String name, String value) {
        this(name, value, new TagType(name, false)); // Default to single-value
    }

    /**
     * Returns the tag type associated with this tag.
     * 
     * @return The tag type.
     */
    public TagType getTagType() {
        return tagType;
    }

    /**
     * Returns the name of the tag.
     * 
     * @return The name of the tag.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the tag. Throws an exception if the name is null or empty.
     * 
     * @param name The new name for the tag.
     * @throws IllegalArgumentException If the name is null or empty.
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name cannot be null or empty");
        }
        this.name = name.trim();
    }

    /**
     * Returns the value associated with the tag.
     * 
     * @return The value of the tag.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the tag. Throws an exception if the value is null or empty.
     * 
     * @param value The new value for the tag.
     * @throws IllegalArgumentException If the value is null or empty.
     */
    public void setValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Tag value cannot be null or empty");
        }
        this.value = value.trim();
    }

    /**
     * Returns a string representation of the tag in the format "name=value".
     * 
     * @return The string representation of the tag.
     */
    @Override
    public String toString() {
        return name + "=" + value;
    }

    /**
     * Compares this tag to another object for equality.
     * Two tags are considered equal if they have the same name (ignoring case) and 
     * the same value (ignoring case).
     * 
     * @param o The object to compare this tag to.
     * @return True if the tags are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return name.equalsIgnoreCase(tag.name) && 
               value.equalsIgnoreCase(tag.value);
    }
}

