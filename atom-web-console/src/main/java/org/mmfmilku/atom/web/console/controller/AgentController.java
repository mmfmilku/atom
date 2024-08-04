/********************************************
 * 文件名称: MainController.java
 * 系统名称: 综合理财管理平台6.0
 * 模块名称:
 * 软件版权: 恒生电子股份有限公司
 * 功能说明:
 * 系统版本: 6.0.0.1
 * 开发人员: chenxp
 * 开发时间: 2024/7/26
 * 审核人员:
 * 相关文档:
 * 修改记录:   修改日期    修改人员    修改单号       版本号                   修改说明
 * V6.0.0.1  20240726-01  chenxp   TXXXXXXXXXXXX    IFMS6.0VXXXXXXXXXXXXX   新增 
 *********************************************/
package org.mmfmilku.atom.web.console.controller;

import org.mmfmilku.atom.agent.client.AgentClient;
import org.mmfmilku.atom.web.console.domain.AgentConfig;
import org.mmfmilku.atom.web.console.interfaces.IOrdConfigService;
import org.mmfmilku.atom.web.console.interfaces.IOrdFileOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * MainController
 *
 * @author chenxp
 * @date 2024/7/26:13:59
 */
@RestController
public class AgentController {

    @Autowired
    IOrdConfigService ordConfigService;
    
    @Autowired
    IOrdFileOperation ordFileOperation;

    @RequestMapping("listVm")
    @ResponseBody
    public List<Map<String, String>> listVm() {
        return AgentClient.listVMMap();
    }

    @RequestMapping("loadAgent")
    public String loadAgent(@RequestParam String vmId, @RequestParam String appName) {
        AgentConfig config = ordConfigService.getConfig(appName);
        String dir = ordFileOperation.getDir(config.getOrdId());
        try {
            AgentClient.loadAgent(vmId, "base-path=" + dir);
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }
    
}
