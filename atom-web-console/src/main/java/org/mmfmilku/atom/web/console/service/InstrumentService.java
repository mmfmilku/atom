package org.mmfmilku.atom.web.console.service;

import org.mmfmilku.atom.api.InstrumentApi;
import org.mmfmilku.atom.transport.frpc.client.FRPCFactory;
import org.mmfmilku.atom.util.AssertUtil;
import org.mmfmilku.atom.util.CodeUtils;
import org.mmfmilku.atom.util.FileUtils;
import org.mmfmilku.atom.web.console.domain.AgentConfig;
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

}
