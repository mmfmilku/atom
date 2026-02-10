package org.mmfmilku.atom.web.console.interfaces;

import com.alibaba.fastjson.TypeReference;

import java.util.Map;

/**
 * 持久化服务
 **/
public interface IPersistService {

    void save(String type, Object data);

    Map<String, Object> get(String type);

    <T> T get(String type, TypeReference<T> typeReference);

}
