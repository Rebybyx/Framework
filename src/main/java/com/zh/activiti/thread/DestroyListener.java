package com.zh.activiti.thread;

import com.zh.activiti.util.StaticUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class DestroyListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
        try {
            try {
                /*if (StaticUtil.serverHeart != null && StaticUtil.serverHeart.isAlive()) {
                    StaticUtil.serverHeart.close();
                    StaticUtil.serverHeart.stop();
                }
                System.out.println("IM心跳服务线程已注销");*/
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("关闭tomcat");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        // TODO Auto-generated method stub

    }

}