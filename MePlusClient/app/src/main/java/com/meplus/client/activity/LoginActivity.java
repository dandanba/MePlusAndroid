package com.meplus.client.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.meplus.client.R;
import com.meplus.client.api.model.Robot;
import com.meplus.client.api.model.User;
import com.meplus.client.app.MPApplication;
import com.meplus.client.events.ErrorEvent;
import com.meplus.client.events.QueryEvent;
import com.meplus.client.events.SignUpEvent;
import com.meplus.client.utils.IntentUtils;
import com.meplus.client.utils.SnackBarUtils;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ListUtils;
import cn.trinea.android.common.util.ToastUtils;
import hugo.weaving.DebugLog;

/**
 * 登录 username: meplus password:meplus
 */
public class LoginActivity extends BaseActivity implements Validator.ValidationListener {
    @Bind(R.id.root)
    ViewGroup mRoot;
    @NotEmpty
    @Bind(R.id.phone_edit)
    EditText mPhoneEdit;
    @Password(min = 6)
    @Bind(R.id.password_edit)
    EditText mPasswordEdit;
    private Validator mValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @DebugLog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQueryEvent(QueryEvent<Robot> event) {
        if (event.ok()) {
            final List<Robot> robotList = event.getList();
            if (!ListUtils.isEmpty(robotList)) {
                MPApplication.getsInstance().setRobot(robotList.get(0));
            }
            startActivity(IntentUtils.generateIntent(this, MainActivity.class));
            finish();
        }
    }


    @DebugLog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorEvent(ErrorEvent event) {
        if (event.ok()) {
            finish();
            ToastUtils.show(this, event.getThrowable().getMessage());
        }
    }


    @OnClick({R.id.password_button, R.id.login_button, R.id.register_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.password_button:
                startActivity(IntentUtils.generateIntent(LoginActivity.this, PasswordActivity.class));
                break;
            case R.id.login_button:
                mValidator.validate();
                break;
            case R.id.register_button:
                startActivity(IntentUtils.generateIntent(LoginActivity.this, RegisterActivity.class));
                break;
        }
    }

    @Override
    public void onValidationSucceeded() {
        doLogin();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignUpEvent(SignUpEvent event) {
        if (event.ok()) {
            finish();
        }
    }

    private void doLogin() {
        final String username = mPhoneEdit.getText().toString();
        final String password = mPasswordEdit.getText().toString();

        User.logInInBackground(username, password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                if (e == null) {
                    final User user = User.getCurrentUser(User.class);
                    user.queryRobot();
                } else {
                    SnackBarUtils.show(mRoot, e.toString());
                }
            }
        });
    }

}
