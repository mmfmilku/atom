package org.mmfmilku.atom.web.console.service;

import com.alibaba.fastjson.JSON;
import org.mmfmilku.atom.api.AppInfoApi;
import org.mmfmilku.atom.api.ExecutableApi;
import org.mmfmilku.atom.api.InstrumentApi;
import org.mmfmilku.atom.api.dto.ExecuteResult;
import org.mmfmilku.atom.transport.frpc.client.FRPCFactory;
import org.mmfmilku.atom.util.AssertUtil;
import org.mmfmilku.atom.util.CodeUtils;
import org.mmfmilku.atom.util.FileUtils;
import org.mmfmilku.atom.util.StringUtils;
import org.mmfmilku.atom.web.console.domain.*;
import org.mmfmilku.atom.web.console.interfaces.IAgentConfigService;
import org.mmfmilku.atom.web.console.interfaces.IInstrumentService;
import org.mmfmilku.atom.web.console.util.Decompile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * InstrumentService
 *
 * @author mmfmilku
 * @date 2024/10/11:14:49
 */
@Service
public class InstrumentService implements IInstrumentService {

    @Autowired
    IAgentConfigService agentConfigService;

    private InstrumentApi getApi(String appName) {
        AgentConfig config = agentConfigService.getConfigByName(appName);
        return FRPCFactory.getService(InstrumentApi.class, config.getFDir());
    }

    private <T> T getApi(String appName, Class<T> apiClass) {
        AgentConfig config = agentConfigService.getConfigByName(appName);
        return FRPCFactory.getService(apiClass, config.getFDir());
    }

    @Override
    public List<String> listClassForPage(String appName, int offset, int size) {
        return getApi(appName).listClassForPage(offset, size);
    }

    @Override
    public List<String> listClassForPage(String appName, int offset, int size, String classShortNameLike) {
        return getApi(appName).searchClassForPage(offset, size, classShortNameLike);
    }

    private String byteCodeDir = "byteCode";

    private String decompileDir = "decompile";

    @Override
    public String decompile(String appName, String fullClassName) {
        AgentConfig config = agentConfigService.getConfigByName(appName);
        String tmpClassFile = Paths.get(config.getTmpDir(), byteCodeDir
                , CodeUtils.toClassFilePath(fullClassName))
                .toFile().getAbsolutePath();
        String tmpJavaFile = Paths.get(config.getTmpDir(), decompileDir
                , CodeUtils.toJavaFilePath(fullClassName))
                .toFile().getAbsolutePath();

        if (!Files.exists(Paths.get(tmpClassFile))) {
            // 字节码文件缓存
            String byteCodeFile = getApi(appName)
                    .writeByteCodeFile(fullClassName,
                            config.getTmpDir() + File.separator + byteCodeDir);
            AssertUtil.isTrue(tmpClassFile.equals(byteCodeFile), "字节码生成路径错误" + byteCodeFile);
        }

        if (!Files.exists(Paths.get(tmpJavaFile))) {
            // 反编译文件缓存
            Decompile.decompile(tmpClassFile, Paths.get(config.getTmpDir(),decompileDir).toString());
        }

        AssertUtil.isTrue(Files.exists(Paths.get(tmpJavaFile)), "反编译文件不存在");

        try {
            return FileUtils.readText(tmpJavaFile);
        } catch (IOException e) {
            throw new RuntimeException("读取反编译文件异常");
        }
    }

    @Override
    public void retransformClass(String appName, String fullClassName) {
        getApi(appName).retransformClass(fullClassName);
    }

    @Override
    public void loadOrdFile(String appName, String file) {
        AgentConfig config = agentConfigService.getConfigByName(appName);
        getApi(appName).loadOrdFile(Paths.get(config.getOrdDir(), file).toString());
    }

    @Override
    public void stopClassOrd(String appName, String fullClassName) {
        getApi(appName).stopOrd(fullClassName);
    }

    @Override
    public ExecuteResult execute(String appName, String executeFile, Object... args) {
        AgentConfig config = agentConfigService.getConfigByName(appName);
        return getApi(appName, ExecutableApi.class)
                .execute(Paths.get(config.getExecuteDir(), executeFile).toString(), args);
    }

    @Override
    public ExecuteResult executeJScript(String appName, String jScriptFile) {
        OrdFile ordFile = agentConfigService.readOrd(appName, jScriptFile, OrdEnum.SCRIPT_ORD);
        return getApi(appName, ExecutableApi.class).executeJScript(ordFile.getText());
    }

    @Override
    public JTerminalInfo getTerminal(String appName, String terminalFile) {
        OrdFile ordFile = agentConfigService.readOrd(appName, terminalFile, OrdEnum.EXECUTE_ORD);
        String text = ordFile.getText();
        JTerminalConfData confData = JSON.parseObject(text, JTerminalConfData.class);
        ExecutableApi api = getApi(appName, ExecutableApi.class);
        Map<String, Object> terminalInfo;
        if (confData == null
                || StringUtils.isEmpty(confData.getTerminalId())
                || (terminalInfo = api.terminalInfo(confData.getTerminalId())) == null) {
            if (confData == null) {
                confData = new JTerminalConfData();
            }
            if (StringUtils.isEmpty(confData.getTerminalName())) {
                confData.setTerminalName(terminalFile);
            }
            // 无对应终端，新建
            String newId = api.newTerminal(confData.getTerminalName());
            confData.setTerminalId(newId);
            ordFile.setText(JSON.toJSONString(confData));
            agentConfigService.writeOrd(appName, ordFile, OrdEnum.EXECUTE_ORD);

            terminalInfo = api.terminalInfo(newId);
        }

        JTerminalInfo jTerminalInfo = new JTerminalInfo();
        jTerminalInfo.setHistory((List<String>) terminalInfo.get("history"));
        return jTerminalInfo;
    }

    @Override
    public ExecuteResult executeJTerminal(String appName, String terminalId, String code) {
        return getApi(appName, ExecutableApi.class).executeTerminal(terminalId, code);
    }

    @Override
    public List<String> listTerminalId(String appName) {
        return getApi(appName, ExecutableApi.class).listTerminalId();
    }

    @Override
    public String newTerminal(String appName, String terminalName) {
        return getApi(appName, ExecutableApi.class).newTerminal(terminalName);
    }

    @Override
    public void deleteTerminal(String appName, String terminalId) {
        getApi(appName, ExecutableApi.class).deleteTerminal(terminalId);
    }

    @Override
    public Map<String, Object> getRunningOrdClass(String appName) {
        return getApi(appName, AppInfoApi.class).getRunningOrd();
    }

}
