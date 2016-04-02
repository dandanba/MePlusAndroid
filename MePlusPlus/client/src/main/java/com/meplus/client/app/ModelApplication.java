package com.meplus.client.app;

import com.meplus.client.api.model.Robot;
import com.meplus.client.api.model.User;

import hugo.weaving.DebugLog;

/**
 * Created by dandanba on 3/1/16.
 */
public class ModelApplication extends BaseApplication {

    private Robot mRobot;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @DebugLog
    public Robot getRobot() {
        return mRobot;
    }

    @DebugLog
    public void setRobot(Robot robot) {
        mRobot = robot;
    }

    public void logOut() {
        User.logOut();
        setRobot(null);
    }
}
