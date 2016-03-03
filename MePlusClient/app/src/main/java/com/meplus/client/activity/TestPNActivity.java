package com.meplus.client.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.meplus.client.R;
import com.meplus.client.events.PNEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 主页面
 */
public class TestPNActivity extends PNActivity {
    @Bind(R.id.textText)
    TextView textText;
    @Bind(R.id.editText)
    EditText editText;

    @OnClick(R.id.button)
    public void onButtonClick(View view) {
        final String message = editText.getText().toString();
        publish(message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testpn);
        ButterKnife.bind(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(PNEvent event) {
        String message = event.getMessage().toString();
        textText.setText(message);
    }
}
