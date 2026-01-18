/********************************************
 * 文件名称: AgentProperties.java
 * 系统名称: 综合理财管理平台6.0
 * 模块名称:
 * 软件版权: 恒生电子股份有限公司
 * 功能说明:
 * 系统版本: 6.0.0.1
 * 开发人员: chenxp
 * 开发时间: 2024/6/4
 * 审核人员:
 * 相关文档:
 * 修改记录:   修改日期    修改人员    修改单号       版本号                   修改说明
 * V6.0.0.1  20240604-01  chenxp   TXXXXXXXXXXXX    IFMS6.0VXXXXXXXXXXXXX   新增 
 *********************************************/
package org.mmfmilku.atom.agent.config;

import org.mmfmilku.atom.api.AgentPropertiesKey;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * AgentProperties
 *
 * @author chenxp
 * @date 2024/6/4:17:11
 */
public class AgentProperties {
    
    private static Properties properties = new Properties();

    @Deprecated
    public static Properties getInstance() {
        return properties;
    }

    public static void loadProperties(String args) {

        System.out.println("input agent args:" + args);
        
        // 设置默认配置
        loadDefault();
        
        Map<String, String> argsMap = FeatureCodec.DEFAULT_COMMANDLINE_CODEC.toMap(args);

        // 设置命令行配置
        properties.putAll(argsMap);
    }

    public static RunningConfig getRunningConfig() {
        RunningConfig runningConfig = new RunningConfig();
        runningConfig.setAppBasePackage(AgentProperties.getProperty(AgentPropertiesKey.APP_BASE_PACKAGE));
        runningConfig.setByteCodeCompile(Boolean.parseBoolean(
                AgentProperties.getProperty(AgentPropertiesKey.BYTE_CODE_COMPILE, "false")));
        runningConfig.setToStringMethod(AgentProperties.getProperty(AgentPropertiesKey.TO_STRING_METHOD));
        return runningConfig;
    }

    public static void setRunningConfig(RunningConfig runningConfig) {
        properties.put(AgentPropertiesKey.APP_BASE_PACKAGE, runningConfig.getAppBasePackage());
        properties.put(AgentPropertiesKey.BYTE_CODE_COMPILE, runningConfig.getByteCodeCompile());
        properties.put(AgentPropertiesKey.TO_STRING_METHOD, runningConfig.getToStringMethod());
    }

    public static boolean byteCodeCompile() {
        return getRunningConfig().getByteCodeCompile();
    }

    public static String toStringFullName() {
        return getRunningConfig().getToStringMethod();
    }

    private static void loadDefault() {
        properties.put(AgentPropertiesKey.APP_CLASSLOADER, "org.springframework.boot.loader.LaunchedURLClassLoader");
        properties.put(AgentPropertiesKey.FSERVER_DIR, System.getProperty("user.dir") + File.separator + "fserver");
        properties.put(AgentPropertiesKey.BYTE_CODE_COMPILE, false);
        properties.put(AgentPropertiesKey.APP_BASE_PACKAGE, "com");
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static Map<Object, Object> getProperties() {
        return new HashMap(properties);
    }

    public static void clear() {
        properties.clear();
    }
}
