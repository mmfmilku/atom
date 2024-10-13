package org.mmfmilku.atom.web.console.service;

import org.mmfmilku.atom.api.InstrumentApi;
import org.mmfmilku.atom.consts.CodeConst;
import org.mmfmilku.atom.transport.frpc.client.FRPCFactory;
import org.mmfmilku.atom.util.CodeUtils;
import org.mmfmilku.atom.web.console.domain.AgentConfig;
import org.mmfmilku.atom.web.console.interfaces.IAgentConfigService;
import org.mmfmilku.atom.web.console.interfaces.IInstrumentService;
import org.mmfmilku.atom.web.console.util.Decompile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
        AgentConfig config = agentConfigService.getConfig(appName);
        return FRPCFactory.getService(InstrumentApi.class, config.getFDir());
    }

    @Override
    public List<String> listClassForPage(String appName, int offset, int size) {
        return getApi(appName).listClassForPage(offset, size);
    }

    @Override
    public List<String> listClassForPage(String appName, int offset, int size, String classShortNameLike) {
        return getApi(appName).searchClassForPage(offset, size, classShortNameLike);
    }

    private String byteCodeDir = File.separator + "byteCode";

    private String decompileDir = File.separator + "decompile";

    @Override
    public String decompile(String appName, String fullClassName) {
        String tmpFile = CodeUtils.toFileName(fullClassName) + CodeConst.CLASS_FILE_SUFFIX;
        if (Files.exists(Paths.get(tmpFile))) {
            return tmpFile;
        }
        AgentConfig config = agentConfigService.getConfig(appName);
        String byteCodeFile = getApi(appName)
                .writeByteCodeFile(fullClassName, config.getTmpDir() + byteCodeDir);
        return Decompile.decompile(byteCodeFile, config.getTmpDir() + decompileDir);
    }

    @Override
    public void retransformClass(String appName, String fullClassName) {
        getApi(appName).getByteCode(fullClassName);
    }

}
