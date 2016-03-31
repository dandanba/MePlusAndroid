package com.meplus.robot.api.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.meplus.avos.Keys;
import com.meplus.robot.events.ErrorEvent;
import com.meplus.robot.events.Event;
import com.meplus.robot.events.QueryEvent;
import com.meplus.robot.events.SaveEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import hugo.weaving.DebugLog;
import rx.Observable;
import rx.schedulers.Schedulers;

@AVClassName("Robot")
public class Robot extends AVObject {
    private static final String TAG = Robot.class.getSimpleName();
    public static final Creator CREATOR = AVObjectCreator.instance;


    public String getUUId() {
        return getString(Keys.KEY_ROBOT_UUID);
    }

    public void setUUId(String uuId) {
        put(Keys.KEY_ROBOT_UUID, uuId);
    }

    public int getRobotId() {
        return getInt(Keys.KEY_ROBOT_ID);
    }

    public void setRobotId(int robotId) {
        put(Keys.KEY_ROBOT_ID, robotId);
    }

    public String getRobotName() {
        return getString(Keys.KEY_ROBOT_NAME);
    }

    public void setRobotName(String robotName) {
        put(Keys.KEY_ROBOT_NAME, robotName);
    }

    public String getRobotDescription() {
        return getString(Keys.KEY_ROBOT_DESCRIPTION);
    }

    public void setRobotDescription(String robotDescription) {
        put(Keys.KEY_ROBOT_DESCRIPTION, robotDescription);
    }

    public static void queryByUUId(String uuId) {
        Observable.just(uuId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(id -> {
                    List<Robot> list = null;
                    AVQuery<Robot> query = Robot.getQuery(Robot.class);
                    query.whereEqualTo(Keys.KEY_ROBOT_UUID, id);
                    try {
                        list = query.find();
                    } catch (AVException e) {
                        Observable.error(e);
                    }
                    return list;
                })
                .subscribe(
                        result -> EventBus.getDefault().post(new QueryEvent<>(Event.STATUS_OK, result)),
                        throwable -> EventBus.getDefault().post(new ErrorEvent(Event.STATUS_OK, throwable))
                );
    }

    @DebugLog
    public void saveRotot() {
        Observable.just(this)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                        robot -> {
                            robot.saveEventually();
                            EventBus.getDefault().post(new SaveEvent<>(Event.STATUS_OK, robot));
                        },
                        throwable -> EventBus.getDefault().post(new ErrorEvent(Event.STATUS_OK, throwable))
                );
    }

}