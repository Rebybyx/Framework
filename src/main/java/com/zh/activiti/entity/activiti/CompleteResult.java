package com.zh.activiti.entity.activiti;


import org.activiti.engine.task.Task;

/**
 * Created by Rebybyx on 2017/4/24.
 */
public class CompleteResult {

    private String status;
    private int process;
    private Task task;
    private String flowName;
    private Object flowDocumentation;

    public CompleteResult() {
        this.status = "";
        this.process = 0;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public Object getFlowDocumentation() {
        return flowDocumentation;
    }

    public void setFlowDocumentation(Object flowDocumentation) {
        this.flowDocumentation = flowDocumentation;
    }

    @Override
    public String toString() {
        return "CompleteResult{" +
                "status='" + status + '\'' +
                ", process=" + process +
                ", task=" + (task == null ? "null" : task.toString()) +
                ", flowName='" + flowName + '\'' +
                ", flowDocumentation='" + flowDocumentation + '\'' +
                '}';
    }
}
