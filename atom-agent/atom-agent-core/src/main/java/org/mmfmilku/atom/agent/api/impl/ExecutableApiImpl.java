package org.mmfmilku.atom.agent.api.impl;

import org.mmfmilku.atom.agent.api.ExecuteGoal;
import org.mmfmilku.atom.agent.config.ClassORDDefine;
import org.mmfmilku.atom.agent.config.OverrideBodyHolder;
import org.mmfmilku.atom.agent.util.OrdUtils;
import org.mmfmilku.atom.api.ExecutableApi;
import org.mmfmilku.atom.api.dto.ExecuteResult;
import org.mmfmilku.atom.transport.frpc.server.FRPCService;

import java.util.Map;

@FRPCService
public class ExecutableApiImpl implements ExecutableApi {

    // 需要放在成员变量中保证加载过该类，后续retransform能找到类
    private ExecuteGoal executeGoal = new ExecuteGoal();

    @Override
    public ExecuteResult execute(String toExecute, Object... args) {

        // 将待执行程序写入执行目标 TODO
        Map<String, ClassORDDefine> defineMap = OverrideBodyHolder.parseOverrideFile(toExecute);
        OrdUtils.loadOrd(defineMap);

        ExecuteResult result = new ExecuteResult();
        // 执行程序
        try {
            // 保存执行结果
            Object executeReturn = executeGoal.execute(args);
            result.setSuccess(true);
            result.setExecuteReturn(executeReturn);
        } catch (Exception e) {
            // 保存执行异常
            result.setThrowable(e);
            result.setSuccess(false);
            e.printStackTrace();
        }
        return result;
    }
}
