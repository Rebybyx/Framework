package com.zh.activiti.util;

import java.io.Serializable;

public class ScreenReceiveData implements Serializable {
    private String devNo;
    private String address;
    private String messageType;
    private String page;
    private String pageCRC32;
    private String pageLastTime;
    private String roadSectionCRC32;
    private String roadSectionLastTime;
    private String stayTime;
    private String displayEffect;

    public String getDevNo() {
        return devNo;
    }

    public void setDevNo(String devNo) {
        this.devNo = devNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPageCRC32() {
        return pageCRC32;
    }

    public void setPageCRC32(String pageCRC32) {
        this.pageCRC32 = pageCRC32;
    }

    public String getPageLastTime() {
        return pageLastTime;
    }

    public void setPageLastTime(String pageLastTime) {
        this.pageLastTime = pageLastTime;
    }

    public String getRoadSectionCRC32() {
        return roadSectionCRC32;
    }

    public void setRoadSectionCRC32(String roadSectionCRC32) {
        this.roadSectionCRC32 = roadSectionCRC32;
    }

    public String getRoadSectionLastTime() {
        return roadSectionLastTime;
    }

    public void setRoadSectionLastTime(String roadSectionLastTime) {
        this.roadSectionLastTime = roadSectionLastTime;
    }

    public String getStayTime() {
        return stayTime;
    }

    public void setStayTime(String stayTime) {
        this.stayTime = stayTime;
    }

    public String getDisplayEffect() {
        return displayEffect;
    }

    public void setDisplayEffect(String displayEffect) {
        this.displayEffect = displayEffect;
    }
}
