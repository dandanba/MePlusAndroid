package com.meplus.fancy.activity;

import android.os.Bundle;

import com.meplus.fancy.R;
import com.meplus.fancy.events.ErrorEvent;
import com.meplus.fancy.events.ResponseEvent;
import com.meplus.fancy.fragments.BooksFragment;
import com.meplus.fancy.model.Response;
import com.meplus.fancy.model.entity.Book;
import com.meplus.fancy.presenters.ApiPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;

public class BooksActivity extends BaseActivity {
    private final static String TAG = BooksActivity.class.getSimpleName();
    private ApiPresenter mApiPresenter = new ApiPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        replaceContainer(R.id.frame_layout, BooksFragment.newInstance());

        final String Data = getIntent().getStringExtra("Data");
        final String LibraryId = getIntent().getStringExtra("LibraryId");
        mApiPresenter.getborrowedlistbyrobot(Data, LibraryId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResponseBookEvent(ResponseEvent<List<Book>> event) {
        final String method = event.getMethod();
        if (ApiPresenter.METHOD_GETBORROWEDLISTBYROBOT.equals(method)) {
            final Response<List<Book>> response = event.getResponse();
            final String message = response.getMessage();
            ToastUtils.show(this, message);

            BooksFragment fragment = (BooksFragment) findFragmentById(R.id.frame_layout);
            fragment.updateBooks(response.getResult());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorEvent(ErrorEvent event) {
        final String method = event.getMethod();
        if (ApiPresenter.METHOD_GETBORROWEDLISTBYROBOT.equals(method)) {
            Throwable error = event.getError();
            ToastUtils.show(this, error.toString());
        }
    }

}
