package org.mmfmilku.atom.web.console.interfaces;

import org.mmfmilku.atom.web.console.domain.AgentConfig;
import org.mmfmilku.atom.web.console.domain.OrdEnum;
import org.mmfmilku.atom.web.console.domain.OrdFile;
import org.mmfmilku.atom.web.console.domain.OrdRunInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * IAgentConfigService
 *
 * @author mmfmilku
 * @date 2024/7/31:9:12
 */
public interface IAgentConfigService {

    Collection<AgentConfig> list();

    void saveConfig(String appName, Map<String, String> saveData);

    AgentConfig getConfigByName(String appName);

    List<OrdRunInfo> listOrd(String appName, String childPath, OrdEnum ordEnum);

    void deleteOrd(String appName, String childPath, OrdEnum ordEnum);
    
    OrdFile readOrd(String appName, String childPath, OrdEnum ordEnum);
    
    void writeOrd(String appName, OrdFile ordFile, OrdEnum ordEnum);
    
}
