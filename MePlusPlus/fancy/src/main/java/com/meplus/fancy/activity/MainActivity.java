package com.meplus.fancy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.meplus.fancy.Constants;
import com.meplus.fancy.R;
import com.meplus.fancy.app.FancyApplication;
import com.meplus.fancy.events.ScannerEvent;
import com.meplus.fancy.model.ApiService;
import com.meplus.fancy.model.entity.Code;
import com.meplus.fancy.utils.ArgsUtils;
import com.meplus.fancy.utils.FIRUtils;
import com.meplus.fancy.utils.IntentUtils;
import com.meplus.fancy.utils.JsonUtils;
import com.meplus.fancy.utils.SignUtils;
import com.topeet.serialtest.serial;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    private static final String LibraryId = "41676";

    private static final String Data = "{\"babyId\":\"0\",\"check\":\"3D0A6F20FFF74DF28E1D226A3B6C7E82\",\"parentsUserId\":\"0\",\"time\":\"0\"}";

    @Bind(R.id.data_edit)
    EditText mDataEdit;

    @Bind(R.id.library_edit)
    EditText mLibraryEdit;

    @Bind(R.id.user_edit)
    EditText mUserEdit;

    @Bind(R.id.isbn_edit)
    EditText mISBNEdit;

    @Bind(R.id.log_text)
    TextView mLogText;

    private serial com3 = new serial();
    private ReadThread mReadThread;
    private StringBuffer mBuffer = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        com3.Open(1, 115200);
        /* Create a receiving thread */
        mReadThread = new ReadThread();
        mReadThread.start();

        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        FIRUtils.checkForUpdateInFIR(this);

        mDataEdit.setText(Data);
        mUserEdit.setText(JsonUtils.readValue(Data, Code.class).getCheck());
        mLibraryEdit.setText(LibraryId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReadThread != null) {
            mReadThread.interrupt();
        }
        com3.Close();
        com3 = null;
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScannerEvent(ScannerEvent event) {
        final String data = event.getContent();
        mBuffer.append(data);

        if (data.endsWith("}")) { // JSON 格式结束
            final Code code = JsonUtils.readValue(mBuffer.toString(), Code.class);
            mDataEdit.setText(mBuffer.toString());
            mUserEdit.setText(code.getCheck());
            mBuffer.delete(0, mBuffer.length());
        } else { // JSON 开始
            if (!mBuffer.toString().startsWith("{") && mBuffer.length() == 13) {
                mISBNEdit.setText(mBuffer.toString());
                mBuffer.delete(0, mBuffer.length());
            }
        }
    }


    @OnClick({R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6})
    public void onClick(View view) {
        final String LibraryId = mLibraryEdit.getText().toString();
        final String UserId = mUserEdit.getText().toString();
        final String ISBN = mISBNEdit.getText().toString();

        Intent intent;
        switch (view.getId()) {
            case R.id.button1: // 用户信息
                intent = IntentUtils.generateIntent(this, UserActivity.class);
                intent.putExtra("LibraryId", LibraryId);
                intent.putExtra("Data", Data);
                startActivity(intent);
                break;

            case R.id.button2: // 借书
                borrowbyrobot(UserId, ISBN, LibraryId);
                break;

            case R.id.button3: // 还书
                returnbyrobot(UserId, ISBN, LibraryId);
                break;

            case R.id.button4://  借书列表
                intent = IntentUtils.generateIntent(this, BooksActivity.class);
                intent.putExtra("LibraryId", LibraryId);
                intent.putExtra("Data", UserId);
                startActivity(intent);
                break;

            case R.id.button5: // 启动多我程序
                intent = IntentUtils.generateIntent(this, Constants.MEPLUS_ROBOT_PACKAGENAME);
                if (intent == null) {
                    ToastUtils.show(this, "请先安装多我机器人程序！");
                } else {
                    startActivity(intent);
                }
                break;

            case R.id.button6: // 清除日志
                mLogText.setText("");
                break;

            default:
                break;
        }
    }

    private void returnbyrobot(String userId, String data, String libraryId) {
        final TreeMap<String, String> args = ArgsUtils.generateArags();
        args.put("UserId", userId);
        args.put("Data", data);
        args.put("LibraryId", libraryId);
        final String sign = SignUtils.sign(args);
        final String timestamp = args.remove("time");

        final ApiService apiService = FancyApplication.getInstance().getApiService();
        apiService.returnbyrobot(args, timestamp, sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            final String message = response.getMessage();
                            ToastUtils.show(this, message);
                            if (response.getResult().getResultNo() == 0) {//  0 代表成功 1 代表未借阅 2 归还失败
//                                充磁 0x02 0x56 0x52 0x32 0x03 0x37
                                final int[] buffer = new int[]{0x02, 0x56, 0x52, 0x32, 0x03, 0x37};
                                final int code = Write(buffer, buffer.length);
                                if (code == 0) {
                                    ToastUtils.show(this, "充磁成功！");
                                }
                                mLogText.append(String.format("write code: %1$d \r\n", code));
                            }
                        },
                        throwable -> ToastUtils.show(this, throwable.toString()),
                        () -> {
                        }
                );
    }

    private void borrowbyrobot(String userId, String data, String libraryId) {
        final TreeMap<String, String> args = ArgsUtils.generateArags();
        args.put("UserId", userId);
        args.put("Data", data);
        args.put("LibraryId", libraryId);
        final String sign = SignUtils.sign(args);
        final String timestamp = args.remove("time");

        final ApiService apiService = FancyApplication.getInstance().getApiService();
        apiService.borrowbyrobot(args, timestamp, sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            final String message = response.getMessage();
                            ToastUtils.show(this, message);
                            if (response.getResult().getResultNo() == 0) {//  0 代表成功 1 代表未预借 2 借出失败
//                               消磁 0x02 0x56 0x52 0x31 0x03 0x34
                                int[] buffer = new int[]{0x02, 0x56, 0x52, 0x31, 0x03, 0x34};
                                final int code = Write(buffer, buffer.length);
                                if (code == 0) {
                                    ToastUtils.show(this, "消磁成功！");
                                }
                                mLogText.append(String.format("write code: %1$d \r\n", code));
                            }
                        },
                        throwable -> ToastUtils.show(this, throwable.toString()),
                        () -> {
                        }
                );
    }

    private int Write(int[] buffer, int len) {
        return com3.Write(buffer, len);
    }


    class ReadThread extends Thread {
        private void handleData(int[] RX) {
            if (RX != null && RX.length > 0) { // 数据有效
                final String data = new String(RX, 0, RX.length);
                EventBus.getDefault().post(new ScannerEvent(data));
            }
        }

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                // 扫描二维码和条形码的结果
                final int[] RX = com3.Read();
                handleData(RX);
            }
        }
    }

    static {
        System.loadLibrary("serialtest");
    }
}
