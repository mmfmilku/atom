package org.mmfmilku.atom.agent.api.impl;

import org.mmfmilku.atom.agent.console.*;
import org.mmfmilku.atom.api.ExecutableApi;
import org.mmfmilku.atom.api.dto.ExecuteResult;
import org.mmfmilku.atom.exception.SystemException;
import org.mmfmilku.atom.transport.frpc.server.FRPCService;
import org.mmfmilku.atom.util.FileUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        ExecuteResult result = new ExecuteResult();
        JTerminalResult jTerminalResult = JTerminal.executeTerminal(terminalId, code);
        result.setSuccess(jTerminalResult.isSuccess());
        result.setThrowable(jTerminalResult.getThrowable());
        result.setExecuteReturn(jTerminalResult.getExecuteReturn());
        return result;
    }

    @Override
    public List<String> listTerminalId() {
        return JTerminal.listId();
    }

    @Override
    public Map<String, Object> terminalInfo(String terminalId) {
        JTerminalDomain jTerminalDomain = JTerminal.terminalInfo(terminalId);
        if (jTerminalDomain == null) {
            return null;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("id", jTerminalDomain.getId());
        data.put("name", jTerminalDomain.getName());
        data.put("history", jTerminalDomain.getHistory());
        // TODO 历史命令、import列表待添加
        return data;
    }

    @Override
    public String newTerminal(String terminalName) {
        JTerminalDomain jTerminalDomain = JTerminal.newTerminal(terminalName);
        return jTerminalDomain.getId();
    }

    @Override
    public void deleteTerminal(String terminalId) {
        JTerminal.deleteTerminal(terminalId);
    }
}
