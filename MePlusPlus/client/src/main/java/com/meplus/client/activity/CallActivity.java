package com.meplus.client.activity;

import android.os.Bundle;
import android.view.View;

import com.meplus.activity.VideoActivity;
import com.meplus.avos.objects.AVOSUser;
import com.meplus.client.R;
import com.meplus.events.EventUtils;
import com.meplus.punub.Command;
import com.meplus.punub.CommandEvent;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 电话页面
 */
public class CallActivity extends VideoActivity {
    private static final String TAG = CallActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        ButterKnife.bind(this);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_call;
    }


    @OnClick({R.id.left_button, R.id.up_button, R.id.right_button, R.id.down_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_button:
                postEvent(Command.ACTION_LEFT);
                break;
            case R.id.up_button:
                postEvent(Command.ACTION_UP);
                break;
            case R.id.right_button:
                postEvent(Command.ACTION_RIGHT);
                break;
            case R.id.down_button:
                postEvent(Command.ACTION_DOWN);
                break;
        }
    }

    private void postEvent(String message) {
        final CommandEvent event = new CommandEvent();
        final AVOSUser user = AVOSUser.getCurrentUser(AVOSUser.class);
        final String sender = user.getUUId();
        event.setCommand(new Command(sender, message));
        EventUtils.postEvent(event);
    }


}
