package org.mmfmilku.atom.web.console.controller;

import org.mmfmilku.atom.api.dto.ExecuteResult;
import org.mmfmilku.atom.web.console.domain.OrdEnum;
import org.mmfmilku.atom.web.console.domain.OrdRunInfo;
import org.mmfmilku.atom.web.console.interfaces.IAgentConfigService;
import org.mmfmilku.atom.web.console.interfaces.IInstrumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("executeConsole")
public class ExecuteConsoleController {

    @Autowired
    IInstrumentService instrumentService;

    @Autowired
    IAgentConfigService agentConfigService;

    @RequestMapping("execute")
    public ExecuteResult execute(@RequestParam String appName, @RequestParam String executeFile) {
        // TODO 入参待支持
        return instrumentService.execute(appName, executeFile);
    }

    @RequestMapping("listExecuteOrd")
    public List<OrdRunInfo> listExecuteOrd(@RequestParam String appName,
                                           @RequestParam(required = false) String childPath) {
        return agentConfigService.listOrd(appName, childPath, OrdEnum.EXECUTE_ORD);
    }

}
