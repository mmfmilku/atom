package org.mmfmilku.atom.web.console.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.mmfmilku.atom.web.console.domain.EnvVO;
import org.mmfmilku.atom.web.console.interfaces.IPersistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 **/
@Component
public class PersistDataOpt {

    @Autowired
    private IPersistService persistService;

    public void save(String type, Object data) {
        persistService.save(type, data);
    }

    public Map<String, String> getAliasMap() {
        Map<String, String> aliasMap = persistService.get("alias", new TypeReference<Map<String, String>>() {
        });
        return aliasMap == null ? new HashMap<>() : aliasMap;
    }

    public Map<String, EnvVO> getEnvMap() {
        Map<String, EnvVO> envMap = persistService.get("env", new TypeReference<Map<String, EnvVO>>(){});
        return envMap == null ? new HashMap<>() : envMap;
    }

}
