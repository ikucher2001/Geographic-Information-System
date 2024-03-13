package at.tugraz.oop2.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import at.tugraz.oop2.parser.OsmNode;

import java.util.ArrayList;
import java.util.Map;

@Data
@Setter
@Getter
public class Road {
    private String name;
    private Long id;
    private JSONObject geom;
    private Map<String, String> tags;
    private String type;
    private ArrayList<Long> child_ids;

    public Road(long id, String name, String geom, Map<String, String> tags, String type, ArrayList<OsmNode> nodes) {
        this.name = name;
        this.id = id;
        this.tags = tags;
        this.type = type;
        this.child_ids = new ArrayList<>();

        for(OsmNode node : nodes) {
            this.child_ids.add(Long.parseLong(node.getId()));
        }

        try{
            JSONParser parser = new JSONParser();
            this.geom = (JSONObject) parser.parse(geom);
        }
        catch (ParseException e) {
            this.geom = null;
        }
    }
}

