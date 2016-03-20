package com.meplus.robot.activity;

import android.os.Bundle;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.meplus.client.R;
import com.meplus.robot.api.model.Robot;
import com.meplus.robot.app.MPApplication;
import com.meplus.robot.events.CreateEvent;
import com.meplus.robot.events.ErrorEvent;
import com.meplus.robot.events.Event;
import com.meplus.robot.events.QueryEvent;
import com.meplus.robot.utils.IntentUtils;
import com.meplus.robot.utils.UUIDUtils;

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
public class LogoActivity extends BaseActivity {
    private static final String TAG = LogoActivity.class.getSimpleName();

    @butterknife.Bind(R.id.shimmer_view_container)
    ShimmerFrameLayout mShimmerViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        ButterKnife.bind(this);

        mShimmerViewContainer.startShimmerAnimation();

        EventBus.getDefault().register(this);
        Robot.query(UUIDUtils.getUUID(this));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }

    @DebugLog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQueryEvent(QueryEvent<Robot> event) {
        if (event.ok()) {
            final List<Robot> robotList = event.getList();
            if (ListUtils.isEmpty(robotList)) {
                Robot.save(UUIDUtils.getUUID(this));
            } else {
                onCreateEvent(new CreateEvent<>(Event.STATUS_OK, robotList.get(0)));
            }
        }
    }

    @DebugLog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCreateEvent(CreateEvent<Robot> event) {
        if (event.ok()) {
            MPApplication.getsInstance().setRobot(event.getData());
            startActivity(IntentUtils.generateIntent(this, MainActivity.class));
            finish();
        }
    }

    @DebugLog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorEvent(ErrorEvent event) {
        if (event.ok()) {
            ToastUtils.show(this, event.getThrowable().getMessage());
        }
    }

}
