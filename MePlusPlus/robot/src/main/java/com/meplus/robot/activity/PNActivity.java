package com.meplus.robot.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.meplus.robot.R;
import com.meplus.robot.Constants;
import com.meplus.robot.callbacks.PNCallback;
import com.meplus.robot.utils.JsonUtils;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;

import hugo.weaving.DebugLog;
import io.agora.sample.agora.AgoraApplication;
import io.agora.sample.agora.BaseEngineHandlerActivity;

/**
 * PubNub Activity
 * https://www.pubnub.com/docs/tutorials/pubnub-publish-subscribe
 * https://www.pubnub.com/docs/android-java/api-reference
 */
public class PNActivity extends BaseEngineHandlerActivity {
    private final static String TAG = "PUBNUB";
    public String mUUID;
    public String mChannel;
    private Pubnub mPubNub;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initAgora();
        initPubNub();
    }

    private void initAgora() {
        final String vendorKey = getString(R.string.vendor_key);
        ((AgoraApplication) getApplication()).setUserInformation(vendorKey, mUUID);
        ((AgoraApplication) getApplication()).setRtcEngine(vendorKey);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unsubscribe();
        if (mPubNub != null) {
            mPubNub.shutdown();
            mPubNub = null;
        }
    }

    public void initPubNub() {
        Log.i(TAG, "initPubNub :" + "uuid :" + mUUID);
        mPubNub = new Pubnub(Constants.PN_PUB_KEY, Constants.PN_SUB_KEY);
        mPubNub.setUUID(mUUID);
        subscribe();
    }

    @DebugLog
    public void subscribe() {
        Log.i(TAG, "subscribe :" + "channel :" + mChannel);
        if (TextUtils.isEmpty(mChannel)) {
            return;
        }
        try {
            mPubNub.subscribe(mChannel, new PNCallback(this) {
                @Override
                public void successCallback(String channel, Object message) {
                    super.successCallback(channel, message);
                    if (mChannel.equals(channel)) {
                        com.meplus.command.Command command = JsonUtils.readValue(message.toString(), com.meplus.command.Command.class);
                        if (!mUUID.equals(command.getSender())) {
                            Log.i(TAG, "delay :" + (command.getTimeStamp() - System.currentTimeMillis()));
                            onMessage(command.getMessage());
                        }
                    }
                }

                @Override
                public void connectCallback(String channel, Object message) {
                    super.connectCallback(channel, message);
                }

            });
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    @DebugLog
    public void unsubscribe() {
        Log.i(TAG, "unsubscribe :" + ",channel :" + mChannel);
        if (TextUtils.isEmpty(mChannel)) {
            return;
        }
        if (mPubNub != null) {
            mPubNub.unsubscribeAll();
        }
    }

    @DebugLog
    public void publish(String message) {
        Log.i(TAG, "publish :" + ",message :" + message);
        if (TextUtils.isEmpty(mChannel)) {
            return;
        }
        if (message.equals("")) return; // Return if empty

        final com.meplus.command.Command command = new com.meplus.command.Command(mUUID, message, System.currentTimeMillis());
        message = JsonUtils.writeValueAsString(command);
        mPubNub.publish(mChannel, message, new PNCallback(this) {
            @Override
            public void successCallback(String channel, Object message) {
            }
        });
    }

    public void onMessage(String message) {
    }

}
