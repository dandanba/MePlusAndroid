package com.meplus.client.api.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVUser;

import java.util.ArrayList;
import java.util.List;

@AVClassName("User")
public class User extends AVUser {
    public static final Creator CREATOR = AVObjectCreator.instance;
    public static final String RELATION_ROBOTS = "robots";
    public final static String KEY_USER_ID = "userId";

    public String getUserId() {
        return getString(KEY_USER_ID);
    }

    public void setUserId(String userId) {
        put(KEY_USER_ID, userId);
    }


    public List<Robot> getRobotList() {
        return robotList;
    }

    public void setRobotList(List<Robot> robotList) {
        this.robotList = robotList;
    }

    private List<Robot> robotList = new ArrayList<>();
}
