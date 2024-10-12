package org.mmfmilku.atom.web.console.controller;

import org.mmfmilku.atom.agent.client.AgentClient;
import org.mmfmilku.atom.web.console.interfaces.IAgentService;
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
    IAgentConfigService agentConfigService;

    @Autowired
    IAgentService agentService;
    
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
        boolean success = agentService.loadAgent(vmId, appName, basePackage);
        return String.valueOf(success);
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
