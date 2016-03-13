package com.meplus.client.api.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

@AVClassName("Robot")
public class Robot extends AVObject {
    public static final Creator CREATOR = AVObjectCreator.instance;

    public final static String KEY_ROBOT_ID = "robotId";
    public final static String KEY_ROBOT_NAME = "robotName";

    public String getRobotId() {
        return getString(KEY_ROBOT_ID);
    }

    public void setRobotId(String robotId) {
        put(KEY_ROBOT_ID, robotId);
    }


    public String getRobotName() {
        return getString(KEY_ROBOT_NAME);
    }

    public void setRobotName(String robotName) {
        put(KEY_ROBOT_NAME, robotName);
    }
}
