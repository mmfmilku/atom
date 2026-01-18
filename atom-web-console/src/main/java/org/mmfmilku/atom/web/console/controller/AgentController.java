package org.mmfmilku.atom.web.console.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.mmfmilku.atom.agent.client.AgentClient;
import org.mmfmilku.atom.api.dto.RunInfo;
import org.mmfmilku.atom.util.FileUtils;
import org.mmfmilku.atom.util.StringUtils;
import org.mmfmilku.atom.web.console.domain.ProcessInfo;
import org.mmfmilku.atom.web.console.interfaces.IAgentService;
import org.mmfmilku.atom.web.console.interfaces.IInstrumentService;
import org.mmfmilku.atom.web.console.interfaces.IAgentConfigService;
import org.mmfmilku.atom.web.console.interfaces.IOrdFileOperation;
import org.mmfmilku.atom.web.console.interfaces.IProcessService;
import org.mmfmilku.atom.web.console.service.AgentConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

    @Autowired
    IProcessService processService;

    @RequestMapping("listVm")
    @ResponseBody
    public List<Map<String, String>> listVm() {
        List<Map<String, String>> vmMapList = AgentClient.listVMMap();
        return vmMapList.stream()
                .filter(vmMap -> !vmMap.get("displayName").trim().isEmpty())
                .peek(vmMap -> {
                    String appName = vmMap.get("displayName");
                    if (aliasMap.containsKey(appName)) {
                        vmMap.put("alias", aliasMap.get(appName));
                    }
                })
                .sorted((o1, o2) -> {
                    if (o1.containsKey("alias")) {
                        return o2.containsKey("alias") ?
                                o1.get("alias").compareTo(o2.get("alias")) : -1;
                    } else {
                        return o2.containsKey("alias") ?
                                1 : o1.get("displayName").compareTo(o2.get("displayName"));
                    }
                })
                .collect(Collectors.toList());
    }

    @RequestMapping("serviceInfo")
    public ProcessInfo serviceInfo(@RequestParam String vmId) {
        return processService.processInfo(vmId);
    }

    @RequestMapping("vmInfo")
    public Map<String, String> vmInfo(@RequestParam String vmId) {
        return agentService.vmInfo(vmId);
    }

    private final Map<String, String> aliasMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        File localDir = new File(AgentConfigService.CONSOLE_BASE_DIR, "local");
        if (!localDir.exists()) {
            localDir.mkdir();
        }
        File dataFile = new File(localDir, "normal.data");
        if (!dataFile.exists()) {
            return;
        }
        try {
            String s = FileUtils.readText(dataFile.getAbsolutePath());
            Map<String, String> map = JSON.parseObject(s, new TypeReference<Map<String,String>>(){});
            aliasMap.putAll(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("setAlias")
    public String setAlias(@RequestParam String appName,
                          @RequestParam String aliasName) {
        aliasMap.put(appName, aliasName);
        // TODO 临时写
        File localDir = new File(AgentConfigService.CONSOLE_BASE_DIR, "local");
        if (!localDir.exists()) {
            localDir.mkdir();
        }
        File dataFile = new File(localDir, "normal.data");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(dataFile))) {
            String jsonString = JSON.toJSONString(aliasMap);
            out.write(jsonString.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("setAlias fail");
        }
        return "success";
    }

    @RequestMapping("agentInfo")
    public RunInfo agentInfo(@RequestParam String appName) {
        return agentService.agentInfo(appName);
    }
    
    @RequestMapping("loadAgent")
    public String loadAgent(@RequestParam String vmId, @RequestParam String appName) {
        boolean success = agentService.loadAgent(vmId, appName);
        return String.valueOf(success);
    }

    @RequestMapping("stopAgent")
    public String stopAgent(@RequestParam String appName) {
        boolean success = agentService.stopAgent(appName);
        return String.valueOf(success);
    }

    @RequestMapping("listAllClass")
    @ResponseBody
    public List<String> listAllClass(@RequestParam String appName) {
        return instrumentService.listClassForPage(appName, 1, Integer.MAX_VALUE);
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

    @Deprecated
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

    @RequestMapping("stopClassOrd")
    public String stopClassOrd(@RequestParam String appName, @RequestParam String fullClassName) {
        instrumentService.stopClassOrd(appName, fullClassName);
        return "success";
    }

    @RequestMapping("allScreenLogs")
    public List<String> allScreenLogs(@RequestParam String appName) {
        return instrumentService.allScreenLogs(appName);
    }

    
}
