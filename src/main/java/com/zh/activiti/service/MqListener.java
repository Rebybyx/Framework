package com.zh.activiti.service;

import com.alibaba.fastjson.JSON;
import com.zh.activiti.entity.redisentity.TimeEntity;
//import com.zh.activiti.entity.system.OnlineTime;
//import com.zh.activiti.service.system.LogServiceI;
//import com.zh.activiti.service.system.OnlineTimeServiceI;
//import com.zh.activiti.thread.ServerHeart;
import com.zh.activiti.util.ConfigUtil;
import com.zh.activiti.util.RedisHelper;
import com.zh.activiti.util.StaticUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Mrkin on 2017/3/3.
 */
@Component
public class MqListener implements InitializingBean, ServletContextAware {

    public static final String clientId = UUID.randomUUID().toString(); // 客户端id
    public static final String jiFangWangClientId = UUID.randomUUID().toString(); // 客户端id
    public static final String lightClientId = UUID.randomUUID().toString(); // 客户端id
    public static final String BROKER_URL = ConfigUtil.get("mq.sendUrl");// mqserver地址

//    @Autowired
//    private LogServiceI logServiceI;
//    @Autowired
//    private OnlineTimeServiceI onlineTimeServiceI;

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        //开启报警线程
        new Thread(() -> {
            initOnlineTime();//在线时长
            initIMHeartServer();
        }).start();

    }

    /**
     * 初始化心跳服务
     */
    void initIMHeartServer() {
        /*try {
            StaticUtil.serverHeart = new ServerHeart();
            StaticUtil.serverHeart.start();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


    void initOnlineTime() {
        /*new Thread(() -> {
            while (true) {
                try {

                    Set<String> list = RedisHelper.getKeys("*" + StaticUtil.TIME_KEY_HEAD);
                    for (String key : list) {
                        String json = RedisHelper.getString(key);
                        if (json == null) {
                            continue;
                        }
                        TimeEntity timeEntity = JSON.parseObject(json, TimeEntity.class);
                        if (timeEntity == null) {
                            continue;
                        }
                        Long loginTime = timeEntity.getLoginTime();
                        Long operateTime = timeEntity.getOperateTime();
                        //单位秒
                        Long onlineTime = (operateTime - loginTime) / 1000;
                        Long currentTime = System.currentTimeMillis();
                        if (currentTime > (operateTime + 1800000)) {
                            *//*Log logInfo = new Log();
                            logInfo.setiOperation("在线时长");
                            logInfo.setiContent(onlineTime.toString());
                            logInfo.setuId(timeEntity.getUserId());
                            java.util.Date operateDate = new Date(operateTime);
                            logInfo.setCreateTime(operateDate);
                            logServiceI.save(logInfo);*//*

                            OnlineTime tbOnlineTime = onlineTimeServiceI.getById(timeEntity.getUserId());
                            if (tbOnlineTime != null) {
                                BigDecimal initOnlineTime = tbOnlineTime.getOnlineTime();
                                BigDecimal newOnlineTime = initOnlineTime.add(new BigDecimal(onlineTime));
                                tbOnlineTime.setOnlineTime(newOnlineTime);
                                onlineTimeServiceI.update(tbOnlineTime);
                            } else {
                                OnlineTime createTbOnlineTime = new OnlineTime();
                                BigDecimal newOnlineTime = new BigDecimal(onlineTime);
                                createTbOnlineTime.setuId(timeEntity.getUserId());
                                createTbOnlineTime.setOnlineTime(newOnlineTime);
                                onlineTimeServiceI.save(createTbOnlineTime);
                            }

                            try {
                                RedisHelper.delAllObject(timeEntity.getUserId() + StaticUtil.TIME_KEY_HEAD);
                                RedisHelper.delAllObject(timeEntity.getUserId() + StaticUtil.ROLE_KEY_HEAD);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Thread.sleep(60000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();*/

    }

}
