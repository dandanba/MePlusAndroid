package com.meplus.robot.events;

import com.meplus.events.BaseEvent;

/**
 * Created by dandanba on 3/3/16.
 */
public class BluetoothEvent extends BaseEvent {
    public BluetoothEvent() {
        super();
    }

    private boolean mConnected;

    public byte getSOC() {
        return mSOC;
    }

    public void setSOC(byte soc) {
        mSOC = soc;
    }

    private byte mSOC;

    public boolean isConnected() {
        return mConnected;
    }

    public void setConnected(boolean state) {
        this.mConnected = state;
    }

}