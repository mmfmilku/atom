package org.mmfmilku.atom.web.console.controller;

import org.mmfmilku.atom.api.dto.ExecuteResult;
import org.mmfmilku.atom.web.console.domain.CodeVO;
import org.mmfmilku.atom.web.console.domain.OrdEnum;
import org.mmfmilku.atom.web.console.domain.OrdRunInfo;
import org.mmfmilku.atom.web.console.interfaces.IAgentConfigService;
import org.mmfmilku.atom.web.console.interfaces.IInstrumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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

    @RequestMapping("executeJScript")
    public ExecuteResult executeJScript(@RequestParam String appName,
                                        @RequestParam String jScriptFile) {
        return instrumentService.executeJScript(appName, jScriptFile);
    }

    @RequestMapping("executeJTerminal")
    public ExecuteResult executeJTerminal(@RequestParam String appName,
                                          @RequestBody CodeVO codeVO) {
        return instrumentService.executeJTerminal(appName, codeVO.getId(), codeVO.getCode());
    }

    @RequestMapping("listTerminalId")
    public List<String> listTerminalId(@RequestParam String appName) {
        return instrumentService.listTerminalId(appName);
    }

    @RequestMapping("terminalInfo")
    public Map<String, String> terminalInfo(@RequestParam String appName,
                                            @RequestParam String terminalId) {
        return instrumentService.terminalInfo(appName, terminalId);
    }

    @RequestMapping("newTerminal")
    public String newTerminal(@RequestParam String appName,
                                          @RequestParam String terminalName) {
        return instrumentService.newTerminal(appName, terminalName);
    }

    @RequestMapping("deleteTerminal")
    public String deleteTerminal(@RequestParam String appName,
                                 @RequestParam String terminalId) {
        instrumentService.deleteTerminal(appName, terminalId);
        return "success";
    }

    @RequestMapping("listExecuteOrd")
    public List<OrdRunInfo> listExecuteOrd(@RequestParam String appName,
                                           @RequestParam(required = false) String childPath) {
        // TODO 三种执行类ord路径都相同，先写死传 EXECUTE_ORD
        return agentConfigService.listOrd(appName, childPath, OrdEnum.EXECUTE_ORD);
    }

}
