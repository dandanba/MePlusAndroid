package com.meplus.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.sample.agora.AgoraApplication;
import io.agora.sample.agora.ChannelActivity;
import io.agora.sample.agora.R;

/**
 * Created by dandanba on 3/30/16.
 * 视频通话页面
 */
public class VideoActivity extends ChannelActivity {
    private static final String TAG = VideoActivity.class.getSimpleName();
    private final List<Integer> mUids = new ArrayList<>();


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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {  // 回调不在UI主线程
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
        if (mUids.contains(uid)) {
            mUids.remove(uid);
            if (mUids.isEmpty()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doBackPressed();
                    }
                });
            }
        }
    }

    @Override
    public void onNetworkQuality(final int quality) {
        super.onNetworkQuality(quality);
        Log.i(TAG, "network quality: " + quality);
    }


    @Override
    public synchronized void onUserJoined(int uid, int elapsed) {
        super.onUserJoined(uid, elapsed);
        if (!mUids.contains(uid)) {
            mUids.add(uid);
        }

    }

    @Override
    public void timeEscaped(int time) {
        super.timeEscaped(time);
        if (time > 10 && mUids.isEmpty()) { // 30 秒钟无人进入，就自动退出。
            Toast.makeText(VideoActivity.this, "对方不在线", Toast.LENGTH_SHORT).show();
            doBackPressed();
        }
    }

    @Override
    public int getUserViewSize() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }

}
