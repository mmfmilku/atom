package org.mmfmilku.atom.web.console.controller;

import org.mmfmilku.atom.web.console.domain.EnvVO;
import org.mmfmilku.atom.web.console.service.PersistDataOpt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 主节点相关
 **/
@RestController
@RequestMapping("master")
public class MasterController {

    @Autowired
    PersistDataOpt dataOpt;

    @RequestMapping("envConnectTest")
    public String envConnectTest(@RequestBody EnvVO envVO) {
        return "";
    }

    @RequestMapping("envList")
    public List<EnvVO> envList() {
        Map<String, EnvVO> envMap = dataOpt.getEnvMap();
        return envMap.values()
                .stream()
                .sorted(Comparator.comparing(EnvVO::getName))
                .collect(Collectors.toList());
    }

    @RequestMapping("addEnv")
    public String addEnv(@RequestBody EnvVO envVO) {
        Map<String, EnvVO> envMap = dataOpt.getEnvMap();
        envMap.put(envVO.getName(), envVO);
        dataOpt.save("env", envMap);
        return "success";
    }

    @RequestMapping("updEnv")
    public String updEnv(@RequestBody EnvVO envVO) {
        return "";
    }

    @RequestMapping("delEnv")
    public String delEnv(@RequestParam String name) {
        return "";
    }

}
