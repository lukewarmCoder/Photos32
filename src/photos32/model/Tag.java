package photos32.model;

import java.io.Serializable;

public class Tag implements Serializable, Comparable<Tag> {
    private String name;
    private String value;
    private TagType tagType;

    public Tag(String name, String value, TagType tagType) {
        this.name = name.trim();
        this.value = value.trim();
        this.tagType = tagType;
    }

    public Tag(String name, String value) {
        this(name, value, new TagType(name, false)); // Default to single-value
    }

    // Getter for TagType
    public TagType getTagType() {
        return tagType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name cannot be null or empty");
        }
        this.name = name.trim();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Tag value cannot be null or empty");
        }
        this.value = value.trim();
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return name.equalsIgnoreCase(tag.name) && 
               value.equalsIgnoreCase(tag.value);
    }

    @Override
    public int hashCode() {
        return (name.toLowerCase() + "=" + value.toLowerCase()).hashCode();
    }

    @Override
    public int compareTo(Tag other) {
        // First compare by name, then by value
        int nameComparison = this.name.compareToIgnoreCase(other.name);
        if (nameComparison != 0) {
            return nameComparison;
        }
        return this.value.compareToIgnoreCase(other.value);
    }

}

