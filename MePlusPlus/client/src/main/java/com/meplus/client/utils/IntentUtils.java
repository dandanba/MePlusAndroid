package com.meplus.client.utils;

import android.content.Context;
import android.content.Intent;

import com.meplus.activity.VideoActivity;

import io.agora.sample.agora.ChannelActivity;

/**
 * Created by dandanba on 3/1/16.
 */
public class IntentUtils extends com.marvinlabs.intents.IntentUtils {
    private static final String TAG = IntentUtils.class.getSimpleName();

    public static Intent generateIntent(Context context, Class<?> cls) {
        return new Intent(context, cls);
    }

    public static Intent generateVideoIntent(Context context, String channel) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra(ChannelActivity.EXTRA_TYPE, VideoActivity.CALLING_TYPE_VIDEO);
        intent.putExtra(ChannelActivity.EXTRA_CHANNEL, channel);
        return intent;
    }
}
