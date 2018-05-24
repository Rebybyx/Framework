package com.zh.activiti.entity.redisentity;

import java.io.Serializable;

/** 存储登录时间，token有效时间，以及操作时间的实体
 * @author GJ
 */
public class TimeEntity implements Serializable {
    private static final long serialVersionUID = 8748775028486761946L;
    private String userId;
    private Long loginTime;
    private Long effectiveTime;
    private Long operateTime;

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public Long getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Long effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
