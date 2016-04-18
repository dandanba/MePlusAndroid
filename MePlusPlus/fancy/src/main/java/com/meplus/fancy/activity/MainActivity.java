package com.meplus.fancy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.meplus.fancy.R;
import com.meplus.fancy.app.FancyApplication;
import com.meplus.fancy.model.ApiService;
import com.meplus.fancy.utils.FIRUtils;
import com.meplus.fancy.utils.IntentUtils;
import com.meplus.fancy.utils.SignUtils;

import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        FIRUtils.checkForUpdateInFIR(this);
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
               /* Intent intent = new Intent(this, ScannerActivity.class);
                startActivityForResult(intent, SCAN_CODE);*/
                break;
            case R.id.button1:
                final TreeMap<String, String> args = new TreeMap<>();
                final String Data = "samplestring1";
                final String LibraryId = "samplestring2";
                args.put("Data", Data);
                args.put("LibraryId", LibraryId);
                final String sign = SignUtils.sign(args);
                final String timestamp = args.remove("time");

                final ApiService apiService = FancyApplication.getInstance().getApiService();
                apiService.getborrowlistbyrobot(args, timestamp, sign)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> ToastUtils.show(this, response.toString()),
                                throwable -> ToastUtils.show(this, throwable.toString()),
                                () -> {
                                }
                        );
                break;

            default:
                break;
        }
    }

  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SCAN_CODE:
                if (resultCode == RESULT_OK) {
                    String result = data.getStringExtra("scan_result");
                    mTextView.setText(result);
                } else if (resultCode == RESULT_CANCELED) {
                    mTextView.setText("扫描出错");
                }
                break;
            default:
                break;
        }

    }*/
}
