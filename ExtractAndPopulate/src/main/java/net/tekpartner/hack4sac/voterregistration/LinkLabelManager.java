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
public class LinkLabelManager {
    private static final Logger logger = Logger.getLogger(LinkLabelManager.class.getName());
    private Map<String, Label> linkLabelsForSections = new HashMap();
    private Map<String, Map<String, Label>> linkLabelsForCategories = new HashMap();
    private Map<String, Map<String, Map<String, Label>>> linkLabelsForSubCategories = new HashMap();
    private Map<String, Map<String, Map<String, Map<String, Label>>>> linkLabelsForQuestions = new HashMap();

    private Set setOfUniqueLinkLabelsForSections = new HashSet();
    private Set setOfUniqueLinkLabelsForCategories = new HashSet();
    private Set setOfUniqueLinkLabelsForSubCategories = new HashSet();
    private Set setOfUniqueLinkLabelsForQuestions = new HashSet();

    public LinkLabelManager() {
    }

    public String getLinkLabel(String section) {
        Label label = null;

        if (!linkLabelsForSectionsContainsSection(section)) {
            label = this.generateLinkLabel(section);
            this.linkLabelsForSections.put(section, label);

            this.setOfUniqueLinkLabelsForSections.add(label);
        } else {
            label = this.linkLabelsForSections.get(section);
        }

        return label.getKey();
    }

    private boolean linkLabelsForSectionsContainsSection(String key) {
        if (this.linkLabelsForSections.containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    public String getLinkLabel(String section, String category) {
        Label label = null;

        if (!linkLabelsForCategoriesContainsSection(section)) {
            this.linkLabelsForCategories.put(section, new HashMap());
        }

        if (!linkLabelsForCategoriesContainsCategory(section, category)) {
            label = this.generateLinkLabel(section, category);
            this.linkLabelsForCategories.get(section).put(category, label);
        } else {
            label = this.linkLabelsForCategories.get(section).get(category);
        }

        this.setOfUniqueLinkLabelsForCategories.add(label);

        return label.getKey();
    }

    private boolean linkLabelsForCategoriesContainsSection(String key) {
        if (this.linkLabelsForCategories.containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean linkLabelsForCategoriesContainsCategory(String section, String key) {
        if (this.linkLabelsForCategoriesContainsSection(section) && this.linkLabelsForCategories.get(section).containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    public String getLinkLabel(String section, String category, String subCategory) {
        Label label = null;

        if (!linkLabelsForSubCategoriesContainsSection(section)) {
            this.linkLabelsForSubCategories.put(section, new HashMap());
        }

        if (!linkLabelsForSubCategoriesContainsCategory(section, category)) {
            this.linkLabelsForSubCategories.get(section).put(category, new HashMap());
        }

        if (!linkLabelsForSubCategoriesContainsSubCategory(section, category, subCategory)) {
            label = this.generateLinkLabel(section, category, subCategory);
            this.linkLabelsForSubCategories.get(section).get(category).put(subCategory, label);

            this.setOfUniqueLinkLabelsForSubCategories.add(label);
        } else {
            label = this.linkLabelsForSubCategories.get(section).get(category).get(subCategory);
        }

        return label.getKey();
    }

    private boolean linkLabelsForSubCategoriesContainsSection(String key) {
        if (this.linkLabelsForSubCategories.containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean linkLabelsForSubCategoriesContainsCategory(String section, String key) {
        if (this.linkLabelsForSubCategoriesContainsSection(section) && this.linkLabelsForSubCategories.get(section).containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean linkLabelsForSubCategoriesContainsSubCategory(String section, String category, String key) {
        if (this.linkLabelsForSubCategoriesContainsCategory(section, category) && this.linkLabelsForSubCategories.get(section).get(category).containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    public String getLinkLabel(String section, String category, String subCategory, Question question) {
        Label label = null;

        if (!linkLabelsForQuestionsContainsSection(section)) {
            this.linkLabelsForQuestions.put(section, new HashMap());
        }

        if (!linkLabelsForQuestionsContainsCategory(section, category)) {
            this.linkLabelsForQuestions.get(section).put(category, new HashMap());
        }

        if (!linkLabelsForQuestionsContainsSubCategory(section, category, subCategory)) {
            this.linkLabelsForQuestions.get(section).get(category).put(subCategory, new HashMap());
        }

        if (!linkLabelsForQuestionsContainsQuestion(section, category, subCategory, question)) {
            label = this.generateLinkLabel(section, category, subCategory, question);
            this.linkLabelsForQuestions.get(section).get(category).get(subCategory).put(Utility.camelCase(question.getQuestion()), label);

            this.setOfUniqueLinkLabelsForQuestions.add(label);
        } else {
            label = this.linkLabelsForQuestions.get(section).get(category).get(subCategory).get(Utility.camelCase(question.getQuestion()));
        }

        return label.getKey();
    }

    private boolean linkLabelsForQuestionsContainsSection(String key) {
        if (this.linkLabelsForQuestions.containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean linkLabelsForQuestionsContainsCategory(String section, String key) {
        if (this.linkLabelsForQuestionsContainsSection(section) && this.linkLabelsForQuestions.get(section).containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean linkLabelsForQuestionsContainsSubCategory(String section, String category, String key) {
        if (this.linkLabelsForQuestionsContainsCategory(section, category) && this.linkLabelsForQuestions.get(section).get(category).containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean linkLabelsForQuestionsContainsQuestion(String section, String category, String subCategory, Question question) {
        if (this.linkLabelsForQuestionsContainsSubCategory(section, category, subCategory) && this.linkLabelsForQuestions.get(section).get(category).get(subCategory).containsKey(Utility.camelCase(question.getQuestion()))) {
            return true;
        } else {
            return false;
        }
    }

    private Label generateLinkLabel(String section) {
        Label linkLabel = new Label();

        String linkLabelValue = "LINK_";
        linkLabelValue = linkLabelValue.concat(Utility.camelCase(section));
        linkLabelValue = linkLabelValue.replaceAll("[^\\w\\s]", "");

        linkLabel.setKey(linkLabelValue);
        linkLabel.setValue(section);

        return linkLabel;
    }

    private Label generateLinkLabel(String section, String category) {
        Label linkLabel = new Label();

        String linkLabelValue = "LINK_";
        linkLabelValue = linkLabelValue.concat(Utility.camelCase(section));
        linkLabelValue = linkLabelValue.concat("_");
        linkLabelValue = linkLabelValue.concat(Utility.camelCase(category));
        linkLabelValue = linkLabelValue.replaceAll("[^\\w\\s]", "");

        linkLabel.setKey(linkLabelValue);
        linkLabel.setValue(category);

        return linkLabel;
    }

    private Label generateLinkLabel(String section, String category, String subCategory) {
        Label linkLabel = new Label();

        String linkLabelValue = "LINK_";
        linkLabelValue = linkLabelValue.concat(Utility.camelCase(section));
        linkLabelValue = linkLabelValue.concat("_");
        linkLabelValue = linkLabelValue.concat(Utility.camelCase(category));
        linkLabelValue = linkLabelValue.concat("_");
        linkLabelValue = linkLabelValue.concat(Utility.camelCase(subCategory));
        linkLabelValue = linkLabelValue.replaceAll("[^\\w\\s]", "");

        linkLabel.setKey(linkLabelValue);
        linkLabel.setValue(subCategory);

        return linkLabel;
    }

    private Label generateLinkLabel(String section, String category, String subCategory, Question question) {
        Label linkLabel = new Label();

        String linkLabelValue = "LINK_";
        linkLabelValue = linkLabelValue.concat(Utility.camelCase(section));
        linkLabelValue = linkLabelValue.concat("_");
        linkLabelValue = linkLabelValue.concat(Utility.camelCase(category));
        linkLabelValue = linkLabelValue.concat("_");
        linkLabelValue = linkLabelValue.concat(Utility.camelCase(subCategory));
        linkLabelValue = linkLabelValue.concat("_");
        linkLabelValue = linkLabelValue.concat(Utility.camelCase(question.getQuestion()));
        linkLabelValue = linkLabelValue.replaceAll("[^\\w\\s]", "");

        if (logger.isDebugEnabled()) {
            logger.debug("LinkLabel for Question: " + linkLabelValue);
        }

        linkLabel.setKey(linkLabelValue);
        linkLabel.setValue(question.getQuestion());

        return linkLabel;
    }

    public void writeLinkLabelsForSectionsToFile() throws IOException {
        FileWriter fw = new FileWriter("C:\\tekpartner\\data\\hack4sac\\UniqueLinkLabelsForSections.txt", false);

        Iterator iterUniqueLinkLabelsForSections = this.setOfUniqueLinkLabelsForSections.iterator();

        while (iterUniqueLinkLabelsForSections.hasNext()) {
            Label label = (Label) iterUniqueLinkLabelsForSections.next();
            fw.write("\"");
            fw.write(label.getKey());
            fw.write("\" : \"");
            fw.write(label.getValue());
            fw.write("\",");
            fw.write("\n");
            fw.write("\"P_");
            fw.write(label.getKey());
            fw.write("\" : \"");
            fw.write(label.getValue());
            fw.write("\",");
            fw.write("\n");
        }

        fw.close();
    }

    public void writeLinkLabelsForCategoriesToFile() throws IOException {
        FileWriter fw = new FileWriter("C:\\tekpartner\\data\\hack4sac\\UniqueLinkLabelsForCategories.txt", false);

        Iterator iterUniqueLinkLabelsForCategories = this.setOfUniqueLinkLabelsForCategories.iterator();

        while (iterUniqueLinkLabelsForCategories.hasNext()) {
            Label label = (Label) iterUniqueLinkLabelsForCategories.next();
            fw.write("\"");
            fw.write(label.getKey());
            fw.write("\" : \"");
            fw.write(label.getValue());
            fw.write("\",");
            fw.write("\n");
            fw.write("\"P_");
            fw.write(label.getKey());
            fw.write("\" : \"");
            fw.write(label.getValue());
            fw.write("\",");
            fw.write("\n");
        }

        fw.close();
    }

    public void writeLinkLabelsForSubCategoriesToFile() throws IOException {
        FileWriter fw = new FileWriter("C:\\tekpartner\\data\\hack4sac\\UniqueLinkLabelsForSubCategories.txt", false);

        Iterator iterUniqueLinkLabelsForSubCategories = this.setOfUniqueLinkLabelsForSubCategories.iterator();

        while (iterUniqueLinkLabelsForSubCategories.hasNext()) {
            Label label = (Label) iterUniqueLinkLabelsForSubCategories.next();
            fw.write("\"");
            fw.write(label.getKey());
            fw.write("\" : \"");
            fw.write(label.getValue());
            fw.write("\",");
            fw.write("\n");
            fw.write("\"P_");
            fw.write(label.getKey());
            fw.write("\" : \"");
            fw.write(label.getValue());
            fw.write("\",");
            fw.write("\n");
        }

        fw.close();
    }

    public void writeLinkLabelsForQuestionsToFile() throws IOException {
        FileWriter fw = new FileWriter("C:\\tekpartner\\data\\hack4sac\\UniqueLinkLabelsForQuestions.txt", false);

        Iterator iterUniqueLinkLabelsForQuestions = this.setOfUniqueLinkLabelsForQuestions.iterator();

        while (iterUniqueLinkLabelsForQuestions.hasNext()) {
            Label label = (Label) iterUniqueLinkLabelsForQuestions.next();
            fw.write("\"");
            fw.write(label.getKey());
            fw.write("\" : \"");
            fw.write(label.getValue().replace("\"","\\\""));
            fw.write("\",");
            fw.write("\n");
            fw.write("\"P_");
            fw.write(label.getKey());
            fw.write("\" : \"");
            fw.write(label.getValue().replace("\"","\\\""));
            fw.write("\",");
            fw.write("\n");
        }

        fw.close();
    }
}