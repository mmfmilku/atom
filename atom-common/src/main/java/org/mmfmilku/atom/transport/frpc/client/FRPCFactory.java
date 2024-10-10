package org.mmfmilku.atom.transport.frpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class FRPCFactory {

    public static <T> T getService(Class<T> serviceClass) {
        if (!serviceClass.isInterface()) {
            throw new RuntimeException("must interface");
        }
        InvocationHandler clientProxy = new FRPCClientProxy<>(serviceClass);
        Object o = Proxy.newProxyInstance(clientProxy.getClass().getClassLoader(),
                new Class[]{serviceClass}, clientProxy);
        return (T) o;
    }

}
