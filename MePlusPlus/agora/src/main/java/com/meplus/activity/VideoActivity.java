package com.meplus.activity;

import android.os.Bundle;
import android.view.View;

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

        findViewById(R.id.channel_bottom_actions_container).setVisibility(View.GONE);
    }
}
