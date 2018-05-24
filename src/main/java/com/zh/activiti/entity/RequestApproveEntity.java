package com.zh.activiti.entity;

/**
 * Created by GJ on 2017/6/15.
 */
public class RequestApproveEntity {

    private int process;
    private String id;
    private int condition;
    private String memo;

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
