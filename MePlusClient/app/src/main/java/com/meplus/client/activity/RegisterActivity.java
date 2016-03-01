package com.meplus.client.activity;

import android.os.Bundle;

import com.meplus.client.R;
import com.meplus.client.fragments.SimpleScannerFragment;

/**
 * 注册页面
 */
public class RegisterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        replaceContainer(R.id.frame_layout, SimpleScannerFragment.newInstance());
    }

}
