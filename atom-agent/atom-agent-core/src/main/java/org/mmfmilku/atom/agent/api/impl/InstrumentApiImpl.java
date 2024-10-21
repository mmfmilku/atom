package org.mmfmilku.atom.agent.api.impl;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.mmfmilku.atom.agent.config.ClassORDDefine;
import org.mmfmilku.atom.agent.config.OverrideBodyHolder;
import org.mmfmilku.atom.agent.instrument.InstrumentationContext;
import org.mmfmilku.atom.agent.instrument.transformer.LoadOrdTransformer;
import org.mmfmilku.atom.agent.util.ByteCodeUtils;
import org.mmfmilku.atom.api.InstrumentApi;
import org.mmfmilku.atom.exception.BizException;
import org.mmfmilku.atom.transport.frpc.FRPCService;
import org.mmfmilku.atom.util.AssertUtil;
import org.mmfmilku.atom.util.ReflectUtils;

import java.io.IOException;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * InstrumentApiImpl
 *
 * @author chenxp
 * @date 2024/10/10:10:27
 */
@FRPCService
public class InstrumentApiImpl implements InstrumentApi {

    @Override
    public List<String> listClassForPage(int offset, int size) {
        if (offset < 1 || size < 1) {
            return Collections.emptyList();
        }
        // TODO,从配置属性中获取应用基础包路径
        String appPackage = "";
        List<String> loadedClasses = InstrumentationContext.getLoadedClasses(appPackage);
        return pageList(offset, size, loadedClasses);
    }

    @Override
    public List<String> searchClassForPage(int offset, int size, String classShortNameLike) {
        if (offset < 1 || size < 1) {
            return Collections.emptyList();
        }
        // TODO,从配置属性中获取应用基础包路径
        String appPackage = "com.example.bootstudy";
        List<String> loadedClasses = InstrumentationContext.getLoadedClasses(appPackage, classShortNameLike);
        return pageList(offset, size, loadedClasses);
    }

    @Override
    public byte[] getByteCode(String fullClassName) {
        try {
            // TODO 获取transform之后的字节码
            return ByteCodeUtils.getByteCode(fullClassName);
        } catch (NotFoundException | IOException | CannotCompileException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public String writeByteCodeFile(String fullClassName, String targetDir) {
        try {
            // TODO 获取transform之后的字节码
            return ByteCodeUtils.writeByteCodeFile(fullClassName, targetDir);
        } catch (NotFoundException | IOException | CannotCompileException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private <T> List<T> pageList(int offset, int size, List<T> list) {
        int startIndex = offset - 1;
        // copy
        List<T> subList = list.subList(startIndex, startIndex + size);
        return new ArrayList<>(subList);
    }

    @Override
    public void retransformClass(String className) {
        AssertUtil.isTrue(!className.startsWith("java"), "禁止重写系统类" + className);
        AssertUtil.isTrue(!className.startsWith("javax"), "禁止重写系统类" + className);
        AssertUtil.isTrue(!className.startsWith("jdk"), "禁止重写系统类" + className);
        AssertUtil.isTrue(!className.startsWith("sun"), "禁止重写系统类" + className);
        AssertUtil.isTrue(!className.startsWith("com.sun"), "禁止重写系统类" + className);
        Class<?> clazz = InstrumentationContext.searchClass(className);
        if (clazz == null) {
            throw new RuntimeException(className + " not exist");
        }
        try {
            InstrumentationContext.retransformClasses(clazz);
        } catch (UnmodifiableClassException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadOrdFile(String file) {
        Map<String, ClassORDDefine> defineMap = OverrideBodyHolder.parseOverrideFile(file);
        LoadOrdTransformer ordTransformer = new LoadOrdTransformer(defineMap);
        InstrumentationContext.addTransformer(ordTransformer);
        try {
            InstrumentationContext.retransformClasses(defineMap.keySet().stream().map(ReflectUtils::forName).toArray(Class[]::new));
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
            throw new BizException(e.getMessage());
        }
        InstrumentationContext.removeTransformer(ordTransformer);
    }
}
