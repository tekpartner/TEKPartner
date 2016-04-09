package net.tekpartner.geocoding;

/**
 * Store TigerLine Data
 * User: cgaajula
 * Date: 3/29/16
 * Time: 10:21 PM
 */
public class TigerLine {
    private Long tigerLineId;
    private String side;

    public TigerLine() {
    }

    public Long getTigerLineId() {
        return tigerLineId;
    }

    public void setTigerLineId(Long tigerLineId) {
        this.tigerLineId = tigerLineId;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }
}