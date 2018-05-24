package com.zh.activiti.entity;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by Mrkin on 2017/1/16.
 */
public class Select {
    private String id;
    private String text;

    private Map<String,Object> map= Maps.newHashMap();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
