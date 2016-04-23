package com.meplus.fancy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.meplus.fancy.Constants;
import com.meplus.fancy.R;
import com.meplus.fancy.events.ErrorEvent;
import com.meplus.fancy.events.ResponseEvent;
import com.meplus.fancy.events.ScannerEvent;
import com.meplus.fancy.model.Response;
import com.meplus.fancy.model.entity.Book;
import com.meplus.fancy.model.entity.Code;
import com.meplus.fancy.presenters.ApiPresenter;
import com.meplus.fancy.utils.FIRUtils;
import com.meplus.fancy.utils.IntentUtils;
import com.meplus.fancy.utils.JsonUtils;
import com.topeet.serialtest.presenters.SerialPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;

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

    private SerialPresenter mSerialPresenter = new SerialPresenter();
    private ApiPresenter mApiPresenter = new ApiPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        FIRUtils.checkForUpdateInFIR(this);

        mSerialPresenter.start();

        mDataEdit.setText(Data);
        mUserEdit.setText(JsonUtils.readValue(Data, Code.class).getCheck());
        mLibraryEdit.setText(LibraryId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSerialPresenter.destroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScannerEvent(ScannerEvent event) {
        final String data = event.getContent();
        mLogText.append(String.format("data: %1$s \r\n", data));
        if (data.startsWith("{") && data.endsWith("}")) { // JSON 格式
            final Code code = JsonUtils.readValue(data, Code.class);
            mDataEdit.setText(data);
            mUserEdit.setText(code.getCheck());
        } else {// ISBN 格式
            mISBNEdit.setText(data);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResponseBookEvent(ResponseEvent<Book> event) {
        final String method = event.getMethod();
        if (ApiPresenter.METHOD_BORROWBYROBOT.equals(method)) {
            final Response<Book> response = event.getResponse();
            final String message = response.getMessage();
            ToastUtils.show(this, message);
            if (response.getResult().getResultNo() == 0) {//  0 代表成功 1 代表未预借 2 借出失败
                final int code = mSerialPresenter.demagnetize();
                if (code == 0) {
                    ToastUtils.show(this, "消磁成功！");
                }
                mLogText.append(String.format("demagnetize code: %1$d \r\n", code));
            }
        } else if (ApiPresenter.METHOD_RETURNBYROBOT.equals(method)) {
            final Response<Book> response = event.getResponse();
            final String message = response.getMessage();
            ToastUtils.show(this, message);
            if (response.getResult().getResultNo() == 0) {//  0 代表成功 1 代表未借阅 2 归还失败
                final int code = mSerialPresenter.magnetize();
                if (code == 0) {
                    ToastUtils.show(this, "充磁成功！");
                }
                mLogText.append(String.format("magnetize code: %1$d \r\n", code));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorEvent(ErrorEvent event) {
        final String method = event.getMethod();
        if (ApiPresenter.METHOD_BORROWBYROBOT.equals(method)) {
            Throwable error = event.getError();
            ToastUtils.show(this, error.toString());
        } else if (ApiPresenter.METHOD_RETURNBYROBOT.equals(method)) {
            Throwable error = event.getError();
            ToastUtils.show(this, error.toString());
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
                mApiPresenter.borrowbyrobot(UserId, ISBN, LibraryId);
                break;

            case R.id.button3: // 还书
                mApiPresenter.returnbyrobot(UserId, ISBN, LibraryId);
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

    static {
        System.loadLibrary("serialtest");
    }
}
