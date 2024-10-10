package org.mmfmilku.atom.agent.api.impl;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.mmfmilku.atom.agent.instrument.InstrumentationContext;
import org.mmfmilku.atom.agent.util.ByteCodeUtils;
import org.mmfmilku.atom.api.InstrumentApi;
import org.mmfmilku.atom.transport.frpc.FRPCService;

import java.io.IOException;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    public List<String> listClassForPage(int offset, int size, String classShortNameLike) {
        if (offset < 1 || size < 1) {
            return Collections.emptyList();
        }
        // TODO,从配置属性中获取应用基础包路径
        String appPackage = "";
        List<String> loadedClasses = InstrumentationContext.getLoadedClasses(appPackage, classShortNameLike);
        return pageList(offset, size, loadedClasses);
    }

    @Override
    public byte[] getByteCode(String fullClassName) {
        try {
            return ByteCodeUtils.getByteCode(fullClassName);
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
}
