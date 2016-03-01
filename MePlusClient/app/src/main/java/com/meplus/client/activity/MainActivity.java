package com.meplus.client.activity;

import android.os.Bundle;

import com.meplus.client.R;
import com.meplus.client.utils.FIRUtils;

/**
 * 主页面
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FIRUtils.checkForUpdateInFIR(this);
    }
}
