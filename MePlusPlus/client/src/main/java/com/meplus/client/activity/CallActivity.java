package com.meplus.client.activity;

import android.os.Bundle;
import android.view.View;

import com.meplus.activity.VideoActivity;
import com.meplus.client.R;
import com.meplus.client.events.CommandEvent;
import com.meplus.client.events.Event;
import com.meplus.command.Command;

import org.greenrobot.eventbus.EventBus;

/**
 * 电话页面
 */
public class CallActivity extends VideoActivity implements View.OnClickListener {
    private static final String TAG = CallActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        findViewById(R.id.left_button).setOnClickListener(this);
        findViewById(R.id.up_button).setOnClickListener(this);
        findViewById(R.id.right_button).setOnClickListener(this);
        findViewById(R.id.down_button).setOnClickListener(this);

    }

    @Override
    public int getContentView() {
        return R.layout.activity_call;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_button:
                post(new CommandEvent(Command.ACTION_LEFT));
                break;
            case R.id.up_button:
                post(new CommandEvent(Command.ACTION_UP));
                break;
            case R.id.right_button:
                post(new CommandEvent(Command.ACTION_RIGHT));
                break;
            case R.id.down_button:
                post(new CommandEvent(Command.ACTION_DOWN));
                break;
        }
    }

    private void post(Event event) {
        EventBus.getDefault().post(event);
    }


}
