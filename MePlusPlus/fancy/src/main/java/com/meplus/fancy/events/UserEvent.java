package com.meplus.fancy.events;

import com.meplus.fancy.model.entity.User;

/**
 * Created by dandanba on 3/3/16.
 */
public class UserEvent {
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private User user;

    public UserEvent(User user) {
        this.user = user;
    }

}
