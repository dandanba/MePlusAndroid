package com.meplus.client.events;

import java.util.List;

/**
 * Created by dandanba on 3/11/16.
 */
public class QueryEvent<T> extends Event {
    public QueryEvent(String status) {
        super(status);
    }

    public QueryEvent(String status, List<T> list) {
        this(status);
        setList(list);
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    private List<T> list;
}
