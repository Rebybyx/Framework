package com.zh.activiti.util;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.zh.activiti.annotation.mongo.Column;
import com.zh.activiti.annotation.mongo.NotColumn;
import org.bson.Document;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Java bean and MongoDB document convert each other.
 * <p>
 * Created by Rebybyx on 2018/1/26 11:49.
 */
public class BsonUtil {
    public static <T> List<T> toBeans(List<Document> documents, Class<T> clazz)
            throws IllegalArgumentException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        List<T> list = new ArrayList<T>();
        for (int i = 0; null != documents && i < documents.size(); i++) {
            list.add(toBean(documents.get(i), clazz));
        }
        return list;
    }

    /*
     * 将Bson 转化为对象
     *
     * @param:Bson文档
     *
     * @param:类pojo
     *
     * @param:返回对象
     */
    public static <T> T toBean(Document document, Class<T> clazz)
            throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        T obj = clazz.newInstance();// 声明一个对象
        Field[] fields = clazz.getDeclaredFields();// 获取所有属性
        Method[] methods = clazz.getMethods();// 获取所有的方法
        /*
         * 查找所有的属性，并通过属性名和数据库字段名通过相等映射
         */
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i].getName();
            Column column = fields[i].getAnnotation(Column.class);
            Object bson = null;
            if (null != column && null != column.name()) {
                bson = document.get(column.name());
            } else if ("id".equals(fieldName)) {
                bson = document.get("_id");
            } else {
                bson = document.get(fieldName);
            }
            if (null == bson) {
                continue;
            } else if (bson instanceof Document) {// 如果字段是文档了递归调用
                bson = toBean((Document) bson, fields[i].getType());
            } else if (bson instanceof MongoCollection) {// 如果字段是文档集了调用colTOList方法

                bson = colToList(bson, fields[i]);
            }
            for (int j = 0; j < methods.length; j++) {// 为对象赋值
                String metdName = methods[j].getName();
                if (equalFieldAndSet(fieldName, metdName)) {
                    methods[j].invoke(obj, bson);
                    break;
                }
            }
        }
        return obj;
    }

    public static List<Document> toBsons(List<Object> objs)
            throws IllegalArgumentException, SecurityException,
            IllegalAccessException, InvocationTargetException,
            NoSuchFieldException {
        List<Document> documents = new ArrayList<Document>();
        for (int i = 0; null != objs && i < objs.size(); i++) {
            documents.add(toBson(objs.get(i)));
        }
        return documents;
    }

    /*
     * 将对象转化为Bson文档
     *
     * @param:对象
     *
     * @param:类型
     *
     * @return:文档
     */
    public static Document toBson(Object obj) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException,
            SecurityException, NoSuchFieldException {
        if (null == obj) {
            return null;
        }
        Class<? extends Object> clazz = obj.getClass();
        Document document = new Document();
        Method[] methods = clazz.getDeclaredMethods();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; null != fields && i < fields.length; i++) {
            Column column = fields[i].getAnnotation(Column.class);// 获取列注解内容
            NotColumn notColumn = fields[i].getAnnotation(NotColumn.class);// 获取否列
            String key = null;// 对应的文档键值
            if (null != column && null != column.name()) {// 存在列映射取值
                key = column.name();
            } else if (null != notColumn) {// 不是列的情况
                continue;
            } else {
                key = fields[i].getName();// 默认情况通过属性名映射
                if ("id".equals(key)) {// 替换id为_id
                    key = "_id";
                }
            }
            String fieldName = fields[i].getName();
            /*
             * 获取对象属性值并映射到Document中
             */
            for (int j = 0; null != methods && j < methods.length; j++) {
                String methdName = methods[j].getName();
                if (null != fieldName && equalFieldAndGet(fieldName, methdName)) {
                    Object val = methods[j].invoke(obj);// 得到值
                    if (null == val) {
                        continue;
                    }
                    if (isJavaClass(methods[j].getReturnType())) {
                        if (methods[j].getReturnType().getName()
                                .equals("java.util.List")) {// 列表处理
                            @SuppressWarnings("unchecked")
                            List<Object> list = (List<Object>) val;
                            List<Document> documents = new ArrayList<Document>();
                            for (Object obj1 : list) {
                                documents.add(toBson(obj1));
                            }
                            document.append(key, documents);
                        } else {// 其它对象处理，基本类型
                            document.append(key, val);
                        }
                    } else {// 自定义类型
                        document.append(key, toBson(val));
                    }
                }
            }
        }
        return document;
    }

    /*
     * 是否是自定义类型】
     *
     * false:是自定义
     */
    private static boolean isJavaClass(Class<?> clz) {
        return clz != null && clz.getClassLoader() == null;
    }

    /*
     * 将文档集转化为列表
     *
     * @param:文档集
     *
     * @param:属性类型
     *
     * @return:返回列表
     */
    private static List<Object> colToList(Object bson, Field field)
            throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        ParameterizedType pt = (ParameterizedType) field.getGenericType();// 获取列表的类型
        List<Object> objs = new ArrayList<Object>();
        @SuppressWarnings("unchecked")
        MongoCollection<Document> cols = (MongoCollection<Document>) bson;
        MongoCursor<Document> cursor = cols.find().iterator();
        while (cursor.hasNext()) {
            Document child = cursor.next();
            @SuppressWarnings("rawtypes")
            Class clz = (Class) pt.getActualTypeArguments()[0];// 获取元素类型
            @SuppressWarnings("unchecked")
            Object obj = toBean(child, clz);
            System.out.println(child);
            objs.add(obj);

        }
        return objs;
    }

    /*
     * 比较setter方法和属性相等
     */
    private static boolean equalFieldAndSet(String field, String name) {
        if (name.toLowerCase().matches("set" + field.toLowerCase())) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * 比较getter方法和属性相等
     */
    private static boolean equalFieldAndGet(String field, String name) {
        if (name.toLowerCase().matches("get" + field.toLowerCase())) {
            return true;
        } else {
            return false;
        }
    }

    public static Object setFieldValue(Object obj, Document doc)
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, InstantiationException {
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Method[] methods = clazz.getDeclaredMethods();
        for (Field field : fields) {
            String name = field.getName();
            String setName = parseSetName(name);
            if (!checkSetMethod(methods, setName)) {
                continue;
            }
            String fieldType = field.getType().getName();
            Method fieldSetMet = clazz.getDeclaredMethod(setName,
                    field.getType());
            if ("java.lang.String".equalsIgnoreCase(fieldType)) {
                String value = doc.getString(name);
                if (value==null||"".equals(value)) {
                    continue;
                }
                fieldSetMet.invoke(obj, value);
            } else if ("java.lang.Integer".equalsIgnoreCase(fieldType)
                    || "int".equalsIgnoreCase(fieldType)) {
                Integer value = doc.getInteger(name);
                if (value==null) {
                    continue;
                }
                fieldSetMet.invoke(obj, value);
            } else if ("java.lang.Long".equalsIgnoreCase(fieldType)
                    || "long".equalsIgnoreCase(fieldType)) {

                Long value = doc.getLong(name);
                if (value==null) {
                    continue;
                }
                fieldSetMet.invoke(obj, value);
            } else if ("java.lang.Double".equalsIgnoreCase(fieldType)
                    || "long".equalsIgnoreCase(fieldType)) {
                Double value = doc.getDouble(name);
                if (value==null) {
                    continue;
                }
                fieldSetMet.invoke(obj, value);
            } else if ("java.lang.Boolean".equalsIgnoreCase(fieldType)
                    || "boolean".equalsIgnoreCase(fieldType)) {
                Boolean value = doc.getBoolean(name);
                if (value==null) {
                    continue;
                }
                fieldSetMet.invoke(obj, value);
            } else if ("java.sql.Timestamp".equalsIgnoreCase(fieldType)) {
                Date date = doc.getDate(name);
                if(date==null){
                    continue;
                }
                fieldSetMet.invoke(obj, new java.sql.Timestamp(date.getTime()));
            } else if ("java.sql.Date".equalsIgnoreCase(fieldType)) {
                Date date = doc.getDate(name);
                if(date==null){
                    continue;
                }
                fieldSetMet.invoke(obj, new java.sql.Date(date.getTime()));
            } else if ("java.util.Date".equalsIgnoreCase(fieldType)) {
                Date date = doc.getDate(name);
                if(date==null){
                    continue;
                }
                fieldSetMet.invoke(obj, date);
            } else if ("java.util.List".equalsIgnoreCase(fieldType)) {
                List<Document> docs = (List<Document>) doc.get(name);
                if (docs==null||docs.size()==0) {
                    continue;
                }
                Class fieldClazz = field.getType(); // 得到field的class及类型全路径+
                Class genericClazz = null;
                if (fieldClazz.isAssignableFrom(List.class)) //
                {
                    Type fc = field.getGenericType(); // 关键的地方，如果是List类型，得到其Generic的类型


                    if (fc instanceof ParameterizedType) //如果是泛型参数的类型
                    {
                        ParameterizedType pt = (ParameterizedType) fc;


                        genericClazz = (Class) pt.getActualTypeArguments()[0]; // 得到泛型里的class类型对象。


                    }
                }
                List list = new ArrayList<>();
                for (Document document : docs) {
                    Object genericobj = genericClazz.newInstance();
                    Object object = setFieldValue(genericobj, document);
                    list.add(object);
                }
                fieldSetMet.invoke(obj, list);
            }else {
                String docName = field.getType().getSimpleName();
                Document document = (Document)doc.get(docName);
                if (document==null) {
                    continue;
                }
                Class fieldClazz = field.getType();
                Object object = fieldClazz.newInstance();
                object = setFieldValue(object,document);
                fieldSetMet.invoke(obj, object);
            }
        }
        return obj;
    }

    public static String parseSetName(String name) {
        if (name == null || "".equals(name)) {
            return null;
        }
        return "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static boolean checkSetMethod(Method[] methods, String methodName) {
        for (Method method : methods) {
            if (methodName.equals(method.getName())) {
                return true;
            }
        }
        return false;
    }
}
