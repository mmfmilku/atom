package org.mmfmilku.atom.agent.handle;

import java.lang.instrument.Instrumentation;

/**
 * AgentHandle
 *
 * @author chenxp
 * @date 2024/5/29:16:14
 */
public interface AgentHandle {
    
    void handle(String agentArgs, Instrumentation inst);
    
}
