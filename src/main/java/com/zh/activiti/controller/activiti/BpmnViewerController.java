package com.zh.activiti.controller.activiti;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.ProcessEngineImpl;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Model;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 流程图片进度跟踪
 * <p>
 * Created by Rebybyx on 2017/4/24.
 */
@Controller
@RequestMapping("/activiti")
public class BpmnViewerController {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;

    @ResponseBody
    @RequestMapping(value = "getModel.do", method = RequestMethod.GET)
    public void getModel(String modelId, HttpServletResponse response) {
        // 设置页面不缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        try {
//            Model m = repositoryService.getModel("1474");
//            BpmnModel bpmnModel = repositoryService.getBpmnModel("process:1:6562");
//            System.out.println();

            JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelId));
            BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
            BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
//            filename = model.getMainProcess().getId() + ".bpmn20.xml";
//            bpmnBytes = new BpmnXMLConverter().convertToXML(model);
//
//            ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);


            // 设置图片的字体
            ProcessEngineImpl defaultProcessEngine = (ProcessEngineImpl) ProcessEngines
                    .getDefaultProcessEngine();
            defaultProcessEngine.getProcessEngineConfiguration()
                    .setActivityFontName("宋体");  // 有中文的话防止图片中出现乱码，否则会显示类似于“□”这样的字
            defaultProcessEngine.getProcessEngineConfiguration()
                    .setLabelFontName("宋体");
            Context.setProcessEngineConfiguration(defaultProcessEngine
                    .getProcessEngineConfiguration());

            ProcessDiagramGenerator processDiagramGenerator = new DefaultProcessDiagramGenerator();
            InputStream imageStream = processDiagramGenerator.generateDiagram(
                    bpmnModel, "png", new ArrayList<>(), new ArrayList<>(),
                    defaultProcessEngine.getProcessEngineConfiguration().getActivityFontName(),
                    defaultProcessEngine.getProcessEngineConfiguration().getLabelFontName(),
                    defaultProcessEngine.getProcessEngineConfiguration().getAnnotationFontName(), null, 1.0);
            response.setContentType("image/png");

            OutputStream os = response.getOutputStream();
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = imageStream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            imageStream.close();
        } catch (
                Exception e)

        {
            e.printStackTrace();
//            throw new RuntimeException("获取流程图异常!");
        }

    }

    @ResponseBody
    @RequestMapping(value = "nolimitbpmnView.do", method = RequestMethod.GET)
    public void bpmnView(String proKey, String objId, HttpServletResponse response) {

        // 设置页面不缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        try {
            String instanceId = "";
            String proDefId = "";
            // 获取流程实例
//            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey("TbAlarm.5").singleResult();
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(proKey + "." + objId).singleResult();
            if (processInstance == null) {
                HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(proKey + "." + objId).singleResult();
                instanceId = hpi.getId();
                proDefId = hpi.getProcessDefinitionId();
            } else {
                instanceId = processInstance.getProcessInstanceId();
                proDefId = processInstance.getProcessDefinitionId();
            }

            if ("".equals(instanceId) || "".equals(proDefId)) {
                throw new Exception("获取流程异常");
            } else {

                BpmnModel bpmnModel = repositoryService
                        .getBpmnModel(proDefId);
                List<HistoricActivityInstance> activityInstances = historyService
                        .createHistoricActivityInstanceQuery()
                        .processInstanceId(instanceId)
                        //.finished()
                        .orderByHistoricActivityInstanceStartTime().asc()
                        .list();

                List<String> activitiIds = new ArrayList<>();

                ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                        .getDeployedProcessDefinition(proDefId);
                for (HistoricActivityInstance hai : activityInstances) {
                    activitiIds.add(hai.getActivityId());// 获取流程走过的节点
                }
                List<String> flowIds = getHighLightedFlows(processDefinition,
                        activityInstances);// 获取流程走过的线 (getHighLightedFlows是下面的方法)

                // 设置图片的字体
                ProcessEngineImpl defaultProcessEngine = (ProcessEngineImpl) ProcessEngines
                        .getDefaultProcessEngine();
                defaultProcessEngine.getProcessEngineConfiguration()
                        .setActivityFontName("宋体");  // 有中文的话防止图片中出现乱码，否则会显示类似于“□”这样的字
                defaultProcessEngine.getProcessEngineConfiguration()
                        .setLabelFontName("宋体");
                Context.setProcessEngineConfiguration(defaultProcessEngine
                        .getProcessEngineConfiguration());

                ProcessDiagramGenerator processDiagramGenerator = new DefaultProcessDiagramGenerator();
                InputStream imageStream = processDiagramGenerator.generateDiagram(
                        bpmnModel, "png", activitiIds, flowIds,
                        defaultProcessEngine.getProcessEngineConfiguration().getActivityFontName(),
                        defaultProcessEngine.getProcessEngineConfiguration().getLabelFontName(),
                        defaultProcessEngine.getProcessEngineConfiguration().getAnnotationFontName(), null, 1.0);
                response.setContentType("image/png");

                OutputStream os = response.getOutputStream();
                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                while ((bytesRead = imageStream.read(buffer, 0, 8192)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.close();
                imageStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
//            throw new RuntimeException("获取流程图异常!");
        }
    }

    private List<String> getHighLightedFlows(ProcessDefinitionEntity processDefinitionEntity, List<HistoricActivityInstance> historicActivityInstances) {
        List<String> highFlows = new ArrayList<>();// 用 以保存高亮的线flowId
        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {// 对历史流程节点进行遍历
            ActivityImpl activityImpl = processDefinitionEntity
                    .findActivity(historicActivityInstances.get(i)
                            .getActivityId());// 得到节点定义的详细信息

            List<ActivityImpl> sameStartTimeNodes = new ArrayList<>(); // 用以保存后需开始时间相同的节点
            ActivityImpl sameActivityImpl1 = processDefinitionEntity
                    .findActivity(historicActivityInstances.get(i + 1)
                            .getActivityId()); // 将后面第一个节点放在时间相同节点的集合里
            sameStartTimeNodes.add(sameActivityImpl1);
            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
                HistoricActivityInstance activityImpl1 = historicActivityInstances
                        .get(j); // 后续第一个节点
                HistoricActivityInstance activityImpl2 = historicActivityInstances
                        .get(j + 1); // 后续第二个节点
                if (activityImpl1.getStartTime().equals(
                        activityImpl2.getStartTime())) { // 如果第一个节点和第二个节点开始时间相同保存
                    ActivityImpl sameActivityImpl2 = processDefinitionEntity
                            .findActivity(activityImpl2.getActivityId());
                    sameStartTimeNodes.add(sameActivityImpl2);
                } else {
                    break;
                }
            }
            List<PvmTransition> pvmTransitions = activityImpl.getOutgoingTransitions();// 取出节点的所有出去的线
            for (PvmTransition pvmTransition : pvmTransitions) { // 对所有的线进行遍历
                ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition
                        .getDestination();// 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                    highFlows.add(pvmTransition.getId());
                }
            }

        }
        return highFlows;
    }
}