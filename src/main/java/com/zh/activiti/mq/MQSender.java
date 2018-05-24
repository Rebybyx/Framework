package com.zh.activiti.mq;


import com.zh.activiti.util.ConfigUtil;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * @author GJ
 */
public class MQSender {

    // 发送次数
    //public static final int SEND_NUM = 5;
    // tcp 地址
    public static final String BROKER_URL = ConfigUtil.get("mq.sendUrl");

    public static final String CLIENTID = "CLIENT_ID";

    public static final String DESTINATION = "MyTopic_03";// "JavaTest";

    public static final String USERNAME = "admin";

    public static final String PASSWORD = "password";

    public static void sendMessage(TopicSession session, TopicPublisher publisher, String json) throws Exception {
        TextMessage objMsg = session.createTextMessage();
        objMsg.setText(json);
        publisher.send(objMsg);
    }

    /**
     * @param myTopic   主题
     * @param json      携带的json数据
//     * @param clientIds 接收者的用户id
     * @throws Exception
     */
    public static void runAndSend(String myTopic, String json) throws Exception {

        TopicConnection connection = null;
        TopicSession session = null;
        try {
            // 创建链接工厂
            TopicConnectionFactory factory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, BROKER_URL);
            // 通过工厂创建一个连接
            connection = factory.createTopicConnection();
            // 启动连接
            connection.start();
            // 创建一个session会话
            session = connection.createTopicSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
            // 创建一个消息队列
            Topic topic = session.createTopic(myTopic);
            // 创建消息发送者
            TopicPublisher publisher = session.createPublisher(topic);
            // 设置持久化模式
            publisher.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            sendMessage(session, publisher, json);
            // 提交会话
            session.commit();

        } catch (Exception e) {
            throw e;
        } finally {
            // 关闭释放资源
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }




    /** 创建主题
     * @param topicName
     * @return
     */
    public static int createTopic(String topicName) {

        TopicConnection connection = null;
        TopicSession session = null;
        MessageProducer producer;
        try {
            // 创建链接工厂
            TopicConnectionFactory factory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, BROKER_URL);
            // 通过工厂创建一个连接
            connection = factory.createTopicConnection();
            connection.start();
            // 获取操作连接
            session = connection.createTopicSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(topicName);
            // 创建消息发送者
            TopicPublisher publisher = session.createPublisher(topic);
            session.close();
        } catch (Exception e) {
            return 0;
        } finally {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 1;
    }
}
