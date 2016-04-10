package com.meplus.speech;


import com.meplus.events.BaseEvent;

/**
 * Created by dandanba on 3/3/16.
 */
public class SpeechEvent extends BaseEvent {
    public SpeechEvent(Speech understand) {
        this.mUnderstand = understand;
    }

    private Speech mUnderstand;

    public Speech getUnderstand() {
        return mUnderstand;
    }

    public void setUnderstand(Speech understand) {
        this.mUnderstand = understand;
    }


}
