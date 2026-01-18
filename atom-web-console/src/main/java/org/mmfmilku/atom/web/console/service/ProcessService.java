package org.mmfmilku.atom.web.console.service;

import org.mmfmilku.atom.web.console.domain.ProcessInfo;
import org.mmfmilku.atom.web.console.interfaces.IProcessService;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 进程服务
 **/
@Service
public class ProcessService implements IProcessService {
    @Override
    public ProcessInfo processInfo(String pid) {

        String os = System.getProperty("os.name").toLowerCase();
        String workingDir;
        if (os.contains("linux") || os.contains("mac") || os.contains("unix")) {
            workingDir = getLinuxProcessDirectory(pid);
        } else if (os.contains("win")) {
            workingDir = getWindowsProcessDirectory(pid);
        } else {
            throw new RuntimeException("not support os:" + os);
        }

        ProcessInfo processInfo = new ProcessInfo();
        processInfo.setWorkingDir(workingDir);
        processInfo.setPid(pid);
        return processInfo;
    }

    private static String getLinuxProcessDirectory(String pid) {
        // 方法1：读取/proc文件系统
//        try {
//            Path cwdPath = Paths.get("/proc/" + pid + "/cwd");
//            if (Files.exists(cwdPath)) {
//                return Files.readSymbolicLink(cwdPath).toString();
//            }
//        } catch (Exception e) {
//            // 忽略，尝试其他方法
//        }

        // 方法2：使用pwdx命令
        try {
            Process process = Runtime.getRuntime().exec("pwdx " + pid);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String output = reader.readLine();
            if (output != null && output.contains(":")) {
                return output.split(":", 2)[1].trim();
            }
        } catch (Exception e) {
            // 忽略，尝试其他方法
        }

        // 方法3：使用lsof命令
        return getDirectoryByLsof(pid);
    }

    private static String getDirectoryByLsof(String pid) {
        try {
            Process process = Runtime.getRuntime().exec(
                    new String[]{"lsof", "-p", String.valueOf(pid), "-a", "-d", "cwd"});
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            // 跳过标题行
            reader.readLine();
            String line = reader.readLine();

            if (line != null) {
                String[] parts = line.split("\\s+");
                if (parts.length > 9) {
                    return parts[9];
                }
            }
        } catch (Exception e) {
            // 忽略错误
        }
        return null;
    }

    private static String getWindowsProcessDirectory(String pid) {
        try {
            // 尝试从命令行中提取工作目录
            String commandLine = getWindowsCommandLine(pid);
            if (commandLine != null) {
                return commandLine;
//                return extractWorkingDirectory(commandLine);
            }
        } catch (Exception e) {
            // 忽略错误
        }
        return null;
    }

    private static String getWindowsCommandLine(String pid) {
        try {
            Process process = Runtime.getRuntime().exec(
                    new String[]{"wmic", "process", "where",
                            "ProcessId=" + pid, "get", "CommandLine", "/value"});

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("CommandLine=")) {
                    return line.substring(12).trim();
                }
            }
        } catch (Exception e) {
            // 忽略错误
        }
        return null;
    }

    private static String extractWorkingDirectory(String commandLine) {
        // 简单的目录提取逻辑
        if (commandLine == null || commandLine.isEmpty()) {
            return null;
        }

        // 如果是Java进程
        if (commandLine.contains("java") || commandLine.contains("javaw")) {
            // 尝试提取jar文件路径
            String[] parts = commandLine.split("\\s+");
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].endsWith(".jar")) {
                    File jarFile = new File(parts[i]);
                    if (jarFile.exists()) {
                        return jarFile.getParent();
                    }
                } else if (parts[i].endsWith("-jar") && i + 1 < parts.length) {
                    File jarFile = new File(parts[i + 1]);
                    if (jarFile.exists()) {
                        return jarFile.getParent();
                    }
                }
            }
        }

        // 尝试从可执行文件路径提取目录
        if (commandLine.contains(".exe")) {
            String exePath = commandLine.split("\\s+")[0];
            File exeFile = new File(exePath);
            if (exeFile.exists()) {
                return exeFile.getParent();
            }
        }

        return null;
    }

    /**
     * 获取Java进程的jar文件所在目录
     */
    public static String getJavaProcessJarDirectory(String pid) {
        String commandLine = getProcessCommandLine(pid);
        if (commandLine == null) {
            return null;
        }

        // 解析命令行，查找jar文件
        String[] parts = commandLine.split("\\s+");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].endsWith(".jar")) {
                File jarFile = new File(parts[i]);
                return jarFile.getParentFile().getAbsolutePath();
            }
            if (parts[i].equals("-jar") && i + 1 < parts.length) {
                File jarFile = new File(parts[i + 1]);
                return jarFile.getParentFile().getAbsolutePath();
            }
        }

        return null;
    }

    private static String getProcessCommandLine(String pid) {
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                return getWindowsCommandLine(pid);
            } else {
                // Linux系统读取/proc/<pid>/cmdline
                Path cmdlinePath = Paths.get("/proc/" + pid + "/cmdline");
                if (Files.exists(cmdlinePath)) {
                    byte[] data = Files.readAllBytes(cmdlinePath);
                    return new String(data).replace('\0', ' ').trim();
                }
            }
        } catch (Exception e) {
            // 忽略错误
        }
        return null;
    }
}
