package com.meplus.command;

import java.lang.Override;
import java.lang.String;

/**
 * Created by GleasonK on 6/25/15.
 */
public class Command {

    public final static String ACTION_CALL = "call";
    public final static String ACTION_UP = "up";
    public final static String ACTION_DOWN = "down";
    public final static String ACTION_LEFT = "left";
    public final static String ACTION_RIGHT = "right";

    public String sender;
    public String message;
    public long timeStamp;

    public Command() {
    }

    public Command(String sender, String message, long timeStamp) {
        this.sender = sender;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public int hashCode() {
        return (this.sender + this.message + this.timeStamp).hashCode();
    }
}
