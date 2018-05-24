package com.zh.activiti.util.activiti;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * Created by Rebybyx on 2017/4/14.
 */
public class ServiceTask implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Thread.sleep(1000);
        System.out.println("Service_Task_Name:delegateExecution = [" + delegateExecution.getEventName() + "]");
    }
}
