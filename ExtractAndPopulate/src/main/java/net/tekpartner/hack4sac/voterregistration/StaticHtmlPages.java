package net.tekpartner.hack4sac.voterregistration;

import net.tekpartner.imagemanager.flickr.FlickrPhoto;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by DELL on 3/31/2016.
 */
public class StaticHtmlPages {
    private static final Logger logger = Logger.getLogger(StaticHtmlPages.class.getName());
    private static final int MAX_NUMBER_OF_PHOTOS_PER_POLLING_STATION = 5;

    public StaticHtmlPages() {
    }

    public void createHtmlPage(Map<String, PollingStation> graphData) {
        Iterator<String> ppid = graphData.keySet().iterator();

        StringBuffer address = new StringBuffer();
        String pollingPlaceName = StringUtils.EMPTY;
        StringBuffer mainData = new StringBuffer();
        HashMap<String, Integer> allSubCategories = new HashMap<>();
        while (ppid.hasNext()) {
            address.delete(0, address.length());
            String poll_id = ppid.next();
            List<FlickrPhoto> flickrPhotos = Utility.getListOfImagesForPollingStation(poll_id);
            if (logger.isDebugEnabled()) {
                logger.debug("Polling Station ID: " + poll_id);
            }
            String pollingplace_address = StringUtils.EMPTY;
            String city = StringUtils.EMPTY;
            PollingStation p = graphData.get(poll_id);
            Iterator<String> section = p.getSections().iterator();
            while (section.hasNext()) {
                String s = Utility.trimmer(section.next());
                double d = Double.parseDouble(s);
                int i = (int) d;
                allSubCategories.put(s, i);
            }
            //logger.debug("value is:"+requireMapping);

            section = p.getSections().iterator();
            while (section.hasNext()) {
                String test = Utility.trimmer(section.next());
                //logger.debug(test);

                Iterator<String> category = p.getCategories(test).iterator();
                while (category.hasNext()) {
                    String thisCategory = category.next();
                    //logger.debug("Category : " + thisCategory);
                    Iterator<String> subcategory = p.getSubCategories(Utility.trimmer(test), Utility.trimmer(thisCategory)).iterator();
                    while (subcategory.hasNext()) {
                        String thisSubcategory = subcategory.next();
                        //logger.debug("thisSubcategory : " + thisSubcategory);
                        Iterator<Question> question = p.getQuestions(Utility.trimmer(test), Utility.trimmer(thisCategory), Utility.trimmer(thisSubcategory)).iterator();

                        while (question.hasNext()) {
                            Question thisQuestion = question.next();
                            if (test.toString().equals("0")) {
                                //Name of polling station
                                if (thisQuestion.getQuestion().equals("Polling Place address")) {
                                    pollingplace_address = thisQuestion.getAnswer();
                                }
                                if (thisQuestion.getQuestion().equals("City")) {
                                    city = thisQuestion.getAnswer();
                                }
                                if (thisQuestion.getQuestion().equals("Polling Place name")) {
                                    pollingPlaceName = thisQuestion.getAnswer();
                                }

                            } else {
                                // Code to create dynamic table rows

                                String strv = "  <tr>" +
                                        "		<td>" + thisCategory + "</td>" +
                                        "		<td>" + thisSubcategory + "</td>" +
                                        "		<td>" + thisQuestion.getQuestion() + "</td>" +
                                        "		<td>" + thisQuestion.getAnswer() + "</td>" +
                                        "		<td>" + thisQuestion.getData() + "</td>" +
                                        "		<td>" + thisQuestion.getComments() + "</td>	" +
                                        "      </tr>" +
                                        "	   <tr>";
                                mainData.append(strv);

                                logger.debug("::::::::::::::::::::::::::::::::::::::::::");
                                logger.debug("Subcategory Id :" + test);
                                logger.debug("Category : " + thisCategory);
                                logger.debug("thisSubcategory : " + thisSubcategory);
                                logger.debug("Questions : " + thisQuestion.getQuestion());
                                logger.debug("Answer : " + thisQuestion.getAnswer());
                                logger.debug("Data : " + thisQuestion.getData());
                                logger.debug("Comments : " + thisQuestion.getComments());
                                logger.debug("::::::::::::::::::::::::::::::::::::::::::");
                            }
                        }
                    }

                }
            }

            try {
                address.append(Utility.getCompletePollingStationAddress(poll_id, pollingplace_address, city));
            } catch (URISyntaxException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            this.writeHtmlFile(poll_id, getStaticPartOfHtmlPage(pollingPlaceName, address.toString(), flickrPhotos) + mainData.toString() + getHtmlFooter());
        }
    }


    private String getHtmlFooter() {
        return "</table>" + "</body>" + "</html>";
    }

    private String getStaticPartOfHtmlPage(String pollingPlaceName, String address, List<FlickrPhoto> flickrPhotos) {
        String htmlHeader =
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "<link rel=\"stylesheet\" href=\"css/gallery.css\">" +
                        "</head>";
        String htmlBody =
                "<body>" +
                        "<table border=\"1\" style=\"width:100%\">" +
                        "  <tr style=\"\">" +
                        "<div class=\"gallery\" align=\"center\">\n" +
                        "    <script type=\"javascript\">\n" +
                        "    </script>\n" +
                        "\n" +
                        "    <h2>" + pollingPlaceName + "</h2>\n" +
                        "    <h4>" + address + "</h4>\n" +
                        "    <div class=\"thumbnails\">\n" +
                        photoPatch(flickrPhotos) +
                        "    </div>\n" +
                        "\n" +
                        "    <div class=\"preview\" align=\"center\">\n" +
                        "        <img name=\"preview\" src=" + flickrPhotos.get(0).getImageURL() + " alt=\"\"/>\n" +
                        "    </div>\n" +
                        "    <div class=\"description\" align=\"center\">\n" +
                        "        <div id=\"id_description\"><h4>" + flickrPhotos.get(0).getDescription() + "</h4></div>\n" +
                        "    </div>" +
                        "  </tr>" +
                        "</table>" +
                        "<br/> " +
                        "<br/>" +

                        "<table  border=\"2\" style=\"width:100%\">" +

                        "      <tr>" +
                        "       <th>Category</th>" +
                        "		<th>Subcategory</th>" +
                        "		<th>Question</th>" +
                        "		<th>Answer</th>" +
                        "		<th>Data</th>" +
                        "		<th>Comments</th>		" +
                        "      </tr>";
        return htmlHeader + htmlBody;
    }

    private String photoPatch(List flickrPhotos) {
        Iterator<FlickrPhoto> images = flickrPhotos.iterator();
        String patch = "";
        int i = 0;
        while ((images.hasNext()) && (i < MAX_NUMBER_OF_PHOTOS_PER_POLLING_STATION)) {
            FlickrPhoto thisPhoto = images.next();
            ++i;
            patch = patch + "        <img onmouseover=\"preview.src=img" + i + ".src;id_description.innerHTML='<h4>" + thisPhoto.getDescription() + "</h4>'\" name=\"img" + i + "\"\n" +
                    "src=\"" + thisPhoto.getImageURL() + "\" alt=\"\"/>\n";
        }
        return patch;
    }

    private void writeHtmlFile(String fileName, String htmlText) {
        try {
            PrintWriter writer = new PrintWriter("C:\\tekpartner\\projects\\hack4sac\\voter_registration\\html_output\\" + fileName + ".html", "UTF-8");
            writer.println(htmlText);
            writer.close();
        } catch (Exception e) {
            logger.error("Got Exception during html file creation:" + e);
        }
    }

    public static void main(String[] args) {
        StaticHtmlPages staticdata = new StaticHtmlPages();
//        staticdata.readValue();
//        staticdata.createHtmlPage();
    }
}