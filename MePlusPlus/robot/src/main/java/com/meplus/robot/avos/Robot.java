package com.meplus.robot.avos;

import com.avos.avoscloud.AVException;
import com.meplus.avos.objects.AVOSRobot;
import com.meplus.events.BaseEvent;
import com.meplus.events.ErrorEvent;
import com.meplus.events.EventUtils;
import com.meplus.events.QueryEvent;
import com.meplus.events.SaveEvent;

import java.util.List;

import hugo.weaving.DebugLog;
import rx.Observable;
import rx.schedulers.Schedulers;


public class Robot {

    @DebugLog
    public static void queryByUUId(String uuId) {
        Observable.just(uuId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(id -> {
                            List<AVOSRobot> list = null;
                            try {
                                list = AVOSRobot.queryRobotByUUID(id);
                            } catch (AVException e) {
                                // 如果，leancloud上，没有表结构，那么会有异常。
                                EventUtils.postEvent(new ErrorEvent(BaseEvent.STATUS_OK, e));
                            }
                            EventUtils.postEvent(new QueryEvent<>(BaseEvent.STATUS_OK, list));
                        },
                        throwable -> EventUtils.postEvent(new ErrorEvent(BaseEvent.STATUS_OK, throwable)));
    }

    @DebugLog
    public static void saveRotot(AVOSRobot avosRobot) {
        Observable.just(avosRobot)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                        robot -> {
                            try {
                                robot.save();
                                EventUtils.postEvent(new SaveEvent<>(BaseEvent.STATUS_OK, robot));
                            } catch (AVException e) {
                                EventUtils.postEvent(new ErrorEvent(BaseEvent.STATUS_OK, e));
                            }
                        },
                        throwable -> EventUtils.postEvent(new ErrorEvent(BaseEvent.STATUS_OK, throwable))
                );
    }

}