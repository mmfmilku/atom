package org.mmfmilku.atom.web.console.interfaces;

import java.util.Map;

public interface IAgentService {

    boolean loadAgent(String vmId, String appName);

    Map<String, String> vmInfo(String vmId);

}
