package net.tekpartner.hack4sac.voterregistration;


import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cgaajula
 * Date: 4/4/16
 * Time: 10:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class NodeLabelManager {
    private static final Logger logger = Logger.getLogger(NodeLabelManager.class.getName());
    private Map<String, Label> nodeLabelsForSections = new HashMap();
    private Map<String, Map<String, Label>> nodeLabelsForCategories = new HashMap();
    private Map<String, Map<String, Map<String, Label>>> nodeLabelsForSubCategories = new HashMap();
    private Map<String, Map<String, Map<String, Map<String, Label>>>> nodeLabelsForQuestions = new HashMap();

    private Set setOfUniqueNodeLabelsForSections = new HashSet();
    private Set setOfUniqueNodeLabelsForCategories = new HashSet();
    private Set setOfUniqueNodeLabelsForSubCategories = new HashSet();
    private Set setOfUniqueNodeLabelsForQuestions = new HashSet();

    public NodeLabelManager() {
    }

    public String getNodeLabel(String section) {
        Label label = null;

        if (!nodeLabelsForSectionsContainsSection(section)) {
            label = this.generateNodeLabel(section);
            this.nodeLabelsForSections.put(section, label);

            this.setOfUniqueNodeLabelsForSections.add(label);
        } else {
            label = this.nodeLabelsForSections.get(section);
        }

        return label.getKey();
    }

    private boolean nodeLabelsForSectionsContainsSection(String key) {
        if (this.nodeLabelsForSections.containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    public String getNodeLabel(String section, String category) {
        Label label = null;

        if (!nodeLabelsForCategoriesContainsSection(section)) {
            this.nodeLabelsForCategories.put(section, new HashMap());
        }

        if (!nodeLabelsForCategoriesContainsCategory(section, category)) {
            label = this.generateNodeLabel(section, category);
            this.nodeLabelsForCategories.get(section).put(category, label);
        } else {
            label = this.nodeLabelsForCategories.get(section).get(category);
        }

        this.setOfUniqueNodeLabelsForCategories.add(label);

        return label.getKey();
    }

    private boolean nodeLabelsForCategoriesContainsSection(String key) {
        if (this.nodeLabelsForCategories.containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean nodeLabelsForCategoriesContainsCategory(String section, String key) {
        if (this.nodeLabelsForCategoriesContainsSection(section) && this.nodeLabelsForCategories.get(section).containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    public String getNodeLabel(String section, String category, String subCategory) {
        Label label = null;

        if (!nodeLabelsForSubCategoriesContainsSection(section)) {
            this.nodeLabelsForSubCategories.put(section, new HashMap());
        }

        if (!nodeLabelsForSubCategoriesContainsCategory(section, category)) {
            this.nodeLabelsForSubCategories.get(section).put(category, new HashMap());
        }

        if (!nodeLabelsForSubCategoriesContainsSubCategory(section, category, subCategory)) {
            label = this.generateNodeLabel(section, category, subCategory);
            this.nodeLabelsForSubCategories.get(section).get(category).put(subCategory, label);

            this.setOfUniqueNodeLabelsForSubCategories.add(label);
        } else {
            label = this.nodeLabelsForSubCategories.get(section).get(category).get(subCategory);
        }

        return label.getKey();
    }

    private boolean nodeLabelsForSubCategoriesContainsSection(String key) {
        if (this.nodeLabelsForSubCategories.containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean nodeLabelsForSubCategoriesContainsCategory(String section, String key) {
        if (this.nodeLabelsForSubCategoriesContainsSection(section) && this.nodeLabelsForSubCategories.get(section).containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean nodeLabelsForSubCategoriesContainsSubCategory(String section, String category, String key) {
        if (this.nodeLabelsForSubCategoriesContainsCategory(section, category) && this.nodeLabelsForSubCategories.get(section).get(category).containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    public String getNodeLabel(String section, String category, String subCategory, Question question) {
        Label label = null;

        if (!nodeLabelsForQuestionsContainsSection(section)) {
            this.nodeLabelsForQuestions.put(section, new HashMap());
        }

        if (!nodeLabelsForQuestionsContainsCategory(section, category)) {
            this.nodeLabelsForQuestions.get(section).put(category, new HashMap());
        }

        if (!nodeLabelsForQuestionsContainsSubCategory(section, category, subCategory)) {
            this.nodeLabelsForQuestions.get(section).get(category).put(subCategory, new HashMap());
        }

        if (!nodeLabelsForQuestionsContainsQuestion(section, category, subCategory, question)) {
            label = this.generateNodeLabel(section, category, subCategory, question);
            this.nodeLabelsForQuestions.get(section).get(category).get(subCategory).put(Utility.camelCase(question.getQuestion()), label);

            this.setOfUniqueNodeLabelsForQuestions.add(label);
        } else {
            label = this.nodeLabelsForQuestions.get(section).get(category).get(subCategory).get(Utility.camelCase(question.getQuestion()));
        }

        return label.getKey();
    }

    private boolean nodeLabelsForQuestionsContainsSection(String key) {
        if (this.nodeLabelsForQuestions.containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean nodeLabelsForQuestionsContainsCategory(String section, String key) {
        if (this.nodeLabelsForQuestionsContainsSection(section) && this.nodeLabelsForQuestions.get(section).containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean nodeLabelsForQuestionsContainsSubCategory(String section, String category, String key) {
        if (this.nodeLabelsForQuestionsContainsCategory(section, category) && this.nodeLabelsForQuestions.get(section).get(category).containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean nodeLabelsForQuestionsContainsQuestion(String section, String category, String subCategory, Question question) {
        if (this.nodeLabelsForQuestionsContainsSubCategory(section, category, subCategory) && this.nodeLabelsForQuestions.get(section).get(category).get(subCategory).containsKey(Utility.camelCase(question.getQuestion()))) {
            return true;
        } else {
            return false;
        }
    }

    private Label generateNodeLabel(String section) {
        Label nodeLabel = new Label();

        String nodeLabelValue = "NODE_";
        nodeLabelValue = nodeLabelValue.concat(Utility.camelCase(section));
        nodeLabelValue = nodeLabelValue.replaceAll("[^\\w\\s]", "");

        nodeLabel.setKey(nodeLabelValue);
        nodeLabel.setValue(section);

        return nodeLabel;
    }

    private Label generateNodeLabel(String section, String category) {
        Label nodeLabel = new Label();

        String nodeLabelValue = "NODE_";
        nodeLabelValue = nodeLabelValue.concat(Utility.camelCase(section));
        nodeLabelValue = nodeLabelValue.concat("_");
        nodeLabelValue = nodeLabelValue.concat(Utility.camelCase(category));
        nodeLabelValue = nodeLabelValue.replaceAll("[^\\w\\s]", "");

        nodeLabel.setKey(nodeLabelValue);
        nodeLabel.setValue(category);

        return nodeLabel;
    }

    private Label generateNodeLabel(String section, String category, String subCategory) {
        Label nodeLabel = new Label();

        String nodeLabelValue = "NODE_";
        nodeLabelValue = nodeLabelValue.concat(Utility.camelCase(section));
        nodeLabelValue = nodeLabelValue.concat("_");
        nodeLabelValue = nodeLabelValue.concat(Utility.camelCase(category));
        nodeLabelValue = nodeLabelValue.concat("_");
        nodeLabelValue = nodeLabelValue.concat(Utility.camelCase(subCategory));
        nodeLabelValue = nodeLabelValue.replaceAll("[^\\w\\s]", "");

        nodeLabel.setKey(nodeLabelValue);
        nodeLabel.setValue(subCategory);

        return nodeLabel;
    }

    private Label generateNodeLabel(String section, String category, String subCategory, Question question) {
        Label nodeLabel = new Label();

        String nodeLabelValue = "NODE_";
        nodeLabelValue = nodeLabelValue.concat(Utility.camelCase(section));
        nodeLabelValue = nodeLabelValue.concat("_");
        nodeLabelValue = nodeLabelValue.concat(Utility.camelCase(category));
        nodeLabelValue = nodeLabelValue.concat("_");
        nodeLabelValue = nodeLabelValue.concat(Utility.camelCase(subCategory));
        nodeLabelValue = nodeLabelValue.concat("_");
        nodeLabelValue = nodeLabelValue.concat(Utility.camelCase(question.getQuestion()));
        nodeLabelValue = nodeLabelValue.replaceAll("[^\\w\\s]", "");

        if (logger.isDebugEnabled()) {
            logger.debug("NodeLabel for Question: " + nodeLabelValue);
        }

        nodeLabel.setKey(nodeLabelValue);
        nodeLabel.setValue(question.getQuestion());

        return nodeLabel;
    }

    public void writeNodeLabelsAndValuesForSectionsToFile() throws IOException {
        FileWriter fw = new FileWriter("C:\\tekpartner\\data\\hack4sac\\UniqueNodeLabelsAndValuesForSections.txt", false);

        Iterator iterUniqueNodeLabelsForSections = this.setOfUniqueNodeLabelsForSections.iterator();

        while (iterUniqueNodeLabelsForSections.hasNext()) {
            Label label = (Label) iterUniqueNodeLabelsForSections.next();
            fw.write("\"");
            fw.write(label.getKey());
            fw.write("\" : \"");
            fw.write(label.getValue());
            fw.write("\",");
            fw.write("\n");
            fw.write("\"P_");
            fw.write(label.getKey());
            fw.write("\" : \"with ");
            fw.write(label.getValue().toLowerCase());
            fw.write("\",");
            fw.write("\n");
        }

        fw.close();
    }

    public void writeNodeLabelsAndValuesForCategoriesToFile() throws IOException {
        FileWriter fw = new FileWriter("C:\\tekpartner\\data\\hack4sac\\UniqueNodeLabelsAndValuesForCategories.txt", false);

        Iterator iterUniqueNodeLabelsForCategories = this.setOfUniqueNodeLabelsForCategories.iterator();

        while (iterUniqueNodeLabelsForCategories.hasNext()) {
            Label label = (Label) iterUniqueNodeLabelsForCategories.next();
            fw.write("\"");
            fw.write(label.getKey());
            fw.write("\" : \"");
            fw.write(label.getValue());
            fw.write("\",");
            fw.write("\n");
            fw.write("\"P_");
            fw.write(label.getKey());
            fw.write("\" : \"with ");
            fw.write(label.getValue().toLowerCase());
            fw.write("\",");
            fw.write("\n");
        }

        fw.close();
    }

    public void writeNodeLabelsAndValuesForSubCategoriesToFile() throws IOException {
        FileWriter fw = new FileWriter("C:\\tekpartner\\data\\hack4sac\\UniqueNodeLabelsAndValuesForSubCategories.txt", false);

        Iterator iterUniqueNodeLabelsForSubCategories = this.setOfUniqueNodeLabelsForSubCategories.iterator();

        while (iterUniqueNodeLabelsForSubCategories.hasNext()) {
            Label label = (Label) iterUniqueNodeLabelsForSubCategories.next();
            fw.write("\"");
            fw.write(label.getKey());
            fw.write("\" : \"");
            fw.write(label.getValue());
            fw.write("\",");
            fw.write("\n");
            fw.write("\"P_");
            fw.write(label.getKey());
            fw.write("\" : \"with ");
            fw.write(label.getValue().toLowerCase());
            fw.write("\",");
            fw.write("\n");
        }

        fw.close();
    }

    public void writeNodeLabelsAndValuesForQuestionsToFile() throws IOException {
        FileWriter fw = new FileWriter("C:\\tekpartner\\data\\hack4sac\\UniqueNodeLabelsAndValuesForQuestions.txt", false);

        Iterator iterUniqueNodeLabelsForQuestions = this.setOfUniqueNodeLabelsForQuestions.iterator();

        while (iterUniqueNodeLabelsForQuestions.hasNext()) {
            Label label = (Label) iterUniqueNodeLabelsForQuestions.next();
            fw.write("\"");
            fw.write(label.getKey());
            fw.write("\" : \"");
            fw.write("Yes/No");
            fw.write("\",");
            fw.write("\n");
            fw.write("\"P_");
            fw.write(label.getKey());
            fw.write("\" : \"");
            fw.write("Yes/No");
            fw.write("\",");
            fw.write("\n");
        }

        fw.close();
    }

    public void writeNodeLabelsForSectionsToFile() throws IOException {
        FileWriter fw = new FileWriter("C:\\tekpartner\\data\\hack4sac\\UniqueNodeLabelsAndValuesForSections.txt", false);

        Iterator iterUniqueNodeLabelsForSections = this.setOfUniqueNodeLabelsForSections.iterator();

        while (iterUniqueNodeLabelsForSections.hasNext()) {
            Label label = (Label) iterUniqueNodeLabelsForSections.next();
            fw.write("\"");
            fw.write(label.getKey());
            fw.write("\",");
            fw.write("\n");
        }

        fw.close();
    }

    public void writeNodeLabelsForCategoriesToFile() throws IOException {
        FileWriter fw = new FileWriter("C:\\tekpartner\\data\\hack4sac\\UniqueNodeLabelsForCategories.txt", false);

        Iterator iterUniqueNodeLabelsForCategories = this.setOfUniqueNodeLabelsForCategories.iterator();

        while (iterUniqueNodeLabelsForCategories.hasNext()) {
            Label label = (Label) iterUniqueNodeLabelsForCategories.next();
            fw.write("\"");
            fw.write(label.getKey());
            fw.write("\",");
            fw.write("\n");
        }

        fw.close();
    }

    public void writeNodeLabelsForSubCategoriesToFile() throws IOException {
        FileWriter fw = new FileWriter("C:\\tekpartner\\data\\hack4sac\\UniqueNodeLabelsForSubCategories.txt", false);

        Iterator iterUniqueNodeLabelsForSubCategories = this.setOfUniqueNodeLabelsForSubCategories.iterator();

        while (iterUniqueNodeLabelsForSubCategories.hasNext()) {
            Label label = (Label) iterUniqueNodeLabelsForSubCategories.next();
            fw.write("\"");
            fw.write(label.getKey());
            fw.write("\",");
            fw.write("\n");
        }

        fw.close();
    }

    public void writeNodeLabelsForQuestionsToFile() throws IOException {
        FileWriter fw = new FileWriter("C:\\tekpartner\\data\\hack4sac\\UniqueNodeLabelsForQuestions.txt", false);

        Iterator iterUniqueNodeLabelsForQuestions = this.setOfUniqueNodeLabelsForQuestions.iterator();

        while (iterUniqueNodeLabelsForQuestions.hasNext()) {
            Label label = (Label) iterUniqueNodeLabelsForQuestions.next();
            fw.write("\"");
            fw.write(label.getKey());
            fw.write("\",");
            fw.write("\n");
        }

        fw.close();
    }
}