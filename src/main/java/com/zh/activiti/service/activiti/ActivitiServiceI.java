package com.zh.activiti.service.activiti;


import com.zh.activiti.entity.activiti.CompleteResult;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import java.util.List;
import java.util.Map;

/**
 * @author Rebybyx
 */
public interface ActivitiServiceI {

    enum Type {
        EQ, NEQ, NONE
    }

    /**
     * 开始流程
     *
     * @param simpleClassName 流程ID（一般为类名）
     * @param objId           业务对象
     * @param vars            变量（存在于整个流程周期）
     */
    public CompleteResult startProcess(String simpleClassName, String objId, Map<String, Object> vars);

    /**
     * 开始流程
     *
     * @param simpleClassName 流程ID（一般为类名）
     * @param objId           业务对象
     */
    public CompleteResult startProcess(String simpleClassName, String objId);

    /**
     * 部署流程（zip方式）
     *
     * @param zipPath  压缩包相对于classpath的路径
     * @param name     流程名称
     * @param category 流程类别
     */
    public void deployProcess(String zipPath, String name, String category);

    /**
     * 部署流程
     *
     * @param bpmnPath 流程文件（.bpmn）相对于classpath的路径
     * @param pngPath  流程图片（.png）相对于classpath的路径
     * @param name     流程名称
     * @param category 流程类别
     */
    public void deployProcess(String bpmnPath, String pngPath, String name, String category);

    /**
     * 删除流程部署
     *
     * @param simpleClassName 流程ID（一般为类名）
     */
    public void delDeployProcess(String simpleClassName);

    /**
     * 办理任务
     *
     * @param taskId 任务id
     */
    public CompleteResult completeTask(String taskId);

    /**
     * 办理任务
     *
     * @param taskId 任务id
     * @param vars   流程变量
     */
    public CompleteResult completeTask(String taskId, Map<String, Object> vars);

    /**
     * 办理任务
     *
     * @param simpleClassName 实体类的simpleClassName
     * @param objId           实体的id
     */
    public CompleteResult completeTask(String simpleClassName, String objId);

    /**
     * 办理任务
     *
     * @param simpleClassName 实体类的simpleClassName
     * @param objId           实体的id
     * @param vars            流程参数
     * @return String 下一个节点的名字
     */
    public CompleteResult completeTask(String simpleClassName, String objId, Map<String, Object> vars);

    /**
     * 办理任务
     *
     * @param taskId   任务id
     * @param assignee 办理人
     */
    public CompleteResult completeTaskByAssignee(String taskId, String assignee);

    /**
     * 办理任务
     *
     * @param taskId   任务id
     * @param assignee 办理人
     * @param vars     流程变量
     */
    public CompleteResult completeTaskByAssignee(String taskId, String assignee, Map<String, Object> vars);

    /**
     * 办理任务
     *
     * @param simpleClassName 实体类的simpleClassName
     * @param objId           实体的id
     * @param assignee        办理人
     */
    public CompleteResult completeTaskByAssignee(String simpleClassName, String objId, String assignee);

    /**
     * 办理任务
     *
     * @param simpleClassName 实体类的simpleClassName
     * @param objId           实体的id
     * @param assignee        办理人
     * @param vars            流程参数
     * @return String 下一个节点的名字
     */
    public CompleteResult completeTaskByAssignee(String simpleClassName, String objId, String assignee, Map<String, Object> vars);

    /**
     * 获取当前任务对象
     *
     * @param simpleClassName 实体类的simpleClassName
     * @param objId           实体的id
     * @return Task 任务对象
     */
    public Task getTask(String simpleClassName, String objId);

    CompleteResult getCurrentTask(String simpleClassName, String objId);

    /**
     * 获取当前任务对象
     *
     * @param simpleClassName 实体类的simpleClassName
     * @param objId           实体的id
     * @param assignee        办理人
     * @return Task 任务对象
     */
    public Task getTaskByAssignee(String simpleClassName, String objId, String assignee);

    /**
     * 获取当前业务实体所经历的所有用户活动（未完结流程实例）
     *
     * @param simpleClassName 实体类的simpleClassName
     * @param objId           实体的id
     * @return List<HistoricActivityInstance>
     */
    public List<HistoricActivityInstance> getHistoryActivity(String simpleClassName, String objId);

    /**
     * 获取当前业务实体所经历的所有用户活动（已完结流程实例）
     *
     * @param simpleClassName 实体类的simpleClassName
     * @param objId           实体的id
     * @return List<HistoricActivityInstance>
     */
    public List<HistoricActivityInstance> getHistoryActivityFinished(String simpleClassName, String objId);

    /**
     * 根据办理人及实体类型（流程id）获取所有任务
     *
     * @param simpleClassName 实体类的simpleClassName
     * @param assignee        办理人
     * @return List<Task> 任务对象list
     */
    public List<Task> getAllTaskByAssignee(String simpleClassName, String assignee);

    /**
     * 获取当前正在执行的流程实例
     *
     * @param simpleClassName 实体类的simpleClassName
     * @param objId           实体的id
     * @return ProcessInstance 流程实例
     */
    public ProcessInstance getProcessInstance(String simpleClassName, String objId);

    /**
     * 获取从当前节点出去的线
     *
     * @param simpleClassName 实体类的simpleClassName
     * @param objId           实体的id
     * @param condition       过滤类型
     * @param objects         过滤的数据
     * @return List<CompleteResult>
     */
    public List<CompleteResult> getOutComeFlow(String simpleClassName, String objId, Type condition, List<Object> objects);

    /**
     * 获取当前节点所有进来的线
     *
     * @param processInstanceId   流程实例id
     * @param processDefinitionId 流程定义id
     * @return List<String>
     */
    public List<String> getInComeFlow(String processInstanceId, String processDefinitionId);

    /**
     * 获取当前节点的来路
     *
     * @param simpleClassName 实体类的simpleClassName
     * @param objId           实体的id
     * @return String
     */
    public String getInComingFlow(String simpleClassName, String objId);

    /**
     * 通过Bean的类名以及办理人获取所有实体的id
     *
     * @param simpleClassName 实体类的simpleClassName
     * @param assignee        办理人
     * @param vars            流程变量（作为条件）
     * @return 所有符合条件实体的id，是以逗号（,）隔开的字符串，没有括号
     */
    public String getEntityIds(String simpleClassName, String assignee, Map<String, Object> vars);

    /**
     * 获取某一个变量的值
     *
     * @param simpleClassName 实体类的simpleClassName
     * @param objId           实体的id
     * @param varName         变量名
     * @return Object 变量值
     */
    public Object getVariable(String simpleClassName, String objId, String varName);

    /**
     * 获取流程中的所有变量
     *
     * @param simpleClassName 实体类的simpleClassName
     * @param objId           实体的id
     * @return List<HistoricVariableInstance>
     */
    public List<HistoricVariableInstance> getVariables(String simpleClassName, String objId);

}
