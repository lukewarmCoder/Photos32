package photos32.model;

import java.io.Serializable;

public class TagType implements Serializable {
    private String name;
    private boolean allowMultipleValues;

    public TagType(String name, boolean allowMultipleValues) {
        this.name = name.trim();
        this.allowMultipleValues = allowMultipleValues;
    }

    public String getName() {
        return name;
    }

    public boolean isAllowMultipleValues() {
        return allowMultipleValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagType tagType = (TagType) o;
        return allowMultipleValues == tagType.allowMultipleValues && 
               name.equalsIgnoreCase(tagType.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
