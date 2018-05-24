package com.zh.activiti.controller.activiti;

import com.zh.activiti.entity.activiti.CompleteResult;
import com.zh.activiti.service.activiti.ActivitiServiceI;
import com.zh.activiti.util.activiti.SaxXmlUtil;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rebybyx on 2017/3/23.
 */
@Controller
@RequestMapping("/activiti")
public class ActivitiController {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ActivitiServiceI activitiService;

    @ResponseBody
    @RequestMapping(value = "nolimitdeploy.do", method = RequestMethod.GET)
    public String nolimitdeploy() {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();

        // 加载流程的配置文件和图片
        deploymentBuilder.addClasspathResource("diagrams/baoxiao.bpmn").addClasspathResource("diagrams/baoxiao.png")
                .name("报销流程").category("报销");

        // 部署流程
        deploymentBuilder.deploy();
        return "aaa";
    }

    @ResponseBody
    @RequestMapping(value = "nolimitdeldeploy.do", method = RequestMethod.GET)
    public String nolimitdeldeploy(String key) {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().processDefinitionKey(key);
        List<ProcessDefinition> list = processDefinitionQuery.list();
        for (ProcessDefinition processDefinition : list
                ) {
            String deploymentId = processDefinition.getDeploymentId();
            repositoryService.deleteDeployment(deploymentId, true);
        }
        return "已删除";
    }

    @ResponseBody
    @RequestMapping(value = "nolimitStartProcess.do", method = RequestMethod.GET)
    public String nolimitStartProcess() {
//        runtimeService.startProcessInstanceByKey("baoxiao_bill");
//        runtimeService.startProcessInstanceByKey("TbAlarm", "TbAlarm.5", new HashMap<String, Object>());
        CompleteResult cr = activitiService.startProcess("TbAlarm", "7");
        return "Started,status:" + cr.getStatus() + ",process:" + cr.getProcess();
    }

    @ResponseBody
    @RequestMapping(value = "nolimitGetTask.do", method = RequestMethod.GET)
    public String nolimitGetTask(String assignee) {
//        System.out.println(assignee);
//        List<Task> taskList = taskService.createTaskQuery()
////                .processDefinitionKey("TbAlarm")
//                .processInstanceBusinessKey("TbAlarm.5")
//        Task task = taskService.createTaskQuery().processDefinitionKey("BaoXiao")
//                .processInstanceBusinessKey("baoxiao")
//                .taskAssignee(assignee)
//                .list();
//                .singleResult();
//        String res = "任务ID：" + task.getId() + " , 任务名称：" + task.getName();
        /*for (Task t :
                taskList) {
            res += "<br>任务ID：" + t.getId() + " , 任务名称：" + t.getName();
        }*/

        Task t = activitiService.getTask("TbAlarm", "7");
        String res = "";
        res += "<br>任务ID：" + t.getId() + " , 任务名称：" + t.getName();
        res = res.substring(4);
        return res;
    }

    @ResponseBody
    @RequestMapping(value = "nolimitCompleteTaskByAss.do", method = RequestMethod.GET)
    public String nolimitCompleteTask(String assignee, int money) {
        Task task = taskService.createTaskQuery().processDefinitionKey("BaoXiao").processInstanceBusinessKey("baoxiao").taskAssignee(assignee).singleResult();

        Map<String, Object> params = new HashMap<>();
        params.put("money", money);

        taskService.complete(task.getId(), params);

        Task t = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        return "completed,执行后，任务ID：" + t.getId() + " , 任务名称：" + t.getName();
    }

    @ResponseBody
    @RequestMapping(value = "nolimitCompleteTask.do", method = RequestMethod.GET)
    public String nolimitCompleteTask(String assignee) {
//        Task task = taskService.createTaskQuery().processInstanceId("127507")
////                .taskAssignee(assignee)
//                .singleResult();

//        Task task = taskService.createTaskQuery().processDefinitionKey("BaoXiao").processInstanceBusinessKey("baoxiao").taskAssignee(assignee).singleResult();
//        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();

//        Map<String, Object> vars = new HashMap<>();
//        vars.put("applyDev", 0);
//
//        taskService.complete(task.getId(), vars);
//        taskService.complete(task.getId());

//        Task t = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
//        Task t = null;o

        String res = "";
//        if (t == null) {
//            List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().processInstanceId("127507")
//                    .orderByHistoricActivityInstanceEndTime().desc()
//                    .orderByHistoricActivityInstanceStartTime().desc()
//                    .list();
////            List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().processInstanceId(task.getProcessInstanceId()).orderByHistoricActivityInstanceEndTime().desc().list();
//            HistoricActivityInstance historicActivityInstance = list.get(0);
//            res = "completed,执行后，任务ID：" + historicActivityInstance.getId() + " , 任务名称：" + historicActivityInstance.getActivityName();
//            res += "<br><br>历史：<br>";
//            for (HistoricActivityInstance hi : list) {
//                res += "任务ID：" + hi.getId() + " , 任务名称：" + hi.getActivityName() + "<br>";
//            }
//
//        } else {
//            res = "completed,执行后，任务ID：" + t.getId() + " , 任务名称：" + t.getName();
//        }

//        Map<String, Object> vars = new HashMap<>();
//        vars.put("applyDev", 1);
//        CompleteResult cr = activitiService.completeTask("TbAlarm", "7", vars);
        CompleteResult cr = activitiService.completeTask("TbAlarm", "7");
        res = "completed,执行后,status:" + cr.getStatus() + ",process:" + cr.getProcess();

        return res;
    }

    @ResponseBody
    @RequestMapping(value = "nolimitGetHistory.do", method = RequestMethod.GET)
    public String nolimitGetHistory() {
//        Task t = activitiService.getTask("TbAlarm","d73bc842-40a0-458c-91bb-6080eb91d3ef");
//        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
//                .processInstanceId("95005")
////                .activityType("userTask")
//                .orderByHistoricActivityInstanceStartTime()
//                .desc()
//                .list();
       /* List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(t.getProcessInstanceId())
//                .activityType("userTask")
                .orderByHistoricActivityInstanceStartTime()
                .desc()
                .list();*/
//        List<HistoricActivityInstance> list =activitiService.getHistoryActivityFinished(TbAlarm.class.getSimpleName(),"d73bc842-40a0-458c-91bb-6080eb91d3ef");
        return  activitiService.getCurrentTask("TbAlarmJFW", "9487b68d-f49a-4208-8f8d-f76ce3618416").toString();

//        List<HistoricActivityInstance> list = activitiService.getHistoryActivity("TbAlarmJFW", "4eae89e6-81ac-48b7-9eb1-8a37dd8476f1");
//
//        String str = "";
//        for (HistoricActivityInstance instance : list) {
//            System.out.println(instance.getActivityName() + " - " + instance.getActivityType());
//            str += instance.getActivityName() + "\n";
//        }
//        return str;
    }

    @ResponseBody
    @RequestMapping(value = "nolimitDelAllDeploy.do", method = RequestMethod.GET)
    public String nolimitDelAllDeploy() {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        List<ProcessDefinition> list = processDefinitionQuery.list();
        String names = "";
        for (ProcessDefinition processDefinition : list) {
            String deploymentId = processDefinition.getDeploymentId();
            names += "," + processDefinition.getName();
            repositoryService.deleteDeployment(deploymentId, true);
        }
        return "已删除，包括：" + names.substring(1);
    }

    @ResponseBody
    @RequestMapping(value = "nolimitTest.do", method = RequestMethod.GET)
    public void nolimitTest() {
//        activitiService.deployProcess("diagrams/baoxiao.bpmn","diagrams/baoxiao.png", "报销流程", "报销");
//        activitiService.startProcess("baoxiao_bill", "1");
//
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("money", 5001);
//        activitiService.completeTask("baoxiao_bill", "1", params);

//        activitiService.completeTask("baoxiao_bill", "1");
//        activitiService.completeTask("baoxiao_bill", "1");

//        List<HistoricActivityInstance> list = activitiService.getHistoryActivity("baoxiao_bill", "1");
//        for (HistoricActivityInstance instance:list) {
//            System.out.println(instance.getActivityName());
//        }

//        List<HistoricActivityInstance> list = activitiService.getHistoryActivityFinished("BaoXiao", "1");
//        for (HistoricActivityInstance instance:list) {
//            System.out.println(instance.getActivityName());
//        }
        SaxXmlUtil sax = new SaxXmlUtil();
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("diagrams/TbAlarm.bpmn");
        Map<String,String> map = sax.getDiagramName(in);
    }

}
