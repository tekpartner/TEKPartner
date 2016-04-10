package net.tekpartner.hack4sac.voterregistration;

import com.google.common.base.CaseFormat;
import net.tekpartner.geocoding.GeocodingManager;
import net.tekpartner.geocoding.MatchingAddress;
import net.tekpartner.imagemanager.flickr.FlickrManager;
import net.tekpartner.imagemanager.flickr.FlickrPhoto;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cgaajula
 * Date: 3/31/16
 * Time: 12:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class Utility {
    private static final Logger logger = Logger.getLogger(Utility.class.getName());
    public static final String VALUE_DELIMITER = "_";

    private static final String flickrAPIKey = "<<<FLICKR_API_KEY>>>";
    private static final String flickrUserName = "<<<FLICKR_USER_NAE>>>";
    private static final String flickrTagPrefix = "saccounty_ppid_";
    private static final FlickrManager flickrManager = new FlickrManager(flickrAPIKey);
    private static final GeocodingManager geocodingManager = new GeocodingManager();

    public static String mergeValues(String category, String subCategory, String question) {
        String mergedValue = StringUtils.lowerCase(category);
        mergedValue = mergedValue.concat(Utility.VALUE_DELIMITER);
        mergedValue = mergedValue.concat(StringUtils.lowerCase(subCategory));
        mergedValue = mergedValue.concat(Utility.VALUE_DELIMITER);
        mergedValue = mergedValue.concat(StringUtils.lowerCase(question));
        return mergedValue;
    }

    public static String trimmer(String inputString) {
        if (inputString != null) {
            inputString = inputString.replaceFirst("\"", "");
            if (inputString.endsWith("\"")) {
                inputString = inputString.substring(0, inputString.length() - 1);
            }
            inputString = inputString.replace("\"\"", "INCHES");
        } else {
            inputString = StringUtils.EMPTY;
        }

        return inputString;
    }

    public static String camelCase(String inputString) {
        String spaceToUnderscore = inputString.trim().replaceAll(" +", "_").toUpperCase();
        String camelCase = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, spaceToUnderscore);
        return camelCase;
    }

    public static List<String> generateFlickrTags(String flickrTagPrefix, String pollingStationId) {
        List<String> flickrTags = new ArrayList<String>();
        flickrTags.add(flickrTagPrefix.trim().toLowerCase().concat(pollingStationId.trim().toLowerCase()));
        return flickrTags;
    }

    /**
     * Get the List of Images associated with the Polling Station in Flickr
     *
     * @param pollingStationId - Polling Station ID
     * @return
     */
    public static JSONArray getJSONArrayOfImagesForPollingStation(String pollingStationId) {
        JSONArray listOfJSONFlickrPhotos = new JSONArray();
        List<FlickrPhoto> flickrPhotos = Utility.getListOfImagesForPollingStation(pollingStationId);

        for (FlickrPhoto flickrPhoto : flickrPhotos) {
            JSONObject jsonFlickrPhoto = new JSONObject();
            try {
                jsonFlickrPhoto.put("URL", flickrPhoto.getImageURL().toString());
                jsonFlickrPhoto.put("DESCRIPTION", flickrPhoto.getDescription());
            } catch (JSONException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            listOfJSONFlickrPhotos.put(jsonFlickrPhoto);
        }

        return listOfJSONFlickrPhotos;
    }

    /**
     * Get the List of Images associated with the Polling Station in Flickr
     *
     * @param pollingStationId - Polling Station ID
     * @return
     */
    public static List<FlickrPhoto> getListOfImagesForPollingStation(String pollingStationId) {
        List<FlickrPhoto> flickrPhotos = flickrManager.getValidPhotos(
                flickrUserName,
                Utility.generateFlickrTags(flickrTagPrefix, pollingStationId),
                FlickrManager.SIZE_MEDIUM_640_640_ON_LONGEST_SIDE_SUFFIX,
                true);

        return flickrPhotos;
    }


    /**
     * Validate and Get the Complete Polling Station Address, given the street address and city.
     *
     * @param pollingStationId
     * @param pollingPlaceAddress - Street Address from CSV
     * @param pollingPlaceCity    - City Name
     * @return
     * @throws java.net.URISyntaxException
     */
    public static String getCompletePollingStationAddress(String pollingStationId, String pollingPlaceAddress, String pollingPlaceCity) throws URISyntaxException {
        String completePollingStationAddress = StringUtils.EMPTY;
        String concatenatedInputString = StringUtils.trimToEmpty(pollingPlaceAddress) + " " + StringUtils.trimToEmpty(pollingPlaceCity);

        if (!concatenatedInputString.trim().isEmpty()) {
            List<MatchingAddress> listMatchingAddresses = geocodingManager.getMatchingAddresses(concatenatedInputString, "<<<GeoEncodingAPIKey>>>");
            for (MatchingAddress matchingAddress : listMatchingAddresses) {
                completePollingStationAddress = matchingAddress.getMatchedAddress();
                break;
            }
        } else {
            logger.error("Address is EMPTY for Polling Station ID: " + pollingStationId);
        }

        return completePollingStationAddress;
    }
}