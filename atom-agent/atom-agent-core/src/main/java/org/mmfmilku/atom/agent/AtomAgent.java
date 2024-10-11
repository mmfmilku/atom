/********************************************
 * 文件名称: AtomAgent.java
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
package org.mmfmilku.atom.agent;

import java.lang.instrument.Instrumentation;

/**
 * AtomAgent
 *
 * @author chenxp
 * @date 2024/5/29:16:12
 */
public class AtomAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        AgentBootstrap.premain(agentArgs, inst);
    }

    public static void agentmain (String agentArgs, Instrumentation inst) {
        AgentBootstrap.agentmain(agentArgs, inst);
    }

}
