package at.tugraz.oop2.tile;

import lombok.Getter;

@Getter
public class BoundingBox {

    //code from
    //https://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Java

    private double north;
    private double south;
    private double east;
    private double west;

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
}

