package com.meplus.robot.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.meplus.client.R;
import com.meplus.robot.utils.IntentUtils;

import butterknife.ButterKnife;

/**
 * 启动Logo页面
 */
public class LogoActivity extends BaseActivity implements Handler.Callback {
    private static final String TAG = LogoActivity.class.getSimpleName();

    @butterknife.Bind(R.id.shimmer_view_container)
    com.facebook.shimmer.ShimmerFrameLayout mShimmerViewContainer;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        ButterKnife.bind(this);
        mShimmerViewContainer.startShimmerAnimation();
        mHandler = new Handler(this);
        mHandler.sendEmptyMessageDelayed(1, 3000);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == 1) {
            startActivity(IntentUtils.generateIntent(LogoActivity.this, TestsActivity.class));
            finish();
//
//            final String username = "robot2";
//            final String password = "robot2";
//            final String email = "wanggeng@meplusplus.com";
//
//            final Robot robot = new Robot();
//            robot.setRobotName(username);
//            robot.setRobotId(UUIDUtils.getUUID(this));
//
//            final SaveCallback callback = new SaveCallback() {
//                public void done(AVException e) {
//                    if (e == null) {
//                        final User user = User.getCurrentUser(User.class);
//                        if (user == null) {
//                            startActivity(IntentUtils.generateIntent(LogoActivity.this, LoginActivity.class));
//                        } else {
//                            final AVRelation<Robot> relation = user.getRelation(User.RELATION_ROBOTS);
//                            final AVQuery<Robot> query = relation.getQuery();
//                            query.setLimit(1);
//                            query.findInBackground(new FindCallback<Robot>() {
//                                @Override
//                                public void done(List<Robot> results, AVException e) {
//                                    if (e == null) {
//                                        user.setRobotList(results);
//                                    } else {
//                                        ToastUtils.show(LogoActivity.this, e.toString());
//                                    }
//                                    startActivity(IntentUtils.generateIntent(LogoActivity.this, MainActivity.class));
//                                }
//                            });
//                        }
//                        finish();
//                    } else {
//                        ToastUtils.show(LogoActivity.this, e.toString());
//                    }
//                }
//            };
//            robot.saveInBackground(callback);
//            callback.done(null);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }

}
