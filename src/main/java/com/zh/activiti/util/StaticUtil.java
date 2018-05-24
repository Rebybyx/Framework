package com.zh.activiti.util;

import com.google.common.collect.Maps;

import java.util.Date;
import java.util.Map;

/**
 * 静态存储类
 * Created by Mrkin on 2016/11/8.
 */
public class StaticUtil {
    public static boolean locked = false;
    public static int count = 0;

    public static String PROJECT_NAME = "projectName";
    public static String ALARM_HEADER = "A";
    public static String DESC = "desc";//逆序

    public static String ASC = "asc";//正序

    public static String DEVCLASSID = "DeviceClass";
    public static String EQPUNITID = "EqpUnit";
    /**
     * 记录当天时间
     */
    public static Date date = new Date();
    public static String SESSION = "session";//session
    public static String FILEUSERID = "FileTreeId";
    public static String SQL_SPLIT = "_";//SQL语句中的分离

    public static int CODE_SUCCESSFUL = 200;//成功

    public static String RESETPASS = "123456"; //默认密码
    /**
     * CONTROLLER异常
     */
    public static int CODE_CONTROLLER = 201;

    /**
     * token 错误
     */
    public static int CODE_ERROR_TOKEN = 202;

    /**
     * 操作失败
     */
    public static int CODE_ERROR_FAIL = 203;
    /**
     * 没有方法权限
     */
    public static int CODE_ERROR_PERMISSION = 203;
    public static String LOGIN_TYPE_WEB = "web"; //登录类型web
    public static String LOGIN_TYPE_APP = "app";//登录类型app
    public static int RESOURCE_TYPE_MENU = 0;//菜单
    public static int RESOURCE_TYPE_FUNCTION = 1;//功能

    public static final String TIME_FLAG_SECOND = "s";
    /**
     * redis 数据库 key时间后缀
     */
    public static String TIME_KEY_HEAD = "time";
    /**
     * redis 数据库 key角色授权后缀
     */
    public static String ROLE_KEY_HEAD = "role";
}
