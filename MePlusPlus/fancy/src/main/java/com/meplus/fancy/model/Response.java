package com.meplus.fancy.model;

/**
 * Created by dandanba on 3/9/16.
 */
public class Response<T> {
    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public T getResult() {
        return Result;
    }

    public void setResult(T result) {
        Result = result;
    }

    private String Code;
    private String Message;
    private T Result;
}
