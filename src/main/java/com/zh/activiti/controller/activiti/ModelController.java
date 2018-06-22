package com.zh.activiti.controller.activiti;

import org.activiti.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Rebybyx on 2018/5/25 14:10.
 */
@RestController
public class ModelController {

    @Autowired
    private RepositoryService repositoryService;

    @RequestMapping(value = "model/create")
    public void create(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/modeler.html?modelId=1474");
    }

}
