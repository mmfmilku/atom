package org.mmfmilku.atom.agent.api.impl;

import org.mmfmilku.atom.api.AppInfoApi;

public class AppInfoApiImpl implements AppInfoApi {
    @Override
    public Integer ping() {
        return 1;
    }
}
