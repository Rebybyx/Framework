package com.zh.activiti.interceptor;

import com.zh.activiti.annotation.LogAnnotation;
//import com.zh.activiti.entity.system.Log;
//import com.zh.activiti.entity.system.User;
//import com.zh.activiti.service.system.LogServiceI;
//import com.zh.activiti.service.system.ResourceServiceI;
//import com.zh.activiti.service.system.UserServiceI;
import com.zh.activiti.util.MD5Util;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日志切面
 * Created by Mrkin on 2016/12/15.
 */
@Aspect
@Component
public class LogInfoAspect {
//    @Autowired
//    private UserServiceI userServiceI;
//    @Autowired
//    private LogServiceI logServiceI;
//    @Autowired
//    private ResourceServiceI resourceServiceI;


//    /**
//     * 如果开启controller是只能读取controller层的切面
//     */
//    //层切点
//    @Pointcut("@annotation(com.zh.LogAnnotation)")
//    public void controllerAspect() {
//        System.out.println("controller切入点");
//    }
//

    /**
     * 如果开启service  能读取controller和service层的切面
     */
    @Pointcut("@annotation(com.zh.activiti.annotation.LogAnnotation)")
    public void serviceAspect() {
        System.out.println("service切入点");
    }

    //    @AfterReturning(pointcut = "controllerAspect()")
//    public void docontrollerAfter(JoinPoint joinPoint) {
//        System.out.println("controller 执行");
//        if (isAnnotaion(joinPoint))
//            save(joinPoint);
//
//    }
    @AfterReturning(pointcut = "serviceAspect()")
    public void doServiceAfter(JoinPoint joinPoint) {
//        System.out.println("service执行");
        //if(ConfigUtil.get("isLogInfo").equals("true")){
        if (isAnnotaion(joinPoint))
            save(joinPoint);
        //}

    }

    public void save(JoinPoint joinPoint) {
        /*HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String userId = (String) request.getAttribute("userId");
        String url = request.getRequestURI();
        String ip = request.getRemoteAddr();
        //获取用户请求方法的参数并序列化为JSON格式字符串
//        String params = "";
//        if (joinPoint.getArgs() !=  null && joinPoint.getArgs().length > 0) {
//            for ( int i = 0; i < joinPoint.getArgs().length; i++) {
//                params += JSON.toJSONString(joinPoint.getArgs()[i]) + ";";
//            }
//        }
        try {
               *//*==========数据库日志=========*//*
            Log log = new Log();
            log.setCreateTime(new Date());
            log.setUrl(url);
            log.setiIp(ip);
            log.setiContent(getContent(joinPoint));
            log.setiOperation(getOperation(joinPoint));
            String sId = resourceServiceI.getResourceIdByUrl(url);
            if (sId != null && !"".equals(sId)) {
                log.setsId(sId);
            } else if (url.contains("login.do")) {
                log.setsId("");
                log.setiContent("登录成功");
            } else {
                log.setiContent("资源不存在");
            }
//            log.setType(getType(joinPoint));
            if (userId == null) {
                Map<String, Object> map = new HashMap<>();
                map.put("uName_EQ", request.getParameter("userLoginName"));
                String password = request.getParameter("userPw");
                User tbUser = userServiceI.getByParam(map);
                if (tbUser != null && password != null && MD5Util.md5(password + tbUser.getuCredentialsSalt()).equals(tbUser.getuPassword())) {
                    log.setuId(tbUser.getId());
                } else {
                    log.setiContent("登录失败");
                }
            } else {
                log.setuId(userId);
            }
            //保存数据库
            if (log.getuId() != null) {
                logServiceI.save(log);
            }
        } catch (Exception ex) {
            //记录本地异常日志
            ex.printStackTrace();
        }*/
    }


    /**
     * 获取注解中操作内容
     *
     * @param joinPoint 切点
     * @return 内容
     * @throws Exception
     */
    public static String getContent(JoinPoint joinPoint)
            throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        String content = "";
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    LogAnnotation annotation = method.getAnnotation(LogAnnotation.class);
                    if (annotation != null) {
                        content = annotation.content();
                    }
                    System.out.println("class----" + targetName + "---------------method:-------" + methodName);
                    break;
                }
            }
        }
        return content;
    }


    /**
     * 获取注解中类型
     *
     * @param joinPoint 切点
     * @return 类型
     * @throws Exception
     */
    public static String getType(JoinPoint joinPoint)
            throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        LogAnnotation.Type operation = LogAnnotation.Type.web;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    LogAnnotation annotation = method.getAnnotation(LogAnnotation.class);
                    if (annotation != null) {
                        operation = annotation.type();
                    }
                    break;
                }
            }
        }
        return operation.toString();
    }

    /**
     * 获取注解中内容
     *
     * @param joinPoint 切点
     * @return 内容
     * @throws Exception
     */
    public static String getOperation(JoinPoint joinPoint)
            throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        String operation = "";
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    LogAnnotation annotation = method.getAnnotation(LogAnnotation.class);
                    if (annotation != null) {
                        operation = annotation.operation();
                    }
                    break;
                }
            }
        }
        return operation;
    }


    public boolean isAnnotaion(JoinPoint joinPoint) {
        boolean result = false;
        try {
            String targetName = joinPoint.getTarget().getClass().getName();
            Class targetClass = Class.forName(targetName);
            Method[] methods = targetClass.getMethods();
            for (Method method : methods) {
                LogAnnotation annotation = method.getAnnotation(LogAnnotation.class);
                if (annotation != null) {
                    result = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}

