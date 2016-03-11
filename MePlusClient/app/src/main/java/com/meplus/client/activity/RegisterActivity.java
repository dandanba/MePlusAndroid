package com.meplus.client.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SignUpCallback;
import com.meplus.client.R;
import com.meplus.client.api.model.User;
import com.meplus.client.events.Event;
import com.meplus.client.events.SignUpEvent;
import com.meplus.client.utils.IntentUtils;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 注册
 * meplus 13641194007@139.com
 */
public class RegisterActivity extends BaseActivity implements Validator.ValidationListener {
    @Bind(R.id.root)
    ViewGroup mRoot;

    @NotEmpty
    @Bind(R.id.phone_edit)
    EditText mPhoneEdit;

    @Email
    @Bind(R.id.email_edit)
    EditText mEmailEdit;

    @Password(min = 6)
    @Bind(R.id.password_edit)
    EditText mPasswordEdit;

    @ConfirmPassword
    @Bind(R.id.confirm_edit)
    EditText mConfirmEdit;

    private Validator mValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

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
        doRegister();
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

    private void doRegister() {
        final String username = mPhoneEdit.getText().toString();
        final String password = mPasswordEdit.getText().toString();
        final String email = mEmailEdit.getText().toString();

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);

        user.signUpInBackground(new SignUpCallback() {
            public void done(AVException e) {
                if (e == null) {
                    EventBus.getDefault().post(new SignUpEvent(Event.STATUS_OK));
                    startActivity(IntentUtils.generateIntent(RegisterActivity.this, MainActivity.class));
                    finish();
                } else {
                    Snackbar.make(mRoot, e.toString(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}
