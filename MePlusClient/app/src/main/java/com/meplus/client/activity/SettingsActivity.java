package com.meplus.client.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.meplus.client.R;
import com.meplus.client.api.model.User;
import com.meplus.client.events.Event;
import com.meplus.client.events.LogoutEvent;
import com.meplus.client.utils.FIRUtils;
import com.meplus.client.utils.IntentUtils;
import com.meplus.client.utils.SnackBarUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import im.fir.sdk.VersionCheckCallback;

/**
 * 设置
 */
public class SettingsActivity extends BaseActivity {

    @Bind(R.id.root)
    LinearLayout mRoot;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    private final VersionCheckCallback mCallback = new VersionCheckCallback() {

        @Override
        public void onSuccess(String json) {
            FIRUtils.onSuccess(SettingsActivity.this, json);
        }

        @Override
        public void onFail(Exception e) {
            SnackBarUtils.show(mRoot, e.toString());
        }

        @Override
        public void onStart() {
        }

        @Override
        public void onFinish() {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        mToolbar.setNavigationIcon(R.drawable.back);
        mToolbar.setTitle("设置");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.bind_layout, R.id.password_layout, R.id.feeback_layout, R.id.update_layout, R.id.logout_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bind_layout:
                startActivity(IntentUtils.generateIntent(this, BindRobotActivity.class));
                break;
            case R.id.password_layout:
                startActivity(IntentUtils.generateIntent(this, EditPasswordActivity.class));
                break;
            case R.id.feeback_layout:
                break;
            case R.id.update_layout:
                FIRUtils.checkForUpdateInFIR(this, mCallback);
                break;
            case R.id.logout_button:
                Snackbar.make(view, "是否退出当前账户吗？", Snackbar.LENGTH_LONG).setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        User.logOut();
                        EventBus.getDefault().post(new LogoutEvent(Event.STATUS_OK));
                        finish();
                    }
                }).show();

                break;
        }
    }


}
