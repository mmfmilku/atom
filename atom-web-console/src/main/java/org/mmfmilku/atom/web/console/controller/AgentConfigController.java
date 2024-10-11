package org.mmfmilku.atom.web.console.controller;

import org.mmfmilku.atom.web.console.domain.AgentConfig;
import org.mmfmilku.atom.web.console.domain.OrdFile;
import org.mmfmilku.atom.web.console.interfaces.IAgentConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AgentConfigController
 *
 * @author mmfmilku
 * @date 2024/7/30:19:45
 */
@RestController
public class AgentConfigController {

    @Autowired
    IAgentConfigService agentConfigService;

    @RequestMapping("getConfig")
    public AgentConfig getConfig(@RequestParam String appName) {
        return agentConfigService.getConfig(appName);
    }

    @RequestMapping("listOrd")
    public List<String> listOrd(@RequestParam String appName) {
        return agentConfigService.listOrd(appName);
    }

    @RequestMapping("deleteOrd")
    public String deleteOrd(@RequestParam String appName, @RequestParam String ordFileName) {
        agentConfigService.deleteOrd(appName, ordFileName);
        return "success";
    }

    @RequestMapping("readOrd")
    public OrdFile readOrd(@RequestParam String appName, @RequestParam String ordFileName) {
        return agentConfigService.readOrd(appName, ordFileName);
    }

    @RequestMapping("writeOrd")
    public String writeOrd(@RequestParam String appName, @RequestBody OrdFile ordFile) {
        agentConfigService.writeOrd(appName, ordFile);
        return "success";
    }

}