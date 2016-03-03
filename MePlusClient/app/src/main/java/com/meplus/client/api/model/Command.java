package com.meplus.client.api.model;

/**
 * Created by GleasonK on 6/25/15.
 */
public class Command {
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
