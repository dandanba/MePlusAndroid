package com.meplus.fancy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.meplus.fancy.R;
import com.meplus.fancy.app.FancyApplication;
import com.meplus.fancy.events.ScannerEvent;
import com.meplus.fancy.fragments.SimpleScannerFragment;
import com.meplus.fancy.model.ApiService;
import com.meplus.fancy.model.entity.Code;
import com.meplus.fancy.utils.FIRUtils;
import com.meplus.fancy.utils.IntentUtils;
import com.meplus.fancy.utils.JsonUtils;
import com.meplus.fancy.utils.SignUtils;

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

    @Bind(R.id.library_edit)
    EditText mLibraryEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        FIRUtils.checkForUpdateInFIR(this);

        mLibraryEdit.setText("54986");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.button, R.id.button1})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                startActivity(IntentUtils.generateIntent(this, ScannerActivity.class));
                break;

            case R.id.button1:
                Intent intent = IntentUtils.generateIntent(this, UserActivity.class);
                intent.putExtra("LibraryId", mLibraryEdit.getText().toString());
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
        if (code != null) {

        }
    }

    private void borrowbyrobot(String data, String libraryId) {
        final TreeMap<String, String> args = new TreeMap<>();
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

                        },
                        throwable -> {
                            ToastUtils.show(this, throwable.toString());
                            SimpleScannerFragment simpleScannerFragment = (SimpleScannerFragment) findFragmentById(R.id.frame_layout);
                            simpleScannerFragment.resumeScanner();
                        },
                        () -> {
                        }
                );
    }


}
