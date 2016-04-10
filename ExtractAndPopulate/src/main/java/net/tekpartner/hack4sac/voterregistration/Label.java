package net.tekpartner.hack4sac.voterregistration;

/**
 * Class to store Label.
 */
public class Label {
    private String key;
    private String value;

    public Label() {
    }

    public Label(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String toString() {
        return "\"" + key + "\"" + " : " + "\"" + value + "\"";
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
