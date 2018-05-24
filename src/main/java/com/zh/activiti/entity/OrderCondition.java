package com.zh.activiti.entity;

/**
 * Created by 海蛟 on 2018/3/12.
 */
public class OrderCondition {
    private String field;
    private OrderConditionMethod method;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public OrderConditionMethod getMethod() {
        return method;
    }

    public void setMethod(OrderConditionMethod method) {
        this.method = method;
    }
}
