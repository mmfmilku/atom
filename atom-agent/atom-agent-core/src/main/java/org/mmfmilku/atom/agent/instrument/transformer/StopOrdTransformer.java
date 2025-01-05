package org.mmfmilku.atom.agent.instrument.transformer;

import org.mmfmilku.atom.agent.config.ClassORDDefine;
import org.mmfmilku.atom.agent.instrument.InstrumentationContext;
import org.mmfmilku.atom.agent.util.ByteCodeUtils;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Map;

public class StopOrdTransformer implements ClassFileTransformer {

    private String stopFullClassName;

    public StopOrdTransformer(String stopFullClassName) {
        this.stopFullClassName = stopFullClassName;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null) {
            return classfileBuffer;
        }
        String fullClassName = className.replace("/", ".");
        if (fullClassName.equals(stopFullClassName)) {
            System.out.println("do StopOrdTransformer class:" + className);
            InstrumentationContext.addOrdClass(fullClassName);
            return null;
        } else {
            return classfileBuffer;
        }
    }
}
