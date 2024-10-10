package org.mmfmilku.atom.web.console.interfaces;

import org.mmfmilku.atom.web.console.domain.AgentConfig;
import org.mmfmilku.atom.web.console.domain.OrdFile;

import java.util.Collection;
import java.util.List;

/**
 * IAgentConfigService
 *
 * @author chenxp
 * @date 2024/7/31:9:12
 */
public interface IAgentConfigService {

    Collection<AgentConfig> list();

    AgentConfig getConfig(String appName);
    
    List<String> listOrd(String appName);

    OrdFile readOrd(String appName, String ordFileName);
    
    void writeOrd(String appName, String ordFileName, String text);
    
}
