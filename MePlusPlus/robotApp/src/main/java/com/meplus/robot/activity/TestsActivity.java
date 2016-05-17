package com.meplus.robot.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.marvinlabs.intents.MediaIntents;
import com.marvinlabs.intents.PhoneIntents;
import com.meplus.activity.BaseActivity;
import com.meplus.avos.objects.AVOSRobot;
import com.meplus.events.EventUtils;
import com.meplus.presenters.AgoraPresenter;
import com.meplus.punub.Command;
import com.meplus.punub.PubnubPresenter;
import com.meplus.robot.Constants;
import com.meplus.robot.R;
import com.meplus.robot.app.MPApplication;
import com.meplus.robot.events.BluetoothEvent;
import com.meplus.robot.presenters.BluetoothPresenter;
import com.meplus.robot.utils.SnackBarUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import cn.trinea.android.common.util.ToastUtils;
import hugo.weaving.DebugLog;
import io.agora.sample.agora.AgoraApplication;

/**
 * 设置
 */
public class TestsActivity extends BaseActivity {
    private static final int MIN_DISTANCE = 10;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.bluetooth_state)
    TextView mBluetoothState;
    @Bind(R.id.echo_test_button)
    TextView mTestEchoButton;
    @Bind(R.id.bms_state)
    ImageButton mBMSState;

    @Bind(R.id.b1)
    ImageView b1;
    @Bind(R.id.b2)
    ImageView b2;
    @Bind(R.id.b3)
    ImageView b3;
    @Bind(R.id.b4)
    ImageView b4;
    @Bind(R.id.b5)
    ImageView b5;

    private BluetoothPresenter mBTPresenter;
    private PubnubPresenter mPubnubPresenter = new PubnubPresenter();
    private AgoraPresenter mAgoraPresenter = new AgoraPresenter();
    private String mChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context context = getApplicationContext();
        // bluetooth
        mBTPresenter = new BluetoothPresenter(context);
        if (!mBTPresenter.isBluetoothAvailable()) { // 蓝牙模块硬件不支持
            ToastUtils.show(context, getString(R.string.bt_unsupport));
            finish();
            return;
        }

        setContentView(R.layout.activity_tests);
        ButterKnife.bind(this);
        mBTPresenter.create(context);

        // 初始化
        final AVOSRobot robot = MPApplication.getsInstance().getRobot();
        final String username = String.valueOf(robot.getRobotId()); // agora 中的用户名
        final String uuId = robot.getUUId();                        // pubnub 中的用户名
        mChannel = robot.getUUId();                             // pubnub 中的channel

        EventUtils.register(this);

        mAgoraPresenter.initAgora((AgoraApplication) getApplication(), username);

        mPubnubPresenter.initPubnub(uuId);
        mPubnubPresenter.subscribe(this, mChannel);

        mToolbar.setNavigationIcon(R.drawable.back);
        mToolbar.setTitle("系统自检");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        updateEchoTestButton(false);
        updateBluetoothState(false);
        updateSOC(0);
        updateDis(MIN_DISTANCE, MIN_DISTANCE, MIN_DISTANCE, MIN_DISTANCE, MIN_DISTANCE);
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
        EventUtils.unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mBTPresenter.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @DebugLog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBluetoothEvent(BluetoothEvent event) {
        if (event.ok()) {
            // 连接
            final boolean isConnected = event.isConnected();
            updateBluetoothState(isConnected);
            if (isConnected) {// 如果保持通讯状态
                // 电量
                final int soc = event.getSOC();
                if (soc > 0) {// 发送电量的数据
                    updateSOC(soc);
                } else { // 只发送连接的数据
                    mBTPresenter.sendDefault();// 自主避障功能使能（默认关闭）
                }

                // 超声波
                final int dis1 = event.getDis1();
                final int dis2 = event.getDis2();
                final int dis3 = event.getDis3();
                final int dis4 = event.getDis4();
                final int dis5 = event.getDis5();
                updateDis(dis1, dis2, dis3, dis4, dis5);
            }
        }
    }

    @OnClick({R.id.bluetooth_state, R.id.channel_test_button, R.id.bms_state,
            R.id.net_test_button, R.id.echo_test_button, R.id.fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bms_state:
                if (!mBTPresenter.sendGoHome()) {
                    ToastUtils.show(this, getString(R.string.bt_unconnected));
                }
                break;
            case R.id.channel_test_button:
                AVOSRobot robot = MPApplication.getsInstance().getRobot();
                startActivity(com.meplus.activity.IntentUtils.generateVideoIntent(this, mChannel, robot.getRobotId()));
                break;
            case R.id.echo_test_button:
                toggleEchoTest();
                break;
            case R.id.net_test_button:
                startActivity(MediaIntents.newOpenWebBrowserIntent(Constants.HOME_URL));
                break;
            case R.id.bluetooth_state:
                mBTPresenter.connectDeviceList(this);
                break;
            case R.id.fab:
                SnackBarUtils.make(view, getString(R.string.feedback))
                        .setAction(getString(R.string.me_ok), v -> startActivity(PhoneIntents.newCallNumberIntent(Constants.SERVICE_PHONENUMBER)))
                        .show();
                break;
        }
    }


    @OnTouch({R.id.left_button, R.id.up_button, R.id.right_button, R.id.down_button})
    public boolean onTouch(View view, MotionEvent event) {
        final int action = event.getAction();
        final int id = view.getId();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                return sendDirection(id);
            case MotionEvent.ACTION_UP:
                return sendDirection(MotionEvent.ACTION_UP);
        }
        return false;
    }

    private void updateSOC(int soc) {
        int index = soc / 10 + 1;
        index = index > 10 ? 10 : index;
        String resName = String.format("battery%1$d", index * 10);
        mBMSState.setImageResource(getResources().getIdentifier(resName, "drawable", getPackageName()));
    }


    private boolean sendDirection(int id) {
        String message = "";
        switch (id) {
            case MotionEvent.ACTION_UP:
                message = Command.ACTION_STOP;
                break;
            case R.id.left_button:
                message = Command.ACTION_LEFT;
                break;
            case R.id.up_button:
                message = Command.ACTION_UP;
                break;
            case R.id.right_button:
                message = Command.ACTION_RIGHT;
                break;
            case R.id.down_button:
                message = Command.ACTION_DOWN;
                break;
        }
        return sendDirection(message);
    }

    private boolean sendDirection(String action) {
        if (!TextUtils.isEmpty(action)) {
            if (!mBTPresenter.sendDirection(action)) {
                ToastUtils.show(this, getString(R.string.bt_unconnected));
            }
            return true;
        }
        return false;
    }

    private void toggleEchoTest() {
        boolean isStarted = (boolean) mTestEchoButton.getTag();
        if (isStarted) {
            mAgoraPresenter.stopEchoTest((AgoraApplication) getApplication());
        } else {
            mAgoraPresenter.startEchoTest((AgoraApplication) getApplication());
        }
        updateEchoTestButton(!isStarted);
    }

    private void updateEchoTestButton(boolean isStarted) {
        mTestEchoButton.setText(isStarted ? getString(R.string.stop_echo_test) : getString(R.string.start_echo_test));
        mTestEchoButton.setTag(isStarted);
    }

    private void updateBluetoothState(boolean state) {
        mBluetoothState.setText(state ? getString(R.string.bt_connect) : getString(R.string.bt_unconnect));
    }

    private void updateDis(int dis1, int dis2, int dis3, int dis4, int dis5) {
        b1.setImageResource(dis1 < MIN_DISTANCE ? R.drawable.b1_selected : R.drawable.b1_normal);
        b2.setImageResource(dis2 < MIN_DISTANCE ? R.drawable.b2_selected : R.drawable.b2_normal);
        b3.setImageResource(dis3 < MIN_DISTANCE ? R.drawable.b3_selected : R.drawable.b3_normal);
        b4.setImageResource(dis4 < MIN_DISTANCE ? R.drawable.b4_selected : R.drawable.b4_normal);
        b5.setImageResource(dis5 < MIN_DISTANCE ? R.drawable.b5_selected : R.drawable.b5_normal);
    }
}
