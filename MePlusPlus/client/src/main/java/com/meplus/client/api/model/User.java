package com.meplus.client.api.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.meplus.avos.Keys;
import com.meplus.client.events.ErrorEvent;
import com.meplus.client.events.Event;
import com.meplus.client.events.QueryEvent;
import com.meplus.client.events.SaveEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import hugo.weaving.DebugLog;
import rx.Observable;
import rx.schedulers.Schedulers;

@AVClassName("User")
public class User extends AVUser {
    public static final Creator CREATOR = AVObjectCreator.instance;

    public int getUserId() {
        return getInt(Keys.KEY_USER_ID);
    }

    public void setUserId(int userId) {
        put(Keys.KEY_USER_ID, userId);
    }


    public String getUUId() {
        return getString(Keys.KEY_USER_UUID);
    }

    public void setUUId(String uuId) {
        put(Keys.KEY_USER_UUID, uuId);
    }

    @DebugLog
    public void addRobot(Robot robot) {
        Observable.just(robot)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                        r -> {
                            AVRelation<Robot> relation = getRelation(Keys.RELATION_ROBOTS);
                            relation.add(r);
                            saveEventually();

                            EventBus.getDefault().post(new SaveEvent<>(Event.STATUS_OK, r));
                        },
                        throwable -> EventBus.getDefault().post(new ErrorEvent(Event.STATUS_OK, throwable))
                );
    }

    @DebugLog
    public void queryRobot() {
        Observable.just(this)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(user -> {
                            final AVRelation<Robot> relation = user.getRelation(Keys.RELATION_ROBOTS);
                            final AVQuery<Robot> query = relation.getQuery();
                            query.setLimit(1);

                            List<Robot> robotList = null;
                            try {
                                robotList = query.find();
                            } catch (AVException e) {
                                Observable.error(e);
                            }
                            EventBus.getDefault().post(new QueryEvent<>(Event.STATUS_OK, robotList));
                        }, throwable -> EventBus.getDefault().post(new ErrorEvent(Event.STATUS_OK, throwable))
                );
    }

}
