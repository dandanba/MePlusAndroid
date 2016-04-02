package com.meplus.client.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.meplus.client.R;
import com.meplus.client.api.model.Robot;
import com.meplus.client.api.model.User;
import com.meplus.client.app.MPApplication;
import com.meplus.client.events.CommandEvent;
import com.meplus.client.events.LogoutEvent;
import com.meplus.client.events.SaveEvent;
import com.meplus.client.presenters.PubnubPresenter;
import com.meplus.client.utils.IntentUtils;
import com.meplus.client.viewholder.NavHeaderViewHolder;
import com.meplus.presenters.AgoraPresenter;
import com.meplus.punub.Command;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import io.agora.sample.agora.AgoraApplication;

/**
 * 主页面
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @Bind(R.id.nav_view)
    NavigationView mNavigationView;
    private NavHeaderViewHolder mHeaderHolder;

    private PubnubPresenter mPubnubPresenter = new PubnubPresenter();
    private AgoraPresenter mAgoraPresenter = new AgoraPresenter();
    private String mChannel;
    private int mUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);

        // 初始化
        final User user = User.getCurrentUser(User.class);
        final Robot robot = MPApplication.getsInstance().getRobot();
        mUserId =  user.getUserId();                                  // agora 中的用户名
        final String uuId = user.getUUId();                             // pubnub 中的用户名
        mChannel = robot == null ? "" : robot.getObjectId();            // pubnub 中的channel

        mAgoraPresenter.initAgora((AgoraApplication) getApplication(), String.valueOf(mUserId));

        mPubnubPresenter.initPubnub(uuId);
        if (!TextUtils.isEmpty(mChannel)) {
            mPubnubPresenter.subscribe(getApplicationContext(), mChannel);
        }
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("首页");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        final View headerView = mNavigationView.getHeaderView(0);
        mHeaderHolder = new NavHeaderViewHolder(headerView);
        mHeaderHolder.updateHeader();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mPubnubPresenter.destroy();
        EventBus.getDefault().unregister(this);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                break;
            case R.id.nav_history:
                startActivity(com.meplus.activity.IntentUtils.generateRecordIntent(this));
                break;
            case R.id.nav_settings:
                startActivity(IntentUtils.generateIntent(this, SettingsActivity.class));
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @DebugLog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogoutEvent(LogoutEvent event) {
        if (event.ok()) {
            startActivity(IntentUtils.generateIntent(this, LoginActivity.class));
            finish();
        }
    }

    @DebugLog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSaveEvent(SaveEvent<Robot> event) {
        if (event.ok()) {
            final Robot robot = event.getData();
            MPApplication.getsInstance().setRobot(robot);
            mChannel = robot.getObjectId();                 // 订阅机器人
            mPubnubPresenter.subscribe(getApplicationContext(), mChannel);

            mHeaderHolder.updateHeader();
        }
    }

    @DebugLog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommandEvent(CommandEvent event) {
        if (event.ok()) {
            final Command command = event.getCommand();
            final String message = command.getMessage();

            switch (message) {
                case Command.ACTION_LEFT:
                case Command.ACTION_UP:
                case Command.ACTION_RIGHT:
                case Command.ACTION_DOWN:
                case Command.ACTION_STOP:
                    mPubnubPresenter.publish(getApplicationContext(), command.getMessage());
                    break;
                case Command.ACTION_CALL:
                    break;
                default:
                    break;
            }

        }
    }

    @OnClick(R.id.fab)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                final Robot robot = MPApplication.getsInstance().getRobot();
                Snackbar.make(view, robot == null ? "绑定多我机器人吗？" : "唤醒多我机器人吗？", Snackbar.LENGTH_LONG).setAction("确定", v -> {
                    if (robot == null) {
                        startActivity(IntentUtils.generateIntent(this, BindRobotActivity.class));
                    } else {
                        startActivity(IntentUtils.generateCallIntent(this, mChannel, mUserId));
                        mPubnubPresenter.publish(getApplicationContext(), Command.ACTION_CALL);
                    }
                }).show();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
