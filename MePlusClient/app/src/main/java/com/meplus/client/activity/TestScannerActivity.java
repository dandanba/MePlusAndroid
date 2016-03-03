package com.meplus.client.activity;

import android.os.Bundle;

import com.meplus.client.Constants;
import com.meplus.client.R;
import com.meplus.client.events.ScannerEvent;
import com.meplus.client.fragments.SimpleScannerFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.trinea.android.common.util.ToastUtils;

/**
 * 测试扫描页面
 */
public class TestScannerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testscanner);
        replaceContainer(R.id.frame_layout, SimpleScannerFragment.newInstance());

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScannerEvent(ScannerEvent event) {
        if (!Constants.sRelease) {
            final String content = event.getContent();
            ToastUtils.show(this, content);
        }
        SimpleScannerFragment simpleScannerFragment =
                (SimpleScannerFragment) findFragmentById(R.id.frame_layout);
        simpleScannerFragment.resumeScanner();
    }
}
