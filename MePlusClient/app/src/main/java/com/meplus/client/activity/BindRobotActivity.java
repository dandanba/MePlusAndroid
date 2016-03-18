package com.meplus.client.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.meplus.client.R;
import com.meplus.client.api.model.Robot;
import com.meplus.client.api.model.User;
import com.meplus.client.events.ScannerEvent;
import com.meplus.client.utils.IntentUtils;
import com.meplus.client.utils.SnackBarUtils;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ListUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * 绑定机器人
 */
public class BindRobotActivity extends BaseActivity implements ValidationListener {

    private static final String TAG = BindRobotActivity.class.getSimpleName();
    @Bind(R.id.root)
    LinearLayout mRoot;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @NotEmpty
    @Bind(R.id.bind_edit)
    EditText mBindEdit;

    private Validator mValidator;
    private PublishSubject<String> mPublishSubject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_robot);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        mToolbar.setNavigationIcon(R.drawable.back);
        mToolbar.setTitle("绑定机器人");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        final List<Robot> robotList = User.getCurrentUser(User.class).getRobotList();
        mBindEdit.setText(ListUtils.isEmpty(robotList) ? "" : robotList.get(0).getRobotId());

        mPublishSubject = PublishSubject.create();
        mPublishSubject.throttleFirst(1, TimeUnit.MICROSECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        s -> SnackBarUtils.show(mToolbar, s));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.bind_button, R.id.scan_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bind_button:
                mValidator.validate();
                break;
            case R.id.scan_button:
                startActivity(IntentUtils.generateIntent(this, ScannerActivity.class));
                break;
        }
    }

    @Override
    public void onValidationSucceeded() {
        final String robotId = mBindEdit.getText().toString();
        doBindRobot(robotId);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScannerEvent(ScannerEvent event) {
        if (event.ok()) {
            final String robotId = event.getContent();
            doBindRobot(robotId);
        }
    }

    private void bindRobot(String robotId) throws AVException {
        // 1. 通过机器人Id查找是否包含这个机器人Id的机器人
        AVQuery<Robot> query = Robot.getQuery(Robot.class);
        query.whereEqualTo(Robot.KEY_ROBOT_ID, robotId);
        List<Robot> robotList = query.find();
        final int size = ListUtils.getSize(robotList);
        if (size > 0) {
            Robot robot = null;
            for (Robot item : robotList) {
                if (item.getRobotId().equals(robotId)) {
                    robot = item;
                    break;
                }
            }
            // 2. 如果包含机器人Id的机器人，将当前的用户绑定关系，保存关系
            User user = User.getCurrentUser(User.class);
            AVRelation<Robot> relation = user.getRelation(User.RELATION_ROBOTS);
            relation.add(robot);
            user.save();
            // 3. 查询出，绑定好的关系的用户的所有机器人，并将机器人的列表返回
            query = relation.getQuery();
            query.limit(10);
            robotList = query.find();
            user.setRobotList(robotList);
        } else {
            throw (new AVException("机器人ID有误！", new IllegalArgumentException()));
        }

    }

    private void doBindRobot(String robotId) {
        Observable.just(robotId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        s -> {
                            try {
                                bindRobot(s);
                            } catch (AVException e) {
                                mPublishSubject.onNext(e.toString());
                            }
                        },
                        e -> SnackBarUtils.show(mRoot, e.toString())
                );

    }

}
