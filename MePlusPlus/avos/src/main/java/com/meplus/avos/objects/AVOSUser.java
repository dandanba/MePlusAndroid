package com.meplus.avos.objects;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;

import java.util.List;

/**
 * Created by dandanba on 4/3/16.
 */
@AVClassName("User")
public class AVOSUser extends AVUser {
    public static final Creator CREATOR = AVObjectCreator.instance;

    private final static String KEY_USER_UUID = "userUUID"; // String
    private final static String KEY_USER_ID = "userIntId"; // Int
    private static final String RELATION_ROBOTS = "robots";

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

    public void addRobot(AVOSRobot avosRobot) throws AVException {
        AVRelation<AVOSRobot> relation = getRelation(AVOSUser.RELATION_ROBOTS);
        relation.add(avosRobot);
        save();
    }

    public List<AVOSRobot> queryRobot(int limit) throws AVException {
        final AVRelation<AVOSRobot> relation = getRelation(AVOSUser.RELATION_ROBOTS);
        final AVQuery<AVOSRobot> query = relation.getQuery();
        query.setLimit(limit);
        final List<AVOSRobot> list = query.find();
        return list;
    }
}
