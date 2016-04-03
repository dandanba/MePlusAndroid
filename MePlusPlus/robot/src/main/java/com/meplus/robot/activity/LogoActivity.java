package com.meplus.robot.activity;

import android.os.Bundle;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.meplus.activity.BaseActivity;
import com.meplus.avos.objects.AVOSRobot;
import com.meplus.events.BaseEvent;
import com.meplus.events.ErrorEvent;
import com.meplus.events.EventUtils;
import com.meplus.events.QueryEvent;
import com.meplus.events.SaveEvent;
import com.meplus.robot.R;
import com.meplus.robot.app.MPApplication;
import com.meplus.robot.avos.Robot;
import com.meplus.robot.utils.UUIDUtils;
import com.meplus.utils.IntentUtils;

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

    private String mUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        ButterKnife.bind(this);

        mShimmerViewContainer.startShimmerAnimation();

        EventUtils.register(this);
        // 查询是否已经注册过
        mUUID = UUIDUtils.getUUID(this);
        Robot.queryByUUId(mUUID);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventUtils.unregister(this);
    }

    @DebugLog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQueryEvent(QueryEvent<AVOSRobot> event) {
        if (event.ok()) {
            final List<AVOSRobot> robotList = event.getList();
            if (ListUtils.isEmpty(robotList)) { // 没有注册过
                final AVOSRobot robot = new AVOSRobot();
                final int robotId = new Random().nextInt(Math.abs((int) System.currentTimeMillis()));
                robot.setRobotId(robotId);
                robot.setUUId(mUUID);
                Robot.saveRotot(robot);
            } else {
                onCreateEvent(new SaveEvent<>(BaseEvent.STATUS_OK, robotList.get(0)));
            }
        }
    }

    @DebugLog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCreateEvent(SaveEvent<AVOSRobot> event) {
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
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }

}
