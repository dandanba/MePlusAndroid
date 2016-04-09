package com.meplus.speech;


import com.meplus.events.BaseEvent;

/**
 * Created by dandanba on 3/3/16.
 */
public class UnderstandEvent extends BaseEvent {
    public UnderstandEvent(Understand understand) {
        super();
        this.mUnderstand = understand;
    }

    private Understand mUnderstand;

    public Understand getUnderstand() {
        return mUnderstand;
    }

    public void setUnderstand(Understand understand) {
        this.mUnderstand = understand;
    }


}
