package com.meplus.client.events;

/**
 * Created by dandanba on 3/3/16.
 */
public class CommandEvent extends Event {
    public String getAction() {
        return action;
    }

    private String action;

    public CommandEvent(String action) {
        super(STATUS_OK);
        this.action = action;
    }

}
