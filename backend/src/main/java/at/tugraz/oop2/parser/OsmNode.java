package at.tugraz.oop2.parser;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class OsmNode extends OsmElement {

    private final String lat;
    private final String lon;

    @Builder
    public OsmNode(String id, String lat, String lon) {
        super(id);
        this.lat = lat;
        this.lon = lon;
    }

}

