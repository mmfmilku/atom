package org.mmfmilku.atom.agent.console;

import org.mmfmilku.atom.agent.log.ScreenLogger;
import org.mmfmilku.atom.agent.util.AgentExeUtils;
import org.mmfmilku.atom.exception.BizException;

public class JScriptExecutor {

    public Object execute(Object[] arg0) {
        // 由于retransform后，在transform过程中的错误无法实时获取，发生错误后的执行需要发现异常
        // 通过异常后的类定义还原来标识异常发生，设置初始类定义抛出异常
        throw new BizException("jScript execute fail!");
    }

    private void echo(Object arg0) {
        ScreenLogger.print(AgentExeUtils.toString(arg0));
    }

}
