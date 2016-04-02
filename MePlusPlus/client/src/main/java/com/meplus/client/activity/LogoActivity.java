package com.meplus.client.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.meplus.client.R;
import com.meplus.client.api.model.Robot;
import com.meplus.client.api.model.User;
import com.meplus.client.app.MPApplication;
import com.meplus.client.events.ErrorEvent;
import com.meplus.client.events.QueryEvent;
import com.meplus.client.utils.IntentUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.ButterKnife;
import cn.trinea.android.common.util.ListUtils;
import cn.trinea.android.common.util.ToastUtils;
import hugo.weaving.DebugLog;

/**
 * 启动Logo页面
 */
public class LogoActivity extends BaseActivity implements Handler.Callback {
    private static final String TAG = LogoActivity.class.getSimpleName();

    @butterknife.Bind(R.id.shimmer_view_container)
    ShimmerFrameLayout mShimmerViewContainer;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mShimmerViewContainer.startShimmerAnimation();
        mHandler = new Handler(this);
        mHandler.sendEmptyMessageDelayed(1, 3000);
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
            startActivity(IntentUtils.generateIntent(LogoActivity.this, MainActivity.class));
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


    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == 1) {
            final User user = User.getCurrentUser(User.class);
            if (user == null) {
                startActivity(IntentUtils.generateIntent(LogoActivity.this, LoginActivity.class));
                finish();
            } else {
                user.queryRobot();
            }
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

}
