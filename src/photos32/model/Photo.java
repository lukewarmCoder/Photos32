package photos32.model;

import java.util.ArrayList;

public class Photo {
    private String filepath;
    private String caption;
    private ArrayList<Tag> tags;
    
    public Photo(String caption) {
        this.caption = caption;
        tags = new ArrayList<>();
    }
}
