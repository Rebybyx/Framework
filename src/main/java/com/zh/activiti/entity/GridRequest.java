package com.zh.activiti.entity;

import java.util.List;

/**
 * Grid请求数据结构，作用从前台接收请求参数
 * <p>
 * Created by Rebybyx on 2018/3/13 22:25.
 */
public class GridRequest {

    private int pageIndex;
    private int pageItemCount;
    private List<WhereCondition> whereConditions;
    private List<OrderCondition> orderConditions;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageItemCount() {
        return pageItemCount;
    }

    public void setPageItemCount(int pageItemCount) {
        this.pageItemCount = pageItemCount;
    }

    public List<WhereCondition> getWhereConditions() {
        return whereConditions;
    }

    public void setWhereConditions(List<WhereCondition> whereConditions) {
        this.whereConditions = whereConditions;
    }

    public List<OrderCondition> getOrderConditions() {
        return orderConditions;
    }

    public void setOrderConditions(List<OrderCondition> orderConditions) {
        this.orderConditions = orderConditions;
    }

}
