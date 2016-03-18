package com.meplus.robot.activity;

import android.os.Bundle;
import android.view.View;

import com.meplus.client.R;
import com.meplus.robot.utils.IntentUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.agora.sample.agora.EntryActivity;

/**
 * 测试
 */
public class TestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.button, R.id.button1, R.id.button2,R.id.button3})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                startActivity(IntentUtils.generateIntent(this, TestPNActivity.class));
                break;
            case R.id.button1:
                startActivity(IntentUtils.generateIntent(this, EntryActivity.class));
                break;
            case R.id.button2:
                startActivity(IntentUtils.generateIntent(this, ScannerActivity.class));
                break;
            case R.id.button3:
                startActivity(IntentUtils.generateIntent(this, TestFaceActivity.class));
                break;
            default:
                break;
        }
    }
}
