package com.meplus.speech;

/**
 * Created by GleasonK on 6/25/15.
 */
public class Understand {
    public static final String ACTION_SPEECH_UNDERSTAND = "speech_understand"; // message!
    public static final String ACTION_ERROR = "error";
    public static final String ACTION_UNDERSTAND = "understand"; // listen speech end
    public static final String ACTION_LISTEN = "listen";// listen speech begin
    public static final String ACTION_SPEECH = "speech";// speech complete

    private String message;
    private String action;

    public Understand(String action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
