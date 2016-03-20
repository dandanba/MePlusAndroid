package com.meplus.robot.activity;

import android.os.Bundle;
import android.util.Log;

import com.meplus.robot.Constants;
import com.meplus.robot.api.model.Command;
import com.meplus.robot.api.model.Robot;
import com.meplus.robot.app.MPApplication;
import com.meplus.robot.callbacks.PNCallback;
import com.meplus.robot.events.PNEvent;
import com.meplus.robot.utils.JsonUtils;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;

import org.greenrobot.eventbus.EventBus;

/**
 * PubNub Activity
 * https://www.pubnub.com/docs/tutorials/pubnub-publish-subscribe
 * https://www.pubnub.com/docs/android-java/api-reference
 */
public class PNActivity extends BaseActivity {
    private final static String TAG = "PUBNUB";
    private String mUUID;
    private String mChannel;
    private Pubnub mPubNub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Robot currentUser = MPApplication.getsInstance().getRobot();
        mUUID = currentUser.getObjectId();
        mChannel = "test";

        initPubNub();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mPubNub == null) {
            initPubNub();
        } else {
            subscribe();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unsubscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    public void subscribe() {
        Log.i(TAG, "subscribe :" + "channel :" + mChannel);
        EventBus.getDefault().register(this);

        try {
            mPubNub.subscribe(mChannel, new PNCallback(this) {
                @Override
                public void successCallback(String channel, Object message) {
                    super.successCallback(channel, message);
                    if (mChannel.equals(channel)) {
                        Command command = JsonUtils.readValue(message.toString(), Command.class);
                        if (!mUUID.equals(command.getSender())) {
                            Log.i(TAG, "delay :" + (command.getTimeStamp() - System.currentTimeMillis()));
                            EventBus.getDefault().post(new PNEvent(command.getMessage()));
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

    public void unsubscribe() {
        Log.i(TAG, "unsubscribe :" + ",channel :" + mChannel);
        if (mPubNub != null) {
            mPubNub.unsubscribeAll();
        }
        EventBus.getDefault().unregister(this);
    }

    public void publish(String message) {
        Log.i(TAG, "publish :" + ",message :" + message);
        if (message.equals("")) return; // Return if empty

        final Command command = new Command(mUUID, message, System.currentTimeMillis());
        message = JsonUtils.writeValueAsString(command);
        mPubNub.publish(mChannel, message, new PNCallback(this) {
            @Override
            public void successCallback(String channel, Object message) {
            }
        });
    }

}
