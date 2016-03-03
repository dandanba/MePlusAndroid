package com.meplus.client.callbacks;

import android.content.Context;
import android.util.Log;

import com.meplus.client.Constants;
import com.pubnub.api.Callback;
import com.pubnub.api.PubnubError;

import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by dandanba on 3/3/16.
 */
public class PNCallback extends Callback {
    private final static String TAG = "PUBNUB";
    private final Context mContext;

    public PNCallback(Context context) {
        mContext = context;
    }

    @Override
    public void successCallback(String channel, Object response) {
        final String message = "successCallback: " + response;
        log(message);
    }

    @Override
    public void connectCallback(String channel, Object response) {
        final String message = "connectCallback: " + response;
        log(message);
    }

    @Override
    public void errorCallback(String channel, PubnubError error) {
        final String message = "errorCallback: " + error.getErrorString();
        log(message);
    }

    private void log(String message) {
        Log.d(TAG, message);
        if (Constants.sRelease) {
            ToastUtils.show(mContext, message);
        }
    }
}
