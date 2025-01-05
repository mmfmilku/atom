package org.mmfmilku.atom.api;

import org.mmfmilku.atom.api.dto.RunInfo;

import java.util.List;
import java.util.Map;

public interface AppInfoApi {

    Integer ping();

    /**
     * AgentProperties信息
     * */
    Map<Object, Object> info();

    /**
     * 程序运行信息
     * */
    RunInfo runInfo();

    /**
     * 获取被重写的类列表
     * */
    Map<String, Object> getRunningOrd();

}
