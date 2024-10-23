package org.mmfmilku.atom.web.console.controller;

import org.mmfmilku.atom.agent.client.AgentClient;
import org.mmfmilku.atom.util.StringUtils;
import org.mmfmilku.atom.web.console.interfaces.IAgentService;
import org.mmfmilku.atom.web.console.interfaces.IInstrumentService;
import org.mmfmilku.atom.web.console.interfaces.IAgentConfigService;
import org.mmfmilku.atom.web.console.interfaces.IOrdFileOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * MainController
 *
 * @author mmfmilku
 * @date 2024/7/26:13:59
 */
@RestController
@RequestMapping("agent")
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

    @RequestMapping("vmInfo")
    public Map<String, String> vmInfo(@RequestParam String vmId) {
        for (Map<String, String> vmMap : AgentClient.listVMMap()) {
            if (vmMap.get("vmId").equals(vmId)) {
                return vmMap;
            }
        }
        return Collections.emptyMap();
    }
    
    @RequestMapping("loadAgent")
    public String loadAgent(@RequestParam String vmId, @RequestParam String appName) {
        boolean success = agentService.loadAgent(vmId, appName);
        return String.valueOf(success);
    }

    @RequestMapping("listClass")
    @ResponseBody
    public List<String> listClass(@RequestParam String appName, @RequestParam int offset,
                                  @RequestParam(required = false) String classShortNameLike) {
        if (!StringUtils.isEmpty(classShortNameLike)) {
            return instrumentService.listClassForPage(appName, offset, 20, classShortNameLike);
        }
        return instrumentService.listClassForPage(appName, offset, 20);
    }

    @RequestMapping("genSource")
    public String genSource(@RequestParam String appName, @RequestParam String fullClassName) {
        return instrumentService.decompile(appName, fullClassName);
    }

    @RequestMapping("retransform")
    public String retransform(@RequestParam String appName, @RequestParam String fullClassName) {
        instrumentService.retransformClass(appName, fullClassName);
        return "success";
    }

    @RequestMapping("loadOrdFile")
    public String loadOrdFile(@RequestParam String appName, @RequestParam String ordFileName) {
        instrumentService.loadOrdFile(appName, ordFileName);
        return "success";
    }
    
}
