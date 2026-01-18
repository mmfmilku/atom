package org.mmfmilku.atom.api;

/**
 * 配置键
 **/
public interface AgentPropertiesKey {

    // --------------------------------- 运行时配置 -------------------------------------
    /**
     * 核心jar路径：目标应用需要加载的核心jar路径
     */
    String CORE_JAR_PATH = "coreJarPath";

    /**
     * 应用类加载器：目标应用的类加载器，用于访问目标应用中的类
     */
    String APP_CLASSLOADER = "appClassloader";

    /**
     * FServer监听路径：FServer监听的路径
     */
    String FSERVER_DIR = "appFserverDir";

    // --------------------------------- 运行时配置 -------------------------------------

    /**
     * toString方法：数据全局toString方法
     */
    String TO_STRING_METHOD = "toStringMethod";

    /**
     * 是否开启字节码编译：是否开启对目标应用字节码的编译
     */
    String BYTE_CODE_COMPILE = "byteCodeCompile";

    /**
     * 目标应用基础包：目标应用中需要被重写的包名
     */
    String APP_BASE_PACKAGE = "appBasePackage";


}
