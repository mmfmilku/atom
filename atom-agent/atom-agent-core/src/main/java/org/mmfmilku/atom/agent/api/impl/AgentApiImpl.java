package org.mmfmilku.atom.agent.api.impl;

import org.mmfmilku.atom.agent.api.AgentApi;
import org.mmfmilku.atom.agent.api.dto.CommonDTO;
import org.mmfmilku.atom.transport.frpc.FRPCService;

@FRPCService
public class AgentApiImpl implements AgentApi {

    @Override
    public CommonDTO retransformClasses() {
        return null;
    }
}
