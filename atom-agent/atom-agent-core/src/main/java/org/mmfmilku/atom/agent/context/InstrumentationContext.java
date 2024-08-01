/********************************************
 * 文件名称: InstrumentationUtils.java
 * 系统名称: 综合理财管理平台6.0
 * 模块名称:
 * 软件版权: 恒生电子股份有限公司
 * 功能说明:
 * 系统版本: 6.0.0.1
 * 开发人员: chenxp
 * 开发时间: 2024/6/17
 * 审核人员:
 * 相关文档:
 * 修改记录:   修改日期    修改人员    修改单号       版本号                   修改说明
 * V6.0.0.1  20240617-01  chenxp   TXXXXXXXXXXXX    IFMS6.0VXXXXXXXXXXXXX   新增 
 *********************************************/
package org.mmfmilku.atom.agent.context;

import org.mmfmilku.atom.agent.instrument.FileDefineTransformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * InstrumentationUtils
 *
 * @author chenxp
 * @date 2024/6/17:14:03
 */
public class InstrumentationContext {

    private Instrumentation inst;
    
    private static InstrumentationContext instance;
    
    private Set<ClassFileTransformer> addedTransformer;
    
    private InstrumentationContext (Instrumentation inst) {
        this.inst = inst;
        addedTransformer = new HashSet<>();
    }
    
    public static synchronized void init(Instrumentation inst) {
        System.out.println("---------------------init InstrumentationContext,inst=" + inst.toString() +
                "-------------------------");
        if (instance == null) {
            instance = new InstrumentationContext(inst);
        } else {
            clearTransformer();
            // 多次attach的inst不是同一个
            instance.inst = inst;
        }
    }
    
    public static InstrumentationContext getInstance() {
        if (instance != null) {
            return instance;
        }
        throw new RuntimeException("InstrumentationContext have not init");
    }
    
    public static void checkInit() {
        if (instance == null) {
            throw new RuntimeException("InstrumentationContext have not init");
        }
    }
    
    public static void addTransformer(ClassFileTransformer transformer) {
        checkInit();
        if (instance.addedTransformer.contains(transformer)) {
            System.out.println("already add transformer:" + transformer.toString());
            return;
        }
        instance.inst.addTransformer(transformer, true);
        instance.addedTransformer.add(transformer);
    }

    public static Class<?> searchClass(String searchClassName) {
        checkInit();
        Class<?>[] loadedClasses = instance.inst.getAllLoadedClasses();
        for (Class<?> loadedClass : loadedClasses) {
            if (loadedClass.getName().equals(searchClassName)) {
                return loadedClass;
            }
        }
        return null;
    }

    public static ClassLoader searchClassLoader(String searchClassLoaderName) {
        checkInit();
        Class<?>[] loadedClasses = instance.inst.getAllLoadedClasses();
        for (Class<?> loadedClass : loadedClasses) {
            ClassLoader classLoader = loadedClass.getClassLoader();
            if (classLoader != null && classLoader.getClass().getName().equals(searchClassLoaderName)) {
                return classLoader;
            }
        }
        return null;
    }

    public static void removeTransformer(ClassFileTransformer transformer) {
        checkInit();
        instance.inst.removeTransformer(transformer);
        instance.addedTransformer.remove(transformer);
    }

    public static void clearTransformer() {
        checkInit();
        instance.addedTransformer.forEach(transformer -> {
            instance.inst.removeTransformer(transformer);
        });
        instance.addedTransformer.clear();
    }
    
    public static void retransformClasses(Class<?>... classes) throws UnmodifiableClassException {
        checkInit();
        instance.inst.retransformClasses(classes);
    }
    
}
