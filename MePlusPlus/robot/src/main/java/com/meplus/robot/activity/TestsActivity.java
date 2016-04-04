package com.meplus.robot.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.marvinlabs.intents.MediaIntents;
import com.marvinlabs.intents.PhoneIntents;
import com.meplus.activity.BaseActivity;
import com.meplus.avos.objects.AVOSRobot;
import com.meplus.events.EventUtils;
import com.meplus.presenters.AgoraPresenter;
import com.meplus.punub.Command;
import com.meplus.robot.Constants;
import com.meplus.robot.R;
import com.meplus.robot.app.MPApplication;
import com.meplus.robot.events.BluetoothEvent;
import com.meplus.robot.presenters.BluetoothPresenter;
import com.meplus.robot.presenters.PubnubPresenter;
import com.meplus.robot.utils.SnackBarUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import hugo.weaving.DebugLog;
import io.agora.sample.agora.AgoraApplication;

/**
 * 设置
 */
public class TestsActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.bluetooth_state)
    TextView mBluetoothState;
    @Bind(R.id.echo_test_button)
    TextView mTestEchoButton;

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

        mAgoraPresenter.initAgora((AgoraApplication) getApplication(), username);

        mPubnubPresenter.initPubnub(uuId);
        mPubnubPresenter.subscribe(this, mChannel);
        EventUtils.register(this);

        mToolbar.setNavigationIcon(R.drawable.back);
        mToolbar.setTitle("系统自检");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        updateEchoTestButton(false);
        updateBluetoothState(false);
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
            updateBluetoothState(event.isConnected());
        }
    }

    @OnClick({R.id.up_button, R.id.down_button, R.id.right_button,
            R.id.left_button, R.id.bluetooth_state, R.id.channel_test_button, R.id.home_test_button,
            R.id.net_test_button, R.id.echo_test_button, R.id.fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.up_button:
                sendDirection(Command.ACTION_UP);
                break;
            case R.id.down_button:
                sendDirection(Command.ACTION_DOWN);
                break;
            case R.id.right_button:
                sendDirection(Command.ACTION_RIGHT);
                break;
            case R.id.left_button:
                sendDirection(Command.ACTION_LEFT);
                break;

            case R.id.home_test_button:
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


    private void sendDirection(String action) {
        if (!mBTPresenter.sendDirection(action)) {
            ToastUtils.show(this, getString(R.string.bt_unconnected));
        }
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
}
