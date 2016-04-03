package com.meplus.client.avos;

import com.avos.avoscloud.AVException;
import com.meplus.avos.objects.AVOSRobot;
import com.meplus.avos.objects.AVOSUser;
import com.meplus.events.BaseEvent;
import com.meplus.events.ErrorEvent;
import com.meplus.events.EventUtils;
import com.meplus.events.QueryEvent;
import com.meplus.events.SaveEvent;

import hugo.weaving.DebugLog;
import rx.Observable;
import rx.schedulers.Schedulers;


public class User {

    @DebugLog
    public static void addRobot(AVOSUser avosUser, AVOSRobot avosRobot) {
        Observable.just(avosUser)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                        user -> {
                            try {
                                user.addRobot(avosRobot);
                            } catch (AVException e) {
                                EventUtils.postEvent(new ErrorEvent(BaseEvent.STATUS_OK, e));
                            }
                            EventUtils.postEvent(new SaveEvent<>(BaseEvent.STATUS_OK, avosRobot));
                        },
                        throwable -> EventUtils.postEvent(new ErrorEvent(BaseEvent.STATUS_OK, throwable))
                );
    }

    @DebugLog
    public static void queryRobot(AVOSUser avosUser) {
        Observable.just(avosUser)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(user -> {
                            try {
                                EventUtils.postEvent(new QueryEvent<>(BaseEvent.STATUS_OK, user.queryRobot()));
                            } catch (AVException e) {
                                EventUtils.postEvent(new ErrorEvent(BaseEvent.STATUS_OK, e));
                            }
                        },
                        throwable -> EventUtils.postEvent(new ErrorEvent(BaseEvent.STATUS_OK, throwable))
                );
    }

}
