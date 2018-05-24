package com.zh.activiti.util;

import java.util.Date;

public class AccessReceive {

   private String  controllerSn;
    private Date time;
    private String  relayState; //继电器状态 0表示门上锁, 1表示门开锁
    private String  electromagnetState;//门磁状态 门关闭时值为0, 门打开时为1
    private String rawData;

    public String getControllerSn() {
        return controllerSn;
    }

    public void setControllerSn(String controllerSn) {
        this.controllerSn = controllerSn;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getRelayState() {
        return relayState;
    }

    public void setRelayState(String relayState) {
        this.relayState = relayState;
    }

    public String getElectromagnetState() {
        return electromagnetState;
    }

    public void setElectromagnetState(String electromagnetState) {
        this.electromagnetState = electromagnetState;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }
}
