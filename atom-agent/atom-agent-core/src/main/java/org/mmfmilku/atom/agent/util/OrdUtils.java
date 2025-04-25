package org.mmfmilku.atom.agent.util;

import org.mmfmilku.atom.agent.config.ClassORDDefine;
import org.mmfmilku.atom.agent.instrument.InstrumentationContext;
import org.mmfmilku.atom.agent.instrument.transformer.LoadOrdTransformer;
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
            InstrumentationContext.retransformClasses(classes);
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
            throw new BizException(e.getMessage());
        }
        InstrumentationContext.removeTransformer(ordTransformer);
    }

    public static void loadOrd(String loadClass, ClassORDDefine classORDDefine) {
        Map<String, ClassORDDefine> defineMap = new HashMap<>();
        defineMap.put(loadClass, classORDDefine);
        loadOrd(defineMap);
    }

}
