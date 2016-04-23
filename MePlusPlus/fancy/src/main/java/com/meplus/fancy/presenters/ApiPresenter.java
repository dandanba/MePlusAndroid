package com.meplus.fancy.presenters;

import com.meplus.fancy.app.FancyApplication;
import com.meplus.fancy.events.ErrorEvent;
import com.meplus.fancy.events.ResponseEvent;
import com.meplus.fancy.model.ApiService;
import com.meplus.fancy.utils.ArgsUtils;
import com.meplus.fancy.utils.SignUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.TreeMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by dandanba on 4/23/16.
 */
public class ApiPresenter {
    public static final String METHOD_BORROWBYROBOT = "borrowbyrobot";
    public static final String METHOD_RETURNBYROBOT = "returnbyrobot";
    public static final String METHOD_GETBORROWEDLISTBYROBOT = "getborrowedlistbyrobot";
    public static final String METHOD_GETBORROWLISTBYROBOT = "getborrowlistbyrobot";

    public void returnbyrobot(String userId, String data, String libraryId) {
        final TreeMap<String, String> args = ArgsUtils.generateArags();
        args.put("UserId", userId);
        args.put("Data", data);
        args.put("LibraryId", libraryId);
        final String sign = SignUtils.sign(args);
        final String timestamp = args.remove("time");

        final ApiService apiService = FancyApplication.getInstance().getApiService();
        apiService.returnbyrobot(args, timestamp, sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> EventBus.getDefault().post(new ResponseEvent<>(METHOD_RETURNBYROBOT, response)),
                        throwable -> EventBus.getDefault().post(new ErrorEvent(METHOD_RETURNBYROBOT, throwable)),
                        () -> {
                        }
                );
    }

    public void borrowbyrobot(String userId, String data, String libraryId) {
        final TreeMap<String, String> args = ArgsUtils.generateArags();
        args.put("UserId", userId);
        args.put("Data", data);
        args.put("LibraryId", libraryId);
        final String sign = SignUtils.sign(args);
        final String timestamp = args.remove("time");

        final ApiService apiService = FancyApplication.getInstance().getApiService();
        apiService.borrowbyrobot(args, timestamp, sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> EventBus.getDefault().post(new ResponseEvent<>(METHOD_BORROWBYROBOT, response)),
                        throwable -> EventBus.getDefault().post(new ErrorEvent(METHOD_BORROWBYROBOT, throwable)),
                        () -> {
                        }
                );
    }

    public void getborrowedlistbyrobot(String data, String libraryId) {
        final TreeMap<String, String> args = ArgsUtils.generateArags();
        args.put("Data", data);
        args.put("LibraryId", libraryId);
        final String sign = SignUtils.sign(args);
        final String timestamp = args.remove("time");

        final ApiService apiService = FancyApplication.getInstance().getApiService();
        apiService.getborrowedlistbyrobot(args, timestamp, sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> EventBus.getDefault().post(new ResponseEvent<>(METHOD_GETBORROWEDLISTBYROBOT, response)),
                        throwable -> EventBus.getDefault().post(new ErrorEvent(METHOD_GETBORROWEDLISTBYROBOT, throwable)),
                        () -> {
                        }
                );
    }

    public void getborrowlistbyrobot(String data, String libraryId) {
        final TreeMap<String, String> args = ArgsUtils.generateArags();
        args.put("Data", data);
        args.put("LibraryId", libraryId);
        final String sign = SignUtils.sign(args);
        final String timestamp = args.remove("time");

        final ApiService apiService = FancyApplication.getInstance().getApiService();
        apiService.getborrowlistbyrobot(args, timestamp, sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> EventBus.getDefault().post(new ResponseEvent<>(METHOD_GETBORROWLISTBYROBOT, response)),
                        throwable -> EventBus.getDefault().post(new ErrorEvent(METHOD_GETBORROWLISTBYROBOT, throwable)),
                        () -> {
                        }
                );
    }

}
