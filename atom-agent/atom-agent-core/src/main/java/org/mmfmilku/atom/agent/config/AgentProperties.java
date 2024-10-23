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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    
    public static Properties getInstance() {
        return properties;
    }
    
    public static final String CONFIG_FILE_NAME = "agent.conf";
    
    public static final String PROP_BASE_PATH = "base-path";

    // toString方法
    public static final String PROP_TO_STRING_METHOD = "to-string-method";

    // 自定义类加载器
    public static final String PROP_APP_CLASSLOADER = "app-classloader";

    // 可重写包名
    public static final String PROP_APP_BASE_PACKAGE = "app-base-package";

    // fserver监听路径
    public static final String PROP_FSERVER_DIR = "app-fserver-dir";

    public static void loadProperties(String args) {
        
        // 设置默认配置
        loadDefault();
        
        Map<String, String> argsMap = FeatureCodec.DEFAULT_COMMANDLINE_CODEC.toMap(args);
        
        // 设置文件配置
        String basePath = argsMap.get(PROP_BASE_PATH);
        if (basePath != null) {
            loadPropertiesFile(basePath);
        }

        // 设置命令行配置
        properties.putAll(argsMap);
    }

    private static void loadDefault() {
        properties.put(PROP_APP_CLASSLOADER, "org.springframework.boot.loader.LaunchedURLClassLoader");
        properties.put(PROP_FSERVER_DIR, System.getProperty("user.dir") + File.separator + "fserver");
    }

    public static void loadPropertiesFile(String basePath) {
        File file = new File(basePath);
        if (file.isFile()) {
            if (file.getName().equals(CONFIG_FILE_NAME)) {
                try (FileInputStream inputStream = new FileInputStream(file)) {
                    properties.load(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            File[] children = file.listFiles();
            if (children == null) {
                return;
            }
            for (File child : children) {
                loadPropertiesFile(child.getAbsolutePath());
            }
        }
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
    
}
