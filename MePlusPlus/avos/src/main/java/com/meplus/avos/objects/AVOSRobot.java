package com.meplus.avos.objects;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;

import java.util.List;

@AVClassName("Robot")
public class AVOSRobot extends AVObject {
    // robot
    public final static String KEY_ROBOT_UUID = "robotUUID"; // String
    public final static String KEY_ROBOT_ID = "robotId"; // Int
    public final static String KEY_ROBOT_NAME = "robotName"; // String
    public final static String KEY_ROBOT_DESCRIPTION = "robotDescription"; // String

    private static final String TAG = AVOSRobot.class.getSimpleName();
    public static final Creator CREATOR = AVObjectCreator.instance;

    public String getUUId() {
        return getString(KEY_ROBOT_UUID);
    }

    public void setUUId(String uuId) {
        put(KEY_ROBOT_UUID, uuId);
    }

    public int getRobotId() {
        return getInt(KEY_ROBOT_ID);
    }

    public void setRobotId(int robotId) {
        put(KEY_ROBOT_ID, robotId);
    }

    public String getRobotName() {
        return getString(KEY_ROBOT_NAME);
    }

    public void setRobotName(String robotName) {
        put(KEY_ROBOT_NAME, robotName);
    }

    public String getRobotDescription() {
        return getString(KEY_ROBOT_DESCRIPTION);
    }

    public void setRobotDescription(String robotDescription) {
        put(KEY_ROBOT_DESCRIPTION, robotDescription);
    }

    public static void registerSubclass() {
        AVObject.registerSubclass(AVOSRobot.class);
    }

    public static List<AVOSRobot> queryRobotByUUID(String uuid) throws AVException {
        AVQuery<AVOSRobot> query = AVOSRobot.getQuery(AVOSRobot.class);
        query.whereEqualTo(KEY_ROBOT_UUID, uuid);
        List<AVOSRobot> list = query.find();
        return list;
    }
}