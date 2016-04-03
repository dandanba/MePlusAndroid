package com.meplus.events;

/**
 * Created by dandanba on 3/11/16.
 * 增加和修改
 */
public class SaveEvent<T> extends BaseEvent {
    public SaveEvent(String status) {
        super(status);
    }

    public SaveEvent(String status, T data) {
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
