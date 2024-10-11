package org.mmfmilku.atom.web.console.interfaces;

import org.mmfmilku.atom.web.console.domain.AgentConfig;
import org.mmfmilku.atom.web.console.domain.OrdFile;

import java.util.List;

/**
 * IOrdFileOperation
 *
 * @author mmfmilku
 * @date 2024/7/31:13:13
 */
public interface IOrdFileOperation {
    
    List<String> listFiles(AgentConfig config);

    OrdFile getOrd(AgentConfig config, String ordName);
    
    void setText(AgentConfig config, OrdFile ordFile);
    
    void delete(AgentConfig config, OrdFile ordFile);
    
}
