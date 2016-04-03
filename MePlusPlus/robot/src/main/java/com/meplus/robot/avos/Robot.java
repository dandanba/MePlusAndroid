package com.meplus.robot.avos;

import com.avos.avoscloud.AVException;
import com.meplus.avos.objects.AVOSRobot;
import com.meplus.robot.events.ErrorEvent;
import com.meplus.robot.events.Event;
import com.meplus.robot.events.QueryEvent;
import com.meplus.robot.events.SaveEvent;

import org.greenrobot.eventbus.EventBus;

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
                                EventBus.getDefault().post(new ErrorEvent(Event.STATUS_OK, e));
                            }
                            EventBus.getDefault().post(new QueryEvent<>(Event.STATUS_OK, list));
                        },
                        throwable -> EventBus.getDefault().post(new ErrorEvent(Event.STATUS_OK, throwable)));
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
                                EventBus.getDefault().post(new SaveEvent<>(Event.STATUS_OK, robot));
                            } catch (AVException e) {
                                EventBus.getDefault().post(new ErrorEvent(Event.STATUS_OK, e));
                            }
                        },
                        throwable -> EventBus.getDefault().post(new ErrorEvent(Event.STATUS_OK, throwable))
                );
    }

}