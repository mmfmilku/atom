package org.mmfmilku.atom.agent.api.impl;

import org.mmfmilku.atom.api.AppInfoApi;
import org.mmfmilku.atom.transport.frpc.FRPCService;

@FRPCService
public class AppInfoApiImpl implements AppInfoApi {
    @Override
    public Integer ping() {
        return 1;
    }
}
