/********************************************
 * 文件名称: OverriteMethodMap.java
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

import org.mmfmilku.atom.agent.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * OverrideBodyHolder
 *
 * @author chenxp
 * @date 2024/6/4:18:55
 */
public class OverrideBodyHolder {

    private static final Map<String, ClassORDDefine> overrideClassMap = new HashMap<>();

    public static Map<String, ClassORDDefine> getORClassMap() {
        return overrideClassMap;
    }

    public static ClassORDDefine getORMethodMap(String classFullName) {
        return overrideClassMap.get(classFullName);
    }

    public static MethodORDDefine getORMethodBody(String classFullName, String methodName) {
        if (!overrideClassMap.containsKey(classFullName)) {
            return null;
        }
        return overrideClassMap.get(classFullName).getMethodORDMap().get(methodName);
    }

    public static void load(String basePath) {
        System.out.println("------------------agent ord load-----------------------");
        if (overrideClassMap.size() > 0) {
            System.out.println("------------------old ord loaded:" + overrideClassMap.size());
            overrideClassMap.clear();
        }
        synchronized (overrideClassMap) {
            File base = new File(basePath);
            if (base.exists()) {
                if (base.isFile()) {
                    loadOverrideFile(base.getAbsolutePath());
                } else {
                    File[] files = base.listFiles();
                    if (files != null && files.length > 0) {
                        for (File file : files) {
                            loadOverrideFile(file.getAbsolutePath());
                        }
                    }
                }
            }
            System.out.println("------------------new ord loaded:" + overrideClassMap.size());
        }
    }

    private static void loadOverrideFile(String file) {
        // 将文件定义的方法覆写体，读取到map中
        if (!file.endsWith(".ord")) {
            return;
        }
        System.out.println("--------------load ord file:" + file);
        try {
            String text = FileUtils.readText(file).trim();
            
            Map<String, ClassORDDefine> parse = ORDParser.parse(text);
            overrideClassMap.putAll(parse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
