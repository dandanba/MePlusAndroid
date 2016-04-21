package com.meplus.fancy.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.meplus.fancy.R;
import com.meplus.fancy.app.FancyApplication;
import com.meplus.fancy.fragments.BooksFragment;
import com.meplus.fancy.model.ApiService;
import com.meplus.fancy.model.entity.User;
import com.meplus.fancy.utils.ArgsUtils;
import com.meplus.fancy.utils.ImageUtils;
import com.meplus.fancy.utils.SignUtils;

import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserActivity extends BaseActivity {
    private final static String TAG = UserActivity.class.getSimpleName();
    @Bind(R.id.icon)
    ImageView mIcon;
    @Bind(R.id.user_id)
    TextView mUserId;
    @Bind(R.id.name_text)
    TextView mNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);

        replaceContainer(R.id.frame_layout, BooksFragment.newInstance());

        final String Data = getIntent().getStringExtra("Data");
        final String LibraryId = getIntent().getStringExtra("LibraryId");
        getborrowlistbyrobot(Data, LibraryId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void getborrowlistbyrobot(String data, String libraryId) {
        final TreeMap<String, String> args = ArgsUtils.generateArags();
        args.put("Data", data);
        args.put("LibraryId", libraryId);
        final String sign = SignUtils.sign(args);
        final String timestamp = args.remove("time");

        final ApiService apiService = FancyApplication.getInstance().getApiService();
        apiService.getborrowlistbyrobot(args, timestamp, sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            final String message = response.getMessage();
                            ToastUtils.show(this, message);

                            final User user = response.getResult();
                            ImageUtils.withActivityInto(this, user.getIconUrl(), mIcon);
                            mUserId.setText(user.getUserId());
                            mNameText.setText(user.getNickName());

                            BooksFragment fragment = (BooksFragment) findFragmentById(R.id.frame_layout);
                            fragment.updateBooks(user.getBorrowBookList());
                        },
                        throwable -> ToastUtils.show(this, throwable.toString()),
                        () -> {
                        }
                );
    }


}
