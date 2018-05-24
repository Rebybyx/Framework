package com.zh.activiti.util;

/**
 * JSON模型
 * <p>
 * 用户后台向前台返回的JSON对象
 *
 * @author 陈晓亮
 */
public class HttpEntity implements java.io.Serializable {

    private static final long serialVersionUID = -5405913030191680871L;

    private boolean isSuccess = true;

    private String message = "";

    private Object data = null;

    private String token;
    private int code = StaticUtil.CODE_SUCCESSFUL;

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}