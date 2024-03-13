package at.tugraz.oop2.model;

import java.util.List;

public class Route {
    private double length;
    private double time;
    private List<RoadSegment> roads;

    public Route() {
        this.length = length;
        this.time = time;
        this.roads = roads;
    }

    public double getLength() {
        return length;
    }

    public double getTime() {
        return time;
    }

    public List<RoadSegment> getRoads() {
        return roads;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void setRoads(List<RoadSegment> roads) {
        this.roads = roads;
    }

}
