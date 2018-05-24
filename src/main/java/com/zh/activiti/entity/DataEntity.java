package com.zh.activiti.entity;

import com.zh.activiti.annotation.Id;
import com.zh.activiti.annotation.LogicDelete;
import com.zh.activiti.annotation.NoMapping;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Mrkin on 2016/12/7.
 */
public class DataEntity {
    @Id
    protected String id;
    protected String createUserId;//创建用户id
    @DateTimeFormat
    protected Date createTime;//创建时间
    @NoMapping
    protected boolean isLeaf;//是否是叶子节点
    @LogicDelete
    protected int deleteStatus = 1;//标志位1:有效，0无效 默认1

    public String getId() {
        if (StringUtils.isBlank(this.id)) {
            this.id = UUID.randomUUID().toString();
        }
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(int deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

}
