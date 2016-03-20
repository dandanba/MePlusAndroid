package com.meplus.robot.events;

/**
 * Created by dandanba on 3/11/16.
 */
public class ModifyEvent<T> extends Event {
    public ModifyEvent(String status) {
        super(status);
    }

    public ModifyEvent(String status, T data) {
        this(status);
        setData(data);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private T data;
}
