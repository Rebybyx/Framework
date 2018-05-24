package com.zh.activiti.interceptor;

import com.alibaba.fastjson.JSON;
import com.zh.activiti.util.HttpEntity;
import com.zh.activiti.util.StaticUtil;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Created by Mrkin on 2016/11/11.
 */
public class MyExceptionHandler implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        try {
            httpServletResponse.setContentType("application/json; charset=UTF-8");
            //httpServletResponse.setCharacterEncoding("UTF-8");
            PrintWriter out = httpServletResponse.getWriter();
            HttpEntity json=new HttpEntity();
            json.setMessage("服务端异常");
            json.setCode(StaticUtil.CODE_CONTROLLER);
            json.setIsSuccess(false);
            out.print(JSON.toJSON(json));
            out.close();
        }catch (Exception ex){

        }


        return null;
    }
}
