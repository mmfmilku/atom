package org.mmfmilku.atom.agent.instrument.transformer;

import org.mmfmilku.atom.agent.instrument.InstrumentationContext;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Map;

public class LoadByteCodeOrdTransformer implements ClassFileTransformer {

    private Map<String, byte[]> byteCodeMap;

    public LoadByteCodeOrdTransformer(Map<String, byte[]> byteCodeMap) {
        this.byteCodeMap = byteCodeMap;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null) {
            return classfileBuffer;
        }
        String fullClassName = className.replace("/", ".");
        if (!byteCodeMap.containsKey(fullClassName)) {
            return classfileBuffer;
        }
        byte[] byteCode = byteCodeMap.get(fullClassName);
        if (byteCode != null) {
            System.out.println("do LoadByteCodeOrdTransformer class:" + className);
            InstrumentationContext.addOrdClass(fullClassName);
            return byteCode;
        }
        return classfileBuffer;
    }
}
