package com.zh.activiti.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zh.activiti.annotation.Id;
import com.zh.activiti.annotation.Index;
import com.zh.activiti.entity.DataEntity;
import com.zh.activiti.entity.Select;
import com.zh.activiti.entity.Tree;
import com.zh.activiti.entity.ZTree;
import com.zh.activiti.mapper.BaseMapper;
import com.zh.activiti.sql.BaseSql;
import com.zh.activiti.util.ReflectionUtil;
import com.zh.activiti.util.StaticUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Mrkin on 2016/10/28.
 */
@Service
public class BaseServiceImpl<T> implements BaseServiceI<T> {
    private Sort sort = Sort.ASC;
    @Autowired
    public BaseMapper<T> baseMapper;

    public List<T> getGrid(Map<String, Object> paramMap, Map<String, String> orderMap, int page, long rows) {
        updateParams(paramMap);
        List<T> list = Lists.newArrayList();
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        if ((rows == 0) && paramMap.size() == 0 && orderMap.size() == 0) {
            list.addAll(baseMapper.find(c));
        } else if (paramMap.size() > 0 && orderMap.size() == 0 && rows == 0) {
            list.addAll(baseMapper.findParam(paramMap, c));
        } else if (paramMap.size() > 0 && orderMap.size() == 0 && rows != 0) {
            list.addAll(baseMapper.findParamAndPage(paramMap, page, rows, c));
        } else if (paramMap.size() > 0 && orderMap.size() > 0 && rows == 0) {
            list.addAll(baseMapper.findParamAndOrder(paramMap, c, orderMap));
        } else if (paramMap.size() > 0 && orderMap.size() > 0 && rows != 0) {
            list.addAll(baseMapper.findParamAndOrderAndPage(paramMap, page, rows, c, orderMap));
        } else if (paramMap.size() == 0 && orderMap.size() == 0 && rows != 0) {
            list.addAll(baseMapper.findPage(c, page, rows));
        } else if (paramMap.size() == 0 && orderMap.size() > 0 && rows == 0) {
            list.addAll(baseMapper.findOrder(c, orderMap));
        } else if (paramMap.size() == 0 && orderMap.size() > 0 && rows != 0) {
            list.addAll(baseMapper.findPageAndOrder(c, page, rows, orderMap));
        }

        return list;
    }

    public T getById(String Id) {
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return baseMapper.getById(Id, c);
    }

    @Override
    public String getNumber(String header, String column, String baseNumber, int count) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        if (sdf.format(StaticUtil.date).equals(sdf.format(new Date()))) {
            try {
                Map<String, Object> params = Maps.newHashMap();
                params.put(column + "_RLK", header + sdf.format(StaticUtil.date));
                Map<String, String> orders = Maps.newHashMap();
                orders.put(column, "desc");
                List<T> list = findParams(params, orders);
                if (list.size() != 0) {
                    String attr = (String) ReflectionUtil.getValue(list.get(0), column);
                    long value = Long.valueOf(attr.substring(header.length()));
                    result = (value + count + 1) + "";
                } else {
                    result = (Long.valueOf(sdf.format(StaticUtil.date) + baseNumber) + count) + "";
                }
            } catch (Exception e) {
                e.printStackTrace();
                StaticUtil.date = new Date();
                result = (Long.valueOf(sdf.format(StaticUtil.date) + baseNumber) + count) + "";
            }
        } else {
            StaticUtil.date = new Date();
            result = (Long.valueOf(sdf.format(StaticUtil.date) + baseNumber) + count) + "";
        }
        return result;
    }

    @Override
    public String getNaturalnessNumber(String header, String column, String baseNumber, int count) {
        String result;
        try {
            Map<String, Object> params = Maps.newHashMap();
            params.put(column + "_RLK", header);
            Map<String, String> orders = Maps.newHashMap();
            orders.put(column, "desc");
            List<T> list = findParams(params, orders);
            if (list.size() != 0) {
                String attr = (String) ReflectionUtil.getValue(list.get(0), column);
                long value = Long.valueOf(attr.substring(header.length()));
                String temp = (value + count + 1) + "";
                if (temp.length() < baseNumber.length()) {
                    for (; temp.length() < baseNumber.length(); ) {
                        temp = "0" + temp;
                    }
                }
                result = temp + "";
            } else {
                String temp = (Long.valueOf(baseNumber) + count) + "";
                if (temp.length() < baseNumber.length()) {
                    for (; temp.length() < baseNumber.length(); ) {
                        temp = "0" + temp;
                    }
                }
                result = temp + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = (Long.valueOf(baseNumber) + count) + "";
        }
        return result;
    }

    @Override
    public List<T> find() {
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return baseMapper.find(c);
    }

    @Override
    public List<T> find(Map<String, String> order) {
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return baseMapper.findOrder(c, order);
    }


    @Override
    public List<T> find(Map<String, String> order, int page, long rows) {
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        return baseMapper.findPageAndOrder(c, page, rows, order);
    }

    @Override
    public List<T> find(int page, long rows) {
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return baseMapper.findPage(c, page, rows);
    }

    @Override
    public List<T> findParams(Map<String, Object> params) {
        updateParams(params);
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return baseMapper.findParam(params, c);
    }

    @Override
    public List<T> findParams(Map<String, Object> params, Map<String, String> order) {
        updateParams(params);
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return baseMapper.findParamAndOrder(params, c, order);
    }

    @Override
    public List<T> findParams(Map<String, Object> params, int page, long rows) {
        updateParams(params);
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return baseMapper.findParamAndPage(params, page, rows, c);
    }

    @Override
    public List<T> findParams(Map<String, Object> params, Map<String, String> order, int page, long rows) {
        updateParams(params);
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return baseMapper.findParamAndOrderAndPage(params, page, rows, c, order);
    }

    @Override
    public int delete(String id) {
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return baseMapper.delete(id, c);
    }

    @Override
    public int save(T t) {
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return baseMapper.insert(t, c);
    }

    @Override
    public int update(T t) {
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return baseMapper.update(t, c);
    }

    @Override
    public T getByParam(Map<String, Object> map) {
        updateParams(map);
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        List<T> list = baseMapper.findParam(map, c);
        return (list != null && list.size() > 0) ? list.get(0) : null;
    }

    @Override
    public int saveBatch(List<T> list) {
        int count = 0;
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        for (int i = 0; i < list.size(); i++) {
            if (baseMapper.insert(list.get(i), c) > 0) ;
            count++;
        }
        return count;
    }

    @Override
    public int deleteBatch(List<Object> ids) {
        String result = "";
        for (Object id : ids) {
            if (id instanceof String) {
                result += "'" + id + "',";
            } else {
                result += "" + id + ",";
            }
        }
        if (result.length() > 0) {
            result = result.substring(0, result.length() - 1);
        }
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return baseMapper.deleteBatch(result, c);
    }

    @Override
    public int deleteLogicBatch(List<T> list) {
        String result = "";
        for (T t : list) {
            String id = getId(t);
            if (id instanceof String) {
                result += "'" + id + "',";
            } else {
                result += "" + id + ",";
            }
        }
        if (result.length() > 0) {
            result = result.substring(0, result.length() - 1);
        }
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return baseMapper.deleteLogicBatch(result, c);
    }

    @Override
    public int deleteLogicBatchByIds(List<Object> ids) {
        String result = "";
        for (Object id : ids) {
            if (id instanceof String) {
                result += "'" + id + "',";
            } else {
                result += "" + id + ",";
            }
        }
        if (result.length() > 0) {
            result = result.substring(0, result.length() - 1);
        }
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return baseMapper.deleteLogicBatch(result, c);
    }

    @Override
    public long count() {
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return baseMapper.count(c);
    }

    @Override
    public long count(Map<String, Object> map) {
        updateParams(map);
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return baseMapper.countParam(map, c);
    }

    @Override
    public List<Tree> allTree(Map<String, Object> params, String textColumn, String parentColumn, List<String> showColumn) {
        params.put(parentColumn + "_NL", "");
        List<Tree> trees = Lists.newArrayList();
        trees.addAll(getRecursiveTree("", params, textColumn, parentColumn, showColumn));
        return trees;
    }

    @Override
    public List<ZTree> allZTree(Map<String, Object> params, String textColumn, String parentColumn, List<String> showColumn) {
        params.put(parentColumn + "_NL", "");
        List<ZTree> trees = Lists.newArrayList();
        trees.addAll(getRecursiveZTree("", params, textColumn, parentColumn, showColumn));
        return trees;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    //排序
    public enum Sort {
        ASC, DESC
    }

    /**
     * 获取一层树的方法
     *
     * @param id
     * @param params
     * @param textColumn
     * @param parentColumn
     * @param showColumn
     * @return
     */
    public List<Tree> getTree(String id, Map<String, Object> params, String textColumn, String parentColumn, List<String> showColumn) {
        Map<String, String> orders = Maps.newHashMap();
        orders.putAll(getTreeOrders());
        List<Tree> trees = Lists.newArrayList();
        trees.addAll(getTrees(id, params, textColumn, parentColumn, showColumn, orders, false));
        return trees;
    }

    @Override
    public int deleteLogic(String id) {
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return baseMapper.logicDelete(id, c);
    }

    @Override
    public List<T> findTreeGrid(String parentColumn, boolean showDelete) {
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        List<T> list = baseMapper.findTreeGridByParams(parentColumn, c, showDelete);
        List<T> result = Lists.newArrayList();
        List<T> root = Lists.newArrayList();
        //第一次获取所有根节点数据
        for (int i = 0; i < list.size(); i++) {
            T t = list.get(i);
            if (ReflectionUtil.getValue(t, parentColumn) == null) {
                root.add(t);
                list.remove(t);//删除数据
                i--;
            }
        }
        for (T t : root) {
            result.add(t);
            sortTrees(list, t, parentColumn, result);
        }
        return result;
    }

    @Override
    public List<Select> findSelectData(Map<String, Object> params, Map<String, String> orders, int page, long rows, String textColumn, String idColumn) {
        updateParams(params);
        List<T> list = Lists.newArrayList();
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        if (page == -1) {
            list.addAll(baseMapper.findParamAndOrder(params, c, orders));
        } else {
            list.addAll(baseMapper.findParamAndOrderAndPage(params, page, rows, c, orders));
        }
        List<Select> result = Lists.newArrayList();
        for (T t : list) {
            Select select = new Select();
            select.setId(ReflectionUtil.getValue(t, idColumn).toString());
            select.setText(ReflectionUtil.getValue(t, textColumn).toString());
            result.add(select);
        }
        return result;
    }

    @Override
    public List<Select> findSelectData(Map<String, Object> params, Map<String, String> orders, int page, long rows, String[] mapShowColumn, String textColumn, String idColumn) {
        updateParams(params);
        List<T> list = Lists.newArrayList();
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        if (page == -1) {
            list.addAll(baseMapper.findParamAndOrder(params, c, orders));
        } else {
            list.addAll(baseMapper.findParamAndOrderAndPage(params, page, rows, c, orders));
        }
        List<Select> result = Lists.newArrayList();
        for (T t : list) {
            Select select = new Select();
            select.setId(ReflectionUtil.getValue(t, idColumn).toString());
            select.setText(ReflectionUtil.getValue(t, textColumn).toString());
            for (String column : mapShowColumn) {
                Object obj = ReflectionUtil.getValue(t, column);
                select.getMap().put(column, obj);
            }
            result.add(select);
        }
        return result;
    }

    @Override
    public String getActName(Object o) {
        /*String str = ConfigUtil.get(StaticUtil.PROJECT_NAME);
        if (str != null) {
            return o.getClass().getSimpleName() + str;
        } else {
            return o.getClass().getSimpleName();
        }*/
        return o.getClass().getSimpleName();
    }

    @Override
    public long countRepeatData(String id, String field, String value) {
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return baseMapper.countRepeatData(id, field, value, c);
    }

    /**
     * 递归追加孩子数据
     *
     * @param list
     * @param root
     * @param parentColumn
     * @param result
     */
    public void sortTrees(List<T> list, T root, String parentColumn, List<T> result) {
        for (int i = 0; i < list.size(); i++) {
            T t = list.get(i);
            if (ReflectionUtil.getValue(t, parentColumn)
                    .equals(ReflectionUtil.getValue(root, new BaseSql().getIdName(root.getClass())))) {
                result.add(t);
                list.remove(t);
                i = -1;//重新遍历不然会少节点
                sortTrees(list, t, parentColumn, result);
            }
        }
    }


    /**
     * 获取所有树的方法
     *
     * @param id
     * @param params
     * @param textColumn
     * @param parentColumn
     * @param showColumn
     * @return
     */
    public List<Tree> getRecursiveTree(String id, Map<String, Object> params, String textColumn, String parentColumn, List<String> showColumn) {

        Map<String, String> orders = Maps.newHashMap();
        List<Tree> trees = Lists.newArrayList();
        orders.putAll(getTreeOrders());
        trees.addAll(getTrees(id, params, textColumn, parentColumn, showColumn, orders, true));
        return trees;
    }

    /**
     * 获取所有树的方法
     *
     * @param id
     * @param params
     * @param textColumn
     * @param parentColumn
     * @param showColumn
     * @return
     */
    public List<ZTree> getRecursiveZTree(String id, Map<String, Object> params, String textColumn, String parentColumn, List<String> showColumn) {
        Map<String, String> orders = Maps.newHashMap();
        List<ZTree> trees = Lists.newArrayList();
        orders.putAll(getTreeOrders());
        trees.addAll(getZTrees(id, params, textColumn, parentColumn, showColumn, orders, true));
        return trees;
    }

    /**
     * 获取index的条件
     *
     * @return
     */
    public Map<String, String> getTreeOrders() {
        Map<String, String> orders = Maps.newHashMap();
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        for (Field field : ReflectionUtil.getDeclaredFields(c)) {
            if (field.getAnnotation(Index.class) != null) {
                orders.put(field.getName(), this.getSort().name());
                break;
            }
        }
        return orders;
    }

    @Override
    public List<Tree> getTrees(String id, Map<String, Object> params, String textColumn, String parentColumn,
                               List<String> showColumn, Map<String, String> orders, boolean isAll) {
        List<T> list = Lists.newArrayList();
        List<Tree> trees = Lists.newArrayList();
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        if (id == null || id.equals("")) {
            params.remove(parentColumn + "_EQ");
            params.put(parentColumn + "_NL", id);
        } else {
            params.remove(parentColumn + "_NL");
            params.put(parentColumn + "_EQ", id);
        }
        list.addAll(this.findParams(params, orders));

        for (int i = 0; i < list.size(); i++) {
            Tree tree = new Tree();
            for (Field field : ReflectionUtil.getDeclaredFields(c)) {
                field.setAccessible(true);
                if (field.getName().equals(new BaseSql<T>().getIdName(c))) {
                    try {
                        tree.setId(field.get(list.get(i)).toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (field.getName().equals(parentColumn)) {
                    try {
                        tree.setParentId(field.get(list.get(i)) == null ? null : field.get(list.get(i)).toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (field.getName().equals(textColumn)) {
                    try {
                        tree.setText(field.get(list.get(i)) == null ? null : field.get(list.get(i)).toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                for (String column : showColumn) {
                    try {
                        if (column == field.getName()) {
                            tree.getMap().put(column, field.get(list.get(i)) == null ? null : field.get(list.get(i)).toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            Map<String, Object> newParams = Maps.newHashMap();
            newParams.putAll(params);
            newParams.remove(parentColumn + "_NL");
            newParams.put(parentColumn + "_EQ", ((DataEntity) list.get(i)).getId());
            if (this.count(newParams) > 0) {
                tree.getState().setExpanded(true);
                if (isAll) {
                    tree.setChildren(this.getTrees(((DataEntity) list.get(i)).getId(), params, textColumn, parentColumn, showColumn, orders, isAll));
                }
            } else {
                tree.setChildren(null);
                tree.getState().setExpanded(false);
            }

            trees.add(tree);
        }
        return trees;
    }

    @Override
    public List<ZTree> getZTrees(String id, Map<String, Object> params, String textColumn, String parentColumn, List<String> showColumn, Map<String, String> orders, boolean isAll) {
        List<T> list = Lists.newArrayList();
        List<ZTree> trees = Lists.newArrayList();
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        if (id == null || id.equals("")) {
            params.remove(parentColumn + "_EQ");
            params.put(parentColumn + "_NL", id);
        } else {
            params.remove(parentColumn + "_NL");
            params.put(parentColumn + "_EQ", id);
        }
        list.addAll(this.findParams(params, orders));

        for (int i = 0; i < list.size(); i++) {
            ZTree tree = new ZTree();
            for (Field field : ReflectionUtil.getDeclaredFields(c)) {
                field.setAccessible(true);
                if (field.getName().equals(new BaseSql<T>().getIdName(c))) {
                    try {
                        tree.setId(field.get(list.get(i)).toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (field.getName().equals(parentColumn)) {
                    try {
                        tree.setpId(field.get(list.get(i)) == null ? null : field.get(list.get(i)).toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (field.getName().equals(textColumn)) {
                    try {
                        tree.setName(field.get(list.get(i)) == null ? null : field.get(list.get(i)).toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                for (String column : showColumn) {
                    try {
                        if (column == field.getName()) {
                            tree.getMap().put(column, field.get(list.get(i)) == null ? null : field.get(list.get(i)).toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            Map<String, Object> newParams = Maps.newHashMap();
            newParams.putAll(params);
            newParams.remove(parentColumn + "_NL");
            newParams.put(parentColumn + "_EQ", ((DataEntity) list.get(i)).getId());
            if (this.count(newParams) > 0) {
                tree.setOpen(true);
                if (isAll) {
                    trees.addAll(this.getZTrees(((DataEntity) list.get(i)).getId(), params, textColumn, parentColumn, showColumn, orders, isAll));
                }
            } else {
                tree.setOpen(false);
            }

            trees.add(tree);
        }
        return trees;
    }

    /**
     * 更新一下like参数值
     *
     * @param params
     */
    public void updateParams(Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String[] conditions = entry.getKey().split(StaticUtil.SQL_SPLIT);
            String condition = (conditions.length > 1 ? conditions[1].toUpperCase() : conditions[0].toLowerCase());
            switch (condition) {
                case "LK":
                    params.put(entry.getKey(), "%" + entry.getValue() + "%");
                    break;
                case "RLK":
                    params.put(entry.getKey(), entry.getValue() + "%");
                    break;
                case "LLK":
                    params.put(entry.getKey(), "%" + entry.getValue());
                    break;
            }
        }
    }

    /**
     * 获取id字段
     *
     * @param t
     * @return
     */
    private String getId(T t) {
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        String id = null;
        for (Field f : ReflectionUtil.getDeclaredFields(c)) {
            f.setAccessible(true);
            Annotation annotation = f.getAnnotation(Id.class);
            if (annotation != null) {
                try {
                    if (f.get(t) != null)
                        id = f.get(t).toString();
                    break;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return id;
    }

   /* public  void setBaseMapper(BaseMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
    }*/
}
