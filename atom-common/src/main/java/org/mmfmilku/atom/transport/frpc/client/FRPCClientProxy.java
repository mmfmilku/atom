package org.mmfmilku.atom.transport.frpc.client;

import org.mmfmilku.atom.transport.frpc.FRPCParam;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class FRPCClientProxy<T> implements InvocationHandler {
    
    private Class<T> delegateClass;

    private FRPCClient frpcClient = FRPCClient.getInstance();

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
        FRPCParam frpcParam = new FRPCParam();
        frpcParam.setServiceClass(delegateClass.getName());
        frpcParam.setApiName(methodName);
        frpcParam.setData(args);
        System.out.println("执行远程调用" + frpcParam);
        return frpcClient.call(frpcParam).getData();
    }
}
