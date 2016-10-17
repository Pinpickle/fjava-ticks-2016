package uk.ac.cam.cas217.fjava.tick2;

import java.io.Serializable;

/**
 * Encapsulates a chat message for simple testing purposes
 */
public class TestMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private String text;
    public String getMessage() {return text;}
    public void setMessage(String msg) {text = msg;}
}