package com.meplus.client.events;

/**
 * Created by dandanba on 3/3/16.
 */
public class PNEvent extends Event {
    private Object message;

    public PNEvent(Object message) {
        super(STATUS_OK);
        this.message = message;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
