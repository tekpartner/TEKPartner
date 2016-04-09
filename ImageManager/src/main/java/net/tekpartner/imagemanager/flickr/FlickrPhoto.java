package net.tekpartner.imagemanager.flickr;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * User: chandra@TEKPartner.net
 * Date: 3/26/16
 * Time: 2:15 PM
 */
public class FlickrPhoto {
    private long id;
    private Boolean isFamily;
    private Boolean isPublic;
    private Boolean isFriend;
    private Integer farm;
    private String owner;
    private String secret;
    private String server;
    private String title;
    private String description;
    private String sortTag;
    private URL imageURL;

    public FlickrPhoto() {
    }

    public FlickrPhoto(JSONObject jsonFlickrPhoto) {
        try {
            this.id = Long.valueOf((String) jsonFlickrPhoto.get("id")).longValue();
            this.isFamily = Boolean.valueOf("1".equals(String.valueOf(jsonFlickrPhoto.get("isfamily"))));
            this.isPublic = Boolean.valueOf("1".equals(String.valueOf(jsonFlickrPhoto.get("ispublic"))));
            this.isFriend = Boolean.valueOf("1".equals(String.valueOf(jsonFlickrPhoto.get("isfriend"))));
            this.farm = (Integer) jsonFlickrPhoto.get("farm");
            this.owner = (String) jsonFlickrPhoto.get("owner");
            this.secret = (String) jsonFlickrPhoto.get("secret");
            this.server = (String) jsonFlickrPhoto.get("server");
            this.title = (String) jsonFlickrPhoto.get("title");
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public URL getImageURL() {
        return imageURL;
    }

    public void setImageURL(URL imageURL) {
        this.imageURL = imageURL;
    }

    public Boolean getFamily() {
        return isFamily;
    }

    public void setFamily(Boolean family) {
        isFamily = family;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public Boolean getFriend() {
        return isFriend;
    }

    public void setFriend(Boolean friend) {
        isFriend = friend;
    }

    public Integer getFarm() {
        return farm;
    }

    public void setFarm(Integer farm) {
        this.farm = farm;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getSortTag() {
        return sortTag;
    }

    public void setSortTag(String sortTag) {
        this.sortTag = sortTag;
    }
}