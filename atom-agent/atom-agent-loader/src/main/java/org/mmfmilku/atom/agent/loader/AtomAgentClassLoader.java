package org.mmfmilku.atom.agent.loader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * atom框架相关类在本加载器中加载
 * 为了可以在atom的执行类中调用目标app中的类（如springboot类加载器加载用户类）
 * */
public class AtomAgentClassLoader extends URLClassLoader {

    public AtomAgentClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // 从缓存中获取加载过的class
        Class<?> loadClass = findLoadedClass(name);
        if (loadClass != null) {
            return loadClass;
        }

        if (isAtomClass(name)) {
            // atom框架类的加载，打破双亲委托
            try {
                Class<?> c = findClass(name);
                if (resolve) {
                    resolveClass(c);
                }
                return c;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("AtomAgentClassLoader findClass fail:" + name);
            }
        }
        //
        return super.loadClass(name, resolve);
    }

    private boolean isAtomClass(String name) {
        // 临时设置两个执行类 TODO
        return name.startsWith("org.mmfmilku.atom");
    }
}
