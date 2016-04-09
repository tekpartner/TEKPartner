package net.tekpartner.geocoding;

/**
 * Store Address Components
 * User: cgaajula
 * Date: 3/29/16
 * Time: 10:21 PM
 */
public class AddressComponents {
    private Integer toAddress;
    private String preQualifier;
    private String preDirection;
    private String preType;
    private String streetName;
    private String suffixType;
    private String suffixDirection;
    private String suffixQualifier;
    private Integer fromAddress;
    private String state;
    private String zip;
    private String city;

    public Integer getToAddress() {
        return toAddress;
    }

    public void setToAddress(Integer toAddress) {
        this.toAddress = toAddress;
    }

    public String getPreQualifier() {
        return preQualifier;
    }

    public void setPreQualifier(String preQualifier) {
        this.preQualifier = preQualifier;
    }

    public String getPreDirection() {
        return preDirection;
    }

    public void setPreDirection(String preDirection) {
        this.preDirection = preDirection;
    }

    public String getPreType() {
        return preType;
    }

    public void setPreType(String preType) {
        this.preType = preType;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getSuffixType() {
        return suffixType;
    }

    public void setSuffixType(String suffixType) {
        this.suffixType = suffixType;
    }

    public String getSuffixDirection() {
        return suffixDirection;
    }

    public void setSuffixDirection(String suffixDirection) {
        this.suffixDirection = suffixDirection;
    }

    public String getSuffixQualifier() {
        return suffixQualifier;
    }

    public void setSuffixQualifier(String suffixQualifier) {
        this.suffixQualifier = suffixQualifier;
    }

    public Integer getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(Integer fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}