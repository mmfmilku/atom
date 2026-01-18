package org.mmfmilku.atom.agent;

import org.junit.Test;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.jar.JarFile;

public class LoaderBootstrapTest {

    public static void main(String[] args) {
        // 由于单元测试入口类不可控，使用main方法测试
        agentmainTest();
    }

    public static void agentmainTest() {
        String path = System.getProperty("user.dir")
                + "\\atom-web-console\\src\\main\\resources\\jar\\atom-agent-core-0.0.1-SNAPSHOT-jar-with-dependencies.jar";
        LoaderBootstrap.agentmain("coreJarPath=" + path, new Instrumentation() {
            @Override
            public void addTransformer(ClassFileTransformer transformer, boolean canRetransform) {

            }

            @Override
            public void addTransformer(ClassFileTransformer transformer) {

            }

            @Override
            public boolean removeTransformer(ClassFileTransformer transformer) {
                return false;
            }

            @Override
            public boolean isRetransformClassesSupported() {
                return false;
            }

            @Override
            public void retransformClasses(Class<?>... classes) throws UnmodifiableClassException {

            }

            @Override
            public boolean isRedefineClassesSupported() {
                return false;
            }

            @Override
            public void redefineClasses(ClassDefinition... definitions) throws ClassNotFoundException, UnmodifiableClassException {

            }

            @Override
            public boolean isModifiableClass(Class<?> theClass) {
                return false;
            }

            @Override
            public Class[] getAllLoadedClasses() {
                return new Class[]{LoaderBootstrapTest.class};
            }

            @Override
            public Class[] getInitiatedClasses(ClassLoader loader) {
                return new Class[]{LoaderBootstrapTest.class};
            }

            @Override
            public long getObjectSize(Object objectToSize) {
                return 0;
            }

            @Override
            public void appendToBootstrapClassLoaderSearch(JarFile jarfile) {

            }

            @Override
            public void appendToSystemClassLoaderSearch(JarFile jarfile) {

            }

            @Override
            public boolean isNativeMethodPrefixSupported() {
                return false;
            }

            @Override
            public void setNativeMethodPrefix(ClassFileTransformer transformer, String prefix) {

            }
        });
    }

}