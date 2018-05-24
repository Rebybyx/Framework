package com.zh.activiti.util;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
//import com.zh.activiti.entity.mongo.DeviceAll;
import org.bson.Document;

/**
 * Created by Rebybyx on 2018/1/31 15:27.
 */
public class MongoOptUtil {

  /*  public static void insert_sun (Sun sun) throws SQLException, IllegalAccessException, NoSuchFieldException, InvocationTargetException {
        MongoDatabase db = MongoDBConn.getDatabase();
        MongoCollection<Document> sunCollection = db.getCollection("c_sun");

        Document doc = BsonUtil.toBson(sun);
        sunCollection.insertOne(doc);
    }*/
     /*public static Document getInfo(String devNo){
         try {
             MongoDatabase db = MongoDBConn.getDatabase();
             MongoCollection<Document> collection = db.getCollection("tb_eqp_info");
             FindIterable<Document> iter = collection.find(new Document("devNo",devNo)).sort(Sorts.orderBy(Sorts.descending("collectTime"))).skip(0).limit(1);
             DeviceAll deviceAll=new DeviceAll();
             //BsonUtil.setFieldValue(deviceAll,iter.first());
             return iter.first();
         }catch (Exception e){
             e.printStackTrace();
             return null;
         }

     }*/
}
