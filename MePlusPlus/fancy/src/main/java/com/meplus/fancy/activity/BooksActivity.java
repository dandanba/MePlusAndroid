package com.meplus.fancy.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.meplus.fancy.R;
import com.meplus.fancy.app.FancyApplication;
import com.meplus.fancy.fragments.BooksFragment;
import com.meplus.fancy.model.ApiService;
import com.meplus.fancy.utils.ArgsUtils;
import com.meplus.fancy.utils.SignUtils;

import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BooksActivity extends BaseActivity {
    private final static String TAG = BooksActivity.class.getSimpleName();
    @Bind(R.id.back_button)
    Button mBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        ButterKnife.bind(this);

        replaceContainer(R.id.frame_layout, BooksFragment.newInstance());

        final String Data = getIntent().getStringExtra("Data");
        final String LibraryId = getIntent().getStringExtra("LibraryId");
        getborrowedlistbyrobot(Data, LibraryId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.back_button)
    public void onClick(View view) {
        onBackPressed();
    }

    private void getborrowedlistbyrobot(String data, String libraryId) {
        final TreeMap<String, String> args = ArgsUtils.generateArags();
        args.put("Data", data);
        args.put("LibraryId", libraryId);
        final String sign = SignUtils.sign(args);
        final String timestamp = args.remove("time");

        final ApiService apiService = FancyApplication.getInstance().getApiService();
        apiService.getborrowedlistbyrobot(args, timestamp, sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            final String message = response.getMessage();
                            ToastUtils.show(this, message);

                            BooksFragment fragment = (BooksFragment) findFragmentById(R.id.frame_layout);
                            fragment.updateBooks(response.getResult());
                        },
                        throwable -> ToastUtils.show(this, throwable.toString()),
                        () -> {
                        }
                );
    }

}
