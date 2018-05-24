package com.zh.activiti.service.activiti;

import com.zh.activiti.entity.activiti.CompleteResult;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipInputStream;

/**
 * @author Rebybyx
 */
@Service
public class ActivitiServiceImpl implements ActivitiServiceI {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HistoryService historyService;

    @Override
    public CompleteResult startProcess(String simpleClassName, String objId, Map<String, Object> vars) {
        runtimeService.startProcessInstanceByKey(simpleClassName, simpleClassName + "." + objId, vars);
        Task t = getTask(simpleClassName, objId);
        return getStatus(t, t.getProcessInstanceId(), t.getProcessDefinitionId());
    }

    @Override
    public CompleteResult startProcess(String simpleClassName, String objId) {
        runtimeService.startProcessInstanceByKey(simpleClassName, simpleClassName + "." + objId);
        Task t = getTask(simpleClassName, objId);
        return getStatus(t, t.getProcessInstanceId(), t.getProcessDefinitionId());
    }

    @Override
    public void deployProcess(String zipPath, String name, String category) {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(zipPath);
        ZipInputStream zipInputStream = new ZipInputStream(is);
        deploymentBuilder.addZipInputStream(zipInputStream).name(name).category(category);
        deploymentBuilder.deploy();
    }

    @Override
    public void deployProcess(String bpmnPath, String pngPath, String name, String category) {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addClasspathResource(bpmnPath).addClasspathResource(pngPath).name(name).category(category);
        deploymentBuilder.deploy();
    }

    @Override
    public void delDeployProcess(String simpleClassName) {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().processDefinitionKey(simpleClassName);
        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();
        String deploymentId = processDefinition.getDeploymentId();
        repositoryService.deleteDeployment(deploymentId, true);
    }

    private CompleteResult getStatus(Task task, String processInstanceId, String processDefinitionId) {
        // get status name
        String statusName = "";
        int process = 0;
        String key = "";

        // ************* 通过当前结点反倒出上一个活动，从而判断是否为网关，同时若已经结束的流程，则直接从历史中找到其结束节点的值
        // 获取流程定义的子类对象
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);

        // 获取流程实例对象
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        String activityId = "";
        if (processInstance == null) {
            // 当前流程实例为null，说明流程已经结束
            HistoricActivityInstance endHis = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .activityType("endEvent")
                    .singleResult();
            statusName = endHis.getActivityName();
            key = endHis.getActivityId();
        } else {
            // 通过当前结点反倒出上一个活动，从而判断是否为网关
            activityId = processInstance.getActivityId();
            ActivityImpl activity = processDefinitionEntity.findActivity(activityId);
            List<PvmTransition> flowList = activity.getIncomingTransitions();
            List<HistoricActivityInstance> hisList = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .orderByHistoricActivityInstanceEndTime().desc()
                    .finished()
                    .list();
            for (HistoricActivityInstance hisActInst : hisList) {
                for (PvmTransition transition : flowList) {
                    transition.getSource().getProperty("name");
                    String actId = transition.getSource().getId();
                    String hisId = hisActInst.getActivityId();
                    if (actId.equals(hisId)) {
                        if ("exclusiveGateway".equals(hisActInst.getActivityType())) {
                            ActivityImpl a = processDefinitionEntity.findActivity(transition.getSource().getId());
                            List<PvmTransition> l = a.getIncomingTransitions();
                            key = l.get(0).getSource().getId();
                            statusName = String.valueOf(transition.getProperty("name"));
                        } else {
                            statusName = String.valueOf(transition.getSource().getProperty("name"));
                            key = actId;
                        }
                        break;
                    }
                }
                if (!statusName.equals("") && !key.equals("")) {
                    break;
                }
            }
//            System.out.println("啊啊啊");
//            for (PvmTransition transition : flowList) {
//                transition.getSource().getProperty("name");
//                String actId = transition.getSource().getId();
//            }
        }


        // 获取已完成的实例对象列表，并按时间倒序排序，
        /*List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
//                .orderByHistoricActivityInstanceEndTime().desc()
                .orderByHistoricActivityInstanceStartTime().desc()
//                .orderByActivityId().desc()
                .finished()
                .list();
        HistoricActivityInstance historicActivityInstance = list.get(0);
        if (historicActivityInstance.getActivityType().equals("exclusiveGateway")) {
            statusName = getTaskIncomingFlow(processInstanceId);
            key = list.get(1).getActivityId();
        } else {
            statusName = historicActivityInstance.getActivityName();
            ProcessDefinitionEntity pde = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(historicActivityInstance.getProcessDefinitionId());
            ActivityImpl activityImpl = pde.findActivity(historicActivityInstance.getActivityId());
            key = activityImpl.getId();
        }*/
        if (statusName == null) {
            System.out.println("111");
        }
        // get process
        String ss[] = key.split("_");
        key = ss[ss.length - 1];
        try {
            process = Integer.valueOf(key);
        } catch (Exception e) {
        }

        CompleteResult cr = new CompleteResult();
        cr.setProcess(process);
        cr.setStatus(statusName);
        return cr;
    }

    private int getProcess(Task task) {
        int process = 0;
        String key = task.getTaskDefinitionKey();
        String ss[] = key.split("_");
        key = ss[ss.length - 1];
        try {
            process = Integer.valueOf(key);
        } catch (Exception e) {
        }
        return process;
    }

    @Override
    public CompleteResult completeTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return new CompleteResult();
        }
        taskService.complete(task.getId());
        Task task_now = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        return getStatus(task_now, task.getProcessInstanceId(), task.getProcessDefinitionId());
    }

    @Override
    public CompleteResult completeTask(String taskId, Map<String, Object> vars) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return new CompleteResult();
        }
        taskService.complete(task.getId(), vars);
        Task task_now = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        return getStatus(task_now, task.getProcessInstanceId(), task.getProcessDefinitionId());
    }

    @Override
    public CompleteResult completeTask(String simpleClassName, String objId) {
        Task task = this.getTask(simpleClassName, objId);
        if (task == null) {
            return new CompleteResult();
        }
        taskService.complete(task.getId());
        Task task_now = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        return getStatus(task_now, task.getProcessInstanceId(), task.getProcessDefinitionId());
    }

    @Override
    public CompleteResult completeTask(String simpleClassName, String objId, Map<String, Object> vars) {
        Task task = this.getTask(simpleClassName, objId);
        if (task == null) {
            return new CompleteResult();
        }
        taskService.complete(task.getId(), vars);
        Task task_now = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        return getStatus(task_now, task.getProcessInstanceId(), task.getProcessDefinitionId());
    }

    @Override
    public Task getTask(String simpleClassName, String objId) {
        Task task = taskService.createTaskQuery().processInstanceBusinessKey(simpleClassName + "." + objId).singleResult();
        return task;
    }

    @Override
    public CompleteResult getCurrentTask(String simpleClassName, String objId) {
        Task task = this.getTask(simpleClassName, objId);
        if (task == null) {
            return new CompleteResult();
        }
        Task task_now = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        return getStatus(task_now, task.getProcessInstanceId(), task.getProcessDefinitionId());
    }

    @Override
    public List<HistoricActivityInstance> getHistoryActivity(String simpleClassName, String objId) {
        String processInstanceId = "";
        Task task = this.getTask(simpleClassName, objId);
        if (task == null) {
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceBusinessKey(simpleClassName + "." + objId)
                    .singleResult();
            processInstanceId = historicProcessInstance.getId();
        } else {
            processInstanceId = task.getProcessInstanceId();
        }
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
//                .activityType("userTask")
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list();
        return list;
    }

    @Override
    public List<HistoricActivityInstance> getHistoryActivityFinished(String simpleClassName, String objId) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(simpleClassName + "." + objId)
                .singleResult();
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(historicProcessInstance.getId())
                .activityType("userTask")
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list();
        return list;
    }

    @Override
    public CompleteResult completeTaskByAssignee(String taskId, String assignee) {
        Task task = taskService.createTaskQuery().taskId(taskId).taskAssignee(assignee).singleResult();
        if (task == null) {
            return new CompleteResult();
        }
        taskService.complete(task.getId());
        Task task_now = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        return getStatus(task_now, task.getProcessInstanceId(), task.getProcessDefinitionId());
    }

    @Override
    public CompleteResult completeTaskByAssignee(String taskId, String assignee, Map<String, Object> vars) {
        Task task = taskService.createTaskQuery().taskId(taskId).taskAssignee(assignee).singleResult();
        if (task == null) {
            return new CompleteResult();
        }
        taskService.complete(task.getId(), vars);
        Task task_now = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        return getStatus(task_now, task.getProcessInstanceId(), task.getProcessDefinitionId());
    }

    @Override
    public CompleteResult completeTaskByAssignee(String simpleClassName, String objId, String assignee) {
        Task task = this.getTaskByAssignee(simpleClassName, objId, assignee);
        if (task == null) {
            return new CompleteResult();
        }
        taskService.complete(task.getId());
        Task task_now = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        return getStatus(task_now, task.getProcessInstanceId(), task.getProcessDefinitionId());
    }

    @Override
    public CompleteResult completeTaskByAssignee(String simpleClassName, String objId, String assignee, Map<String, Object> vars) {
        Task task = this.getTaskByAssignee(simpleClassName, objId, assignee);
        if (task == null) {
            return new CompleteResult();
        }
        taskService.complete(task.getId(), vars);
        Task task_now = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        return getStatus(task_now, task.getProcessInstanceId(), task.getProcessDefinitionId());
    }

    @Override
    public Task getTaskByAssignee(String simpleClassName, String objId, String assignee) {
        Task task = taskService.createTaskQuery().processInstanceBusinessKey(simpleClassName + "." + objId).taskAssignee(assignee).singleResult();
        return task;
    }

    @Override
    public List<Task> getAllTaskByAssignee(String simpleClassName, String assignee) {
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey(simpleClassName)
                .taskAssignee(assignee)
                .list();
        return list;
    }

    @Override
    public ProcessInstance getProcessInstance(String simpleClassName, String objId) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(simpleClassName + "." + objId)
                .singleResult();
        return processInstance;
    }

    @Override
    public List<CompleteResult> getOutComeFlow(String simpleClassName, String objId, Type condition, List<Object> objects) {
        Task task = this.getTask(simpleClassName, objId);

        // 获取流程定义的子类对象
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(task.getProcessDefinitionId());

        // 获取流程实例对象
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();

        // 获取正在执行的活动id
        String activityId = processInstance.getActivityId();

        //获取正在执行的活动（包括流程图上的所有节点）
        ActivityImpl activity = processDefinitionEntity.findActivity(activityId);

        // 获取从当前节点出去的线的对象
        List<PvmTransition> list = activity.getOutgoingTransitions();//"type" -> "exclusiveGateway"
        if (list != null && list.size() == 1) {
            PvmTransition transition = list.get(0);
            if (transition != null && "exclusiveGateway".equals(transition.getDestination().getProperty("type"))) {
                ActivityImpl a = processDefinitionEntity.findActivity(transition.getDestination().getId());
                list = a.getOutgoingTransitions();
            }
        }

        List<CompleteResult> outList = new ArrayList<>();
        switch (condition) {
            case EQ:

                for (PvmTransition transition : list) {
                    if (transition.getProperty("documentation") == null) {
                        continue;
                    }
                    for (Object object : objects) {
                        if (object.equals(transition.getProperty("documentation"))) {
                            CompleteResult cr = new CompleteResult();
                            cr.setFlowName(String.valueOf(transition.getProperty("name")));
                            cr.setFlowDocumentation(transition.getProperty("documentation"));
                            outList.add(cr);
                            break;
                        }
                    }

                }
                break;
            case NEQ:
                for (PvmTransition transition : list) {
                    if (transition.getProperty("documentation") == null) {
                        continue;
                    }
                    boolean isAdd = true;
                    for (Object object : objects) {
                        if (object.toString().equals(transition.getProperty("documentation"))) {
                            isAdd = false;
                            break;
                        }
                    }
                    if (isAdd) {
                        CompleteResult cr = new CompleteResult();
                        cr.setFlowName(String.valueOf(transition.getProperty("name")));
                        cr.setFlowDocumentation(transition.getProperty("documentation"));
                        outList.add(cr);
                    }
                }
                break;

            case NONE:
                for (PvmTransition transition : list) {
                    if (transition.getProperty("documentation") == null) {
                        continue;
                    }
                    CompleteResult cr = new CompleteResult();
                    cr.setFlowName(String.valueOf(transition.getProperty("name")));
                    cr.setFlowDocumentation(transition.getProperty("documentation"));
                    outList.add(cr);
                }
                break;
        }

        return outList;
    }

    @Override
    public List<String> getInComeFlow(String processInstanceId, String processDefinitionId) {
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        String activityId = "";
        if (processInstance == null) {
//            HistoricProcessInstance h = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            List<HistoricActivityInstance> list = historyService
                    .createHistoricActivityInstanceQuery()        // 创建查询对象
                    .processInstanceId(processInstanceId)    // 根据当前实例的id查询
                    .activityType("endEvent")
                    .orderByHistoricActivityInstanceEndTime().desc()
//                .finished()        // 已经结束的活动节点
                    .list();
            activityId = list.get(0).getActivityId();
        } else {
            activityId = processInstance.getActivityId();
        }

        ActivityImpl activity = processDefinitionEntity.findActivity(activityId);

        List<PvmTransition> list = activity.getIncomingTransitions();
        List<String> inList = new ArrayList<>();
        for (PvmTransition transition : list) {
            inList.add((String) transition.getProperty("name"));
        }
        System.out.println(inList.toString());
        return inList;
    }

    private String getTaskIncomingFlow(String processInstanceId) {
        // 获得当前流程实例所经历的历史任务
        List<HistoricActivityInstance> list = historyService
                .createHistoricActivityInstanceQuery()        // 创建查询对象
                .processInstanceId(processInstanceId)    // 根据当前实例的id查询
                .activityType("exclusiveGateway")
                .orderByHistoricActivityInstanceEndTime().desc()
//                .finished()        // 已经结束的活动节点
                .list();

        // 获取前一个活动节点的活动id
        String activityId = list.get(0).getActivityId();
        String processDefinitionId = list.get(0).getProcessDefinitionId();

        // 获取流程定义实体的对象（不需要创建查询对象，否则会报错）
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);

        // 获取前一个活动节点的实现类对象
        ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);

        List<String> outcomeFlow = new ArrayList<>();

        // 获取前一个节点的出路
        List<PvmTransition> transitions = activityImpl.getOutgoingTransitions();
        for (PvmTransition transition : transitions) {
            String flowName = (String) transition.getProperty("name");
            System.out.println(flowName);
            outcomeFlow.add(flowName);
        }
        System.out.println("out:" + outcomeFlow.toString());

        // 获得当前结点的所有入路
        List<String> incomeFlow = this.getInComeFlow(processInstanceId, processDefinitionId);
        System.out.println("out:" + outcomeFlow.toString());
        for (String string : incomeFlow) {
            // 对比获取相应线的名字
            if (outcomeFlow.contains(string)) {
                return string;
            }
        }
        return "";
    }

    @Override
    public String getInComingFlow(String simpleClassName, String objId) {
        Task t = getTask(simpleClassName, objId);
        return getTaskIncomingFlow(t.getProcessInstanceId());
    }

    @Override
    public String getEntityIds(String simpleClassName, String assignee, Map<String, Object> vars) {
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery().processDefinitionKey(simpleClassName);
        if (vars != null) {
            Set<String> keys = vars.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object value = vars.get(key);
                if (value != null) {
                    historicProcessInstanceQuery.variableValueEquals(key, value);
                }
            }
        }
        List<HistoricProcessInstance> list = historicProcessInstanceQuery.orderByProcessInstanceEndTime().desc().orderByProcessInstanceStartTime().desc().list();
        String ids = "";
        for (HistoricProcessInstance processsInstance :
                list) {
            String bKey = processsInstance.getBusinessKey();
            if (bKey != null && !bKey.equals("")) {
                String bk[] = bKey.split("\\.");
                if (bk.length == 2) {
                    ids += "," + bk[1];
                }
            }
        }
        if (ids.equals("")) {
            return ids;
        } else {
            return ids.substring(1);
        }
    }

    @Override
    public Object getVariable(String simpleClassName, String objId, String varName) {
        HistoricProcessInstance hp = historyService.createHistoricProcessInstanceQuery().processDefinitionKey(simpleClassName).processInstanceBusinessKey(simpleClassName + "." + objId).singleResult();
        HistoricVariableInstance hvi = historyService.createHistoricVariableInstanceQuery().processInstanceId(hp.getId()).variableName(varName).singleResult();
        return hvi.getValue();
    }

    @Override
    public List<HistoricVariableInstance> getVariables(String simpleClassName, String objId) {
        HistoricProcessInstance hp = historyService.createHistoricProcessInstanceQuery().processDefinitionKey(simpleClassName).processInstanceBusinessKey(simpleClassName + "." + objId).singleResult();
        List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().processInstanceId(hp.getId()).list();
        return list;
    }
}

