package org.mmfmilku.atom.agent.api.impl;

import org.mmfmilku.atom.agent.compiler.parser.syntax.Import;
import org.mmfmilku.atom.agent.console.*;
import org.mmfmilku.atom.api.ExecutableApi;
import org.mmfmilku.atom.api.dto.ExecuteResult;
import org.mmfmilku.atom.api.dto.JTerminalDTO;
import org.mmfmilku.atom.exception.SystemException;
import org.mmfmilku.atom.transport.frpc.server.FRPCService;
import org.mmfmilku.atom.util.FileUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
        if (jScriptResult.getThrowable() != null) {
            result.setErrMsg(jScriptResult.getThrowable().toString());
        }
        return result;
    }

    @Override
    public ExecuteResult executeTerminal(String terminalId, String code, Object... args) {
        ExecuteResult result = new ExecuteResult();
        JTerminalResult jTerminalResult = JTerminal.executeTerminal(terminalId, code);
        result.setSuccess(jTerminalResult.isSuccess());
        result.setThrowable(jTerminalResult.getThrowable());
        result.setExecuteReturn(jTerminalResult.getExecuteReturn());
        if (jTerminalResult.getThrowable() != null) {
            result.setErrMsg(jTerminalResult.getThrowable().toString());
        }
        return result;
    }

    @Override
    public List<String> listTerminalId() {
        return JTerminal.listId();
    }

    @Override
    public JTerminalDTO terminalInfo(String terminalId) {
        JTerminalDomain jTerminalDomain = JTerminal.terminalInfo(terminalId);
        if (jTerminalDomain == null) {
            return null;
        }
        JTerminalDTO jTerminalDTO = new JTerminalDTO(jTerminalDomain.getId(), jTerminalDomain.getName());
        jTerminalDTO.setHistory(jTerminalDomain.getHistory());
        jTerminalDTO.setResultHistory(jTerminalDomain.getResultHistory()
                .stream()
                .map(jTerminalResult -> {
                    ExecuteResult result = new ExecuteResult();
                    result.setSuccess(jTerminalResult.isSuccess());
                    result.setThrowable(jTerminalResult.getThrowable());
                    result.setExecuteReturn(jTerminalResult.getExecuteReturn());
                    if (jTerminalResult.getThrowable() != null) {
                        result.setErrMsg(jTerminalResult.getThrowable().toString());
                    }
                    return result;
                })
                .collect(Collectors.toList())
        );
        jTerminalDTO.setContextVars(jTerminalDomain.getContextVars());
        jTerminalDTO.setContextVarsType(jTerminalDomain.getContextVarsType());
        jTerminalDTO.setImportList(jTerminalDomain.getImportList()
                .stream()
                .map(Import::getSourceCode)
                .collect(Collectors.toSet())
        );
        return jTerminalDTO;
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
