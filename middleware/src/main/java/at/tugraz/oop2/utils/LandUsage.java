package at.tugraz.oop2.utils;
import org.locationtech.jts.geom.Geometry;
import java.awt.Color;

import at.tugraz.oop2.utils.Colors;
public class LandUsage {
    private String type;
    private Geometry geometry;
    private Color color;

    public LandUsage(String type, Geometry geometry, Color color) {
        this.type = type;
        this.geometry = geometry;
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

}
