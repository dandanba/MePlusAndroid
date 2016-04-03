package com.meplus.client.avos;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.meplus.avos.objects.AVOSRobot;
import com.meplus.avos.objects.AVOSUser;
import com.meplus.client.events.ErrorEvent;
import com.meplus.client.events.Event;
import com.meplus.client.events.QueryEvent;
import com.meplus.client.events.SaveEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import hugo.weaving.DebugLog;
import rx.Observable;
import rx.schedulers.Schedulers;


public class User {

    @DebugLog
    public static void addRobot(AVUser user, AVOSRobot robot) {
        Observable.just(robot)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                        r -> {
                            AVRelation<AVOSRobot> relation = user.getRelation(AVOSUser.RELATION_ROBOTS);
                            relation.add(r);
                            try {
                                user.save();
                            } catch (AVException e) {
                                EventBus.getDefault().post(new ErrorEvent(Event.STATUS_OK, e));
                            }
                            EventBus.getDefault().post(new SaveEvent<>(Event.STATUS_OK, r));
                        },
                        throwable -> EventBus.getDefault().post(new ErrorEvent(Event.STATUS_OK, throwable))
                );
    }

    @DebugLog
    public static void queryRobot(AVOSUser avosUser) {
        Observable.just(avosUser)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(user -> {
                            final AVRelation<AVOSRobot> relation = user.getRelation(AVOSUser.RELATION_ROBOTS);
                            final AVQuery<AVOSRobot> query = relation.getQuery();
                            query.setLimit(1);
                            try {
                                List<AVOSRobot> robotList = query.find();
                                EventBus.getDefault().post(new QueryEvent<>(Event.STATUS_OK, robotList));
                            } catch (AVException e) {
                                EventBus.getDefault().post(new ErrorEvent(Event.STATUS_OK, e));
                            }
                        },
                        throwable -> EventBus.getDefault().post(new ErrorEvent(Event.STATUS_OK, throwable))
                );
    }

}
