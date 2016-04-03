package com.meplus.client.avos;

import com.avos.avoscloud.AVException;
import com.meplus.avos.objects.AVOSRobot;
import com.meplus.events.BaseEvent;
import com.meplus.events.ErrorEvent;
import com.meplus.events.QueryEvent;

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
                                EventBus.getDefault().post(new QueryEvent<>(BaseEvent.STATUS_OK, list));
                            } catch (AVException e) {
                                EventBus.getDefault().post(new ErrorEvent(BaseEvent.STATUS_OK, e));
                            }
                        },
                        throwable -> EventBus.getDefault().post(new ErrorEvent(BaseEvent.STATUS_OK, throwable)));
    }


}