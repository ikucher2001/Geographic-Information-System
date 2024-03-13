package at.tugraz.oop2.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class Road {
    private String name;
    private Long id;
    private Geom geom;
    private Map<String, String> tags;
    private String type;

    public Road() {
        this.name = name;
        this.id = id;
        this.geom = geom;
        this.tags = tags;
        this.type = type;
    }

    @Data
    @NoArgsConstructor
    private static class GeoJSON extends Geom {
        private Map<String, Object> crs;
    }

    public void setId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be positive and non-null.");
        }
        this.id = id;
    }

    public static List<Road> getDummyData() {
        List<Road> dummyRoadData = new ArrayList<>();
        Road road1 = new Road();
        road1.setName("Sandgasse");
        road1.setId(32685265L);
        GeoJSON geoJSON1 = new GeoJSON();
        geoJSON1.setType("LineString");
        geoJSON1.setCoordinates(new double[] {15.45578, 47.058266, 15.456056, 47.058384, 15.456229, 47.058456});
        road1.setGeom(geoJSON1);
        // Set other attributes and tags for road1

        Road road2 = new Road();
        road2.setName("Another Road");
        road2.setId(12345678L);
        GeoJSON geoJSON2 = new GeoJSON();
        geoJSON2.setType("LineString");
        geoJSON2.setCoordinates(new double []{15.123456, 47.654321, 15.789012, 47.987654});
        road2.setGeom(geoJSON2);
        // Set other attributes and tags for road2

        dummyRoadData.add(road1);
        dummyRoadData.add(road2);
        return dummyRoadData;
    }

}

