package org.mmfmilku.atom.agent.api.impl;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.mmfmilku.atom.agent.config.AgentProperties;
import org.mmfmilku.atom.agent.config.ClassORDDefine;
import org.mmfmilku.atom.agent.config.OverrideBodyHolder;
import org.mmfmilku.atom.agent.instrument.InstrumentationContext;
import org.mmfmilku.atom.agent.instrument.transformer.LoadOrdTransformer;
import org.mmfmilku.atom.agent.instrument.transformer.StopOrdTransformer;
import org.mmfmilku.atom.agent.util.ByteCodeUtils;
import org.mmfmilku.atom.api.InstrumentApi;
import org.mmfmilku.atom.exception.BizException;
import org.mmfmilku.atom.transport.frpc.FRPCService;
import org.mmfmilku.atom.util.AssertUtil;
import org.mmfmilku.atom.util.ReflectUtils;

import java.io.IOException;
import java.lang.instrument.UnmodifiableClassException;
import java.util.*;

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
        String appPackage = AgentProperties.getProperty(AgentProperties.PROP_APP_BASE_PACKAGE);
        List<String> loadedClasses = InstrumentationContext.getLoadedClasses(appPackage);
        return pageList(offset, size, loadedClasses);
    }

    @Override
    public List<String> searchClassForPage(int offset, int size, String classShortNameLike) {
        if (offset < 1 || size < 1) {
            return Collections.emptyList();
        }
        String appPackage = AgentProperties.getProperty(AgentProperties.PROP_APP_BASE_PACKAGE);
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
        if (list.size() < offset) {
            return Collections.emptyList();
        }
        int startIndex = offset - 1;
        // copy
        List<T> subList = list.subList(startIndex, Math.min(list.size(), startIndex + size));
        return new ArrayList<>(subList);
    }

    private void checkClass(String className) {
        AssertUtil.isTrue(!className.startsWith("java"), "禁止重写系统类" + className);
        AssertUtil.isTrue(!className.startsWith("javax"), "禁止重写系统类" + className);
        AssertUtil.isTrue(!className.startsWith("jdk"), "禁止重写系统类" + className);
        AssertUtil.isTrue(!className.startsWith("sun"), "禁止重写系统类" + className);
        AssertUtil.isTrue(!className.startsWith("com.sun"), "禁止重写系统类" + className);
    }

    @Override
    public void retransformClass(String className) {
        checkClass(className);
        // TODO 如何清理
        OverrideBodyHolder.load(AgentProperties.getProperty(AgentProperties.PROP_BASE_PATH));
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
            Class[] classes = defineMap.keySet().stream().map(InstrumentationContext::searchClass).toArray(Class[]::new);
            System.out.println("retransformClasses：" + Arrays.toString(classes));
            InstrumentationContext.retransformClasses(classes);
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
            throw new BizException(e.getMessage());
        }
        InstrumentationContext.removeTransformer(ordTransformer);
    }

    @Override
    public void stopOrd(String stopFullClassName) {
        StopOrdTransformer ordTransformer = new StopOrdTransformer(stopFullClassName);
        InstrumentationContext.addTransformer(ordTransformer);
        try {
            Class<?> stopClazz = InstrumentationContext.searchClass(stopFullClassName);
            System.out.println("retransformClasses：" + stopClazz);
            if (stopClazz != null) {
                InstrumentationContext.retransformClasses(stopClazz);
            }
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
            throw new BizException(e.getMessage());
        }
        InstrumentationContext.removeTransformer(ordTransformer);
    }
}
