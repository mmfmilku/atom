package org.mmfmilku.atom.agent.util;

import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.agent.config.ClassORDDefine;
import org.mmfmilku.atom.agent.config.Keywords;
import org.mmfmilku.atom.agent.config.MethodORDDefine;
import org.mmfmilku.atom.agent.instrument.InstrumentationContext;
import org.mmfmilku.atom.agent.instrument.transformer.LoadOrdTransformer;
import org.mmfmilku.atom.agent.instrument.transformer.StopOrdTransformer;
import org.mmfmilku.atom.exception.BizException;

import java.lang.instrument.UnmodifiableClassException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class OrdUtils {

    public static void loadOrd(Map<String, ClassORDDefine> defineMap) {
        System.out.println("loadOrd for defineMap:\n" + defineMap.toString());
        LoadOrdTransformer ordTransformer = new LoadOrdTransformer(defineMap);
        InstrumentationContext.addTransformer(ordTransformer);
        try {
            Class[] classes = defineMap.keySet().stream().map(InstrumentationContext::searchClass).toArray(Class[]::new);
            System.out.println("retransformClasses：" + Arrays.toString(classes));
            if (classes == null || classes.length == 0 || classes[0] == null) {
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

    public static void loadOrd(JavaAST javaAST) {
        System.out.println("loadOrd for javaAST:\n" + javaAST.getSourceCode());
        Map<String, ClassORDDefine> defineMap = astToOrd(javaAST);
        loadOrd(defineMap);
    }

    public static Map<String, ClassORDDefine> astToOrd(JavaAST javaAST) {
        // 执行useImport
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
