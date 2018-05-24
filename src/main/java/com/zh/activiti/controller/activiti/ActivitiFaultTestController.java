package com.zh.activiti.controller.activiti;

import com.zh.activiti.entity.activiti.CompleteResult;
import com.zh.activiti.service.activiti.ActivitiServiceI;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rebybyx on 2017/3/23.
 */
@Controller
@RequestMapping("/activitiTest")
public class ActivitiFaultTestController {

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
    @RequestMapping(value = "nolimitStartProcess.do", method = RequestMethod.GET)
    public String nolimitStartProcess() {
        CompleteResult cr = activitiService.startProcess("Fault", "211");
        return "Started,status:" + cr.getStatus() + ",process:" + cr.getProcess();
    }

    @ResponseBody
    @RequestMapping(value = "nolimitGetTask.do", method = RequestMethod.GET)
    public String nolimitGetTask(String assignee) {
        Task t = activitiService.getTask("Fault", "211");
        String res = "任务ID：" + t.getId() + " , 任务名称：" + t.getName();

        //res = activitiService.getOutComeFlow("Fault", "21").toString();

        return res;
    }

    @ResponseBody
    @RequestMapping(value = "nolimitCompleteTask.do", method = RequestMethod.GET)
    public String nolimitCompleteTask(String assignee) {
        String res = "";

        Map<String, Object> vars = new HashMap<>();
//        vars.put("confirm", 1);
        vars.put("applyDev", 0);
        CompleteResult cr = activitiService.completeTask("Fault", "211", vars);

//        CompleteResult cr = activitiService.completeTask("Fault", "211");
        res = "completed,执行后,status:" + cr.getStatus() + ",process:" + cr.getProcess();

        return res;
    }

    @ResponseBody
    @RequestMapping(value = "nolimitGetHistory.do", method = RequestMethod.GET)
    public String nolimitGetHistory() {
        List<HistoricActivityInstance> list = activitiService.getHistoryActivity("Fault", "211");

        String str = "";
        for (HistoricActivityInstance instance : list) {
            System.out.println(instance.getActivityName() + " - " + instance.getActivityType());
            str += instance.getActivityName() + "\n";
        }
        return str;
    }

}
