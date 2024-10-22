package org.mmfmilku.atom.agent.api.impl;

import org.mmfmilku.atom.agent.config.AgentProperties;
import org.mmfmilku.atom.api.AppInfoApi;
import org.mmfmilku.atom.transport.frpc.FRPCService;

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
}
