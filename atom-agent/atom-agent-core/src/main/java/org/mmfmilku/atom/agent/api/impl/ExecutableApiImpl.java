package org.mmfmilku.atom.agent.api.impl;

import org.mmfmilku.atom.agent.api.ExecuteGoal;
import org.mmfmilku.atom.agent.config.ClassORDDefine;
import org.mmfmilku.atom.agent.config.OverrideBodyHolder;
import org.mmfmilku.atom.agent.util.OrdUtils;
import org.mmfmilku.atom.api.ExecutableApi;
import org.mmfmilku.atom.api.dto.ExecuteResult;

import java.util.Map;

public class ExecutableApiImpl implements ExecutableApi {
    @Override
    public ExecuteResult execute(String toExecute, Object... args) {

        // 将待执行程序写入执行目标 TODO
        Map<String, ClassORDDefine> defineMap = OverrideBodyHolder.parseOverrideFile(toExecute);
        OrdUtils.loadOrd(defineMap);

        ExecuteResult result = new ExecuteResult();
        ExecuteGoal executeGoal = new ExecuteGoal();
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
