package at.tugraz.oop2.utils;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
public class BoundingBox {
    double north;
    double south;
    double east;
    double west;

    public BoundingBox(final int x, final int y, final int zoom) {
        this.north = tile2lat(y, zoom);
        this.south = tile2lat(y + 1, zoom);
        this.west = tile2lon(x, zoom);
        this.east = tile2lon(x + 1, zoom);
    }

    static double tile2lon(int x, int z) {
        return x / Math.pow(2.0, z) * 360.0 - 180;
    }

    static double tile2lat(int y, int z) {
        double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
        return Math.toDegrees(Math.atan(Math.sinh(n)));
    }

    public Polygon createBoundingBoxGeometry(BoundingBox bbox) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate[] coords = new Coordinate[]{
                new Coordinate(bbox.west, bbox.north), // Top left
                new Coordinate(bbox.east, bbox.north), // Top right
                new Coordinate(bbox.east, bbox.south), // Bottom right
                new Coordinate(bbox.west, bbox.south), // Bottom left
                new Coordinate(bbox.west, bbox.north)  // Closing the loop
        };
        return geometryFactory.createPolygon(coords);
    }

}

