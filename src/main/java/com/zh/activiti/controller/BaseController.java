package com.zh.activiti.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zh.activiti.annotation.*;
import com.zh.activiti.entity.*;
import com.zh.activiti.service.BaseServiceI;
import com.zh.activiti.util.HttpEntity;
import com.zh.activiti.util.ReflectionUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.zh.activiti.entity.OrderConditionMethod.ASC;
import static com.zh.activiti.entity.WhereConditionMethod.*;

//import com.zh.activiti.service.sys.FilesServiceI;

/**
 * 基本controller  里面包含保存、删除、逻辑删除grid、tree的方法
 * Created by Mrkin on 2016/11/3.
 */
public class
BaseController<T> {
    @Autowired
    protected BaseServiceI<T> service;


    @InitBinder
    protected void initBinder(HttpServletRequest request,
                              ServletRequestDataBinder binder) throws Exception {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        CustomDateEditor dateEditor = new CustomDateEditor(fmt, true);
        binder.registerCustomEditor(Date.class, dateEditor);
    }

    /**
     * @param object
     * @return
     */
    public String tojson(Object object) {
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
    public String includetojson(Object object, String[] include) {
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
    public String includetojson(Object object, Class<?> t, String[] include) {
        try {
            SimplePropertyPreFilter filter = new SimplePropertyPreFilter(t, include);
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
    public String notIncludetojson(Object object, String[] notInclude) {
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

    @ResponseBody
    @RequestMapping(value = "saveBody.do", method = RequestMethod.POST)
    public String saveBody(@RequestBody T t) {
        String id = this.getId(t);
        T temp = null;
        if ((temp = service.getById(id)) != null) {
            service.update(t);
        } else {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String userId = (String) request.getAttribute("userId");
            ((DataEntity) t).setCreateTime(new Date());
            ((DataEntity) t).setCreateUserId(userId);
            service.save(t);
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        saveLog(request, "保存", "对" + t.getClass().getAnnotation(Table.class).value() + "表保存数据");
        HttpEntity httpEntity = new HttpEntity();
        httpEntity.setMessage("保存成功");
        return tojson(httpEntity);
    }

    @ApiOperation(value = "保存", notes = "json数据中不用传创建时间和创建人id")
    @ResponseBody
    @RequestMapping(value = "save.do", method = RequestMethod.POST)
    public String save(String json) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        T t = (T) parse(json, c);
        String id = this.getId(t);
        if ((service.getById(id)) != null) {
            service.update(t);
        } else {
            String userId = (String) request.getAttribute("userId");
            ((DataEntity) t).setCreateTime(new Date());
            ((DataEntity) t).setCreateUserId(userId);
            service.save(t);
        }
        saveLog(request, "保存", "对" + c.getAnnotation(Table.class).value() + "表保存数据");
        HttpEntity httpEntity = new HttpEntity();
        httpEntity.setMessage("保存成功");
        return tojson(httpEntity);
    }

    /**
     * 非继承DataEntity的save
     *
     * @param t
     * @return
     */
    public String save(T t) {
        String id = this.getId(t);
        if (service.getById(id) != null) {
            service.update(t);
        } else {
            service.save(t);
        }
        HttpEntity httpEntity = new HttpEntity();
        httpEntity.setMessage("保存成功");
        return tojson(httpEntity);
    }

    @ApiOperation(value = "获取数据", notes = "根据id获取数据")
    @ResponseBody
    @RequestMapping(value = "getById.do", method = RequestMethod.POST)
    public String getById(String id) {
        HttpEntity httpEntity = new HttpEntity();
        httpEntity.setMessage("获取成功");
        httpEntity.setData(service.getById(id));
        return tojson(httpEntity);
    }

    @ApiOperation(value = "真实删除数据", notes = "根据id获取数据")
    @ResponseBody
    @RequestMapping(value = "delete.do", method = RequestMethod.DELETE)
    public String delete(String id) {
        HttpEntity httpEntity = new HttpEntity();
        httpEntity.setMessage("删除成功");
        httpEntity.setData(service.delete(id));
        return tojson(httpEntity);
    }

    @ApiOperation(value = "真实删除数据", notes = "根据id获取数据")
    @ResponseBody
    @RequestMapping(value = "deleteBatch.do", method = RequestMethod.DELETE)
    public String deleteBatch(@RequestBody List<Object> ids) {
        HttpEntity httpEntity = new HttpEntity();
        httpEntity.setMessage("删除成功");
        httpEntity.setData(service.deleteBatch(ids));
        return tojson(httpEntity);
    }

    @ApiOperation(value = "逻辑删除数据", notes = "根据id获取数据")
    @ResponseBody
    @RequestMapping(value = "logicDelete.do", method = RequestMethod.POST)
    public String logicDelete(String id) {
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        HttpEntity httpEntity = new HttpEntity();
        httpEntity.setMessage("删除成功");
        httpEntity.setData(service.deleteLogic(id));
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        saveLog(request, "逻辑删除", "对" + c.getAnnotation(Table.class).value() + "表进行逻辑删除数据id:" + id);
        return tojson(httpEntity);
    }

    @ApiOperation(value = "批量逻辑删除数据", notes = "")
    @ResponseBody
    @RequestMapping(value = "logicDeleteBatch_entity.do", method = RequestMethod.POST)
    public String logicDeleteBatch_entity(@RequestBody List<T> list) {
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        HttpEntity httpEntity = new HttpEntity();
        if (list == null || list.size() == 0) {
            httpEntity.setIsSuccess(false);
            httpEntity.setMessage("未选择需要删除的数据。");
            return tojson(httpEntity);
        }
        httpEntity.setData(service.deleteLogicBatch(list));
        httpEntity.setMessage("所选数据已成功删除。");
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        for (T t : list) {
            String id = getId(t);
            saveLog(request, "批量逻辑删除", "对" + c.getAnnotation(Table.class).value() + "表进行逻辑删除数据id:" + id);
        }
        return tojson(httpEntity);
    }

    @ApiOperation(value = "批量逻辑删除数据", notes = "根据id获取数据")
    @ResponseBody
    @RequestMapping(value = "logicDeleteBatch.do", method = RequestMethod.POST)
    public String logicDeleteBatch(@RequestBody List<Object> ids) {
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        HttpEntity httpEntity = new HttpEntity();
        httpEntity.setMessage("删除成功");
        httpEntity.setData(service.deleteLogicBatchByIds(ids));
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        for (Object id : ids) {
            saveLog(request, "批量逻辑删除", "对" + c.getAnnotation(Table.class).value() + "表进行逻辑删除数据id:" + id);
        }
        return tojson(httpEntity);
    }


    @ApiOperation(value = "获取信息列表", notes = "获取信息列表")
    @ApiImplicitParams({@ApiImplicitParam(name = "gridRequest", value = "前台请求的参数实体", dataType = "GridRequest")})
    @ResponseBody
    @RequestMapping(value = "getGrid.do", method = RequestMethod.POST)
    public String getGrid(@RequestBody GridRequest gridRequest) {
        // TODO 优化想法，修改BaseSql类中的getSQL()方法，减少迭代次数，避免结构臃肿冗余
        Map<String, Object> paramMap = Maps.newHashMap();
        Map<String, String> orderMap = Maps.newHashMap();
        if (gridRequest.getWhereConditions() != null && gridRequest.getWhereConditions().size() > 0) {
            for (WhereCondition where : gridRequest.getWhereConditions()) {
                switch (where.getMethod()) {
                    case EQUAL:
                        paramMap.put(where.getField() + "_EQ", where.getValue());
                        break;
                    case NOT_EQUAL:
                        paramMap.put(where.getField() + "_NEQ", where.getValue());
                        break;
                    case LIKE:
                        paramMap.put(where.getField() + "_LK", where.getValue());
                        break;
                    case LESS_THAN:
                        paramMap.put(where.getField() + "_LT", where.getValue());
                        break;
                    case MORE_THAN:
                        paramMap.put(where.getField() + "_GT", where.getValue());
                        break;
                    case LESS_OR_EQUAL_THAN:
                        paramMap.put(where.getField() + "_LTE", where.getValue());
                        break;
                    case MORE_OR_EQUAL_THAN:
                        paramMap.put(where.getField() + "_GTE", where.getValue());
                        break;
                    case IN:
                        paramMap.put(where.getField() + "_IN", where.getValue());
                        break;
                    case NIN:
                        paramMap.put(where.getField() + "_NIN", where.getValue());
                        break;
                }
            }
        }
        if (gridRequest.getOrderConditions() != null && gridRequest.getOrderConditions().size() > 0) {
            for (OrderCondition order : gridRequest.getOrderConditions()) {
                switch (order.getMethod()) {
                    case ASC:
                        orderMap.put(order.getField(), "asc");
                        break;
                    case DESC:
                        orderMap.put(order.getField(), "desc");
                        break;
                }
            }
        }
        List<T> list = service.getGrid(paramMap, orderMap, gridRequest.getPageIndex(), gridRequest.getPageItemCount());
        long total;
        if (paramMap.size() > 0) {
            total = service.count(paramMap);
        } else {
            total = service.count();
        }
        long records = total;
        if (gridRequest.getPageItemCount() != 0) {
            total = total % gridRequest.getPageItemCount() == 0 ? total / gridRequest.getPageItemCount() : total / gridRequest.getPageItemCount() + 1;
        }
        Grid grid = new Grid();
        grid.setData(list);
        grid.setPageIndex(gridRequest.getPageIndex());
        grid.setTotalCount(records);
        grid.setPageItemCount(total);
        HttpEntity httpEntity = new HttpEntity();
        httpEntity.setIsSuccess(true);
        httpEntity.setMessage("获取成功");
        httpEntity.setData(grid);
        return tojson(httpEntity);
    }

    @ApiOperation(value = "获取tree数据", notes = "根据id获取数据")
    @ResponseBody
    @RequestMapping(value = "getTree.do", method = RequestMethod.POST)
    public String getTree() {

        return "";
    }

    @ApiOperation(value = "字段重复验证", notes = "验证此字段的值是否已存在")
    @ResponseBody
    @RequestMapping(value = "noPermission_checkRepeat.do", method = RequestMethod.POST)
    public String checkRepeat(String id, String field, String value) {
        HttpEntity httpEntity = new HttpEntity();
        long count = service.countRepeatData(id, field, value);
        httpEntity.setData(count == 0);
        return tojson(httpEntity);
    }

    /**
     * json 解析成对象
     *
     * @param json
     * @param t
     * @return
     */
    public Object parse(String json, Class t) {
        return JSON.parseObject(json, t);
    }


    /**
     * json 解析成List<T>
     *
     * @param json json数据
     * @param t    类型
     * @return
     */
    public List parseList(String json, Class t) {
        return JSON.parseArray(json, t);
    }


    /**
     * 获取id字段
     *
     * @param t
     * @return
     */
    public String getId(T t) {
        Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        String id = null;
        for (Field f : ReflectionUtil.getDeclaredFields(c)) {
            String filedName = f.getName();
            f.setAccessible(true);
            Annotation annotation = f.getAnnotation(Id.class);
            //不更新不映射的属性
            if (annotation != null) {
                //1、获取属性上的指定类型的注释
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

//    /**
//     * 此方法不适合增加条件过滤 如果需要增加条件过滤请重写sql语句
//     *
//     * @param parentColum
//     * @return
//     */
    /*public String getTreeGrid(String parentColum, boolean showDelete) {
        HttpEntity httpEntity = new HttpEntity();
        Grid grid = new Grid();
        grid.setData(service.findTreeGrid(parentColum, showDelete));
        grid.setPageIndex(0);
        grid.setTotalCount(1);
        grid.setPageItemCount(1);
        httpEntity.setData(grid);
        httpEntity.setData(grid);
        return tojson(httpEntity);
    }*/

    /**
     * 此方法不适合增加条件过滤 如果需要增加条件过滤请重写sql语句
     *
     * @param parentColum
     * @return
     */
    public String getTreeGrid(String parentColum, boolean showDelete) {
        HttpEntity httpEntity = new HttpEntity();
        Grid grid = new Grid();
        grid.setData(service.findTreeGrid(parentColum, showDelete));
        grid.setPageIndex(0);
        grid.setTotalCount(1);
        grid.setPageItemCount(1);
        httpEntity.setData(grid);
        return tojson(httpEntity);
    }


    public void saveLog(HttpServletRequest request, String operation, String content) {
        /*String userId = (String) request.getAttribute("userId");
        String url = request.getRequestURI();
        String ip = request.getRemoteAddr();

        String sId = resourceServiceI.getResourceIdByUrl(url);
        // TODO 保存日志
        Log log = new Log();
        log.setiContent(content);
        log.setiOperation(operation);
        log.setUrl(url);
        log.setuId(userId);

        log.setsId(sId);

        log.setCreateTime(new Date());
        log.setiIp(ip);

        logServiceI.save(log);*/
    }

    boolean locked = false;
    int count = 0;

    /**
     * 生成流水号
     *
     * @param header 头
     * @param column 列名
     * @return
     */
    public String getNumber(String header, String column, String baseNumber) {
        String result = null;
        if (locked) {
            count++;
        }
        locked = true;
        try {
            result = service.getNumber(header, column, baseNumber, count);
            result = header + (Long.valueOf(result));
            count--;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            count = 0;
            locked = false;
        }
        return result;
    }

    boolean NaturalnessLocked = false;
    int NaturalnessCount = 0;

    /**
     * 生成自然流水号
     *
     * @param header     头
     * @param column     列名
     * @param baseNumber 0001
     * @return
     */
    public String getNaturalnessNumber(String header, String column, String baseNumber) {
        String result = null;
        if (NaturalnessLocked) {
            NaturalnessCount++;
        }
        NaturalnessLocked = true;
        try {
            result = service.getNaturalnessNumber(header, column, baseNumber, NaturalnessCount);
            result = header + result;
            NaturalnessCount--;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            NaturalnessCount = 0;
            NaturalnessLocked = false;
        }
        return result;
    }

    /**
     * 保存文件
     *
     * @param files
     * @param deleteFileIds 需要删除的文件ids
     * @return
     *//*
    public List<String> saveFile(MultipartFile[] files, String userId, List<String> deleteFileIds) {
        for (String fileId : deleteFileIds) {
            filesServiceI.deleteFile(fileId);
        }

        List<String> filesId = Lists.newArrayList();
        for (MultipartFile file : files) {
            filesId.add(filesServiceI.save(file, UUID.randomUUID().toString(), userId));
        }
        return filesId;
    }

    *//**
     * 保存文件
     *
     * @param files
     * @return
     *//*
    public List<String> saveFile(MultipartFile[] files, String userId) {
        List<String> filesId = Lists.newArrayList();
        if (files != null)
            for (MultipartFile file : files) {
                filesId.add(filesServiceI.save(file, UUID.randomUUID().toString(), userId));
            }
        return filesId;
    }

    *//**
     * 保存文件
     *
     * @param file
     * @param userId       用户id
     * @param deleteFileId 需要删除的文件id
     * @return
     *//*
    public String saveFile(MultipartFile file, String userId, String deleteFileId) {
        filesServiceI.deleteFile(deleteFileId);
        return filesServiceI.save(file, UUID.randomUUID().toString(), userId);
    }


    *//**
     * 保存文件
     *
     * @param file
     * @param userId 用户id
     * @return
     *//*
    public String saveFile(MultipartFile file, String userId) {
        return filesServiceI.save(file, UUID.randomUUID().toString(), userId);
    }*/
}
