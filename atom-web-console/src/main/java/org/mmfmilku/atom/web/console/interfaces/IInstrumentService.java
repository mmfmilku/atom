package org.mmfmilku.atom.web.console.interfaces;

import org.mmfmilku.atom.api.dto.ExecuteResult;
import org.mmfmilku.atom.web.console.domain.JTerminalInfo;

import java.util.List;
import java.util.Map;

/**
 * InstrumentService
 *
 * @author mmfmilku
 * @date 2024/10/11:14:47
 */
public interface IInstrumentService {

    List<String> listClassForPage(String appName, int offset, int size);

    List<String> listClassForPage(String appName, int offset, int size, String classShortNameLike);

    String decompile(String appName, String fullClassName);

    void retransformClass(String appName, String fullClassName);

    void loadOrdFile(String appName, String file);

    void stopClassOrd(String appName, String fullClassName);

    /**
     * 执行程序相对于执行文件夹的路径名称
     * */
    ExecuteResult execute(String appName, String executeFile, Object... args);

    /**
     * 执行脚本化java代码
     * */
    ExecuteResult executeJScript(String appName, String jScriptFile);

    /**
     * 获取终端
     * */
    JTerminalInfo getTerminal(String appName, String terminalFile);

    /**
     * 获取终端上下文
     * */
    JTerminalInfo getTerminalContext(String appName, String terminalId);

    /**
     * 执行JTerminal
     * */
    ExecuteResult executeJTerminal(String appName, String terminalId, String code);

    List<String> listTerminalId(String appName);

    String newTerminal(String appName, String terminalName);

    void deleteTerminal(String appName, String terminalId);

    /**
     * 获取被重写的类
     * */
    Map<String, Object> getRunningOrdClass(String appName);

    List<String> allScreenLogs(String appName);

}
