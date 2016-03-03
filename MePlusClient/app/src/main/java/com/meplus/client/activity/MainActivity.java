package com.meplus.client.activity;

import android.os.Bundle;
import android.view.View;

import com.meplus.client.R;
import com.meplus.client.utils.FIRUtils;
import com.meplus.client.utils.IntentUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.agora.sample.agora.EntryActivity;

/**
 * 主页面
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        FIRUtils.checkForUpdateInFIR(this);
    }

    @OnClick({R.id.button, R.id.button1, R.id.button2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                startActivity(IntentUtils.generateIntent(this, TestPNActivity.class));
                break;
            case R.id.button1:
                startActivity(IntentUtils.generateIntent(this, EntryActivity.class));
                break;
            case R.id.button2:
                startActivity(IntentUtils.generateIntent(this, TestScannerActivity.class));
                break;
            default:
                break;
        }
    }
}
