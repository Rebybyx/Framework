package com.zh.activiti.entity.activiti;

import com.zh.activiti.entity.DataEntity;

/**
 * Created by Rebybyx on 2017/4/20.
 */
public class StatusEntity extends DataEntity {

    protected String statusName;    // 汉字，由工作流返回获得，直接显示即可
    protected int statusProcess; // 流程进度百分比


    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public int getStatusProcess() {
        return statusProcess;
    }

    public void setStatusProcess(int statusProcess) {
        this.statusProcess = statusProcess;
    }
}
