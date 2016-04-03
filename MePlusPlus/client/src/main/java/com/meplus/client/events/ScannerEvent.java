package com.meplus.client.events;

import com.meplus.events.BaseEvent;

/**
 * Created by dandanba on 3/3/16.
 */
public class ScannerEvent extends BaseEvent {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ScannerEvent(String content) {
        super(STATUS_OK);
        this.content = content;
    }

}
