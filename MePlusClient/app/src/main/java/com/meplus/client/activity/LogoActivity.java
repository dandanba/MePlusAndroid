package com.meplus.client.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.meplus.client.R;
import com.meplus.client.api.model.User;
import com.meplus.client.utils.IntentUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 启动Logo页面
 */
public class LogoActivity extends BaseActivity implements Handler.Callback {
    private static final String TAG = LogoActivity.class.getSimpleName();
    @Bind(R.id.image)
    ImageView mImage;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        ButterKnife.bind(this);
        mHandler = new Handler(this);
        mHandler.sendEmptyMessageDelayed(1, 3000);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == 1) {
            final User currentUser = User.getCurrentUser(User.class);
            if (currentUser == null) {
                startActivity(IntentUtils.generateIntent(this, LoginActivity.class));
            } else {
                startActivity(IntentUtils.generateIntent(this, MainActivity.class));
            }
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }

}
