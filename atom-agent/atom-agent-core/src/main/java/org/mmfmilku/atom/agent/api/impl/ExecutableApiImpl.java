package org.mmfmilku.atom.agent.api.impl;

import org.mmfmilku.atom.agent.console.JScript;
import org.mmfmilku.atom.agent.console.JScriptResult;
import org.mmfmilku.atom.api.ExecutableApi;
import org.mmfmilku.atom.api.dto.ExecuteResult;
import org.mmfmilku.atom.exception.SystemException;
import org.mmfmilku.atom.transport.frpc.server.FRPCService;
import org.mmfmilku.atom.util.FileUtils;

import java.io.IOException;

@FRPCService
public class ExecutableApiImpl implements ExecutableApi {

    @Override
    public ExecuteResult execute(String toExecute, Object... args) {
        try {
            String jScript = FileUtils.readText(toExecute).trim();
            // 执行JScript
            return executeJScript(jScript);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public ExecuteResult executeJScript(String jScript, Object... args) {
        ExecuteResult result = new ExecuteResult();
        // 执行JScript
        JScriptResult jScriptResult = JScript.execute(jScript);
        result.setSuccess(jScriptResult.isSuccess());
        result.setThrowable(jScriptResult.getThrowable());
        result.setExecuteReturn(jScriptResult.getExecuteReturn());

        return result;
    }

    @Override
    public ExecuteResult executeTerminal(String terminalId, String code, Object... args) {
        return null;
    }
}
