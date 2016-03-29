package com.meplus.client.events;

/**
 * Created by dandanba on 3/11/16.
 */
public class ErrorEvent extends Event {
    public ErrorEvent(String status) {
        super(status);
    }

    public ErrorEvent(String status, Throwable throwable) {
        this(status);
        setThrowable(throwable);
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    private Throwable throwable;
}
