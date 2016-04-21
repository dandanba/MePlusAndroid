package com.meplus.fancy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.button, R.id.button1, R.id.button2, R.id.button3, R.id.button4})
    public void onClick(View view) {

        final String LibraryId = mLibraryEdit.getText().toString();
        final String UserId = mUserEdit.getText().toString();
        final String ISBN = mISBNEdit.getText().toString();

        Intent intent;
        switch (view.getId()) {
            case R.id.button:
                startActivity(IntentUtils.generateIntent(this, ScannerActivity.class));
                break;

            case R.id.button1:
                intent = IntentUtils.generateIntent(this, UserActivity.class);
                intent.putExtra("LibraryId", LibraryId);
                intent.putExtra("Data", Data);
                startActivity(intent);
                break;

            case R.id.button2:
                borrowbyrobot(UserId, ISBN, LibraryId);
                break;

            case R.id.button3:
                returnbyrobot(UserId, ISBN, LibraryId);
                break;

            case R.id.button4:
                intent = IntentUtils.generateIntent(this, BooksActivity.class);
                intent.putExtra("LibraryId", LibraryId);
                intent.putExtra("Data", UserId);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScannerEvent(ScannerEvent event) {
        final String data = event.getContent();
        final Code code = JsonUtils.readValue(data, Code.class);
        if (code != null) { // 扫描用户的二维码
            mDataEdit.setText(data);
            mUserEdit.setText(code.getCheck());
        } else { // 扫描图书
            mISBNEdit.setText(data);
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
                        },
                        throwable -> ToastUtils.show(this, throwable.toString()),
                        () -> {
                        }
                );
    }


}
