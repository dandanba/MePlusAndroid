package com.meplus.robot.presenters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.meplus.punub.Command;
import com.meplus.punub.Constants;
import com.meplus.punub.PNCallback;
import com.meplus.robot.events.CommandEvent;
import com.meplus.robot.events.Event;
import com.meplus.robot.utils.JsonUtils;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;

import org.greenrobot.eventbus.EventBus;

import hugo.weaving.DebugLog;

/**
 * PNPresenter
 * https://www.pubnub.com/docs/tutorials/pubnub-publish-subscribe
 * https://www.pubnub.com/docs/android-java/api-reference
 */
public class PubnubPresenter {
    private final static String TAG = "PUBNUB";
    private Pubnub mPubnub;
    private String mChannel;

    public void initPubnub(String uuId) {
        mPubnub = new Pubnub(Constants.PN_PUB_KEY, Constants.PN_SUB_KEY);
        mPubnub.setUUID(uuId);
    }

    public void destroy() {
        unsubscribe();
        if (mPubnub != null) {
            mPubnub.shutdown();
            mPubnub = null;
        }
    }

    @DebugLog
    public void subscribe(Context context, String channel) {
        Log.i(TAG, "subscribe :" + "channel :" + channel);
        if (TextUtils.isEmpty(channel)) {
            return;
        }
        mChannel = channel;
        try {
            mPubnub.subscribe(channel, new PNCallback(context) {
                @Override
                public void successCallback(String channel, Object message) {
                    super.successCallback(channel, message);
                    if (channel.equals(channel)) {
                        Command command = JsonUtils.readValue(message.toString(), Command.class);
                        if (!mPubnub.getUUID().equals(command.getSender())) {
                            Log.i(TAG, "delay :" + (System.currentTimeMillis() - command.getTimeStamp()));
                            final CommandEvent event = new CommandEvent(Event.STATUS_OK);
                            event.setCommand(command);
                            EventBus.getDefault().post(event);
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
        if (mPubnub != null) {
            mPubnub.unsubscribeAll();
        }
    }

    @DebugLog
    public void publish(Context context, String message) {
        Log.i(TAG, "publish :" + ",message :" + message);
        if (TextUtils.isEmpty(mChannel)) {
            return;
        }
        if (message.equals("")) return; // Return if empty

        final Command command = new Command(mPubnub.getUUID(), message);
        message = JsonUtils.writeValueAsString(command);
        mPubnub.publish(mChannel, message, new PNCallback(context) {
            @Override
            public void successCallback(String channel, Object message) {
            }
        });
    }
}
