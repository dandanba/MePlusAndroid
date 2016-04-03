package com.meplus.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.sample.agora.AgoraApplication;
import io.agora.sample.agora.ChannelActivity;
import io.agora.sample.agora.R;

/**
 * Created by dandanba on 3/30/16.
 */
public class VideoActivity extends ChannelActivity {

    private static final String TAG = VideoActivity.class.getSimpleName();
    private int uid;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    @Override
    public void initViews() {
        super.initViews();
        // 隐藏掉，选择声音和视频的切换按钮
        findViewById(R.id.channel_bottom_actions_container).setVisibility(View.GONE);
        // 隐藏掉，标题栏
        findViewById(R.id.channel_actionbar).setVisibility(View.GONE);
        // 禁止滑动
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        // 浮动框不显示
        setFloat(false);
    }

    @Override
    public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {
        // 回调不在UI主线程
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // @see ChannelActivity onLeaveChannel
                ((AgoraApplication) getApplication()).setIsInChannel(false);
                ((AgoraApplication) getApplication()).setChannelTime(0);
                finish();
            }
        });
    }

    @Override
    public void onUserOffline(final int uid, final int reason) {
        super.onUserOffline(uid, reason);
        // 如果有人离线，那么，在 1对1的视频情况下就把当前的视频退出
//        if (reason == Constants.USER_OFFLINE_QUIT) { // 用户主动
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                doBackPressed();
            }
        });
//        }
    }

    @Override
    public void onNetworkQuality(final int quality) {
        super.onNetworkQuality(quality);
        Log.i(TAG, "network quality: " + quality);
    }


    @Override
    public synchronized void onUserJoined(int uid, int elapsed) {
        super.onUserJoined(uid, elapsed);
        this.uid = uid;
    }

    @Override
    public void timeEscaped(int time) {
        super.timeEscaped(time);
        if (time > 10 && uid == 0) { // 30 秒钟无人进入，就自动退出。
            Toast.makeText(VideoActivity.this, "对方不在线", Toast.LENGTH_SHORT).show();
            doBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }

}
