package net.tekpartner.geocoding;

/**
 * Class to store Coordinates.
 * <p/>
 * User: cgaajula
 * Date: 3/29/16
 * Time: 10:21 PM
 */
public class Coordinates {
    private Double x;
    private Double y;

    public Coordinates() {
    }

    public Coordinates(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }
}