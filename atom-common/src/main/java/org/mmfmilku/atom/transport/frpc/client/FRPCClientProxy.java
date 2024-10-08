package org.mmfmilku.atom.transport.frpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

public class FRPCClientProxy<T> implements InvocationHandler {
    
    private Class<T> delegateClass;

    public FRPCClientProxy(Class<T> delegateClass) {
        this.delegateClass = delegateClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String declaringClassName = method.getDeclaringClass().getName();
        if (declaringClassName.equals(delegateClass.getName())) {
            return remoteCall(method.getName(), args);
        } else if (declaringClassName.equals(Object.class.getName())) {
            return method.invoke(this, args);
        }
        throw new RuntimeException("FRPCService interface can not extends any interface :" + declaringClassName);
    }
    
    private Object remoteCall(String methodName, Object[] args) {
        System.out.println("-------------代理开始-------------");
        System.out.println("调用服务:" + delegateClass.getName());
        System.out.println("调用方法:" + methodName);
        System.out.println("传参:" + Arrays.toString(args));
        System.out.println("-------------代理结束-------------");
        return null;
    }
}
