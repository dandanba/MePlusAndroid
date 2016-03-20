package com.meplus.robot.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.meplus.client.BuildConfig;
import com.meplus.client.R;
import com.meplus.robot.api.model.Robot;
import com.meplus.robot.app.MPApplication;
import com.meplus.robot.events.ModifyEvent;
import com.meplus.robot.utils.SnackBarUtils;
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
import hugo.weaving.DebugLog;

/**
 * 修改机器人信息
 */
public class ModifyActivity extends BaseActivity implements Validator.ValidationListener {
    @Bind(R.id.root)
    ViewGroup mRoot;

    @NotEmpty
    @Bind(R.id.name_edit)
    EditText mNameEdit;

    private Validator mValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        if (BuildConfig.DEBUG) {
            mNameEdit.setText("meplus");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.register_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_button:
                mValidator.validate();
                break;
        }
    }

    @Override
    public void onValidationSucceeded() {
        doModify();
    }


    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                SnackBarUtils.show(mRoot, message);
            }
        }
    }


    @DebugLog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onModifyEvent(ModifyEvent<Robot> event) {
        if (event.ok()) {
            finish();
        }
    }

    private void doModify() {
        final Robot robot = MPApplication.getsInstance().getRobot();
        robot.setRobotName(mNameEdit.getText().toString());
        Robot.modify(robot);
    }


}
