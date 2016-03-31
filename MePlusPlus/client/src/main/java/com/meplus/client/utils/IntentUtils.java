package com.meplus.client.utils;

import android.content.Context;
import android.content.Intent;

import com.meplus.client.activity.CallActivity;

/**
 * Created by dandanba on 3/1/16.
 */
public class IntentUtils extends com.marvinlabs.intents.IntentUtils {
    private static final String TAG = IntentUtils.class.getSimpleName();

    public static Intent generateIntent(Context context, Class<?> cls) {
        return new Intent(context, cls);
    }

    public static Intent generateCallIntent(Context context, String channel, int userId) {
        Intent intent = new Intent(context, CallActivity.class);
        intent.putExtra(CallActivity.EXTRA_TYPE, CallActivity.CALLING_TYPE_VIDEO);
        intent.putExtra(CallActivity.EXTRA_USER_ID, userId);
        intent.putExtra(CallActivity.EXTRA_CHANNEL, channel);
        return intent;
    }

}
