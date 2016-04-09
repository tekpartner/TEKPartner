package net.tekpartner.geocoding;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Store the Matching Address
 *
 * User: chandra@TEKPartner.net
 * Date: 3/26/16
 * Time: 2:15 PM
 */
public class MatchingAddress {
    private String matchedAddress;
    private Coordinates coordinates;
    private TigerLine tigerLine;
    private AddressComponents addressComponents;

    public MatchingAddress() {
    }

    public MatchingAddress(JSONObject jsonMatchingAddress) {
        try {
            this.matchedAddress = (String) jsonMatchingAddress.get("matchedAddress");
            this.coordinates = getCoordinates(jsonMatchingAddress);
            this.tigerLine = getTigerLine(jsonMatchingAddress);
            this.addressComponents = getAddressComponents(jsonMatchingAddress);
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public String getMatchedAddress() {
        return matchedAddress;
    }

    public void setMatchedAddress(String matchedAddress) {
        this.matchedAddress = matchedAddress;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public TigerLine getTigerLine() {
        return tigerLine;
    }

    public void setTigerLine(TigerLine tigerLine) {
        this.tigerLine = tigerLine;
    }

    public AddressComponents getAddressComponents() {
        return addressComponents;
    }

    public void setAddressComponents(AddressComponents addressComponents) {
        this.addressComponents = addressComponents;
    }

    private Coordinates getCoordinates(JSONObject input) throws JSONException {
        Double x = (Double) ((JSONObject) input.get("coordinates")).get("x");
        Double y = (Double) ((JSONObject) input.get("coordinates")).get("y");

        return new Coordinates(x, y);
    }

    private TigerLine getTigerLine(JSONObject input) throws JSONException {
        Long tigerLineId = Long.valueOf((String) ((JSONObject) input.get("tigerLine")).get("tigerLineId"));
        String side = (String) ((JSONObject) input.get("tigerLine")).get("side");

        TigerLine tigerLine = new TigerLine();
        tigerLine.setTigerLineId(tigerLineId);
        tigerLine.setSide(side);

        return tigerLine;
    }

    private AddressComponents getAddressComponents(JSONObject input) throws JSONException {
        Integer fromAddress = Integer.valueOf((String) ((JSONObject) input.get("addressComponents")).get("fromAddress"));
        Integer toAddress = Integer.valueOf((String) ((JSONObject) input.get("addressComponents")).get("toAddress"));
        String preQualifier = (String) ((JSONObject) input.get("addressComponents")).get("preQualifier");
        String preDirection = (String) ((JSONObject) input.get("addressComponents")).get("preDirection");
        String preType = (String) ((JSONObject) input.get("addressComponents")).get("preType");
        String streetName = (String) ((JSONObject) input.get("addressComponents")).get("streetName");
        String suffixType = (String) ((JSONObject) input.get("addressComponents")).get("suffixType");
        String suffixDirection = (String) ((JSONObject) input.get("addressComponents")).get("suffixDirection");
        String suffixQualifier = (String) ((JSONObject) input.get("addressComponents")).get("suffixQualifier");
        String state = (String) ((JSONObject) input.get("addressComponents")).get("state");
        String zip = (String) ((JSONObject) input.get("addressComponents")).get("zip");
        String city = (String) ((JSONObject) input.get("addressComponents")).get("city");

        AddressComponents addressComponents = new AddressComponents();
        addressComponents.setFromAddress(fromAddress);
        addressComponents.setToAddress(toAddress);
        addressComponents.setPreQualifier(preQualifier);
        addressComponents.setPreDirection(preDirection);
        addressComponents.setPreType(preType);
        addressComponents.setStreetName(streetName);
        addressComponents.setSuffixType(suffixType);
        addressComponents.setSuffixDirection(suffixDirection);
        addressComponents.setSuffixQualifier(suffixQualifier);
        addressComponents.setState(state);
        addressComponents.setZip(zip);
        addressComponents.setCity(city);

        return addressComponents;
    }
}