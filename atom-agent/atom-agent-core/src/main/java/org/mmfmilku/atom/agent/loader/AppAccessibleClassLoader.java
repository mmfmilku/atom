package org.mmfmilku.atom.agent.loader;

import java.net.URL;
import java.net.URLClassLoader;

public class AppAccessibleClassLoader extends URLClassLoader {

    public AppAccessibleClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            return loadedClass;
        }

        if (isAtomClass(name)) {
            try {
                // atom框架相关类在本加载器中加载，为了可以在类中调用目标app中的类
                // TODO 会导致该加载器加载的 JScriptExecutor 中执行atom相关类，与agent中执行的atom相关类是两套
                // 解决办法是全体类都使用该类加载器加载，或者仅 JScriptExecutor 使用该类加载器
                Class<?> c = findClass(name);
                if (resolve) {
                    resolveClass(c);
                }
                return c;
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
        return super.loadClass(name, resolve);
    }

    private boolean isAtomClass(String name) {
        // 临时设置两个执行类 TODO
        return name.startsWith("org.mmfmilku.atom.agent.console");
    }
}
