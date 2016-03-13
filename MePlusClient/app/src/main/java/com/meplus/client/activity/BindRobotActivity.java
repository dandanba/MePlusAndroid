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
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.meplus.client.R;
import com.meplus.client.api.model.Robot;
import com.meplus.client.api.model.User;
import com.meplus.client.events.BindEvent;
import com.meplus.client.events.Event;
import com.meplus.client.events.ScannerEvent;
import com.meplus.client.utils.IntentUtils;
import com.meplus.client.utils.SnackBarUtils;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ListUtils;

/**
 * 绑定机器人
 */
public class BindRobotActivity extends BaseActivity implements Validator.ValidationListener {

    @Bind(R.id.root)
    LinearLayout mRoot;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @NotEmpty
    @Bind(R.id.bind_edit)
    EditText mBindEdit;

    private Validator mValidator;

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
    }

    @Override
    protected void onDestroy() {
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

    private void doBindRobot(final String robotId) {

        // 通过机器人的ID查找机器人
        // 创建关系

        AVQuery<Robot> query = Robot.getQuery(Robot.class);
        query.whereEqualTo(Robot.KEY_ROBOT_ID, robotId);
        query.findInBackground(new FindCallback<Robot>() {
            @Override
            public void done(List<Robot> results, AVException e) {
                if (e == null) {
                    final int size = ListUtils.getSize(results);
                    if (size > 0) {
                        final Robot robot = results.get(0);
                        final User user = User.getCurrentUser(User.class);
                        final AVRelation<Robot> relation = user.getRelation(User.RELATION_ROBOTS);
                        relation.add(robot);
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    final AVQuery<Robot> query = relation.getQuery();
                                    query.limit(1);
                                    query.findInBackground(new FindCallback<Robot>() {
                                        @Override
                                        public void done(List<Robot> results, AVException e) {
                                            if (e == null) {
                                                user.setRobotList(results);
                                                EventBus.getDefault().post(new BindEvent(Event.STATUS_OK));
                                                finish();
                                            } else {
                                                SnackBarUtils.show(mRoot, e.toString());
                                            }
                                        }
                                    });


                                } else {
                                    SnackBarUtils.show(mRoot, e.toString());
                                }
                            }
                        });
                    } else {
                        SnackBarUtils.show(mRoot, "机器人ID有误！");
                    }
                } else {
                    SnackBarUtils.show(mRoot, e.toString());
                }
            }
        });

//
//        user.setRobotId(robotId);
//        user.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(AVException e) {
//                if (e == null) {
//                    EventBus.getDefault().post(new BindEvent(Event.STATUS_OK));
//                    finish();
//                } else {
//                    SnackbarUtils.show(mRoot, e.toString());
//                }
//            }
//        });
    }

}