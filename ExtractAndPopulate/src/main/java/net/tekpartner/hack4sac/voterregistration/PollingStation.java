package net.tekpartner.hack4sac.voterregistration;

//The Polling station has following two functions
//1.Store values from csv in an organized fashion
//2.Help in main method to be able to add a polling station to a data base by returning a String that contains queries
//These are the properties of data in polling station
//"ppid","sectionData","category","subcategory","qid","question","answer","data","comments"

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by Aravind on 3/23/2016.
 */
public class PollingStation {
    private static final Logger logger = Logger.getLogger(PollingStation.class.getName());

    private String ppid;
    private Map<String, Map<String, Map<String, Set<Question>>>> sectionData = new HashMap();

    public PollingStation(String ppid) {
        this.ppid = ppid;
    }

    private boolean containsSection(String key) {
        if (this.sectionData.containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean containsCategory(String section, String key) {
        if (containsSection(section) && this.sectionData.get(section).containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean containsSubcategory(String section, String category, String key) {
        if (containsCategory(section, category) && this.sectionData.get(section).get(category).containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    public void addLine(String section, String category, String subcategory, String qid, String question, String answer,
                        String data, String comments, Set<String> yesNo) {
        if (!containsSection(section)) {
            this.sectionData.put(section, new HashMap());
        }
        if (!containsCategory(section, category)) {
            this.sectionData.get(section).put(category, new HashMap());
        }
        if (!containsSubcategory(section, category, subcategory)) {
            this.sectionData.get(section).get(category).put(subcategory, new HashSet());
        }
        this.sectionData.get(section).get(category).get(subcategory).add(new Question(qid, question, answer, data, comments));

        if (StringUtils.equalsIgnoreCase(answer, "Yes") || StringUtils.equalsIgnoreCase(answer, "No")) {
            yesNo.add(question);
        }
    }

    public void addLine(String section, String category, String subcategory, String qid, String question, String answer,
                        String data, String comments, Map<String, Map<String, Map<String, Set<Question>>>> yesNoQuestions) {
        if (logger.isDebugEnabled()) {
            logger.debug(StringUtils.trimToEmpty(section) + " - "
                    + StringUtils.trimToEmpty(category) + " - "
                    + StringUtils.trimToEmpty(subcategory) + " - "
                    + StringUtils.trimToEmpty(qid) + " - "
                    + StringUtils.trimToEmpty(question) + " - "
                    + StringUtils.trimToEmpty(answer) + " - "
                    + StringUtils.trimToEmpty(data) + " - "
                    + StringUtils.trimToEmpty(comments));
        }
        if (!containsSection(section)) {
            this.sectionData.put(section, new HashMap());
        }
        if (!containsCategory(section, category)) {
            this.sectionData.get(section).put(category, new HashMap());
        }
        if (!containsSubcategory(section, category, subcategory)) {
            this.sectionData.get(section).get(category).put(subcategory, new HashSet());
        }
        this.sectionData.get(section).get(category).get(subcategory).add(new Question(qid, question, answer, data, comments));

        if (StringUtils.equalsIgnoreCase(answer, "Yes") || StringUtils.equalsIgnoreCase(answer, "No")) {
            this.populateYesNoQuestions(yesNoQuestions, section, category, subcategory, new Question(qid, question, answer, data, comments));
        }
    }

    private void populateYesNoQuestions(Map<String, Map<String, Map<String, Set<Question>>>> yesNoQuestions, String section, String category, String subCategory, Question question) {
        boolean questionExists = false;

        if (!yesNoQuestions.containsKey(section)) {
            yesNoQuestions.put(section, new HashMap());
        }

        if (!yesNoQuestions.get(section).containsKey(category)) {
            yesNoQuestions.get(section).put(category, new HashMap());
        }

        if (!yesNoQuestions.get(section).get(category).containsKey(subCategory)) {
            yesNoQuestions.get(section).get(category).put(subCategory, new HashSet());
        }

        Set<Question> questions = yesNoQuestions.get(section).get(category).get(subCategory);
        for (Question aQuestion : questions) {
            if (StringUtils.equals(aQuestion.getQuestion(), question.getQuestion())) {
                questionExists = true;
            }
        }

        if (!questionExists) {
            yesNoQuestions.get(section).get(category).get(subCategory).add(question);
        }
    }

    private void print() {
        Iterator<String> i = this.sectionData.keySet().iterator();
        if (logger.isDebugEnabled()) {
            logger.debug("sectionData:  ");
        }
        while (i.hasNext()) {
            String test = i.next();
            if (logger.isDebugEnabled()) {
                logger.debug("     " + test);
            }
            Iterator<String> j = this.sectionData.get(test).keySet().iterator();
            if (logger.isDebugEnabled()) {
                logger.debug("   Category:    ");
            }
            while (j.hasNext()) {
                String test1 = j.next();
                if (logger.isDebugEnabled()) {
                    logger.debug("             " + test1);
                }
                Iterator<String> k = this.sectionData.get(test).get(test1).keySet().iterator();
                if (logger.isDebugEnabled()) {
                    logger.debug("                Sub Category:     ");
                }
                while (k.hasNext()) {
                    String test3 = k.next();
                    if (logger.isDebugEnabled()) {
                        logger.debug("                    " + test3);
                        logger.debug(this.sectionData.get(test).get(test1).get(test3).toString());
                    }
                }
            }
        }
    }

    public Set<String> getSections() {
        return this.sectionData.keySet();
    }

    public Set<String> getCategories(String section) {
        return this.sectionData.get(section).keySet();
    }

    public Set<String> getSubCategories(String section, String category) {
        return this.sectionData.get(section).get(category).keySet();
    }

    public Set<Question> getQuestions(String section, String category, String subCategory) {
        return this.sectionData.get(section).get(category).get(subCategory);
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
    }
}