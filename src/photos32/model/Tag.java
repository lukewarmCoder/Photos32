package photos32.model;

import java.io.Serializable;

public class Tag implements Serializable {
    private String name;
    private String value;

    public Tag(String name, String value) {
        this.name = name.trim();
        this.value = value.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
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
        return name.equalsIgnoreCase(tag.name) && value.equalsIgnoreCase(tag.value);
    }

    @Override
    public int hashCode() {
        return (name.toLowerCase() + "=" + value.toLowerCase()).hashCode();
    }

}
