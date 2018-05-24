package com.zh.activiti.entity;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by Mrkin on 2017/3/6.
 */
public class TimeLine {
    private String node;//日期或者普通字符串
    private List<Object> obj= Lists.newArrayList();

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }


    public List<Object> getObj() {
        return obj;
    }

    public void setObj(List<Object> obj) {
        this.obj = obj;
    }
}
