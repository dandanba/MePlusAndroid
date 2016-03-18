package com.meplus.robot.api.model;

import android.util.Log;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.meplus.robot.api.MePlus;

import java.util.List;

import cn.trinea.android.common.util.ListUtils;
import hugo.weaving.DebugLog;
import rx.Observable;

@AVClassName("Robot")
public class Robot extends AVObject {
    private static final String TAG = Robot.class.getSimpleName();
    public static final Creator CREATOR = AVObjectCreator.instance;
    public final static String KEY_ROBOT_ID = "robotId";
    public final static String KEY_ROBOT_NAME = "robotName";

    public String getRobotId() {
        return getString(KEY_ROBOT_ID);
    }

    public void setRobotId(String robotId) {
        put(KEY_ROBOT_ID, robotId);
    }


    public String getRobotName() {
        return getString(KEY_ROBOT_NAME);
    }

    public void setRobotName(String robotName) {
        put(KEY_ROBOT_NAME, robotName);
    }

    @DebugLog
    public static Observable<Robot> getRobot(String robotId) {

        Observable.OnSubscribe<Robot> function = subscriber -> {
            subscriber.onStart();

            AVQuery<Robot> query = Robot.getQuery(Robot.class);
            query.whereEqualTo(Robot.KEY_ROBOT_ID, robotId);
            List<Robot> robotList = null;
            Robot robot = null;
            try {
                robotList = query.find();
            } catch (AVException e) {
                Log.e(TAG, "getRobot", e);
            }

            final int size = ListUtils.getSize(robotList);
            if (size > 0) {
                for (Robot item : robotList) {
                    if (item.getRobotId().equals(robotId)) {
                        robot = item;
                        break;
                    }
                }
            }

            if (robot == null) {
                final int code = MePlus.ErrorCodeEmptyData;
                final String description = MePlus.getDescrpition(code);
                subscriber.onError(new AVException(code, description));
            } else {
                subscriber.onNext(robot);
            }

            subscriber.onCompleted();
        };

        return Observable.create(function);
    }
}