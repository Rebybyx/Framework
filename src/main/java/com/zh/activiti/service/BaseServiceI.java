package com.zh.activiti.service;

import com.zh.activiti.entity.Select;
import com.zh.activiti.entity.Tree;
import com.zh.activiti.entity.ZTree;

import java.util.List;
import java.util.Map;

/**
 * Created by Mrkin on 2016/10/28.
 * 查询条件连接不区分大小写
 * params key->value key field_symbol  demo(username_EQ)查询用户名等于 value
 * like demo(username_like)
 * EQ 等于 NEQ 不等于  GT 大于 GTE 大于等于 LT 小于 LTE 小于等于  NL 是空  NN 不为空
 * IN in NIN not in demo(username_IN)(username_NIN)  value 字符串'1','2','3'  int或者long  1,2,3
 * 不区分大小写 但尽量使用大写
 * or条件查询 暂时不支持
 * 局部条件添加不支持(括号添加)
 * 不支持between 查询
 */
public interface BaseServiceI<T> {

    /**
     * @param Id
     * @return T
     */
    T getById(String Id);

    /**
     * 生成流水号
     *
     * @param header     前缀
     * @param column     列名
     * @param baseNumber 基础编号 例如000001
     * @param count      上锁以后的请求数
     * @return
     */
    String getNumber(String header, String column, String baseNumber, int count);

    /**
     * 生成自然流水号 0001 0002    9999
     *
     * @param header     前缀
     * @param column     列名
     * @param baseNumber 基础编号 例如000000
     * @param count      上锁以后的请求数
     * @return
     */
    String getNaturalnessNumber(String header, String column, String baseNumber, int count);

    /**
     * @param params
     * @param order
     * @param page
     * @param rows
     * @return
     */
    List<T> getGrid(Map<String, Object> params, Map<String, String> order, int page, long rows);

    /**
     * 查询所有数据
     *
     * @return
     */
    List<T> find();

    /**
     * 查询所有数据并排序
     *
     * @param order key 属性名(列名)  value  asc/desc
     * @return
     */
    List<T> find(Map<String, String> order);

    /**
     * 分页查询 排序
     *
     * @param order key 属性名(列名)  value  asc/desc
     * @param page  页数
     * @param rows  查询一页的条数
     * @return
     */
    List<T> find(Map<String, String> order, int page, long rows);

    /**
     * @param page 页数
     * @param rows 查询一页的条数
     * @return
     */
    List<T> find(int page, long rows);

    /**
     * @param params key->value key field_symbol  demo(username_=)查询用户名等于 value
     * @return
     */
    List<T> findParams(Map<String, Object> params);

    /**
     * 根据条件查询
     *
     * @param params key->value key field_symbol  demo(username_=)查询用户名等于 value
     * @param order  排序字段 升降  key->value(column+desc/asc)或者(column)
     * @return
     */
    List<T> findParams(Map<String, Object> params, Map<String, String> order);

    /**
     * 根据条件和分页查询
     *
     * @param params
     * @param page
     * @param rows
     * @return
     */
    List<T> findParams(Map<String, Object> params, int page, long rows);

    /**
     * 根据条件、排序、分页查询
     *
     * @param params key->value key field_symbol  demo(username_=)查询用户名等于 value
     * @param order  排序字段 升降  key->value(column+desc/asc)或者(column)
     * @param page   页数
     * @param rows   查询一页的条数
     * @return
     */
    List<T> findParams(Map<String, Object> params, Map<String, String> order, int page, long rows);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    int delete(String id);

    /**
     * 插入数据
     *
     * @param t
     * @return
     */
    int save(T t);

    /**
     * 更新数据
     *
     * @param t
     * @return
     */
    int update(T t);

    /**
     * 根据条件
     *
     * @param map
     * @return
     */
    T getByParam(Map<String, Object> map);

    /**
     * 批量插入
     *
     * @param list
     * @return
     */
    int saveBatch(List<T> list);

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    int deleteBatch(List<Object> ids);

    /**
     * 批量逻辑删除
     *
     * @param list
     * @return
     */
    int deleteLogicBatch(List<T> list);

    /**
     * 批量逻辑删除
     *
     * @param ids
     * @return
     */
    int deleteLogicBatchByIds(List<Object> ids);

    /**
     * 获取数量
     *
     * @return
     */
    long count();

    /**
     * 获取数量
     *
     * @param params 查询条件
     * @return
     */
    long count(Map<String, Object> params);

    /**
     * 获取所有树
     *
     * @param params       查询条件
     * @param parentColumn
     * @param showColumn
     * @return
     */
    List<Tree> allTree(Map<String, Object> params, String textColumn, String parentColumn, List<String> showColumn);


    /**
     * 获取所有树
     *
     * @param params       查询条件
     * @param parentColumn
     * @param showColumn
     * @return
     */
    List<ZTree> allZTree(Map<String, Object> params, String textColumn, String parentColumn, List<String> showColumn);

    /**
     * 获取一层树
     *
     * @param id
     * @param params
     * @param textColumn
     * @param parentColumn
     * @param showColumn
     * @return
     */
    List<Tree> getTree(String id, Map<String, Object> params, String textColumn, String parentColumn, List<String> showColumn);


    /**
     * 获取当前id下的所有子树
     *
     * @param id
     * @param params
     * @param textColumn
     * @param parentColumn
     * @param showColumn
     * @param orders
     * @param isAll
     * @return
     */
    List<Tree> getTrees(String id, Map<String, Object> params, String textColumn, String parentColumn,
                        List<String> showColumn, Map<String, String> orders, boolean isAll);
    /**
     * 获取当前id下的所有子树
     *
     * @param id
     * @param params
     * @param textColumn
     * @param parentColumn
     * @param showColumn
     * @param orders
     * @param isAll
     * @return
     */
    List<ZTree> getZTrees(String id, Map<String, Object> params, String textColumn, String parentColumn,
                          List<String> showColumn, Map<String, String> orders, boolean isAll);

    /**
     * 逻辑删除 删除标志位需要添加LogicDelete注解
     *
     * @param id
     * @return
     */
    int deleteLogic(String id);


    /**
     * TreeGrid 数据加载
     *
     * @param showDelete   是否显示逻辑删除的数据
     * @param parentColumn 父节id字段名称
     * @return
     */
    List<T> findTreeGrid(String parentColumn, boolean showDelete);


    /**
     * 查询数据只显示id和name
     *
     * @param params
     * @param orders     排序
     * @param page
     * @param rows
     * @param textColumn 显示字段
     * @param idColumn   id
     * @return
     */
    List<Select> findSelectData(Map<String, Object> params, Map<String, String> orders, int page, long rows, String textColumn, String idColumn);


    /**
     * 查询数据只显示id和name
     *
     * @param params
     * @param orders     排序
     * @param page
     * @param rows
     * @param mapShowColumn 数组
     * @param textColumn 显示字段
     * @param idColumn   id
     * @return
     */
    List<Select> findSelectData(Map<String, Object> params, Map<String, String> orders, int page, long rows, String[] mapShowColumn, String textColumn, String idColumn);

    /**
     * 不同项目调用不同的工作流  根据配置文件即可
     *
     * @param o 警情流程的类名
     * @return
     */
    String getActName(Object o);

    /**
     * 动态查重
     *
     * @param id
     * @param field
     * @param value
     * @return 不重复时返回0，否则返回大于0的数
     */
    long countRepeatData(String id, String field, String value);

}