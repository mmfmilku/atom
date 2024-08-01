/********************************************
 * 文件名称: AgentConfigController.java
 * 系统名称: 综合理财管理平台6.0
 * 模块名称:
 * 软件版权: 恒生电子股份有限公司
 * 功能说明:
 * 系统版本: 6.0.0.1
 * 开发人员: chenxp
 * 开发时间: 2024/7/30
 * 审核人员:
 * 相关文档:
 * 修改记录:   修改日期    修改人员    修改单号       版本号                   修改说明
 * V6.0.0.1  20240730-01  chenxp   TXXXXXXXXXXXX    IFMS6.0VXXXXXXXXXXXXX   新增 
 *********************************************/
package org.mmfmilku.atom.web.console.controller;

import org.mmfmilku.atom.web.console.domain.AgentConfig;
import org.mmfmilku.atom.web.console.domain.OrdFile;
import org.mmfmilku.atom.web.console.interfaces.IOrdConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AgentConfigController
 *
 * @author chenxp
 * @date 2024/7/30:19:45
 */
@RestController
public class OrdConfigController {

    @Autowired
    IOrdConfigService ordConfigService;

    @RequestMapping("getConfig")
    public AgentConfig getConfig(@RequestParam String appName) {
        return ordConfigService.getConfig(appName);
    }

    @RequestMapping("listOrd")
    public List<String> listOrd(@RequestParam String appName) {
        return ordConfigService.listOrd(appName);
    }

    @RequestMapping("deleteOrd")
    public String deleteOrd(@RequestParam String appName, @RequestParam String ordFileName) {
        ordConfigService.deleteOrd(appName, ordFileName);
        return "success";
    }

    @RequestMapping("readOrd")
    public OrdFile readOrd(@RequestParam String appName, @RequestParam String ordFileName) {
        return ordConfigService.readOrd(appName, ordFileName);
    }

    @RequestMapping("writeOrd")
    public String writeOrd(@RequestParam String appName, @RequestBody OrdFile ordFile) {
        ordConfigService.writeOrd(appName, ordFile);
        return "success";
    }

}
