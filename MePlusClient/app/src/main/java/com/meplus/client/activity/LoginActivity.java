package com.meplus.client.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.meplus.client.R;
import com.meplus.client.api.model.User;
import com.meplus.client.events.Event;
import com.meplus.client.events.SignUpEvent;
import com.meplus.client.utils.IntentUtils;
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
    @Bind(R.id.password_button)
    TextView mPasswordButton;
    @Bind(R.id.login_button)
    TextView mLoginButton;
    @Bind(R.id.register_button)
    TextView mRegisterButton;
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
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.password_button, R.id.login_button, R.id.register_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.password_button:
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
                Snackbar.make(mRoot, message, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignUpEvent(SignUpEvent event) {
        if (event.getStatus().equals(Event.STATUS_OK)) {
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
                    startActivity(IntentUtils.generateIntent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Snackbar.make(mRoot, e.toString(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

}
