package com.meplus.client.activity;

import android.os.Bundle;

import com.meplus.activity.VideoActivity;
import com.meplus.command.Command;

import org.greenrobot.eventbus.EventBus;

import io.agora.rtc.IRtcEngineEventHandler;

/**
 * 电话页面
 */
public class CallActivity extends VideoActivity {
    private static final String TAG = CallActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {
        super.onLeaveChannel(stats);
        final Command event = new Command(String.valueOf(userId), Command.ACTION_HUNG_UP, System.currentTimeMillis());
        EventBus.getDefault().post(event);
    }

}
