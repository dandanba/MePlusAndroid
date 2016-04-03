package com.meplus.avos.objects;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;

/**
 * Created by dandanba on 4/3/16.
 */
@AVClassName("User")
public class AVOSUser extends AVUser {
    public static final Creator CREATOR = AVObjectCreator.instance;
    // user
    public final static String KEY_USER_UUID = "userUUID"; // String
    public final static String KEY_USER_ID = "userIntId"; // Int
    public static final String RELATION_ROBOTS = "robots";

    public int getUserId() {
        return getInt(KEY_USER_ID);
    }

    public void setUserId(int userId) {
        put(KEY_USER_ID, userId);
    }

    public String getUUId() {
        return getString(KEY_USER_UUID);
    }

    public void setUUId(String uuId) {
        put(KEY_USER_UUID, uuId);
    }

    public static void registerSubclass() {
        AVObject.registerSubclass(AVOSRobot.class);
    }
}
