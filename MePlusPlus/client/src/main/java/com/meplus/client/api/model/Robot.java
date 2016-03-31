package com.meplus.client.api.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.meplus.client.events.ErrorEvent;
import com.meplus.client.events.Event;
import com.meplus.client.events.QueryEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import hugo.weaving.DebugLog;
import rx.Observable;
import rx.schedulers.Schedulers;

@AVClassName("Robot")
public class Robot extends AVObject {
    private static final String TAG = Robot.class.getSimpleName();
    public static final Creator CREATOR = AVObjectCreator.instance;
    public final static String KEY_UUID = "uuId";
    public final static String KEY_ROBOT_ID = "robotId";
    public final static String KEY_ROBOT_NAME = "robotName";
    public final static String KEY_ROBOT_DESCRIPTION = "robotDescription";

    public String getUUId() {
        return getString(KEY_UUID);
    }

    public void setUUId(String uuId) {
        put(KEY_UUID, uuId);
    }

    public int getRobotId() {
        return getInt(KEY_ROBOT_ID);
    }

    public void setRobotId(int robotId) {
        put(KEY_ROBOT_ID, robotId);
    }

    public String getRobotName() {
        return getString(KEY_ROBOT_NAME);
    }

    public void setRobotName(String robotName) {
        put(KEY_ROBOT_NAME, robotName);
    }

    public String getRobotDescription() {
        return getString(KEY_ROBOT_DESCRIPTION);
    }

    public void setRobotDescription(String robotDescription) {
        put(KEY_ROBOT_DESCRIPTION, robotDescription);
    }

    @DebugLog
    public static void queryByUUID(String uuId) {
        Observable.just(uuId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(id -> {
                    List<Robot> list = null;
                    AVQuery<Robot> query = Robot.getQuery(Robot.class);
                    query.whereEqualTo(Robot.KEY_UUID, id);
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



}