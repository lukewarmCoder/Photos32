package photos32.model;

import java.io.Serializable;

/**
 * Represents a tag type that can be applied to a photo.
 * Each tag type has a name and a flag indicating whether it allows multiple values.
 */
public class TagType implements Serializable {
    private String name;
    private boolean allowMultipleValues;

    /**
     * Constructs a new TagType with the specified name and whether it allows multiple values.
     * 
     * @param name The name of the tag type (e.g., "location", "person").
     * @param allowMultipleValues A flag indicating whether multiple values can be associated with this tag type.
     */
    public TagType(String name, boolean allowMultipleValues) {
        this.name = name.trim();
        this.allowMultipleValues = allowMultipleValues;
    }

    /**
     * Returns the name of this tag type.
     * 
     * @return The name of the tag type.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns whether this tag type allows multiple values to be associated with it.
     * 
     * @return True if multiple values are allowed, false otherwise.
     */
    public boolean isAllowMultipleValues() {
        return allowMultipleValues;
    }

    /**
     * Compares this tag type to another object for equality.
     * Two tag types are considered equal if they have the same name (ignoring case) and 
     * the same value for the allowMultipleValues field.
     * 
     * @param o The object to compare this tag type to.
     * @return True if the tag types are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagType tagType = (TagType) o;
        return allowMultipleValues == tagType.allowMultipleValues && 
               name.equalsIgnoreCase(tagType.name);
    }

    /**
     * Returns a string representation of this tag type, which is simply its name.
     * 
     * @return The name of the tag type.
     */
    @Override
    public String toString() {
        return name;
    }
}