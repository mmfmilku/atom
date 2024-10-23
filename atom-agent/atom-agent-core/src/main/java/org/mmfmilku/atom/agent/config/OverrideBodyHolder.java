package org.mmfmilku.atom.agent.config;

import org.mmfmilku.atom.agent.compiler.CompilerUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.agent.util.ByteCodeUtils;
import org.mmfmilku.atom.exception.BizException;
import org.mmfmilku.atom.exception.SystemException;
import org.mmfmilku.atom.util.AssertUtil;
import org.mmfmilku.atom.util.FileUtils;
import org.mmfmilku.atom.consts.CodeConst;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OverrideBodyHolder
 *
 * @author mmfmilku
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
            System.out.println(overrideClassMap);
        }
    }

    public static void loadFile(String filePath) {
        System.out.println("------------------agent ord load-----------------------");
        synchronized (overrideClassMap) {
            File base = new File(filePath);
            if (base.exists()) {
                AssertUtil.isTrue(base.isFile(), "agent加载文件");
                loadOverrideFile(base.getAbsolutePath());
            }
            System.out.println("------------------new ord loaded:" + overrideClassMap.size());
            System.out.println(overrideClassMap);
        }
    }

    public static Map<String, ClassORDDefine> parseOverrideFile(String file) {
        // 将文件定义的方法覆写体读取
        if (file.endsWith(".ord")) {
            System.out.println("--------------parse .ord file:" + file);
            try {
                String text = FileUtils.readText(file).trim();

                return ORDParser.parse(text);
            } catch (IOException e) {
                e.printStackTrace();
                throw new SystemException(e.getMessage());
            }
        } else if (file.endsWith(CodeConst.JAVA_FILE_SUFFIX)) {
            System.out.println("--------------parse .java file:" + file);
            try {
                String text = FileUtils.readText(file).trim();
                JavaAST javaAST = CompilerUtil.parseAST(text);
                ByteCodeUtils.toJavassistCode(javaAST);
                return javaAST.getClassList()
                        .stream()
                        .map(clazz -> {
                            Map<String, MethodORDDefine> methodORDMap = clazz.getMethods()
                                    .stream()
                                    .map(method -> {
                                        MethodORDDefine methodORDDefine = new MethodORDDefine(method.getMethodName());
                                        methodORDDefine.setSrcMap(Collections.singletonMap(
                                                Keywords.METHOD,
                                                method.getCodeBlock().getSourceCode()));
                                        return methodORDDefine;
                                    }).collect(Collectors.toMap(MethodORDDefine::getMethodName, v -> v));
                            ClassORDDefine ordDefine = new ClassORDDefine();
                            ordDefine.setName(clazz.getClassFullName());
                            ordDefine.setMethodORDMap(methodORDMap);
                            return ordDefine;
                        }).collect(Collectors.toMap(ClassORDDefine::getName, v -> v));
            } catch (IOException e) {
                e.printStackTrace();
                throw new SystemException(e.getMessage());
            }
        } else {
            return Collections.emptyMap();
//            throw new BizException("不支持的文件类型" + file);
        }
    }

    private static void loadOverrideFile(String file) {
        // 将文件定义的方法覆写体，读取到map中
        Map<String, ClassORDDefine> defineMap = parseOverrideFile(file);
        overrideClassMap.putAll(defineMap);
    }

}
