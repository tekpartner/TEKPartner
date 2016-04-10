package net.tekpartner.hack4sac.voterregistration;

/**
 * Created by MAXIMUS on 3/24/2016.
 */
public class Question {
    private String qid;
    private String question;
    private String answer;
    private String data;
    private String comments;

    Question(String qid, String question, String answer, String data, String comments) {
        this.qid = qid;
        this.question = question;
        this.answer = answer;
        this.data = data;
        this.comments = comments;
    }

    public String toString() {
        return this.question + " : " + this.answer;
    }

    public String getQid() {
        return this.qid;
    }

    public String getQuestion() {
        return this.question;
    }

    public String getAnswer() {
        return this.answer;
    }

    public String getData() {
        return this.data;
    }

    public String getComments() {
        return this.comments;
    }
}
