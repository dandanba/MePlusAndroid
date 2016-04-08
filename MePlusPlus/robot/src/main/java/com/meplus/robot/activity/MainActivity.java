package com.meplus.robot.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.meplus.activity.BaseActivity;
import com.meplus.avos.objects.AVOSRobot;
import com.meplus.events.EventUtils;
import com.meplus.events.SaveEvent;
import com.meplus.presenters.AgoraPresenter;
import com.meplus.punub.Command;
import com.meplus.punub.CommandEvent;
import com.meplus.punub.PubnubPresenter;
import com.meplus.robot.R;
import com.meplus.robot.app.MPApplication;
import com.meplus.robot.events.BluetoothEvent;
import com.meplus.robot.presenters.BluetoothPresenter;
import com.meplus.robot.viewholder.NavHeaderViewHolder;
import com.meplus.robot.viewholder.QRViewHolder;
import com.meplus.speech.TtsPresenter;
import com.meplus.speech.Understand;
import com.meplus.speech.UnderstandEvent;
import com.meplus.speech.UnderstandPersenter;
import com.meplus.utils.IntentUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
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
    @Bind(R.id.bluetooth_state)
    TextView mBluetoothState;
    @Bind(R.id.face_image)
    ImageView mFaceImage;
    @Bind(R.id.voice_text)
    TextView mVoiceText;

    private NavHeaderViewHolder mHeaderHolder;

    private BluetoothPresenter mBTPresenter;
    private PubnubPresenter mPubnubPresenter = new PubnubPresenter();
    private AgoraPresenter mAgoraPresenter = new AgoraPresenter();
    private UnderstandPersenter mUnderstandPersenter = new UnderstandPersenter();
    private TtsPresenter mTtsPresenter = new TtsPresenter();

    private String mChannel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context context = getApplicationContext();
        // bluetooth
        mBTPresenter = new BluetoothPresenter(context);
        if (!mBTPresenter.isBluetoothAvailable()) { // 蓝牙模块硬件支持
            ToastUtils.show(context, getString(R.string.bt_unsupport));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        EventUtils.register(this);

        mBTPresenter.create(context);

        // 初始化
        final AVOSRobot robot = MPApplication.getsInstance().getRobot();
        final String username = String.valueOf(robot.getRobotId()); // agora 中的用户名
        final String uuId = robot.getUUId();                        // pubnub 中的用户名
        mChannel = robot.getUUId();                                 // pubnub 中的channel

        mAgoraPresenter.initAgora((AgoraApplication) getApplication(), username);

        mPubnubPresenter.initPubnub(uuId);
        mPubnubPresenter.subscribe(getApplicationContext(), mChannel);


        mUnderstandPersenter.create(this);
        mTtsPresenter.create(this);


        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("首页");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        final View headerView = mNavigationView.getHeaderView(0);
        mHeaderHolder = new NavHeaderViewHolder(headerView);
        mHeaderHolder.updateView(robot);

        updateBluetoothState(false);
    }


    @Override
    public void onResume() {
        super.onResume();
        mUnderstandPersenter.startUnderstanding(); // 程序启动
    }

    @Override
    public void onPause() {
        super.onPause();
        mUnderstandPersenter.stopUnderstanding();
        mTtsPresenter.stopSpeaking();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBTPresenter.isBluetoothEnabled()) {// 蓝牙软件部分不支持
            mBTPresenter.enableBluetooth(this);
        } else {
            if (!mBTPresenter.isServiceAvailable()) { // 蓝牙服务没启动
                mBTPresenter.startBluetoothService();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBTPresenter.disconnect();
        mBTPresenter.stopBluetoothService();
        mPubnubPresenter.destroy();
        mUnderstandPersenter.destroy();
        mTtsPresenter.destroy();
        EventUtils.unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mBTPresenter.onActivityResult(this, requestCode, resultCode, data);
    }


    @DebugLog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnderstandEvent(UnderstandEvent event) {
        if (event.ok()) {
            final Understand understand = event.getUnderstand();
            final String action = understand.getAction();
            if (action.equals(Understand.ACTION_ERROR)) { // 包含tts和understand的错误!
                ToastUtils.show(this, "error");
            } else if (action.equals(Understand.ACTION_SPEECH_UNDERSTAND)) {// 理解后的内容
                final String message = understand.getMessage();
                mVoiceText.setText(message);
                mTtsPresenter.startSpeaking(message);
            } else if (action.equals(Understand.ACTION_UNDERSTAND)) { // 开始理解
                mFaceImage.setImageResource(R.drawable.think_anim);
                AnimationDrawable animationDrawable = (AnimationDrawable) mFaceImage.getDrawable();
                animationDrawable.start();
            } else if (action.equals(Understand.ACTION_LISTEN)) { // 开始听
                mFaceImage.setImageResource(R.drawable.listener_anim);
                AnimationDrawable animationDrawable = (AnimationDrawable) mFaceImage.getDrawable();
                if (!animationDrawable.isRunning()) {
                    animationDrawable.start();
                }
            } else if (action.equals(Understand.ACTION_SPEECH)) { // 说完了
                mVoiceText.setText("");
                mFaceImage.setImageResource(R.drawable.hello_world);

                mUnderstandPersenter.startUnderstanding();
            }
        }
    }

    @DebugLog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSaveEvent(SaveEvent<AVOSRobot> event) {
        if (event.ok()) {
            final AVOSRobot robot = event.getData();
            MPApplication.getsInstance().setRobot(robot);
            mHeaderHolder.updateView(robot);
        }
    }

    @DebugLog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBluetoothEvent(BluetoothEvent event) {
        if (event.ok()) {
            if (event.isConnected()) {
                // 自主避障功能使能（默认关闭）
                mBTPresenter.sendDefault();
            }
            updateBluetoothState(event.isConnected());
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
                    if (!mBTPresenter.sendDirection(message)) {
                        ToastUtils.show(this, getString(R.string.bt_unconnected));
                    }
                    break;
                case Command.ACTION_CALL:
                    if (!MPApplication.getsInstance().getIsInChannel()) { // 如果正在通电话那么就不能在进入了
                        AVOSRobot robot = MPApplication.getsInstance().getRobot();
                        startActivity(com.meplus.activity.IntentUtils.generateVideoIntent(this, mChannel, robot.getRobotId()));
                    }
                    break;
                case Command.ACTION_HOME:
                    if (!mBTPresenter.sendGoHome()) {
                        ToastUtils.show(this, getString(R.string.bt_unconnected));
                    }
                    break;
                default:
                    break;
            }

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

    @OnClick({R.id.fab, R.id.bluetooth_state, R.id.home_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab: // 展示二维码的图片
                showQRDialog();
                break;
            case R.id.bluetooth_state:
                mBTPresenter.connectDeviceList(this);
                break;
            case R.id.home_button:
                if (!mBTPresenter.sendGoHome()) {
                    ToastUtils.show(this, getString(R.string.bt_unconnected));
                }
                break;
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

    private void updateBluetoothState(boolean state) {
        mBluetoothState.setText(state ? getString(R.string.bt_connect) : getString(R.string.bt_unconnect));
    }

}
