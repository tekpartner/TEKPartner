package net.tekpartner.imagemanager.flickr;

import org.apache.log4j.Logger;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: chandra@TEKPartner.net
 * Date: 3/22/16
 * Time: 11:14 PM
 * <p/>
 * Manager Class for Accessing List of Images with transformations using Cloudinary
 * <p/>
 * Reference: http://cloudinary.com/blog/delivering_all_your_websites_images_through_a_cdn
 */
public class CloudinaryManager {
    private static final Logger logger = Logger.getLogger(CloudinaryManager.class.getName());

    private FlickrManager flickrManager = null;
    private String userName = null;
    private String cloudName = null;

    /**
     * Constructor.
     *
     * @param apiKey    - API Key for Flickr
     * @param userName  - User Name for Flickr
     * @param cloudName - Cloud Name for Cloudinary
     */
    public CloudinaryManager(String apiKey, String userName, String cloudName) {
        this.flickrManager = new FlickrManager(apiKey);
        this.userName = userName;
        this.cloudName = cloudName;
    }

    /**
     * Get a list of image urls for given set of tags.
     *
     * @param tags - Tags to search for
     * @return - List of Image URLs
     */
    public List getListOfImages(List<String> tags) throws URISyntaxException {
        List<FlickrPhoto> listOfFlickrImages = this.flickrManager.getPhotos(this.userName, tags, FlickrManager.SIZE_LARGE_1024_ON_LONGEST_SIDE_SUFFIX);
        List<String> listOfImages = new ArrayList();

        for (FlickrPhoto flickrImage : listOfFlickrImages) {
            String cloudinaryImage = "http://res.cloudinary.com/" + this.cloudName + "/image/fetch/w_250,h_250/" + flickrImage.getImageURL().toURI().toString();
            listOfImages.add(cloudinaryImage);
        }

        return listOfImages;
    }

    /**
     * Get a list of image urls for given set of tags.
     *
     * @param tags - Tags to search for
     * @return - List of Image URLs
     */
    public List getListOfValidImages(List<String> tags) throws URISyntaxException {
        List<FlickrPhoto> listOfFlickrImages = this.flickrManager.getValidPhotos(this.userName, tags, FlickrManager.SIZE_LARGE_1024_ON_LONGEST_SIDE_SUFFIX);
        List<String> listOfImages = new ArrayList();

        for (FlickrPhoto flickrImage : listOfFlickrImages) {
            String cloudinaryImage = "http://res.cloudinary.com/" + this.cloudName + "/image/fetch/w_250,h_250/" + flickrImage.getImageURL().toURI().toString();
            listOfImages.add(cloudinaryImage);
        }

        return listOfImages;
    }

    public static void main(String[] args) {
        final String apiKey = args[0];
        final String userName = args[1];
        final String cloudName = args[2];
        String[] tags = Arrays.copyOfRange(args, 3, args.length);

        CloudinaryManager cloudinaryManager = new CloudinaryManager(apiKey, userName, cloudName);
        List listOfImages = null;
        try {
            listOfImages = cloudinaryManager.getListOfImages(Arrays.asList(tags));
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if (logger.isInfoEnabled()) {
            logger.info(listOfImages);
        }
    }
}