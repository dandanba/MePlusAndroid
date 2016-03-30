package com.meplus.fancy.app;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.meplus.fancy.Constants;
import com.meplus.fancy.model.ApiService;
import com.meplus.fancy.utils.RetrofitUtils;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import im.fir.sdk.FIR;
import io.fabric.sdk.android.Fabric;
import retrofit2.Retrofit;

/**
 * Created by dandanba on 3/8/16.
 */
public class FancyApplication extends Application {

    private static FancyApplication sInstance;

    private Retrofit mRetrofit;

    private RefWatcher mRefWatcher;

    public static FancyApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FIR.init(this);
        Fabric.with(this, new Crashlytics(), new Answers());
        mRetrofit = RetrofitUtils.getClient();
        mRefWatcher = Constants.sRelease ? RefWatcher.DISABLED : LeakCanary.install(this);
        sInstance = this;
    }

    public ApiService getApiService() {
        return mRetrofit.create(ApiService.class);
    }

    public RefWatcher getRefWatcher() {
        return mRefWatcher;
    }
}
