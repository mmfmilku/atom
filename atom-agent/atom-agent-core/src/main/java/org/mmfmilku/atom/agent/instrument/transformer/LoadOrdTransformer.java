package org.mmfmilku.atom.agent.instrument.transformer;

import org.mmfmilku.atom.agent.config.ClassORDDefine;
import org.mmfmilku.atom.agent.util.ByteCodeUtils;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Map;

public class LoadOrdTransformer implements ClassFileTransformer {

    private Map<String, ClassORDDefine> defineMap;

    public LoadOrdTransformer(Map<String, ClassORDDefine> defineMap) {
        this.defineMap = defineMap;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null) {
            return classfileBuffer;
        }
        String fullClassName = className.replace("/", ".");
        if (!defineMap.containsKey(fullClassName)) {
            return classfileBuffer;
        }
        ClassORDDefine classOrdDefine = defineMap.get(fullClassName);
        if (classOrdDefine != null) {
            System.out.println("do LoadOrdTransformer class:" + className);
            return ByteCodeUtils.redefineClass(classfileBuffer, classOrdDefine);
        }
        return classfileBuffer;
    }
}
