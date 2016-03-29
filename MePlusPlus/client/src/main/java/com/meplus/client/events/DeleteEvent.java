package com.meplus.client.events;

/**
 * Created by dandanba on 3/11/16.
 * 删除
 */
public class DeleteEvent<T> extends Event {
    public DeleteEvent(String status) {
        super(status);
    }

    public DeleteEvent(String status, T data) {
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
