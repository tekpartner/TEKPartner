package net.tekpartner.imagemanager.flickr;

import com.goebl.david.Webb;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * User: chandra@TEKPartner.net
 * Date: 3/22/16
 * Time: 11:14 PM
 * <p/>
 * Manager Class for Accessing List of Images From Flickr
 */
public class FlickrManager {
    private static final Logger logger = Logger.getLogger(FlickrManager.class.getName());

    private static final String TAG_VALID = "valid";
    private static final String TAG_SORT_ORDER = "SORT";
    private String apiKey = null;

    public static final String SIZE_SMALL_SQUARE_75X75_SUFFIX = "s";
    public static final String SIZE_LARGE_SQUARE_150X150_SUFFIX = "q";
    public static final String SIZE_THUMBNAIL_100_ON_LONGEST_SIDE_SUFFIX = "t";
    public static final String SIZE_SMALL_240_ON_LONGEST_SIDE_SUFFIX = "m";
    public static final String SIZE_SMALL_320_ON_LONGEST_SIDE_SUFFIX = "n";
    public static final String SIZE_MEDIUM_500_ON_LONGEST_SIDE_SUFFIX = "-";
    public static final String SIZE_MEDIUM_640_640_ON_LONGEST_SIDE_SUFFIX = "z";
    public static final String SIZE_MEDIUM_800_800_ON_LONGEST_SIDE_SUFFIX = "c";
    public static final String SIZE_LARGE_1024_ON_LONGEST_SIDE_SUFFIX = "b";

    /**
     * Constructor.
     *
     * @param apiKey - API Key for Flickr Account
     */
    public FlickrManager(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Get List of Photos
     *
     * @param userName   - Flickr User Name
     * @param tags       - Tags to search for
     * @param sizeSuffix - Size of the Image to return
     * @return
     */
    public List<FlickrPhoto> getPhotos(String userName, List<String> tags, String sizeSuffix) {
        return getPhotos(userName, tags, sizeSuffix, false, false);
    }

    /**
     * Get List of Photos
     *
     * @param userName    - Flickr User Name
     * @param tags        - Tags to search for
     * @param sizeSuffix  - Size of the Image to return
     * @param orderedList - Should the returned list be ordered by the tag starting with "SORT_ORDER_????" ("SORT_ORDER_1", "SORT_ORDER_2", ....)
     * @return
     */
    public List<FlickrPhoto> getPhotos(String userName, List<String> tags, String sizeSuffix, boolean orderedList) {
        return getPhotos(userName, tags, sizeSuffix, false, orderedList);
    }

    /**
     * Get List of Valid Photos
     *
     * @param userName   - Flickr User Name
     * @param tags       - Tags to search for
     * @param sizeSuffix - Size of the Image to return
     * @return
     */
    public List<FlickrPhoto> getValidPhotos(String userName, List<String> tags, String sizeSuffix) {
        return getPhotos(userName, tags, sizeSuffix, true, false);
    }

    /**
     * Get List of Valid Photos
     *
     * @param userName    - Flickr User Name
     * @param tags        - Tags to search for
     * @param sizeSuffix  - Size of the Image to return
     * @param orderedList - Should the returned list be ordered by the tag starting with "SORT_ORDER_????" ("SORT_ORDER_1", "SORT_ORDER_2", ....)
     * @return
     */
    public List<FlickrPhoto> getValidPhotos(String userName, List<String> tags, String sizeSuffix, boolean orderedList) {
        return getPhotos(userName, tags, sizeSuffix, true, orderedList);
    }

    /**
     * Get List of Photos
     *
     * @param userName    - Flickr User Name
     * @param tags        - Tags to search for
     * @param sizeSuffix  - Size of the Image to return
     * @param orderedList - Should the returned list be ordered by the tag starting with "SORT_ORDER_????" ("SORT_ORDER_1", "SORT_ORDER_2", ....)
     * @return
     */
    private List<FlickrPhoto> getPhotos(String userName, List<String> tags, String sizeSuffix, boolean onlyValid, boolean orderedList) {
        List<FlickrPhoto> flickrPhotos = new ArrayList();
        Webb webb = Webb.create();
        if (logger.isDebugEnabled()) {
            logger.debug("Getting Photo with Tags ------------------> " + this.formatTags(tags));
            logger.debug("Getting Photo with Tags ------------------> " + userName);
            logger.debug("Getting Photo with Tags ------------------> " + this.apiKey);
        }
        JSONObject result = webb
                .get("https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=" + this.apiKey + "&user_id=" + userName + "&tags=" + this.formatTags(tags) + "&format=json&nojsoncallback=1")
                .retry(1, false) // at most one retry, don't do exponential backoff
                .asJsonObject()
                .getBody();

        try {
            JSONArray listOfPhotos = (JSONArray) ((JSONObject) result.get("photos")).get("photo");
            for (int i = 0; i < listOfPhotos.length(); i++) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Getting Photo ------------------> " + i);
                }
                FlickrPhoto flickrPhoto = new FlickrPhoto(listOfPhotos.getJSONObject(i));
                JSONObject photoDetails = webb
                        .get("https://api.flickr.com/services/rest/?method=flickr.photos.getinfo&api_key=" + this.apiKey + "&format=json&nojsoncallback=1&photo_id=" + flickrPhoto.getId())
                        .retry(1, false) // at most one retry, don't do exponential backoff
                        .asJsonObject()
                        .getBody();
                JSONObject jsonPhotoDetails = (JSONObject) photoDetails.get("photo");
                Set setOfTags = this.getTags(photoDetails);
                if (logger.isDebugEnabled()) {
                    logger.debug(setOfTags.toString());
                }

                if ((!onlyValid) || (onlyValid && setOfTags.contains(FlickrManager.TAG_VALID))) {
                    flickrPhoto.setDescription((String) ((JSONObject) jsonPhotoDetails.get("description")).get("_content"));
                    String strFlickrImageURL = "https://farm" + flickrPhoto.getFarm() + ".staticflickr.com/" + flickrPhoto.getServer() + "/" + flickrPhoto.getId() + "_" + flickrPhoto.getSecret();

                    if (!StringUtils.isEmpty(sizeSuffix)) {
                        strFlickrImageURL = strFlickrImageURL.concat("_" + sizeSuffix);
                    }
                    strFlickrImageURL = strFlickrImageURL.concat(".jpg");

                    try {
                        flickrPhoto.setImageURL(new URL(strFlickrImageURL));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

                    Iterator<String> iterTags = setOfTags.iterator();
                    while (iterTags.hasNext()) {
                        String sortTag = (String) iterTags.next();
                        if (sortTag.startsWith(StringUtils.lowerCase(FlickrManager.TAG_SORT_ORDER))) {
                            flickrPhoto.setSortTag(sortTag);
                        }
                    }

                    flickrPhotos.add(flickrPhoto);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if (orderedList) {
            Collections.sort(flickrPhotos, new Comparator<FlickrPhoto>() {
                @Override
                public int compare(FlickrPhoto o1, FlickrPhoto o2) {
                    if (o1.getSortTag() == null) {
                        return 1;
                    } else if (o2.getSortTag() == null) {
                        return -1;
                    } else {
                        return o1.getSortTag().compareToIgnoreCase(o2.getSortTag());
                    }
                }
            });
        }

        return flickrPhotos;
    }

    /**
     * Format an array of tags
     *
     * @param tags - Array of Tags Strings
     * @return - Array Values as a String, delimited.
     */
    private String formatTags(List<String> tags) {
        String delimiter = ",";
        String formattedSetOfTags = null;

        for (String tag : tags) {
            if (formattedSetOfTags == null) {
                formattedSetOfTags = StringUtils.strip(tag);
            } else {
                formattedSetOfTags = formattedSetOfTags.concat(delimiter).concat(StringUtils.strip(tag));
            }
        }

        return formattedSetOfTags;
    }

    /**
     * Get the tags associated with a Photo
     *
     * @param photoDetails - Photo Details
     * @return - Set of Tags
     * @throws JSONException
     */
    private Set getTags(JSONObject photoDetails) throws JSONException {
        JSONArray tags = (JSONArray) ((JSONObject) ((JSONObject) photoDetails.get("photo")).get("tags")).get("tag");
        Set setOfTags = new HashSet();
        for (int i = 0; i < tags.length(); i++) {
            JSONObject aTag = tags.getJSONObject(i);
            setOfTags.add(StringUtils.lowerCase((String) aTag.get("raw")));
        }

        return setOfTags;
    }

    public static void main(String[] args) {
        final String apiKey = args[0];
        final String userName = args[1];
        String[] tags = Arrays.copyOfRange(args, 2, args.length);

        FlickrManager flickrManager = new FlickrManager(apiKey);
        List<FlickrPhoto> listOfFlickrPhotos = flickrManager.getValidPhotos(userName, Arrays.asList(tags), FlickrManager.SIZE_SMALL_240_ON_LONGEST_SIDE_SUFFIX, true);

        Iterator iterListOfFlickrPhotos = listOfFlickrPhotos.iterator();
        while (iterListOfFlickrPhotos.hasNext()) {
            FlickrPhoto flickrPhoto = (FlickrPhoto) iterListOfFlickrPhotos.next();

            if (logger.isInfoEnabled()) {
                logger.info(flickrPhoto.getImageURL().toString());
                logger.info(flickrPhoto.getTitle());
            }
        }
    }
}