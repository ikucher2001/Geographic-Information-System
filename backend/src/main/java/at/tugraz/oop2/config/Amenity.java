package at.tugraz.oop2.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;

@Data
@Getter
@Setter
public class Amenity {
    private String name;
    private Long id;
    private JSONObject geom;
    private Map<String, String> tags;
    private String type;

    public Amenity(long id, String name, String geom, Map<String, String> tags, String type) {
        this.name = name;
        this.id = id;
        this.tags = tags;
        this.type = type;

        try{
            JSONParser parser = new JSONParser();
            this.geom = (JSONObject) parser.parse(geom);
        }
        catch (ParseException e) {
            this.geom = null;
        }
    }
}
