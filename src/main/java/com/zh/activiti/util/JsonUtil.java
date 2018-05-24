package com.zh.activiti.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

/**
 * Created by Mrkin on 2017/3/6.
 */
public class JsonUtil {
    /**
     * @param object
     * @return
     */
    public static String tojson(Object object) {
        try {
            return JSON.toJSONString(object, SerializerFeature.WriteDateUseDateFormat);
        } catch (Exception e) {
            e.printStackTrace();
            return "tojsonerror";
        }
    }

    /**
     * @param object
     * @param include 需要转换的属性
     * @return jsonstring
     */
    public static String includetojson(Object object, String[] include) {
        try {
            SimplePropertyPreFilter filter = new SimplePropertyPreFilter(include);
            return JSON.toJSONString(object, filter, SerializerFeature.WriteDateUseDateFormat);
        } catch (Exception e) {
            e.printStackTrace();
            return "tojsonerror";
        }
    }
    /**
     * @param object
     * @param include 需要转换的属性
     * @return jsonstring
     */
    public static String includetojson(Object object,Class<?> t, String[] include) {
        try {
            SimplePropertyPreFilter filter = new SimplePropertyPreFilter(t,include);
            return JSON.toJSONString(object, filter, SerializerFeature.WriteDateUseDateFormat);
        } catch (Exception e) {
            e.printStackTrace();
            return "tojsonerror";
        }
    }
    /**
     * @param object
     * @param notInclude 不需要转换的属性
     * @return jsonstring
     */
    public static String notIncludetojson(Object object, String[] notInclude) {
        try {
            SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
            for (int i = 0; i < notInclude.length; i++) {
                filter.getExcludes().add(notInclude[i]);
            }
            return JSON.toJSONString(object, filter, SerializerFeature.WriteDateUseDateFormat);
        } catch (Exception e) {
            e.printStackTrace();
            return "tojsonerror";
        }

    }
}
