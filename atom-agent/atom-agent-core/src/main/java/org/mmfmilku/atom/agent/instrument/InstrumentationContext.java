package org.mmfmilku.atom.agent.instrument;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.*;
import java.util.function.Function;

/**
 * InstrumentationUtils
 *
 * @author mmfmilku
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

    public static synchronized void clear() {
        System.out.println("---------------------clear InstrumentationContext-------------------------");
        if (instance != null) {
            clearTransformer();
            instance = null;
        }
    }
    
    public static InstrumentationContext getInstance() {
        if (instance != null) {
            return instance;
        }
        throw new RuntimeException("InstrumentationContext have not init");
    }
    
    public static void addTransformer(ClassFileTransformer transformer) {
        InstrumentationContext instance = getInstance();
        if (instance.addedTransformer.contains(transformer)) {
            System.out.println("already add transformer:" + transformer.toString());
            return;
        }
        instance.inst.addTransformer(transformer, true);
        instance.addedTransformer.add(transformer);
    }

    public static Class<?> searchClass(String searchClassName) {
        InstrumentationContext instance = getInstance();
        Class<?>[] loadedClasses = instance.inst.getAllLoadedClasses();
        for (Class<?> loadedClass : loadedClasses) {
            if (loadedClass.getName().equals(searchClassName)) {
                return loadedClass;
            }
        }
        return null;
    }
    
    private static List<String> getLoadedClasses(Function<Class<?>, Boolean> matchFunc) {
        InstrumentationContext instance = getInstance();
        Class<?>[] allLoadedClasses = instance.inst.getAllLoadedClasses();
        List<String> result = new ArrayList<>();
        for (Class<?> loadedClass : allLoadedClasses) {
            // 排除lambda
            if (matchFunc.apply(loadedClass) && !loadedClass.getName().contains("$$Lambda")) {
                result.add(loadedClass.getName());
            }
        }
        return result;
    }

    public static List<String> getLoadedClasses(String packageName) {
        return getLoadedClasses(clazz -> clazz.getName().startsWith(packageName));
    }

    public static List<String> getLoadedClasses(String packageName, String classShortNameLike) {
        return getLoadedClasses(clazz -> clazz.getName().startsWith(packageName)
                && clazz.getSimpleName().contains(classShortNameLike));
    }

    public static ClassLoader searchClassLoader(String searchClassLoaderName) {
        InstrumentationContext instance = getInstance();
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
        InstrumentationContext instance = getInstance();
        instance.inst.removeTransformer(transformer);
        instance.addedTransformer.remove(transformer);
    }

    public static void clearTransformer() {
        InstrumentationContext instance = getInstance();
        instance.addedTransformer.forEach(transformer -> {
            instance.inst.removeTransformer(transformer);
        });
        instance.addedTransformer.clear();
    }
    
    public static void retransformClasses(Class<?>... classes) throws UnmodifiableClassException {
        InstrumentationContext instance = getInstance();
        instance.inst.retransformClasses(classes);
    }
    
}
