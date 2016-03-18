package com.meplus.client.activity;

import android.os.Bundle;

import com.meplus.client.BuildConfig;
import com.meplus.client.R;
import com.meplus.client.events.ScannerEvent;
import com.meplus.client.fragments.SimpleScannerFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.trinea.android.common.util.ToastUtils;

/**
 * 测试扫描页面
 */
public class ScannerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        replaceContainer(R.id.frame_layout, SimpleScannerFragment.newInstance());

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScannerEvent(ScannerEvent event) {
        if (event.ok()) {
            if (BuildConfig.DEBUG) {
                final String content = event.getContent();
                ToastUtils.show(this, content);
            }
            SimpleScannerFragment simpleScannerFragment =
                    (SimpleScannerFragment) findFragmentById(R.id.frame_layout);
            simpleScannerFragment.resumeScanner();
        }
    }
}
