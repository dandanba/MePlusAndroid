/*
 * Copyright 2014 Akexorcist
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.meplus.robot.presenters;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.meplus.punub.Command;
import com.meplus.robot.activity.DeviceListActivity;
import com.meplus.robot.events.BluetoothEvent;
import com.meplus.robot.events.Event;

import org.greenrobot.eventbus.EventBus;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.BluetoothConnectionListener;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class BluetoothPresenter {
    private static final String TAG = BluetoothPresenter.class.getSimpleName();
    BluetoothSPP bt;

    public boolean isConnected() {
        return connected;
    }

    boolean connected = false;

    public BluetoothPresenter(Context context) {
        bt = new BluetoothSPP(context);
    }

    public boolean isBluetoothAvailable() {
        return bt.isBluetoothAvailable();
    }


    public boolean isBluetoothEnabled() {
        return bt.isBluetoothEnabled();
    }

    public boolean isServiceAvailable() {
        return bt.isServiceAvailable();
    }

    public void create(Context context) {
        bt.setBluetoothConnectionListener(new BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                connected = true;
                postEvent();
                Toast.makeText(context, "Connected to " + name + "\n" + address, Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() {
                connected = false;
                postEvent();
                Toast.makeText(context, "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() {
                connected = false;
                postEvent();
                Toast.makeText(context, "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });
        bt.setOnDataReceivedListener((data, message) -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show());

    }

    /**
     * 启动蓝牙软件部分
     *
     * @param activity
     */
    public void enableBluetooth(Activity activity) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
    }

    /**
     * 启动蓝牙的服务
     */
    public void startBluetoothService() {
        bt.setupService();
        bt.startService(BluetoothState.DEVICE_OTHER);
    }

    /**
     * 选择可以拦截的蓝牙
     *
     * @param activity
     */
    public void connectDeviceList(Activity activity) {
        Intent intent = new Intent(activity, DeviceListActivity.class);
        activity.startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
            bt.disconnect();
        }
    }

    /**
     *
     */
    public void stopBluetoothService() {
        bt.stopService();
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            } else {
                Toast.makeText(activity, "Bluetooth was not enabled.", Toast.LENGTH_SHORT).show();
                activity.finish();
            }
        }
    }

    private void postEvent() {
        final BluetoothEvent event = new BluetoothEvent(Event.STATUS_OK);
        event.setConnected(connected);
        EventBus.getDefault().post(event);
    }

    private int PERCENT = 60;
    private int MAX = 500;

    public void sendDefault() {
        // 自主避障功能使能（默认关闭）
        byte CheckSum = (byte) (0X66 + (byte) 0XAA + 0X09 + 0X10);
        byte[] buffer = new byte[]{0X66, (byte) 0XAA, 0X09, 0X10, 00, 00, 00, 00, CheckSum};
    }

    public void sendDirection(String action) {
        final int V = (MAX * PERCENT / 100);

        int V1 = 0;
        int V2 = 0;
        byte V1H = 0;
        byte V1L = 0;
        byte V2H = 0;
        byte V2L = 0;
        byte CheckSum;

        if (action.equals(Command.ACTION_UP)) {
            V1 = V;
            V2 = V;
        } else if (action.equals(Command.ACTION_DOWN)) {
            V1 = -V;
            V2 = -V;
        } else if (action.equals(Command.ACTION_LEFT)) {
            V1 = -V;
            V2 = V;
        } else if (action.equals(Command.ACTION_RIGHT)) {
            V1 = V;
            V2 = -V;
        } else if (action.equals(Command.ACTION_STOP)) {
        }

        V1H = (byte) (V1 >> 8);
        V2H = (byte) (V2 >> 8);
        V1L = (byte) (V1);
        V2L = (byte) (V2);

        CheckSum = (byte) (0X66 + (byte) 0XAA + 0X09 + 0X11 + V1H + V1L + V2H + V2L);
        byte[] buffer = new byte[]{0X66, (byte) 0XAA, 0X09, 0X11, V1H, V1L, V2H, V2L, CheckSum};

        send(buffer);
    }


    private void send(byte[] buffer) {
        bt.send(buffer, false);
    }


}
