package com.zh.activiti.entity;

/**
 * grid实体数据类，用作向前台返回数据
 * Created by Mrkin on 2016/12/9.
 */
public class Grid {
    private int pageIndex;
    private long pageItemCount;
    private long totalCount;
    private Object data;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public long getPageItemCount() {
        return pageItemCount;
    }

    public void setPageItemCount(long pageItemCount) {
        this.pageItemCount = pageItemCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
