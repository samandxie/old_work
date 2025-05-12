package com.yu.exception;

public class CustomizeException extends RuntimeException{
    private int code;
    private String msg;

    public CustomizeException(int code, String msg) {
        super(msg);
        this.code=code;
        this.msg=msg;
    }
    public CustomizeException(String msg) {
        super(msg);
        this.msg=msg;
    }
}
