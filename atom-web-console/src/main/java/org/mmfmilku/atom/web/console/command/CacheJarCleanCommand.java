package org.mmfmilku.atom.web.console.command;

import org.mmfmilku.atom.web.console.service.AgentConfigService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 清理缓存jar文件
 *
 **/
@Component
public class CacheJarCleanCommand implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        File baseDir = new File(AgentConfigService.CONSOLE_BASE_DIR, "jar");
        if (baseDir.exists()) {
            for (File file : baseDir.listFiles()) {
                file.delete();
            }
        }
    }
}
