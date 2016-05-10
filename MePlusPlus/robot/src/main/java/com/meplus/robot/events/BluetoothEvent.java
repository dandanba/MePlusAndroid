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
    private byte dis1, dis2, dis3, dis4, dis5;
    private byte mSOC;

    public byte getDis5() {
        return dis5;
    }

    public void setDis5(byte dis5) {
        this.dis5 = dis5;
    }

    public byte getDis1() {
        return dis1;
    }

    public void setDis1(byte dis1) {
        this.dis1 = dis1;
    }

    public byte getDis2() {
        return dis2;
    }

    public void setDis2(byte dis2) {
        this.dis2 = dis2;
    }

    public byte getDis3() {
        return dis3;
    }

    public void setDis3(byte dis3) {
        this.dis3 = dis3;
    }

    public byte getDis4() {
        return dis4;
    }

    public void setDis4(byte dis4) {
        this.dis4 = dis4;
    }

    public byte getSOC() {
        return mSOC;
    }

    public void setSOC(byte soc) {
        mSOC = soc;
    }

    public boolean isConnected() {
        return mConnected;
    }

    public void setConnected(boolean state) {
        this.mConnected = state;
    }

}