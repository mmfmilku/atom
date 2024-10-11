package org.mmfmilku.atom.transport.frpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class FRPCFactory {

    public static <T> T getService(Class<T> serviceClass, String fDir) {
        if (!serviceClass.isInterface()) {
            throw new RuntimeException("must interface");
        }
        InvocationHandler clientProxy = new FRPCClientProxy<>(serviceClass, fDir);
        Object o = Proxy.newProxyInstance(clientProxy.getClass().getClassLoader(),
                new Class[]{serviceClass}, clientProxy);
        return (T) o;
    }

}
