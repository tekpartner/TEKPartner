package net.tekpartner.geocoding;

import com.goebl.david.Webb;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: chandra@TEKPartner.net
 * Date: 3/22/16
 * Time: 11:14 PM
 * <p/>
 * Manager Class for Fetching Addresses from a Geocoding Service
 * <p/>
 * Reference: https://geocoding.geo.census.gov/geocoder/Geocoding_Services_API.pdf
 */
public class GeocodingManager {
    private static final Logger logger = Logger.getLogger(GeocodingManager.class.getName());

    /**
     * Constructor.
     */
    public GeocodingManager() {
    }

    /**
     * Get List of Matching Addresses
     *
     * @param inputAddress - Input Address
     * @return
     */
    public List<MatchingAddress> getMatchingAddresses(String inputAddress) throws URISyntaxException {
        URI uri = new URI(
                "http",
                "geocoding.geo.census.gov",
                "/geocoder/locations/onelineaddress",
                "address=" + inputAddress + "&benchmark=9&format=json",
                null);
        List<MatchingAddress> matchingAddresses = new ArrayList();
        Webb webb = Webb.create();
        JSONObject result = webb
                .get(uri.toASCIIString())
                .retry(3, false) // at most one retry, don't do exponential backoff
                .asJsonObject()
                .getBody();

        try {
            JSONArray listOfAddresses = (JSONArray) ((JSONObject) result.get("result")).get("addressMatches");
            for (int i = 0; i < listOfAddresses.length(); i++) {
                MatchingAddress matchingAddress = new MatchingAddress(listOfAddresses.getJSONObject(i));
                matchingAddresses.add(matchingAddress);
            }
        } catch (JSONException e) {
            logger.error("Exception raised for data: " + inputAddress);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return matchingAddresses;
    }

    public static void main(String[] args) {
        final String inputAddress = args[0];

        GeocodingManager GeocodingManager = new GeocodingManager();
        List<MatchingAddress> listOfMatchingAddresses = null;
        try {
            listOfMatchingAddresses = GeocodingManager.getMatchingAddresses(inputAddress);
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        Iterator iterListOfMatchingAddresses = listOfMatchingAddresses.iterator();
        while (iterListOfMatchingAddresses.hasNext()) {
            MatchingAddress matchingAddress = (MatchingAddress) iterListOfMatchingAddresses.next();
            if (logger.isInfoEnabled()) {
                logger.info(matchingAddress.toString());
            }
        }
    }
}