package com.meplus.client.events;

/**
 * Created by dandanba on 3/3/16.
 */
public class SignUpEvent extends Event {
    private String status;

    public SignUpEvent(String status) {
        this.status = status;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
