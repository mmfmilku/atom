package org.mmfmilku.atom.agent.util;

import org.mmfmilku.atom.agent.config.ClassORDDefine;
import org.mmfmilku.atom.agent.instrument.InstrumentationContext;
import org.mmfmilku.atom.agent.instrument.transformer.LoadOrdTransformer;
import org.mmfmilku.atom.agent.instrument.transformer.StopOrdTransformer;
import org.mmfmilku.atom.exception.BizException;

import java.lang.instrument.UnmodifiableClassException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class OrdUtils {

    public static void loadOrd(Map<String, ClassORDDefine> defineMap) {
        LoadOrdTransformer ordTransformer = new LoadOrdTransformer(defineMap);
        InstrumentationContext.addTransformer(ordTransformer);
        try {
            Class[] classes = defineMap.keySet().stream().map(InstrumentationContext::searchClass).toArray(Class[]::new);
            System.out.println("retransformClasses：" + Arrays.toString(classes));
            if (classes == null || classes.length == 0) {
                throw new BizException("no loadOrd class found");
            }
            InstrumentationContext.retransformClasses(classes);
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
            throw new BizException(e.getMessage());
        } finally {
            InstrumentationContext.removeTransformer(ordTransformer);
        }
    }

    public static void loadOrd(ClassORDDefine classORDDefine) {
        Map<String, ClassORDDefine> defineMap = new HashMap<>();
        defineMap.put(classORDDefine.getName(), classORDDefine);
        loadOrd(defineMap);
    }

    public static void stopOrd(String stopFullClassName) {
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
        } finally {
            InstrumentationContext.removeTransformer(ordTransformer);
        }
    }

}
