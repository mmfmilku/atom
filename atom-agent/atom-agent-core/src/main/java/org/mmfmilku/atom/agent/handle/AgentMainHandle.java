package org.mmfmilku.atom.agent.handle;

import org.mmfmilku.atom.agent.config.OverrideBodyHolder;
import org.mmfmilku.atom.agent.instrument.InstrumentationContext;
import org.mmfmilku.atom.agent.instrument.transformer.FileDefineTransformer;
import org.mmfmilku.atom.agent.instrument.transformer.ParamPrintTransformer;

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
public class AgentMainHandle implements AgentHandle {

    @Override
    public void handle(String agentArgs, Instrumentation inst) {
        System.out.println("agent AgentMainHandle start addTransformer");
        // TODO fix 多次attach添加转换后，Instrumentation中将出现重复类定义
        InstrumentationContext.addTransformer(new FileDefineTransformer());
        InstrumentationContext.addTransformer(new ParamPrintTransformer());
        Class<?>[] classes = OverrideBodyHolder.getORClassMap().keySet().stream().map(v -> {
            System.out.println("class for name:" + v);
            Class<?> aClass = InstrumentationContext.searchClass(v);
            if (aClass == null) {
                System.out.println("class not found:" + v);
            }
            return aClass;
        }).filter(Objects::nonNull).collect(Collectors.toList()).toArray(new Class[]{});
        System.out.println(Arrays.toString(classes));
        if (classes.length == 0) {
            System.out.println("no class to retransformClasses!");
            throw new RuntimeException("no class to retransformClasses!");
        }
        try {
            // 动态加载时需要重新转换
            InstrumentationContext.retransformClasses(classes);
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        }
        System.out.println("agent AgentMainHandle end");

    }

}
