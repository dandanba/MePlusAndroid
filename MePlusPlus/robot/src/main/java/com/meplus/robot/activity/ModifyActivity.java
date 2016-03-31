package com.meplus.robot.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.meplus.robot.R;
import com.meplus.robot.api.model.Robot;
import com.meplus.robot.app.MPApplication;
import com.meplus.robot.events.SaveEvent;
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

    @Bind(R.id.description_edit)
    EditText mDescriptionEdit;

    private Validator mValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
        final Robot robot = MPApplication.getsInstance().getRobot();
        final String robotName = robot.getRobotName();
        final String robotDescription = robot.getRobotDescription();
        mNameEdit.setText(TextUtils.isEmpty(robotName) ? "" : robotName);
        mDescriptionEdit.setText(TextUtils.isEmpty(robotDescription) ? "" : robotDescription);
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
    public void onModifyEvent(SaveEvent<Robot> event) {
        if (event.ok()) {
            finish();
        }
    }

    private void doModify() {
        final Robot robot = MPApplication.getsInstance().getRobot();
        robot.setRobotName(mNameEdit.getText().toString());
        robot.setRobotDescription(mDescriptionEdit.getText().toString());
        robot.saveRotot();
    }

}
