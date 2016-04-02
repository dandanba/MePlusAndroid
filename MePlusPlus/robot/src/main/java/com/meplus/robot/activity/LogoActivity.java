package com.meplus.robot.activity;

import android.os.Bundle;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.meplus.robot.R;
import com.meplus.robot.api.model.Robot;
import com.meplus.robot.app.MPApplication;
import com.meplus.robot.events.ErrorEvent;
import com.meplus.robot.events.Event;
import com.meplus.robot.events.QueryEvent;
import com.meplus.robot.events.SaveEvent;
import com.meplus.robot.utils.IntentUtils;
import com.meplus.robot.utils.UUIDUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Random;

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
        Robot.queryByUUId(UUIDUtils.getUUID(this));
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
            if (ListUtils.isEmpty(robotList)) {
                final Robot robot = new Robot();
                final int robotId = new Random().nextInt(Math.abs((int) System.currentTimeMillis()));
                robot.setRobotId(robotId);
                robot.setUUId(UUIDUtils.getUUID(this));
                robot.saveRotot();
            } else {
                onCreateEvent(new SaveEvent<>(Event.STATUS_OK, robotList.get(0)));
            }
        }
    }

    @DebugLog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCreateEvent(SaveEvent<Robot> event) {
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

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }

}
