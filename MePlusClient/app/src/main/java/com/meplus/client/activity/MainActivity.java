package com.meplus.client.activity;

import android.os.Bundle;
import android.view.View;

import com.meplus.client.R;
import com.meplus.client.utils.FIRUtils;
import com.meplus.client.utils.IntentUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 主页面
 */
public class MainActivity extends BaseActivity {
    @OnClick(R.id.button)
    public void onButtonClick(View view) {
        startActivity(IntentUtils.generateIntent(this, TestPNActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        FIRUtils.checkForUpdateInFIR(this);
    }

}
