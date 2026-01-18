package org.mmfmilku.atom.web.console.interfaces;

import org.mmfmilku.atom.api.dto.RunInfo;

import java.util.Map;

public interface IAgentService {

    boolean loadAgent(String vmId, String appName);

    boolean stopAgent(String appName);

    Map<String, String> vmInfo(String vmId);

    RunInfo agentInfo(String appName);
}
