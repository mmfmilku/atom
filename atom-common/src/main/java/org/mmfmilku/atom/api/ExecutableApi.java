package org.mmfmilku.atom.api;

import org.mmfmilku.atom.api.dto.ExecuteResult;

import java.util.List;
import java.util.Map;

/**
 * 可执行接口，用于即时执行代码块
 * */
public interface ExecutableApi {

    /**
     * 发起执行
     * @param toExecute 执行目标文件
     * @param args 执行参数
     * */
    ExecuteResult execute(String toExecute, Object... args);

    /**
     * 发起执行
     * @param jScript 执行脚本化代码
     * @param args 执行参数
     * */
    ExecuteResult executeJScript(String jScript, Object... args);

    /**
     * 在终端上执行
     * @param terminalId 终端id
     * @param code 执行的代码
     * @param args 执行参数
     * */
    ExecuteResult executeTerminal(String terminalId, String code, Object... args);

    List<String> listTerminalId();

    Map<String, String> terminalInfo(String terminalId);

    String newTerminal(String terminalName);

    void deleteTerminal(String terminalId);

}
