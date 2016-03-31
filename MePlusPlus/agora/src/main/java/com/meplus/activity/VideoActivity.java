package com.meplus.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.sample.agora.AgoraApplication;
import io.agora.sample.agora.ChannelActivity;
import io.agora.sample.agora.R;

/**
 * Created by dandanba on 3/30/16.
 */
public class VideoActivity extends ChannelActivity {

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
    public void onUserOffline(final int uid) {
        super.onUserOffline(uid);
        // 回调不在UI主线程
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                doBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }
}
