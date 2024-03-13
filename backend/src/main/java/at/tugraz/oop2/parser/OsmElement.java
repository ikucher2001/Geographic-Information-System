package at.tugraz.oop2.parser;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public abstract class OsmElement {

    private String id;

    private Map<String, String> tags;

    public OsmElement(String id) {
        this.id = id;
        tags = new HashMap<>();

    }

    public void addTag(String key, String value) {
        tags.put(key, value);
    }

}

