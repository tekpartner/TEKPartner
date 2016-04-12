package net.tekpartner.hack4sac.voterregistration;

import net.tekpartner.imagemanager.flickr.FlickrPhoto;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.*;

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

            List<String> pollingStationData = new ArrayList<>();
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
                                List<String> childList = new ArrayList<>();
                                // Code to create dynamic table rows
                                childList.add(new String("\"" + Utility.replaceDoubleQuotesToIncludeEscapeCharacters(thisCategory) + "\""));
                                childList.add(new String("\"" + Utility.replaceDoubleQuotesToIncludeEscapeCharacters(thisSubcategory) + "\""));
                                childList.add(new String("\"" + Utility.replaceDoubleQuotesToIncludeEscapeCharacters(thisQuestion.getQuestion()) + "\""));
                                childList.add(new String("\"" + Utility.replaceDoubleQuotesToIncludeEscapeCharacters(thisQuestion.getAnswer()) + "\""));
                                childList.add(new String("\"" + Utility.replaceDoubleQuotesToIncludeEscapeCharacters(thisQuestion.getData()) + "\""));
                                childList.add(new String("\"" + Utility.replaceDoubleQuotesToIncludeEscapeCharacters(thisQuestion.getComments()) + "\""));
                                pollingStationData.add(childList.toString());

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
            this.writeHtmlFileForPollingStation(poll_id, "{\"data\": " + pollingStationData.toString() + "}");

            try {
                address.append(Utility.getCompletePollingStationAddress(poll_id, pollingplace_address, city));
            } catch (URISyntaxException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            this.writeHtmlFile(poll_id, getStaticPartOfHtmlPage(poll_id, pollingPlaceName, address.toString(), flickrPhotos));
        }
    }

    private String getHtmlFooter() {
        return "</table>" + "</body>" + "</html>";
    }

    private String getStaticPartOfHtmlPage(String pollingStationId, String pollingPlaceName, String address, List<FlickrPhoto> flickrPhotos) {
        String htmlHeader =
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "<link rel=\"stylesheet\" href=\"css/gallery.css\">" +
                        "<link rel=\"stylesheet\" type=\"text/css\" href=\"https://cdn.datatables.net/1.10.11/css/jquery.dataTables.min.css\">" +
                        "</head>";
        String htmlBody =
                "<body>" +
                        "<script type=\"text/javascript\" language=\"javascript\" src=\"https://code.jquery.com/jquery-1.12.0.min.js\"></script>" +
                        "<script type=\"text/javascript\" language=\"javascript\" src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js\"></script>" +
                        "<script type=\"text/javascript\" language=\"javascript\" src=\"https://cdn.datatables.net/1.10.11/js/jquery.dataTables.min.js\"></script>" +
                        "<script type=\"text/javascript\" language=\"javascript\" src=\"https://cdn.datatables.net/1.10.11/js/dataTables.bootstrap.min.js\"></script>" +
                        "<script type=\"text/javascript\" class=\"init\">" +
                        "    $(document).ready(function () {" +
                        "        $('#myTable').DataTable({" +
                        "            ajax: '" + pollingStationId + ".json'," +
                        "            scrollY: 600," +
                        "            scrollCollapse: true," +
                        "            paging: false" +
                        "        });" +
                        "    });" +
                        "</script>" +
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
                        "   </div>" +
                        "  </tr>" +
                        "</table>" +
                        "<br/> " +
                        "<br/>" +

                        "<table id=\"myTable\" class=\"display\" cellspacing=\"0\" width=\"100%\">" +
                        "      <thead>" +
                        "      <tr>" +
                        "       <th>Category</th>" +
                        "		<th>Subcategory</th>" +
                        "		<th>Question</th>" +
                        "		<th>Answer</th>" +
                        "		<th>Data</th>" +
                        "		<th>Comments</th>		" +
                        "      </tr>" +
                        "      </thead>" +
                        "      </table>" +
                        "      </body>" +
                        "      </html>";
        return htmlHeader + htmlBody;
    }

    private String photoPatch(List flickrPhotos) {
        Iterator<FlickrPhoto> images = flickrPhotos.iterator();
        String patch = "";
        int i = 0;
        while ((images.hasNext()) && (i < MAX_NUMBER_OF_PHOTOS_PER_POLLING_STATION)) {
            FlickrPhoto thisPhoto = images.next();
            ++i;
            patch = patch + "        <img onmouseover=\"preview.src=img" + i + ".src;id_description.innerHTML='<h4>" + Utility.replaceSingleQuotesToIncludeEscapeCharacters(thisPhoto.getDescription()) + "</h4>'\" name=\"img" + i + "\"\n" +
                    "src=\"" + thisPhoto.getImageURL() + "\" alt=\"\"/>\n";
        }
        return patch;
    }

    private void writeHtmlFile(String pollingStationId, String htmlText) {
        try {
            String fileName = "C:\\tekpartner\\projects\\hack4sac\\voter_registration\\html_output\\data_tables\\" + Utility.camelCase(pollingStationId) + ".html";
            File file = new File(fileName);
            file.getParentFile().mkdirs();
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            writer.println(htmlText);
            writer.close();
        } catch (Exception e) {
            logger.error("Got Exception during html file creation:" + e);
        }
    }

    private void writeHtmlFileForPollingStation(String pollingStationId, String htmlText) {
        try {
            String fileName = "C:\\tekpartner\\projects\\hack4sac\\voter_registration\\html_output\\data_tables\\" + Utility.camelCase(pollingStationId) + ".json";
            File file = new File(fileName);
            file.getParentFile().mkdirs();
            PrintWriter writer = new PrintWriter(file, "UTF-8");
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