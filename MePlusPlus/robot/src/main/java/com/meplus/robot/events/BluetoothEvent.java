package com.meplus.robot.events;

import com.meplus.events.BaseEvent;

/**
 * Created by dandanba on 3/3/16.
 */
public class BluetoothEvent extends BaseEvent {
    public BluetoothEvent(String status) {
        super(status);
    }

    private boolean mConnected;

    public boolean isConnected() {
        return mConnected;
    }

    public void setConnected(boolean state) {
        this.mConnected = state;
    }

}