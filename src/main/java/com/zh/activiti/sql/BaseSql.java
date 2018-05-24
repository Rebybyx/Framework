package com.zh.activiti.sql;

import com.zh.activiti.annotation.*;
import com.zh.activiti.entity.WhereCondition;
import com.zh.activiti.util.ReflectionUtil;
import com.zh.activiti.util.StaticUtil;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Created by 46595 on 2016/10/27.
 */
public class BaseSql<T> {
    private String idName = "id";
    //private String DATABASE= ConfigUtil.get("database");

    /**
     * @param id
     * @param t
     * @return 返回通过查询Id 的sql语句
     */
    public String getById(@Param("id") final String id, final Class<T> t) {
        getIdName(t);
        SQL sql = new SQL();
        sql.SELECT("*");
        sql.FROM(getTable(t));
        sql.WHERE(idName + "=#{id}");
       /* String delelteColumn=ReflectionUtil.getColumn(t,LogicDelete.class);
        if (delelteColumn!=null){
            sql.WHERE(delelteColumn+"=1");
        }*/
        return sql.toString();
    }

    public String findSQL(final Class<T> t) {
        SQL sql = new SQL();
        sql.SELECT("*").FROM(getTable(t));
        return sql.toString();
    }

    public String findOrderSQL(final Class<T> t, @Param("order") Map<String, String> order) {
        SQL sql = new SQL();
        sql.SELECT("*").FROM(getTable(t));
        getOrderSql(order, sql);
        return sql.toString();
    }

    public String findPageSQL(final Class<T> t, @Param("page") int page, @Param("rows") long rows) {
        SQL sql = new SQL();
        sql.SELECT("*").FROM(getTable(t));
        return sql.toString() + getpageStr(page, rows);
    }

    public String findPageAndOrderSQL(final Class<T> t, @Param("page") int page, @Param("rows") long rows, @Param("order") Map<String, String> order) {
        SQL sql = new SQL();
        sql.SELECT("*").FROM(getTable(t));
        getOrderSql(order, sql);
        return sql.toString() + getpageStr(page, rows);
    }

    /**
     * @param params key->value key field_symbol  demo(username_=)查询用户名等于 value
     * @param t
     * @return
     */
    public String findParamSQL(@Param("params") Map<String, Object> params, final Class<T> t) {
        SQL sql = new SQL();
        sql.SELECT("*").FROM(getTable(t));
        getSQL(params, sql);
        return sql.toString();
    }

    /**
     * @param params key->value key field_symbol  demo(username_=)查询用户名等于 value
     * @param t
     * @param order  排序字段 升降  key->value(column+desc/asc)或者(column)
     * @return
     */
    public String findParamAndOrderSQL(@Param("params") Map<String, Object> params, final Class<T> t, @Param("order") Map<String, String> order) {
        SQL sql = new SQL();
        sql.SELECT("*").FROM(getTable(t));
        getSQL(params, sql);
        getOrderSql(order, sql);
        return sql.toString();
    }

    /**
     * 根据条件和分页进行数据查询
     *
     * @param params key->value key field_symbol  demo(username_=)查询用户名等于 value
     * @param page
     * @param rows
     * @param t
     * @return
     */
    public String findParamAndPageSQL(@Param("params") Map<String, Object> params, @Param("page") int page, @Param("rows") long rows, final Class<T> t) {
        SQL sql = new SQL();
        sql.SELECT("*").FROM(getTable(t));
        getSQL(params, sql);
        return sql.toString() + getpageStr(page, rows);
    }


    /**
     * @param params key->value key field_symbol  demo(username_=)查询用户名等于 value
     * @param page
     * @param rows
     * @param t
     * @param order
     * @return
     */
    public String findParamAndOrderAndPageSQL(@Param("params") Map<String, Object> params, @Param("page") int page, @Param("rows") long rows, final Class<T> t, @Param("order") Map<String, String> order) {
        SQL sql = new SQL();
        sql.SELECT("*").FROM(getTable(t));
        getSQL(params, sql);
        getOrderSql(order, sql);
        return sql.toString() + getpageStr(page, rows);
    }

    /**
     * 插入语句
     *
     * @param object
     * @param t
     * @return
     * @throws Exception
     */
    public String insert(@Param("object") T object, Class<T> t) throws Exception {
        SQL sql = new SQL();
        sql.INSERT_INTO(getTable(t));

        for (Field f : ReflectionUtil.getDeclaredFields(t)) {
            String filedName = f.getName();
            Annotation annotation = f.getAnnotation(NoMapping.class);
            //不插入不映射的属性
            if (annotation == null) {
                //1、获取属性上的指定类型的注释
                if (!filedName.equals("serialVersionUID")) {
                    sql.VALUES(filedName, "#{object." + filedName + "}");
                }
            }
        }
        return sql.toString();
    }

    /**
     * 更新
     *
     * @param object
     * @param t
     * @return
     * @throws Exception
     */
    public String update(@Param("object") T object, Class<T> t) throws Exception {
        getIdName(t);
        SQL sql = new SQL();
        sql.UPDATE(getTable(t));
        for (Field f : ReflectionUtil.getDeclaredFields(t)) {
            String filedName = f.getName();
            Annotation annotation = f.getAnnotation(NoMapping.class);
            //不更新不映射的属性
            if (annotation == null) {
                //1、获取属性上的指定类型的注释
                if (!filedName.equals(idName) && !filedName.equals("serialVersionUID"))
                    sql.SET(filedName + "=#{object." + filedName + "}");
            }
        }
        sql.WHERE(idName + "=#{object." + idName + "}");
        return sql.toString();
    }

    /**
     * 获取id列
     *
     * @param t
     * @return id字段默认为id
     */
    public String getIdName(Class<T> t) {
        for (Field f : ReflectionUtil.getDeclaredFields(t)) {
            //1、获取属性上的指定类型的注释
            Annotation annotation = f.getAnnotation(Id.class);
            if (annotation != null) {
                this.idName = f.getName();
                return this.idName;
            }
        }
        return this.idName;
    }

    /**
     * 获取Index列
     *
     * @param t
     * @return id字段默认为id
     */
    public String getIndexName(Class<T> t) {
        for (Field f : ReflectionUtil.getDeclaredFields(t)) {
            //1、获取属性上的指定类型的注释
            Annotation annotation = f.getAnnotation(Index.class);
            if (annotation != null) {
                return f.getName();
            }
        }
        return null;
    }

    /**
     * 删除sql
     *
     * @param id
     * @param t
     * @return
     */
    public String delete(@Param("id") final String id, final Class<T> t) {
        getIdName(t);
        SQL sql = new SQL();
        sql.DELETE_FROM(getTable(t));
        sql.WHERE(idName + "=#{id}");
        return sql.toString();
    }

    //批量逻辑删除
    public String deleteBatch(@Param("ids") final String ids, final Class<T> t) {
        getIdName(t);
        SQL sql = new SQL();
        sql.DELETE_FROM(getTable(t));
        if (ids.length() > 0) {
            sql.WHERE(idName + " IN ("+ids+")");
        }
        return sql.toString();
    }

    public String logicDelete(@Param("id") final String id, final Class<T> t) {
        String deleteColumn = "";
        deleteColumn = ReflectionUtil.getColumn(t, LogicDelete.class);
        getIdName(t);
        SQL sql = new SQL();
        sql.UPDATE(getTable(t));
        if (deleteColumn != null) {
            sql.SET(deleteColumn + "=0");
            sql.WHERE(idName + "=#{id}");
        }
        return sql.toString();
    }

    //批量逻辑删除
    public String deleteLogicBatch(@Param("ids") final String ids, final Class<T> t) {
        String deleteColumn = "";
        deleteColumn = ReflectionUtil.getColumn(t, LogicDelete.class);
        getIdName(t);
        SQL sql = new SQL();
        sql.UPDATE(getTable(t));
        if (deleteColumn != null) {
            sql.SET(deleteColumn + "=0");
        }
        if (ids.length() > 0) {
            sql.WHERE(idName + " IN (" + ids + ")");
        }
        return sql.toString();
    }

    /**
     * 批量删除sql
     *
     * @param ids
     * @param t
     * @return
     */
    public String deleleBatch(@Param("ids") final String ids, final Class<T> t) {
        getIdName(t);
        SQL sql = new SQL();
        sql.DELETE_FROM(getTable(t));
        sql.WHERE(idName + "in ( " + ids + " )");
        return sql.toString();
    }

    /**
     * 获取表名
     *
     * @param t
     * @return
     */
    public String getTable(Class<T> t) {
        if (t.getAnnotation(Table.class) != null && !t.getAnnotation(Table.class).value().equals("")) {
            return t.getAnnotation(Table.class).value();
        }
        return t.getSimpleName();
    }

    /**
     * 获取所有数据的数量
     *
     * @param t
     * @return
     */
    public String countSql(Class<T> t) {
        SQL sql = new SQL();
        sql.SELECT("count(*)").FROM(getTable(t));
        return sql.toString();
    }

    /**
     * 根据条件获取数据数量
     *
     * @param params key->value key field_symbol  demo(username_=)查询用户名等于 value
     * @param t
     * @return
     */
    public String countParamsSQL(@Param("params") Map<String, Object> params, final Class<T> t) {
        SQL sql = new SQL();
        sql.SELECT("count(*)").FROM(getTable(t));
        getSQL(params, sql);
        return sql.toString();
    }

    /**
     * 增加SQL 的条件（原框架方法）
     *
     * @param params
     * @param sql
     */
    private void getSQL(Map<String, Object> params, SQL sql) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String[] conditions = entry.getKey().split(StaticUtil.SQL_SPLIT);
            String condition = (conditions.length>1?conditions[1].toUpperCase():conditions[0].toLowerCase());
            switch (condition) {
                case "IN":
                    if (entry.getValue() != null && !entry.getValue().toString().equals(""))
                        sql.WHERE(conditions[0] + " IN (" + entry.getValue() + ")");
                    break;
                case "NIN"://Not in
                    if (entry.getValue() != null && !entry.getValue().toString().equals(""))
                        sql.WHERE(conditions[0] + " NOT IN (" + entry.getValue() + ")");
                    break;
                case "EQ"://等于
                    sql.WHERE(conditions[0] + " " + "=" + " #{params." + entry.getKey() + "}");
                    break;
                case "GT"://大于
                    sql.WHERE(conditions[0] + " " + ">" + " #{params." + entry.getKey() + "}");
                    break;
                case "LT"://小于
                    sql.WHERE(conditions[0] + " " + "<" + " #{params." + entry.getKey() + "}");
                    break;
                case "GTE"://大于等于
                    sql.WHERE(conditions[0] + " " + ">=" + " #{params." + entry.getKey() + "}");
                    break;
                case "LTE"://小于等于
                    sql.WHERE(conditions[0] + " " + "<=" + " #{params." + entry.getKey() + "}");
                    break;
                case "NL"://IS NULL
                    sql.WHERE(conditions[0] + " IS NULL");
                    break;
                case "NN"://IS NOT NULL
                    sql.WHERE(conditions[0] + " IS NOT NULL");
                    break;
                case "LK":
                case "LLK":
                case "RLK":
                    sql.WHERE(conditions[0] + " LIKE " + " #{params." + entry.getKey() + "}");
                    break;
                case "NEQ"://不等于
                    sql.WHERE(conditions[0] + " <> " + " #{params." + entry.getKey() + "}");
//                default:
//                    sql.WHERE(conditions[0] + " " + conditions[1] + " #{params." + entry.getKey() + "}");
//                    break;
            }
        }
    }

    /**
     * 增加SQL 的条件（新前台改造）
     *
     * @param whereConditions
     * @param sql
     */
    private void getSQL_new(List<WhereCondition> whereConditions, SQL sql) {
        /*for (WhereCondition where : whereConditions) {
            switch (where.getMethod()) {
                *//*case "IN":
                    if (entry.getValue() != null && !entry.getValue().toString().equals(""))
                        sql.WHERE(conditions[0] + " IN (" + entry.getValue() + ")");
                    break;
                case "NIN"://Not in
                    if (entry.getValue() != null && !entry.getValue().toString().equals(""))
                        sql.WHERE(conditions[0] + " NOT IN (" + entry.getValue() + ")");
                    break;*//*
                case EQUAL://等于
                    sql.WHERE(where.getField() + " " + "=" + " #{params." + entry.getKey() + "}");
                    break;
                case "GT"://大于
                    sql.WHERE(conditions[0] + " " + ">" + " #{params." + entry.getKey() + "}");
                    break;
                case "LT"://小于
                    sql.WHERE(conditions[0] + " " + "<" + " #{params." + entry.getKey() + "}");
                    break;
                case "GTE"://大于等于
                    sql.WHERE(conditions[0] + " " + ">=" + " #{params." + entry.getKey() + "}");
                    break;
                case "LTE"://小于等于
                    sql.WHERE(conditions[0] + " " + "<=" + " #{params." + entry.getKey() + "}");
                    break;
                case "NL"://IS NULL
                    sql.WHERE(conditions[0] + " IS NULL");
                    break;
                case "NN"://IS NOT NULL
                    sql.WHERE(conditions[0] + " IS NOT NULL");
                    break;
                case "LK":
                case "LLK":
                case "RLK":
                    sql.WHERE(conditions[0] + " LIKE " + " #{params." + entry.getKey() + "}");
                    break;
                case "NEQ"://不等于
                    sql.WHERE(conditions[0] + " <> " + " #{params." + entry.getKey() + "}");
//                default:
//                    sql.WHERE(conditions[0] + " " + conditions[1] + " #{params." + entry.getKey() + "}");
//                    break;
            }
        }*/
    }

    /**
     * 执行sql order数据
     *
     * @param order
     * @param sql
     */
    public void getOrderSql(Map<String, String> order, SQL sql) {
        for (Map.Entry<String, String> entry : order.entrySet()) {
            sql.ORDER_BY("" + entry.getKey() + " " + entry.getValue() + "");
        }
    }


    /**
     * 获取分页字符串 mysql
     *
     * @param page
     * @param rows
     * @return
     */
    public String getpageStr(int page, long rows) {
        return " limit " + page * rows + "," + rows;
    }


    /**
     * @param showDelete 是否显示逻辑删除的数据
     * @param t
     * @return
     */
    public String findParamTreeGridSQL(@Param("parentColumn") String parentColumn, final Class<T> t, boolean showDelete) {
        SQL sql = new SQL();
        sql.SELECT("a.*,COUNT(b." + getIdName(t) + ")<=0 as leaf");
        sql.FROM(getTable(t) + " a");
        sql.LEFT_OUTER_JOIN(getTable(t) + " b on a." + getIdName(t) + "=b." + parentColumn);
        //getSQL(params, sql);
        String deleteColumn = ReflectionUtil.getColumn(t, LogicDelete.class);
        if (!showDelete) {
            if (deleteColumn != null && !deleteColumn.isEmpty()) {
                sql.WHERE("a." + deleteColumn + "=1");
            }
        }
        return sql.toString() + " group by a." + getIdName(t) + (getIndexName(t) != null ? " order by " + getIndexName(t) + " asc " : "");
    }

    /** groupby
     * @param params
     * @param sql
     */
    public void getGroupBy(Map<String, Object> params,SQL sql){
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String[] conditions = entry.getKey().split(StaticUtil.SQL_SPLIT);
            String condition = (conditions.length>1?conditions[1].toUpperCase():conditions[0].toUpperCase());
            switch (condition){
                case "GB":
                    sql.GROUP_BY(conditions[0]);
                    break;
            }
        }
    }

    public String countRepeatData(@Param("id") final String id, String field, @Param("value") Object value, final Class<T> t) {
        SQL sql = new SQL();
        sql.SELECT("count(*)").FROM(getTable(t));
        sql.WHERE(field + " = #{value}").WHERE("id != #{id}");
        return sql.toString();
    }

}
