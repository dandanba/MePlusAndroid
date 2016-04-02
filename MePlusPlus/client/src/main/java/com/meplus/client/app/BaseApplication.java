package com.meplus.client.app;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.meplus.client.BuildConfig;
import com.meplus.avos.Constants;
import com.meplus.client.api.model.Robot;
import com.meplus.client.api.model.User;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import im.fir.sdk.FIR;
import io.agora.sample.agora.AgoraApplication;
import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;

/**
 * Created by dandanba on 3/1/16.
 */
public class BaseApplication extends AgoraApplication {
    public RefWatcher getRefWatcher() {
        return mRefWatcher;
    }

    protected RefWatcher mRefWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            mRefWatcher = LeakCanary.install(this);
        } else {
            mRefWatcher = RefWatcher.DISABLED;
        }

        // stetho
        Stetho.initializeWithDefaults(this);

        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        Fabric.with(this, new Crashlytics(), new Answers());
        FIR.init(this);

        AVObject.registerSubclass(Robot.class);
        AVObject.registerSubclass(User.class);

        AVOSCloud.initialize(this, Constants.AVOS_APP_ID, Constants.AVOS_APP_KEY); // 初始化参数依次为 this, AppId, AppKey
        // AVOSCloud.useAVCloudUS(); // 启用北美节点
        // AVAnalytics.enableCrashReport(this.getApplicationContext(), true);  // 启用崩溃错误统计
        AVOSCloud.setLastModifyEnabled(true);
        AVOSCloud.setDebugLogEnabled(true);
    }
}
