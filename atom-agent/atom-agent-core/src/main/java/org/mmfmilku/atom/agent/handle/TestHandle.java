/********************************************
 * 文件名称: TestHandle.java
 * 系统名称: 综合理财管理平台6.0
 * 模块名称:
 * 软件版权: 恒生电子股份有限公司
 * 功能说明:
 * 系统版本: 6.0.0.1
 * 开发人员: chenxp
 * 开发时间: 2024/5/29
 * 审核人员:
 * 相关文档:
 * 修改记录:   修改日期    修改人员    修改单号       版本号                   修改说明
 * V6.0.0.1  20240529-01  chenxp   TXXXXXXXXXXXX    IFMS6.0VXXXXXXXXXXXXX   新增 
 *********************************************/
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
public class TestHandle implements AgentHandle {

    @Override
    public void handle(String agentArgs, Instrumentation inst) {
        System.out.println("agent TestHandle start addTransformer");
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
        System.out.println("agent TestHandle end");

    }

}
