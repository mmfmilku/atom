package org.mmfmilku.atom.agent.console;

import org.mmfmilku.atom.exception.BizException;

import java.util.Map;

public class JTerminalExecutor {

    // 编译后方法名会擦除，直接将方法名定义为arg0
    public Object execute(Map<String, Object> arg0) {
        // 由于retransform后，在transform过程中的错误无法实时获取，发生错误后的执行需要发现异常
        // 通过异常后的类定义还原来标识异常发生，设置初始类定义抛出异常
        throw new BizException("jTerminal execute fail!");
    }

}
