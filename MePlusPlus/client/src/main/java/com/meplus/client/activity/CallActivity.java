package com.meplus.client.activity;

import android.os.Bundle;
import android.view.View;

import com.meplus.activity.VideoActivity;
import com.meplus.client.R;
import com.meplus.client.events.CommandEvent;
import com.meplus.client.events.Event;
import com.meplus.command.Command;

import org.greenrobot.eventbus.EventBus;

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
        new ControlViewHolder(getWindow().getDecorView());
    }

    @Override
    public int getContentView() {
        return R.layout.activity_call;
    }

    class ControlViewHolder {
        ControlViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick({R.id.left_button, R.id.up_button, R.id.right_button, R.id.down_button})
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


}
