package org.mmfmilku.atom.agent.api.impl;

import org.mmfmilku.atom.agent.config.AgentProperties;
import org.mmfmilku.atom.agent.instrument.InstrumentationContext;
import org.mmfmilku.atom.api.AppInfoApi;
import org.mmfmilku.atom.api.dto.RunInfo;
import org.mmfmilku.atom.transport.frpc.server.FRPCService;
import org.mmfmilku.atom.util.JavaUtil;

import java.util.Map;

@FRPCService
public class AppInfoApiImpl implements AppInfoApi {
    @Override
    public Integer ping() {
        return 1;
    }

    @Override
    public Map<Object, Object> info() {
        return AgentProperties.getProperties();
    }

    @Override
    public RunInfo runInfo() {
        String startClass = JavaUtil.getStartClass();
        RunInfo runInfo = new RunInfo();
        runInfo.setStartClass(startClass);
        return runInfo;
    }

    @Override
    public Map<String, Object> getRunningOrd() {
        return InstrumentationContext.getOrdList();
    }
}
