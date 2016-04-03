package com.meplus.client.avos;

import com.avos.avoscloud.AVException;
import com.meplus.avos.objects.AVOSRobot;
import com.meplus.client.events.ErrorEvent;
import com.meplus.client.events.Event;
import com.meplus.client.events.QueryEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import hugo.weaving.DebugLog;
import rx.Observable;
import rx.schedulers.Schedulers;

public class Robot {

    @DebugLog
    public static void queryByUUID(String uuId) {
        Observable.just(uuId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(id -> {
                            try {
                                final List<AVOSRobot> list = AVOSRobot.queryRobotByUUID(id);
                                EventBus.getDefault().post(new QueryEvent<>(Event.STATUS_OK, list));
                            } catch (AVException e) {
                                EventBus.getDefault().post(new ErrorEvent(Event.STATUS_OK, e));
                            }
                        },
                        throwable -> EventBus.getDefault().post(new ErrorEvent(Event.STATUS_OK, throwable)));
    }


}