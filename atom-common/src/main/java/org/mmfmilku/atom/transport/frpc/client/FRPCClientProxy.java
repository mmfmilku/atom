package org.mmfmilku.atom.transport.frpc.client;

import org.mmfmilku.atom.transport.frpc.FRPCParam;
import org.mmfmilku.atom.transport.frpc.FRPCReturn;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class FRPCClientProxy<T> implements InvocationHandler {
    
    private Class<T> delegateClass;

    private String fDir;

    private FRPCClient frpcClient;

    public FRPCClientProxy(Class<T> delegateClass, String fDir) {
        this.delegateClass = delegateClass;
        this.fDir = fDir;
        this.frpcClient = FRPCClient.getInstance(fDir);
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
        FRPCReturn frpcReturn = frpcClient.call(frpcParam);
        System.out.println("远程调用返回" + frpcReturn);
        return frpcReturn.getData();
    }
}
