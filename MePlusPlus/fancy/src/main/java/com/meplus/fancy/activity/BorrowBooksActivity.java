package com.meplus.fancy.activity;

import android.os.Bundle;

import com.meplus.fancy.R;
import com.meplus.fancy.events.BookEvent;
import com.meplus.fancy.events.ErrorEvent;
import com.meplus.fancy.events.ResponseEvent;
import com.meplus.fancy.events.ScannerEvent;
import com.meplus.fancy.fragments.BooksFragment;
import com.meplus.fancy.model.Response;
import com.meplus.fancy.model.entity.User;
import com.meplus.fancy.presenters.ApiPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

/**
 * 已经在凡学应用上订阅的图书列表
 */
public class BorrowBooksActivity extends BaseActivity {
    private final static String TAG = BorrowBooksActivity.class.getSimpleName();

    private ApiPresenter mApiPresenter = new ApiPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_books);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        replaceContainer(R.id.frame_layout, BooksFragment.newInstance());

        final String Data = getIntent().getStringExtra("Data");
        final String LibraryId = getIntent().getStringExtra("LibraryId");
        mApiPresenter.getborrowlistbyrobot(ApiPresenter.METHOD_GETBORROWLISTBYROBOT, Data, LibraryId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResponseEventUser(ResponseEvent<User> event) {
        final String method = event.getMethod();
        if (ApiPresenter.METHOD_GETBORROWLISTBYROBOT.equals(method)) {
            final Response<User> response = event.getResponse();
            if (response.isOk()) {
                final User user = response.getResult();
                BooksFragment fragment = (BooksFragment) findFragmentById(R.id.frame_layout);
                fragment.updateBooks(user.getBorrowBookList());
            } else {
                response.showMessage(this);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorEvent(ErrorEvent event) {
        final String method = event.getMethod();
        if (ApiPresenter.METHOD_GETBORROWLISTBYROBOT.equals(method)) {
            event.showError(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScannerEvent(ScannerEvent event) {
        final String data = event.getContent();
        if (!data.startsWith("{") || !data.endsWith("}")) { // 不是JSON格式 就是 ISBN 格式
            EventBus.getDefault().post(new BookEvent(BookEvent.ACTION_BORROW, data));
        }
    }


}
