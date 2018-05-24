package com.zh.activiti.mq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by Mrkin on 2017/3/24.
 */
public class Sub {
   public static void subs(){
       try {
       ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
       Connection connection = null;

           connection = factory.createConnection();

       connection.setClientID("wm5920");
       connection.start();

       Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
       Topic topic = session.createTopic("wm5920.topic");

       //持久订阅方式，不会漏掉信息
       TopicSubscriber subs=session.createDurableSubscriber(topic, "wm5920");
       subs.setMessageListener(new MessageListener() {
           public void onMessage(Message message) {
               TextMessage tm = (TextMessage) message;
               try {
                   System.out.println("Received message: " + tm.getText());
               } catch (JMSException e) {
                   e.printStackTrace();
               }
           }
       });
       } catch (JMSException e) {
           e.printStackTrace();
       }
   }
}
