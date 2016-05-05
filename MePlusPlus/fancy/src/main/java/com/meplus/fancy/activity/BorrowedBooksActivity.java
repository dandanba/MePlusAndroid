package com.meplus.fancy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.meplus.fancy.R;
import com.meplus.fancy.events.BookEvent;
import com.meplus.fancy.events.ErrorEvent;
import com.meplus.fancy.events.ResponseEvent;
import com.meplus.fancy.events.ScannerEvent;
import com.meplus.fancy.events.SerialEvent;
import com.meplus.fancy.fragments.BooksFragment;
import com.meplus.fancy.model.Response;
import com.meplus.fancy.model.entity.Book;
import com.meplus.fancy.presenters.ApiPresenter;
import com.meplus.fancy.utils.IntentUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 已经借阅的图书列表
 */
public class BorrowedBooksActivity extends BaseActivity implements Handler.Callback {
    private final static String TAG = BorrowedBooksActivity.class.getSimpleName();

    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;

    private ApiPresenter mApiPresenter = new ApiPresenter();
    private Handler mDelaySender;
    private final int sMaxCount = (int) (MainActivity.sDelayMillis / 1000);
    private int mCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDelaySender = new Handler(this);

        setContentView(R.layout.activity_borrowed_books);
        ButterKnife.bind(this);

        mProgressBar.setVisibility(View.GONE);

        EventBus.getDefault().register(this);
        replaceContainer(R.id.frame_layout, BooksFragment.newInstance());
        update();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
        mDelaySender.removeMessages(1);
        mDelaySender.removeMessages(2);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResponseEventListBook(ResponseEvent<List<Book>> event) {
        final String method = event.getMethod();
        if (ApiPresenter.METHOD_GETBORROWEDLISTBYROBOT.equals(method)) {
            final Response<List<Book>> response = event.getResponse();
            if (response.isOk()) {
                BooksFragment fragment = (BooksFragment) findFragmentById(R.id.frame_layout);
                fragment.updateBooks(response.getResult());
            } else {
                response.showMessage(this);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorEvent(ErrorEvent event) {
        final String method = event.getMethod();
        if (ApiPresenter.METHOD_GETBORROWEDLISTBYROBOT.equals(method)) {
            event.showError(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScannerEvent(ScannerEvent event) {
        final String data = event.getContent();
        if (!data.startsWith("{") || !data.endsWith("}")) { // 不是JSON格式 就是 ISBN 格式
            final String type = event.getType();
            if (type.equals(ScannerEvent.TYPE_CAMERA)) { // 延迟发送
                final Message msg = mDelaySender.obtainMessage();
                msg.what = 1;
                msg.obj = data;
                mDelaySender.sendMessageDelayed(msg, MainActivity.sDelayMillis);

                mCount = 0;
                mDelaySender.sendEmptyMessage(2);
            } else {
                EventBus.getDefault().post(new BookEvent(BookEvent.ACTION_RETURN, data));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSerialEvent(SerialEvent event) {
        update();
    }

    @OnClick({R.id.scan_button})
    public void onClick(View view) {
        final Intent intent = IntentUtils.generateIntent(this, ScannerActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == 1) {
            mProgressBar.setVisibility(View.GONE);

            String data = (String) msg.obj;
            EventBus.getDefault().post(new BookEvent(BookEvent.ACTION_RETURN, data));
            return true;
        } else if (msg.what == 2) {
            mDelaySender.removeMessages(2);
            if (mCount == 0) {
                mProgressBar.setVisibility(View.VISIBLE);
                mDelaySender.sendEmptyMessageDelayed(2, 1000);
            } else if (mCount < sMaxCount) {
                Log.i(TAG, "count: " + mCount);
                mProgressBar.setProgress(mCount * 100 / sMaxCount);
                mDelaySender.sendEmptyMessageDelayed(2, 1000);
            }
            mCount++;
            return true;
        }
        return false;
    }

    private void update() {
        final String Data = getIntent().getStringExtra("Data");
        final String LibraryId = getIntent().getStringExtra("LibraryId");
        mApiPresenter.getborrowedlistbyrobot(ApiPresenter.METHOD_GETBORROWEDLISTBYROBOT, Data, LibraryId);
    }
}
