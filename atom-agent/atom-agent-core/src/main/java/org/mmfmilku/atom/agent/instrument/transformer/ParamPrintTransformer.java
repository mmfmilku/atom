/********************************************
 * 文件名称: ParamPrintTransformer.java
 * 系统名称: 综合理财管理平台6.0
 * 模块名称:
 * 软件版权: 恒生电子股份有限公司
 * 功能说明:
 * 系统版本: 6.0.0.1
 * 开发人员: chenxp
 * 开发时间: 2024/6/18
 * 审核人员:
 * 相关文档:
 * 修改记录:   修改日期    修改人员    修改单号       版本号                   修改说明
 * V6.0.0.1  20240618-01  chenxp   TXXXXXXXXXXXX    IFMS6.0VXXXXXXXXXXXXX   新增 
 *********************************************/
package org.mmfmilku.atom.agent.instrument.transformer;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.mmfmilku.atom.agent.config.*;
import org.mmfmilku.atom.agent.instrument.InstrumentationContext;
import org.mmfmilku.atom.agent.util.ByteCodeUtils;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ParamPrintTransformer
 * 打印方法入参与出参
 *
 * @author chenxp
 * @date 2024/6/18:14:34
 */
public class ParamPrintTransformer implements ClassFileTransformer {
    
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null) {
            return classfileBuffer;
        }
        String fullClassName = className.replace("/", ".");
        ClassORDDefine classOrdDefine = OverrideBodyHolder.getORMethodMap(fullClassName);
        if (classOrdDefine != null) {
            System.out.println("do ParamPrintTransformer class:" + className);
            try {
                CtClass ctClass = ByteCodeUtils.getCtClass(classfileBuffer);
                Map<String, MethodORDDefine> methodORDMap = classOrdDefine.getMethodORDMap();
                for (Map.Entry<String, MethodORDDefine> entry : methodORDMap.entrySet()) {
                    Map<Keywords, String> srcMap = entry.getValue().getSrcMap();
                    Map<Keywords, List<String>> paramMap = entry.getValue().getParamMap();
                    final String methodName = entry.getKey();
                    CtMethod ctMethod = ctClass.getDeclaredMethod(methodName);
                    if (srcMap.containsKey(Keywords.PRINT_PARAM)) {
                        String src = srcMap.get(Keywords.PRINT_PARAM);
                        src = ORDParser.isEmptyBlock(src) ? buildPrintParam(ctMethod, null) : src;
                        ctMethod.insertBefore(src);
                    }
                    if (srcMap.containsKey(Keywords.PRINT_RETURN)) {
                        String src = srcMap.get(Keywords.PRINT_RETURN);
                        src = ORDParser.isEmptyBlock(src) ? buildPrintReturn(ctMethod, null) : src;
                        ctMethod.insertAfter(src);
                    }
                    if (srcMap.containsKey(Keywords.PRINT_CALL)) {
                        ctMethod.instrument(new ExprEditor() {
                            @Override
                            public void edit(MethodCall m) throws CannotCompileException {
                                boolean match = false;
                                List<String> paramList = paramMap.get(Keywords.PRINT_CALL);
                                if (paramList == null || paramList.size() == 0) {
                                    return;
                                }
                                String callClass = m.getClassName();
                                String callMethod = m.getMethodName();
                                for (String param : paramList) {
                                    if (param.equals(callClass)) {
                                        match = true;
                                        break;
                                    }
                                    String[] split = param.split("#");
                                    if (split.length > 1 && split[0].equals(callClass) && split[1].equals(callMethod)) {
                                        match = true;
                                        break;
                                    }
                                }
                                if (match) {
                                    try {
                                        String src = srcMap.get(Keywords.PRINT_CALL);
                                        src = ORDParser.isEmptyBlock(src) ? buildPrintCall(ctMethod, m) : src;
                                        m.replace(src);
                                    } catch (NotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                }
                // 返回字节码，并且detachCtClass对象
                byte[] byteCode = ctClass.toBytecode();
                //detach的意思是将内存中曾经被javassist加载过的对象移除，如果下次有需要在内存中找不到会重新走javassist加载
                ctClass.detach();
                return byteCode;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return classfileBuffer;
    }

    private static String buildPrintCall(CtMethod ctMethod, MethodCall methodCall) throws NotFoundException {
        return "{" +
                "" +
                buildPrintParam(ctMethod, methodCall) +
                "$_ = $proceed($$);" +
                buildPrintReturn(ctMethod, methodCall) +
                "}";
    }

    private static String buildPrintParam(CtMethod ctMethod, MethodCall methodCall) throws NotFoundException {
        String callMethod = "";
        if (methodCall != null) {
            callMethod = methodCall.getMethod().getLongName();
        }
        StringBuilder insertSource = new StringBuilder("{");
        String defaultPrintMethod = "org.mmfmilku.atom.agent.instrument.transformer.ParamPrintTransformer.print";
        String callPrintFormat = String.format(defaultPrintMethod +
                "(\"%s\",\"%s\",%s);", ctMethod.getLongName(), callMethod, "$args");
        insertSource.append(callPrintFormat);
        insertSource.append("}");
        return insertSource.toString();
    }

    private static String buildPrintReturn(CtMethod ctMethod, MethodCall methodCall) throws NotFoundException {
        String callMethod = "";
        if (methodCall != null) {
            callMethod = methodCall.getMethod().getLongName();
        }
        StringBuilder insertSource = new StringBuilder("{");
        String defaultPrintMethod = "org.mmfmilku.atom.agent.instrument.transformer.ParamPrintTransformer.print";
        String callPrintFormat = String.format(defaultPrintMethod +
                "(\"%s\",\"%s\",%s);", ctMethod.getLongName(), callMethod, "$_");
        insertSource.append(callPrintFormat);
        insertSource.append("}");
        return insertSource.toString();
    }


    /**
     * 打印参数或返回值对象
     *
     * @param methodName 方法名
     * @param callMethod 调用方法
     * @param args       入参数组
     */
    public static void print(String methodName, String callMethod, Object[] args) {
        System.out.println(String.format("agent ParamPrint \n    methodName=%s,\n    callMethod=%s,\n    args=%s"
                , methodName, callMethod, toString(args)));
    }

    /**
     * 打印参数或返回值对象
     *
     * @param methodName 方法名
     * @param callMethod 调用方法
     * @param data       数据对象
     */
    public static void print(String methodName, String callMethod, Object data) {
        System.out.println(String.format("agent ParamPrint-return \n    methodName=%s,\n    callMethod=%s,\n    return=%s"
                , methodName, callMethod, toString(data)));
    }

    public static void print(String methodName, String callMethod, int data) {
        print(methodName, callMethod, Integer.valueOf(data));
    }

    public static void print(String methodName, String callMethod, long data) {
        print(methodName, callMethod, Long.valueOf(data));
    }

    public static void print(String methodName, String callMethod, float data) {
        print(methodName, callMethod, Float.valueOf(data));
    }

    public static void print(String methodName, String callMethod, double data) {
        print(methodName, callMethod, Double.valueOf(data));
    }

    public static void print(String methodName, String callMethod, boolean data) {
        print(methodName, callMethod, Boolean.valueOf(data));
    }

    private static String toString(Object[] args) {
        return "[" + Stream.of(args).map(ParamPrintTransformer::toString).collect(Collectors.joining(",")) + "]";
    }

    private static String toString(Object o) {
        String toStringLongName = AgentProperties.getProperty(AgentProperties.PROP_TO_STRING_METHOD);
        if (toStringLongName == null) {
            return o.toString();
        }
        int i = toStringLongName.lastIndexOf(".");
        String toStringClass = toStringLongName.substring(0, i);
        String toStringMethod = toStringLongName.substring(i + 1);
        try {
            Class<?> toStringClazz = InstrumentationContext.searchClass(toStringClass);
            if (toStringClazz == null) {
                return o.toString();
            }
            Method method = toStringClazz.getMethod(toStringMethod, Object.class);
            Object toStringData = method.invoke(null, o);
            return toStringData.toString();
        } catch (NullPointerException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return o.toString();
        }
    }
}
