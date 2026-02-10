package org.mmfmilku.atom.web.console.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import org.mmfmilku.atom.util.FileUtils;
import org.mmfmilku.atom.web.console.interfaces.IPersistService;
import org.springframework.stereotype.Service;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 **/
@Service
public class PersistService implements IPersistService {

    private Map<String, Object> cache = new ConcurrentHashMap<>();

    @Override
    public void save(String type, Object data) {
        File localDir = new File(AgentConfigService.CONSOLE_BASE_DIR, "local");
        if (!localDir.exists()) {
            localDir.mkdir();
        }
        File dataFile = new File(localDir, type + ".data");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(dataFile))) {
            String jsonString = JSON.toJSONString(data);
            out.write(jsonString.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("PersistService save fail");
        }
        cache.put(type, data);
    }

    @Override
    public Map<String, Object> get(String type) {
        return get(type, new TypeReference<Map<String, Object>>(){});
    }

    @Override
    public <T> T get(String type, TypeReference<T> typeReference) {
        if (cache.containsKey(type)) {
            return (T) cache.get(type);
        }
        File localDir = new File(AgentConfigService.CONSOLE_BASE_DIR, "local");
        if (!localDir.exists()) {
            localDir.mkdir();
        }
        File dataFile = new File(localDir, type +".data");
        if (!dataFile.exists()) {
            return null;
        }
        try {
            String s = FileUtils.readText(dataFile.getAbsolutePath());
            T data = JSON.parseObject(s, typeReference, Feature.SupportAutoType);
            cache.put(type, data);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("PersistService get fail");
        }
    }
}
