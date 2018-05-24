package com.zh.activiti.interceptor;

import com.zh.activiti.util.activiti.SaxXmlUtil;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by Rebybyx on 2017/4/20.
 */
public class ActivitiDiagramDeployer implements InitializingBean, ApplicationContextAware {

    private static final Log LOGGER = LogFactory.getLog(ActivitiDiagramDeployer.class);
    private Resource[] deploymentResources;
    private String category;
    ApplicationContext appCtx;

    public void setDeploymentResources(Resource[] resources) {
        this.deploymentResources = resources;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.appCtx = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (category == null) {
            throw new FatalBeanException("缺失属性 : category");
        }
        if (deploymentResources != null) {
            RepositoryService repositoryService = appCtx
                    .getBean(RepositoryService.class);
            for (Resource r : deploymentResources) {
                Map<String, String> nameMap = null;
                SaxXmlUtil sax = new SaxXmlUtil();
                nameMap = sax.getDiagramName(r.getInputStream());

                String deploymentName = nameMap.get("name");
                String deploymentCategory = nameMap.get("category");
//                String deploymentName = category + "_" + r.getFilename();
                String resourceName = r.getFilename();
                boolean doDeploy = true;
                List<Deployment> deployments = repositoryService
                        .createDeploymentQuery().deploymentName(deploymentName).deploymentCategory(deploymentCategory)
                        .orderByDeploymenTime().desc().list();
                if (!deployments.isEmpty()) {
                    Deployment existing = deployments.get(0);
                    try {
                        InputStream in = repositoryService.getResourceAsStream(
                                existing.getId(), resourceName);
                        if (in != null) {
                            File f = File.createTempFile(
                                    "deployment",
                                    "xml",
                                    new File(System
                                            .getProperty("java.io.tmpdir")));
                            f.deleteOnExit();
                            OutputStream out = new FileOutputStream(f);
                            IOUtils.copy(in, out);
                            in.close();
                            out.close();
                            doDeploy = (FileUtils.checksumCRC32(f) != FileUtils
                                    .checksumCRC32(r.getFile()));
                        } else
                            throw new ActivitiException("不能读取资源 "
                                    + resourceName + ", 输入流为空");
                    } catch (ActivitiException ex) {
                        LOGGER.error("Unable to read " + resourceName
                                + " of deployment " + existing.getName()
                                + ", id: " + existing.getId()
                                + ", will re-deploy");
                    }
                }
                if (doDeploy) {
                    repositoryService.createDeployment().name(nameMap.get("name")).category(nameMap.get("category"))
                            .addInputStream(resourceName, r.getInputStream())
                            .deploy();
                    LOGGER.warn("文件部署成功 : " + r.getFilename());
                }
            }
        }
    }

}
