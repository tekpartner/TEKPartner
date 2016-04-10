package net.tekpartner.hack4sac.voterregistration;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Created by Aravind on 3/23/2016.
 */

public class ExtractAndPopulate {
    private static final Logger logger = Logger.getLogger(ExtractAndPopulate.class.getName());

    private boolean writeToGraphDB;
    private boolean writeStaticHTMLPages;
    private String dataSource;

    private static final String LABEL_OPTION = "Option";

    private String SERVER_ROOT_URI = "http://localhost:7474/db/data/";
    private String user = "neo4j";
    private String pass = "neo.!Q@W#E";
    private int limit_rows = 1000;
    private String data_location = "http://saccounty.cloudapi.junar.com/api/v2/datastreams/VRE-DENOR-FULL-LIST/data.csv/?auth_key=bab23f7e02c26374f1826b99dcce23e69d44c803&limit=" + limit_rows + "#sthash.4MKJTzJ4.dpuf";
    private String labelMainNode = "PollingStation";
    private String webURLTemplateForPollingStations = "http://hack4sac.tekpartner.net.s3.amazonaws.com/voter-reg/html_output/##POLLING_STATION_ID##.html";

    private Map<String, PollingStation> graphData = new HashMap();
    private Map<String, Map<String, Map<String, Set<Question>>>> yesNoQuestions = new HashMap();

    private NodeLabelManager nodeLabelManager = new NodeLabelManager();
    private LinkLabelManager linkLabelManager = new LinkLabelManager();
    private StaticHtmlPages staticHtmlPages = new StaticHtmlPages();

    public ExtractAndPopulate() {
    }

    //Source: https://github.com/GGSvennson/Neo4jRestApi/
    private WebTarget testDatabaseAuthentication() {
        // START SNIPPET: testAuthentication
        Client client = ClientBuilder.newClient();

        HttpAuthenticationFeature authFeature = HttpAuthenticationFeature.basic(this.user, this.pass);
        client.register(authFeature);

        WebTarget target = client.target(SERVER_ROOT_URI);

        Response response = target
                .request()
                .header("application/xml", "true")
                .get();
        if (logger.isDebugEnabled()) {
            logger.debug("GET, status code " + response.getStatus());
        }
        response.close();
        return target;
        // END SNIPPET: testAuthentication
    }

    private URI createNode(WebTarget target) {
        // START SNIPPET: createNode
        final String nodeEntryPointUri = "node";

        // POST {} to the node entry point URI
        Response response = target
                .path(nodeEntryPointUri)
                .request(MediaType.APPLICATION_JSON)
                .header("application/xml", "true")
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity("{}", MediaType.APPLICATION_JSON_TYPE));

        final URI location = response.getLocation();
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                    "POST to [%s], status code [%d], location header [%s]",
                    nodeEntryPointUri, response.getStatus(), location.toString()));
        }

        response.close();

        return location;
        // END SNIPPET: createNode
    }

    private String extractPathFromNode(URI node) {
        String auxUri = node.toString();
        return auxUri.replace(SERVER_ROOT_URI, "");
    }

    private void addProperty(WebTarget target, URI nodeUri, String propertyName,
                             String propertyValue) {
        // START SNIPPET: addProp
        String auxPath = this.extractPathFromNode(nodeUri);
        String propertyUri = auxPath + "/properties/" + propertyName;
        // http://localhost:7474/db/data/node/{node_id}/properties/{property_name}

        Response response = target
                .path(propertyUri)
                .request(MediaType.APPLICATION_JSON)
                .header("application/xml", "true")
                .accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity("\"" + propertyValue.replace("\"", "\\\"") + "\"", MediaType.APPLICATION_JSON_TYPE));

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("PUT to [%s], status code [%d]",
                    propertyUri, response.getStatus()));
        }

        response.close();
        // END SNIPPET: addProp
    }

    private URI addRelationship(WebTarget target, URI startNode, URI endNode,
                                String relationshipType, String jsonAttributes)
    //throws URISyntaxException
    {
        //URI fromUri = new URI( startNode.toString() + "/relationships" );
        String auxPath = this.extractPathFromNode(startNode);
        String fromUri = auxPath + "/relationships";
        String relationshipJson = this.generateJsonRelationship(endNode,
                relationshipType, jsonAttributes);

        // POST {} to the node entry point URI
        Response response = target
                .path(fromUri)
                .request(MediaType.APPLICATION_JSON)
                .header("application/xml", "true")
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(relationshipJson, MediaType.APPLICATION_JSON_TYPE));

        final URI location = response.getLocation();

        if (logger.isDebugEnabled()) {
            logger.debug("Relationship JSON: " + relationshipJson);
            logger.debug("fromUri: " + fromUri);
            logger.debug("response.getStatus(): " + response.getStatus());
            logger.debug("location.toString(): " + location.toString());
            logger.debug(String.format(
                    "POST to [%s], status code [%d], location header [%s]",
                    fromUri, response.getStatus(), location.toString()));
        }

        response.close();
        return location;
    }

    private String generateJsonRelationship(URI endNode,
                                            String relationshipType, String... jsonAttributes) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"to\" : \"");
        sb.append(endNode.toString());
        sb.append("\", ");

        sb.append("\"type\" : \"");
        sb.append(relationshipType.replace("\"", "\\\""));
        if (jsonAttributes == null || jsonAttributes.length < 1) {
            sb.append("\"");
        } else {
            sb.append("\", \"data\" : ");
            for (int i = 0; i < jsonAttributes.length; i++) {
                sb.append(jsonAttributes[i]);
                if (i < jsonAttributes.length - 1) { // Miss off the final comma
                    sb.append(", ");
                }
            }
        }

        sb.append(" }");
        return sb.toString();
    }

    private void addLabel(WebTarget target, URI nodeUri, String labelName) {
        // START SNIPPET: addProp
        String auxPath = this.extractPathFromNode(nodeUri);
        String propertyUri = auxPath + "/labels";
        // http://localhost:7474/db/data/node/{node_id}/properties/{property_name}

        Response response = target
                .path(propertyUri)
                .request(MediaType.APPLICATION_JSON)
                .header("application/xml", "true")
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity("\"" + labelName + "\"", MediaType.APPLICATION_JSON_TYPE));

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("PUT to [%s], status code [%d]",
                    propertyUri, response.getStatus()));
        }

        response.close();
        // END SNIPPET: addProp
    }

    public void read() {
        if (logger.isInfoEnabled()) {
            logger.info("Begin Reading Data from data.saccounty.net : " + data_location);
        }

        try {
            int counter = 0;
            String scannedLine;

            if (StringUtils.equalsIgnoreCase(this.dataSource, "data.saccounty.net")) {
                URL data = new URL(this.data_location);
                InputStream input = data.openStream();
                Scanner scan = new Scanner(input);

                //first line consists of header. So ignore line one
                scan.nextLine();
                while (scan.hasNext()) {
                    scannedLine = scan.nextLine();
                    counter++;
                    this.processLineRead(new Scanner(scannedLine));

                    if (counter > limit_rows) {
                        //break;
                    }
                }
            } else if (StringUtils.equalsIgnoreCase(this.dataSource, "File")) {
                BufferedReader br =
                        new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/VREData.csv")));

                //first line consists of header. So ignore line one
                br.readLine();
                while ((scannedLine = br.readLine()) != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Scanned Line : " + scannedLine);
                    }
                    counter++;
                    this.processLineRead(new Scanner(scannedLine));

                    if (counter > limit_rows) {
                        //break;
                    }
                }
            } else {
                logger.error("Invalid Option for DataSource: " + this.dataSource);
            }

            if (logger.isInfoEnabled()) {
                logger.info("System completed organising data from CSV file, and added scanned " + counter + " number of rows.");
                logger.info("System completed organising data from CSV file, and added " + this.graphData.size() + " number of polling stations.");
            }
        } catch (Exception e) {
            logger.error("Exception occurred: " + e);
        }
    }

    private void processLineRead(Scanner line_read) {
        line_read.useDelimiter(",\"");
        String ppid = Utility.trimmer(line_read.next());
        if (!this.graphData.containsKey(ppid)) {
            this.graphData.put(ppid, new PollingStation(ppid));
        }
        this.graphData.get(ppid).addLine(Utility.trimmer(line_read.next()),//section
                Utility.trimmer(line_read.next()),//category
                Utility.trimmer(line_read.next()),//subcategory
                Utility.trimmer(line_read.next()),//qid
                Utility.trimmer(line_read.next()),//question
                Utility.trimmer(line_read.next()),//answer
                Utility.trimmer(line_read.next()),//data
                Utility.trimmer(line_read.next()),//comments
                this.yesNoQuestions
        );
    }

    /**
     * Write to the Graph Database
     */
    public void writeToGraphDatabase() {
        WebTarget target = this.testDatabaseAuthentication();
        for (String pollStationId : this.graphData.keySet()) {
            String pollingPlaceAddress = StringUtils.EMPTY;
            String pollingPlaceCity = StringUtils.EMPTY;
            PollingStation pollingStation = this.graphData.get(pollStationId);

            //pollingStation.print();
            URI id = this.createNode(target);
            this.addLabel(target, id, this.labelMainNode);

            this.addProperty(target, id, "images", Utility.getJSONArrayOfImagesForPollingStation(pollStationId).toString());
            this.addProperty(target, id, "website", StringUtils.replace(webURLTemplateForPollingStations, "##POLLING_STATION_ID##", pollStationId));

            URI option = this.createNode(target);
            this.addLabel(target, option, ExtractAndPopulate.LABEL_OPTION);

            this.addRelationship(target, id, option, "OPTIONS", "{}");

            Map<String, URI> categories = new HashMap();// name of category vs its node URI
            for (String section : pollingStation.getSections()) {
                if (section.equals("0")) {
                    for (String category : pollingStation.getCategories(section)) {
                        for (String subCategory : pollingStation.getSubCategories(section, category)) {
                            for (Question question : pollingStation.getQuestions(section, category, subCategory)) {
                                this.addProperty(target, id, Utility.camelCase(question.getQuestion()), question.getAnswer());
                                if (StringUtils.equalsIgnoreCase(Utility.camelCase(question.getQuestion()), "pollingPlaceAddress")) {
                                    pollingPlaceAddress = question.getAnswer();
                                } else if (StringUtils.equalsIgnoreCase(Utility.camelCase(question.getQuestion()), "city")) {
                                    pollingPlaceCity = question.getAnswer();
                                }
                            }
                        }
                    }
                } else {
                    if (checkIfSectionContainsQuestionsIsOfTypeYesOrNo(section)) {
                        for (String category : pollingStation.getCategories(section)) {
                            if (checkIfCategoryContainsQuestionsIsOfTypeYesOrNo(section, category)) {
                                URI categoryNode;
                                if (!section.equals("0") && !categories.containsKey(category)) {
                                    categoryNode = this.createNode(target);
                                    this.addLabel(target, categoryNode, this.nodeLabelManager.getNodeLabel(section, category));
                                    this.addRelationship(target, option, categoryNode, this.linkLabelManager.getLinkLabel(section, category), "{}");
                                    categories.put(category, categoryNode);
                                }
                                for (String subCategory : pollingStation.getSubCategories(section, category)) {
                                    if (checkIfSubCategoryContainsQuestionsIsOfTypeYesOrNo(section, category, subCategory)) {
                                        URI subCategoryNode = null;
                                        if (!section.equals("0")) {
                                            subCategoryNode = this.createNode(target);

                                            if (!subCategory.isEmpty()) {
                                                this.addLabel(target, subCategoryNode, this.nodeLabelManager.getNodeLabel(section, category, subCategory));
                                                this.addRelationship(target, categories.get(category), subCategoryNode, this.linkLabelManager.getLinkLabel(section, category, subCategory), "{}");
                                            } else {
                                                this.addLabel(target, subCategoryNode, this.nodeLabelManager.getNodeLabel(section, category, "General"));
                                                this.addRelationship(target, categories.get(category), subCategoryNode, this.linkLabelManager.getLinkLabel(section, category, "General"), "{}");
                                            }
                                        }

                                        for (Question question : pollingStation.getQuestions(section, category, subCategory)) {
                                            if (this.checkIfQuestionIsOfTypeYesOrNo(section, category, subCategory, question)) {
                                                URI questionNode = this.createNode(target);
                                                this.addLabel(target, questionNode, this.nodeLabelManager.getNodeLabel(section, category, subCategory, question));
                                                this.addRelationship(target, subCategoryNode, questionNode, this.linkLabelManager.getLinkLabel(section, category, subCategory, question), "{}");

                                                String answer = question.getAnswer();
                                                if (StringUtils.isEmpty(answer)) {
                                                    answer = "N/A";
                                                }
                                                this.addProperty(target, questionNode, "value", answer);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            try {
                this.addProperty(target, id, "completeAddress", Utility.getCompletePollingStationAddress(pollStationId, pollingPlaceAddress, pollingPlaceCity));
            } catch (URISyntaxException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public void writeNodeLabelsToFile() throws IOException {
        this.nodeLabelManager.writeNodeLabelsForSectionsToFile();
        this.nodeLabelManager.writeNodeLabelsForCategoriesToFile();
        this.nodeLabelManager.writeNodeLabelsForSubCategoriesToFile();
        this.nodeLabelManager.writeNodeLabelsForQuestionsToFile();

        this.nodeLabelManager.writeNodeLabelsAndValuesForSectionsToFile();
        this.nodeLabelManager.writeNodeLabelsAndValuesForCategoriesToFile();
        this.nodeLabelManager.writeNodeLabelsAndValuesForSubCategoriesToFile();
        this.nodeLabelManager.writeNodeLabelsAndValuesForQuestionsToFile();
    }

    public void writeLinkLabelsToFile() throws IOException {
        this.linkLabelManager.writeLinkLabelsForSectionsToFile();
        this.linkLabelManager.writeLinkLabelsForCategoriesToFile();
        this.linkLabelManager.writeLinkLabelsForSubCategoriesToFile();
        this.linkLabelManager.writeLinkLabelsForQuestionsToFile();
    }

    /**
     * Check If Section contains Questions is of Type Yes/No
     *
     * @param section - Section
     * @return
     */
    private boolean checkIfSectionContainsQuestionsIsOfTypeYesOrNo(String section) {
        boolean sectionContainsQuestionsIsOfTypeYesOrNo = false;
        if (this.yesNoQuestions.containsKey(section)) {
            sectionContainsQuestionsIsOfTypeYesOrNo = true;
        }
        return sectionContainsQuestionsIsOfTypeYesOrNo;
    }

    /**
     * Check If Category contains Questions is of Type Yes/No
     *
     * @param section  - Section
     * @param category - Category
     * @return
     */
    private boolean checkIfCategoryContainsQuestionsIsOfTypeYesOrNo(String section, String category) {
        boolean categoryContainsQuestionsIsOfTypeYesOrNo = false;
        if (this.yesNoQuestions.containsKey(section)) {
            HashMap sectionMap = (HashMap) this.yesNoQuestions.get(section);

            if (sectionMap.containsKey(category)) {
                categoryContainsQuestionsIsOfTypeYesOrNo = true;
            }
        }
        return categoryContainsQuestionsIsOfTypeYesOrNo;
    }

    /**
     * Check If SubCategory contains Questions is of Type Yes/No
     *
     * @param section     - Section
     * @param category    - Category
     * @param subCategory - SubCategory
     * @return
     */
    private boolean checkIfSubCategoryContainsQuestionsIsOfTypeYesOrNo(String section, String category, String subCategory) {
        boolean subCategoryContainsQuestionsIsOfTypeYesOrNo = false;
        if (this.yesNoQuestions.containsKey(section)) {
            HashMap sectionMap = (HashMap) this.yesNoQuestions.get(section);

            if (sectionMap.containsKey(category)) {
                HashMap categoryMap = (HashMap) sectionMap.get(category);

                if (categoryMap.containsKey(subCategory)) {
                    subCategoryContainsQuestionsIsOfTypeYesOrNo = true;
                }
            }
        }
        return subCategoryContainsQuestionsIsOfTypeYesOrNo;
    }

    /**
     * Check If Question is of Type Yes/No
     *
     * @param section     - Section
     * @param category    - Category
     * @param subCategory - SubCategory
     * @param question    - Question
     * @return
     */
    private boolean checkIfQuestionIsOfTypeYesOrNo(String section, String category, String subCategory, Question question) {
        boolean questionIsOfTypeYesOrNo = false;
        if (this.yesNoQuestions.containsKey(section)) {
            HashMap sectionMap = (HashMap) this.yesNoQuestions.get(section);

            if (sectionMap.containsKey(category)) {
                HashMap categoryMap = (HashMap) sectionMap.get(category);

                if (categoryMap.containsKey(subCategory)) {
                    Set yesNoQuestions = (HashSet) categoryMap.get(subCategory);

                    if (this.checkIfQuestionBelongsInSetOfQuestions(question, yesNoQuestions)) {
                        questionIsOfTypeYesOrNo = true;
                    }
                }
            }
        }
        return questionIsOfTypeYesOrNo;
    }

    /**
     * Check If Question belongs in a Set of Questions
     *
     * @param question       - Question
     * @param yesNoQuestions - Set of Questions with Yes/No Response
     * @return
     */
    private boolean checkIfQuestionBelongsInSetOfQuestions(Question question, Set yesNoQuestions) {
        boolean questionBelongsInSetOfQuestions = false;
        Iterator iterYesNoQuestions = yesNoQuestions.iterator();

        while (iterYesNoQuestions.hasNext()) {
            Question yesNoQuestion = (Question) iterYesNoQuestions.next();

            if (StringUtils.equalsIgnoreCase(yesNoQuestion.getQuestion(), question.getQuestion())) {
                questionBelongsInSetOfQuestions = true;
                break;
            }
        }
        return questionBelongsInSetOfQuestions;
    }

    /**
     * Set the Data Source
     *
     * @param dataSource - Data Source
     */
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @param writeToGraphDB
     */
    public void setWriteToGraphDB(String writeToGraphDB) {
        this.writeToGraphDB = Boolean.valueOf(writeToGraphDB);
    }

    /**
     * @param writeStaticHTMLPages
     */
    public void setWriteStaticHTMLPages(String writeStaticHTMLPages) {
        this.writeStaticHTMLPages = Boolean.valueOf(writeStaticHTMLPages);
    }

    /**
     *
     */
    public void performExtractAndPopulate() {
        this.read();

        if (this.writeToGraphDB) {
            if (logger.isInfoEnabled()) {
                logger.info("Writing to Graph Database.");
            }
            this.writeToGraphDatabase();
            try {
                this.writeLinkLabelsToFile();
                this.writeNodeLabelsToFile();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        if (this.writeStaticHTMLPages) {
            if (logger.isInfoEnabled()) {
                logger.info("Writing the HTML files.");
            }
            this.staticHtmlPages.createHtmlPage(this.graphData);
        }
    }

    public static void main(String[] args) {
        ExtractAndPopulate extractAndPopulate = new ExtractAndPopulate();

        OptionParser parser = new OptionParser("d:g:h:");
        OptionSet options = parser.parse(args);
        extractAndPopulate.setDataSource((String) options.valueOf("d"));
        extractAndPopulate.setWriteToGraphDB((String) options.valueOf("g"));
        extractAndPopulate.setWriteStaticHTMLPages((String) options.valueOf("h"));
        extractAndPopulate.performExtractAndPopulate();
    }
}