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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachineDescriptor;
import org.mmfmilku.atom.agent.client.AgentClient;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MainController
 *
 * @author chenxp
 * @date 2024/7/26:13:59
 */
@RestController
public class AgentController {

    @RequestMapping("listVm")
    @ResponseBody
    public List<Map<String, String>> listVm() {
        List<VirtualMachineDescriptor> listVM = AgentClient.listVM();
        return listVM.stream().map(vm -> {
            Map<String, String> map = new HashMap<>();
            map.put("vmId", vm.id());
            map.put("displayName", vm.displayName());
            return map;
        }).collect(Collectors.toList());
    }

    @RequestMapping("loadAgent")
    public String loadAgent(@RequestParam String vmId) {
        try {
            AgentClient.loadAgent(vmId);
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }
    
}
