package at.tugraz.oop2.model;

import java.util.Map;
import org.locationtech.jts.geom.LineString;

public class RoadSegment {
    private String name;
    private long id;
    private LineString geometry; // from JTS
    private Map<String, String> tags;

    public RoadSegment() {
        this.name = name;
        this.id = id;
        this.geometry = geometry;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public LineString getGeometry() {
        return geometry;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setGeometry(LineString geometry) {
        this.geometry = geometry;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

}
