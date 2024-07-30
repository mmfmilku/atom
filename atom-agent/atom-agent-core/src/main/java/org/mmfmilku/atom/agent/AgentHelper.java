/********************************************
 * 文件名称: AgentHelper.java
 * 系统名称: 综合理财管理平台6.0
 * 模块名称:
 * 软件版权: 恒生电子股份有限公司
 * 功能说明:
 * 系统版本: 6.0.0.1
 * 开发人员: chenxp
 * 开发时间: 2024/6/4
 * 审核人员:
 * 相关文档:
 * 修改记录:   修改日期    修改人员    修改单号       版本号                   修改说明
 * V6.0.0.1  20240604-01  chenxp   TXXXXXXXXXXXX    IFMS6.0VXXXXXXXXXXXXX   新增 
 *********************************************/
package org.mmfmilku.atom.agent;

import org.mmfmilku.atom.agent.config.AgentProperties;
import org.mmfmilku.atom.agent.config.OverrideBodyHolder;
import org.mmfmilku.atom.agent.context.InstrumentationContext;
import org.mmfmilku.atom.agent.handle.AgentHandle;
import org.mmfmilku.atom.agent.handle.TestHandle;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * AgentHelper
 *
 * @author chenxp
 * @date 2024/6/4:15:02
 */
public class AgentHelper {

    private static List<AgentHandle> handleChain = new ArrayList<>();

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("------------------------premain-----------------------");
        main(agentArgs, inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("------------------------agentmain-----------------------");
        main(agentArgs, inst);
    }

    public static void main(String agentArgs, Instrumentation inst) {
        
        try {
            InstrumentationContext.init(inst);

            if (Objects.nonNull(agentArgs)) {
                AgentProperties.loadProperties(agentArgs);
                System.out.println("init properties end");
                System.out.println(AgentProperties.getInstance());

                OverrideBodyHolder.load(AgentProperties.getProperty(AgentProperties.PROP_BASE_PATH));
            }

            for (AgentHandle handle : handleChain) {
                handle.handle(agentArgs, inst);
            }
        } catch (Throwable e) {
            System.out.println("agent main error");
            e.printStackTrace();
            throw e;
        }
        
        
    }
    
    private static void initHandleChain() {
        handleChain.add(new TestHandle());
    }
    
    static {
        initHandleChain();
    }
    
}
