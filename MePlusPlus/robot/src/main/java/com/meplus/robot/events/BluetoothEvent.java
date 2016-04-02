package com.meplus.robot.events;

/**
 * Created by dandanba on 3/3/16.
 */
public class BluetoothEvent extends Event {
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