package com.meplus.activity;

import android.content.Context;
import android.content.Intent;

import io.agora.sample.agora.ChannelActivity;

/**
 * Created by dandanba on 3/31/16.
 */
public class IntentUtils {
    public static Intent generateCallIntent(Context context, String channel, int userId) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra(VideoActivity.EXTRA_TYPE, VideoActivity.CALLING_TYPE_VIDEO);
        intent.putExtra(VideoActivity.EXTRA_USER_ID, userId);
        intent.putExtra(VideoActivity.EXTRA_CHANNEL, channel);
        return intent;
    }
}
