package com.meplus.robot.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.meplus.client.BuildConfig;
import com.meplus.client.R;
import com.meplus.robot.utils.SnackBarUtils;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 修改机器人信息
 */
public class ModifyActivity extends BaseActivity implements Validator.ValidationListener {
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
        setContentView(R.layout.activity_modify);
        ButterKnife.bind(this);

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        if (BuildConfig.DEBUG) {
            mPhoneEdit.setText("meplus");
            mPasswordEdit.setText("meplus");
            mConfirmEdit.setText("meplus");
            mEmailEdit.setText("13641194007@139.com");
        }

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

    private void doModify() {
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

}
