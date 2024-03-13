package at.tugraz.oop2.tile;

import java.awt.*;

public class Colors {

    public static final Color BACKGROUND = new Color(255, 255, 255);
    public static final Color MOTORWAY = new Color(255, 0, 0);
    public static final Color TRUNK = new Color(255, 140, 0);
    public static final Color PRIMARY = new Color(255, 165, 0);
    public static final Color SECONDARY = new Color(255, 255, 0);
    public static final Color ROAD = new Color(128, 128, 128);
    public static final Color FOREST = new Color(173, 209, 158);
    public static final Color RESIDENTIAL = new Color(223, 233, 233);
    public static final Color VINEYARD = new Color(172, 224, 161);
    public static final Color GRASS = new Color(205, 235, 176);
    public static final Color RAILWAY = new Color(235, 219, 233);
    public static final Color WATER = new Color(0, 128, 255);


    public static Color getColor(String tag) {
        switch (tag) {
            case "motorway":
                return MOTORWAY;
            case "trunk":
                return TRUNK;
            case "primary":
                return PRIMARY;
            case "secondary":
                return SECONDARY;
            case "road":
                return ROAD;
            case "forest":
                return FOREST;
            case "residential":
                return RESIDENTIAL;
            case "vineyard":
                return VINEYARD;
            case "grass":
                return GRASS;
            case "railway":
                return RAILWAY;
            case "water":
                return WATER;
            default:
                return BACKGROUND;
        }
    }

}
