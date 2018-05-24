package com.zh.activiti.util;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.sql.SQLException;

/**
 * Connection MongoDB Driver
 * <p>
 * Created by Rebybyx on 2018/1/26 10:44.
 */
public class MongoDBConn {

    private static String ip = ConfigUtil.get("mongo.ip");
    private static int port = Integer.valueOf(ConfigUtil.get("mongo.port"));
    private static String databaseName = ConfigUtil.get("mongo.databaseName");

    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;

    public static MongoDatabase getDatabase() throws SQLException {
        if (mongoDatabase != null) {
            return mongoDatabase;
        }
        create();
        return mongoDatabase;
    }

    private static void create() {
        try {
            // 连接到 mongodb 服务
            mongoClient = new MongoClient(ip, port);

            // 连接到数据库
            mongoDatabase = mongoClient.getDatabase(databaseName);
           // LogQueueUtils.info("Connect to database successfully. Host:" + ip + ":" + port + ", Database:" + databaseName + ".");

        } catch (Exception e) {
           // LogQueueUtils.error(null, e);
            if (mongoClient != null) {
                mongoClient.close();
            }
        }
    }

    public static void refresh() throws SQLException {
        if (mongoClient != null) {
            mongoClient.close();
        }
        create();
    }

}
