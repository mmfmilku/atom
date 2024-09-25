package org.mmfmilku.atom.agent.handle;

import org.mmfmilku.atom.agent.config.OverrideBodyHolder;
import org.mmfmilku.atom.agent.context.InstrumentationContext;
import org.mmfmilku.atom.agent.instrument.FileDefineTransformer;
import org.mmfmilku.atom.agent.instrument.ParamPrintTransformer;
import org.mmfmilku.atom.agent.instrument.TestTransformer;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

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
