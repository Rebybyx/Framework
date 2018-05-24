package com.zh.activiti.util;

public class AccessArryPostJson {

    private boolean success = true;

    private String msg = "";

    private Object controllers = null;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getControllers() {
        return controllers;
    }

    public void setControllers(Object controllers) {
        this.controllers = controllers;
    }
}
