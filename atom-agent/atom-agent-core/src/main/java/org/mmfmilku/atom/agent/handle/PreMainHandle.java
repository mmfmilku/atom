package org.mmfmilku.atom.agent.handle;

import org.mmfmilku.atom.agent.instrument.InstrumentationContext;
import org.mmfmilku.atom.agent.instrument.transformer.FileDefineTransformer;
import org.mmfmilku.atom.agent.instrument.transformer.ParamPrintTransformer;

import java.lang.instrument.Instrumentation;

/**
 * TestHandle
 *
 * @author chenxp
 * @date 2024/5/29:16:14
 */
public class PreMainHandle implements AgentHandle {

    @Override
    public void handle(String agentArgs, Instrumentation inst) {
        System.out.println("agent PreMainHandle start addTransformer");
        // TODO fix 多次attach添加转换后，Instrumentation中将出现重复类定义
        InstrumentationContext.addTransformer(new FileDefineTransformer());
        InstrumentationContext.addTransformer(new ParamPrintTransformer());
        System.out.println("agent PreMainHandle end");
    }

}
