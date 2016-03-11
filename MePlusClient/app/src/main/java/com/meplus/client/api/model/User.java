package com.meplus.client.api.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVUser;

@AVClassName("User")
public class User extends AVUser {
    public static final Creator CREATOR = AVObjectCreator.instance;

    public String getRobotId() {
        return getString("robotId");
    }

    public void setRobotId(String birthday) {
        put("robotId", birthday);
    }


}
