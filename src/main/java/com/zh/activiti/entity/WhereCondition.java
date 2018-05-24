package com.zh.activiti.entity;

/**
 * Created by 海蛟 on 2018/3/12.
 */
public class WhereCondition {
    private String field;
    private WhereConditionMethod method;
    private String value;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public WhereConditionMethod getMethod() {
        return method;
    }

    public void setMethod(WhereConditionMethod method) {
        this.method = method;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
