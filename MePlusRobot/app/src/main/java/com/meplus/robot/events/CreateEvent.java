package com.meplus.robot.events;

/**
 * Created by dandanba on 3/11/16.
 */
public class CreateEvent<T> extends Event {
    public CreateEvent(String status) {
        super(status);
    }

    public CreateEvent(String status, T data) {
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
