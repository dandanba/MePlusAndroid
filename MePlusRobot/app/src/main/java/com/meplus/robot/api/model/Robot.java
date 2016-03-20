package com.meplus.robot.api.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.meplus.robot.events.CreateEvent;
import com.meplus.robot.events.ErrorEvent;
import com.meplus.robot.events.Event;
import com.meplus.robot.events.QueryEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

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

    public static void query(String robotId) {
        Observable.just(robotId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(id -> {
                    List<Robot> list = null;
                    AVQuery<Robot> query = Robot.getQuery(Robot.class);
                    query.whereEqualTo(Robot.KEY_ROBOT_ID, id);
                    try {
                        list = query.find();
                    } catch (AVException e) {
                        Observable.error(e);
                    }
                    return list;
                })
                .subscribe(
                        list -> EventBus.getDefault().post(new QueryEvent<>(Event.STATUS_OK, list)),
                        throwable -> EventBus.getDefault().post(new ErrorEvent(Event.STATUS_OK, throwable))
                );

    }

    public static void save(String robotId) {
        Observable.just(robotId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(id -> {
                    Robot robot = new Robot();
                    robot.setRobotId(id);
                    try {
                        robot.save();
                    } catch (AVException e) {
                        Observable.error(e);
                    }
                    return robot;
                })
                .subscribe(
                        robot -> EventBus.getDefault().post(new CreateEvent<>(Event.STATUS_OK, robot)),
                        throwable -> EventBus.getDefault().post(new ErrorEvent(Event.STATUS_OK, throwable))
                );
    }
}