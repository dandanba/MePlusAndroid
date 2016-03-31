package com.meplus.robot.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.meplus.robot.R;
import com.meplus.command.Command;
import com.meplus.robot.api.model.Robot;
import com.meplus.robot.app.MPApplication;
import com.meplus.robot.events.SaveEvent;
import com.meplus.robot.utils.IntentUtils;
import com.meplus.robot.viewholder.NavHeaderViewHolder;
import com.meplus.robot.viewholder.QRViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import io.agora.sample.agora.RecordActivity;

/**
 * 主页面
 */
public class MainActivity extends PNActivity implements NavigationView.OnNavigationItemSelectedListener {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @Bind(R.id.nav_view)
    NavigationView mNavigationView;
    private NavHeaderViewHolder mHeaderHolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 初始化
        final Robot robot = MPApplication.getsInstance().getRobot();
        mUUID = robot.getObjectId();
        mChannel = robot.getObjectId();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("首页");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        final View headerView = mNavigationView.getHeaderView(0);
        mHeaderHolder = new NavHeaderViewHolder(headerView);
        mHeaderHolder.updateView(robot);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                break;
            case R.id.nav_history:
                startActivity(IntentUtils.generateIntent(this, RecordActivity.class));
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
    public void onSaveEvent(SaveEvent<Robot> event) {
        if (event.ok()) {
            final Robot robot = event.getData();
            MPApplication.getsInstance().setRobot(robot);
            mHeaderHolder.updateView(robot);
        }
    }

    @OnClick(R.id.fab)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab: // 展示二维码的图片
                showQRDialog();
                break;
        }
    }

    @Override
    public void onMessage(String message) {
        super.onMessage(message);
        if (Command.ACTION_CALL.equals(message)) {
            if (!MPApplication.getsInstance().getIsInChannel()) { // 如果正在通电话那么就不能在进入了
                Robot robot = MPApplication.getsInstance().getRobot();
                startActivity(com.meplus.activity.IntentUtils.generateCallIntent(this, mChannel, robot.getRobotId()));
            }
        }
    }

    private void showQRDialog() {
        boolean wrapInScrollView = true;
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.me_bind_tip)
                .customView(R.layout.dialog_qr, wrapInScrollView)
                .positiveText(R.string.me_ok)
                .show();
        final View view = dialog.getView();
        final QRViewHolder viewHolder = new QRViewHolder(view);
        final String code = MPApplication.getsInstance().getRobot().getUUId();
        viewHolder.update(code);
    }


}
