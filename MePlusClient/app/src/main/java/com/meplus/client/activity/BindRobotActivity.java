package com.meplus.client.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.meplus.client.R;
import com.meplus.client.api.model.User;
import com.meplus.client.events.BindEvent;
import com.meplus.client.events.Event;
import com.meplus.client.events.ScannerEvent;
import com.meplus.client.utils.IntentUtils;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 绑定机器人
 */
public class BindRobotActivity extends BaseActivity implements Validator.ValidationListener {

    @Bind(R.id.root)
    LinearLayout mRoot;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @NotEmpty
    @Bind(R.id.bind_edit)
    EditText mBindEdit;

    private Validator mValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_robot);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        mToolbar.setNavigationIcon(R.drawable.back);
        mToolbar.setTitle("绑定机器人");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.bind_button, R.id.scan_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bind_button:
                mValidator.validate();
                break;
            case R.id.scan_button:
                startActivity(IntentUtils.generateIntent(this, ScannerActivity.class));
                break;
        }
    }

    @Override
    public void onValidationSucceeded() {
        final String robotId = mBindEdit.getText().toString();
        doBindRobot(robotId);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScannerEvent(ScannerEvent event) {
        if (event.ok()) {
            final String robotId = event.getContent();
            doBindRobot(robotId);
        }
    }

    private void doBindRobot(String robotId) {
        User user = User.getCurrentUser(User.class);
        user.setRobotId(robotId);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    EventBus.getDefault().post(new BindEvent(Event.STATUS_OK));
                    finish();
                } else {
                    Snackbar.make(mRoot, e.toString(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

}
