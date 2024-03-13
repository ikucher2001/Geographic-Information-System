package at.tugraz.oop2.model;

public class Geom {
    private String type; // Represents the type of geometric shape (e.g., "Point", "LineString", etc.)
    private double[] coordinates; // Represents the coordinates of the geometric shape

    // Constructors, getters, and setters

    public Geom() {
    }

    public Geom(String type, double[] coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }
}
