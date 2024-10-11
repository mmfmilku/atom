package org.mmfmilku.atom.web.console.controller;

import org.mmfmilku.atom.agent.client.AgentClient;
import org.mmfmilku.atom.api.AppInfoApi;
import org.mmfmilku.atom.transport.frpc.client.FRPCFactory;
import org.mmfmilku.atom.web.console.domain.AgentConfig;
import org.mmfmilku.atom.web.console.interfaces.IInstrumentService;
import org.mmfmilku.atom.web.console.interfaces.IAgentConfigService;
import org.mmfmilku.atom.web.console.interfaces.IOrdFileOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * MainController
 *
 * @author mmfmilku
 * @date 2024/7/26:13:59
 */
@RestController
public class AgentController {

    @Autowired
    IAgentConfigService ordConfigService;
    
    @Autowired
    IOrdFileOperation ordFileOperation;
    
    @Autowired
    IInstrumentService instrumentService;

    @RequestMapping("listVm")
    @ResponseBody
    public List<Map<String, String>> listVm() {
        return AgentClient.listVMMap();
    }
    
    @RequestMapping("loadAgent")
    public String loadAgent(@RequestParam String vmId, @RequestParam String appName, @RequestParam(required = false) String basePackage) {
        AgentConfig config = ordConfigService.getConfig(appName);
        // TODO,ord文件读取不支持低柜文件夹，暂时使用ord目录
        String dir = config.getOrdDir();
        // TODO 配置 classloader
        String customClassloader = "org.springframework.boot.loader.LaunchedURLClassLoader";
        try {
            AgentClient.loadAgent(vmId, "base-path=" + dir 
                    + ";app-classloader=" + customClassloader
                    + ";app-base-package=" + basePackage
                    + ";app-fserver-dir=" + config.getFDir()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        
        AppInfoApi infoApi = FRPCFactory.getService(AppInfoApi.class, config.getFDir());
        infoApi.ping();

        return "success";
    }

    @RequestMapping("listClass")
    @ResponseBody
    public List<String> listClass(@RequestParam String appName, @RequestParam int offset, @RequestParam int size,
                                  @RequestParam String classShortNameLike) {
        if (classShortNameLike != null) {
            return instrumentService.listClassForPage(appName, offset, 1000, classShortNameLike);
        }
        return instrumentService.listClassForPage(appName, offset, 1000);
    }

    @RequestMapping("genSource")
    public String genSource(@RequestParam String appName, @RequestParam String fullClassName) {
        return instrumentService.decompile(appName, fullClassName);
    }
    
}
