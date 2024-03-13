package at.tugraz.oop2.model;

public class BoundingBox {
    private Double tlX;
    private Double tlY;
    private Double brX;
    private Double brY;

    public BoundingBox(Double tlX, Double tlY, Double brX, Double brY) {
        this.tlX = tlX;
        this.tlY = tlY;
        this.brX = brX;
        this.brY = brY;
    }

    public Double getTlX() {
        return tlX;
    }

    public void setTlX(Double tlX) {
        if (tlX == null || tlX < 0) {
            throw new IllegalArgumentException("Top-left X coordinate cannot be null or negative.");
        }
        this.tlX = tlX;
    }

    public Double getTlY() {
        return tlY;
    }

    public void setTlY(Double tlY) {
        if (tlY == null || tlY < 0) {
            throw new IllegalArgumentException("Top-left Y coordinate cannot be null or negative.");
        }
        this.tlY = tlY;
    }

    public Double getBrX() {
        return brX;
    }

    public void setBrX(Double brX) {
        if (brX == null || brX < 0) {
            throw new IllegalArgumentException("Bottom-right X coordinate cannot be null or negative.");
        }
        if (this.tlX != null && brX <= this.tlX) {
            throw new IllegalArgumentException("Bottom-right X coordinate must be greater than top-left X coordinate.");
        }
        this.brX = brX;
    }

    public Double getBrY() {
        return brY;
    }

    public void setBrY(Double brY) {
        if (brY == null || brY < 0) {
            throw new IllegalArgumentException("Bottom-right Y coordinate cannot be null or negative.");
        }
        if (this.tlY != null && brY <= this.tlY) {
            throw new IllegalArgumentException("Bottom-right Y coordinate must be greater than top-left Y coordinate.");
        }
        this.brY = brY;
    }
}
