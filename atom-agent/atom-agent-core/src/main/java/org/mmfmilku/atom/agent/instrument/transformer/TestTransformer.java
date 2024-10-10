/********************************************
 * 文件名称: TestTransformer.java
 * 系统名称: 综合理财管理平台6.0
 * 模块名称:
 * 软件版权: 恒生电子股份有限公司
 * 功能说明:
 * 系统版本: 6.0.0.1
 * 开发人员: chenxp
 * 开发时间: 2024/6/4
 * 审核人员:
 * 相关文档:
 * 修改记录:   修改日期    修改人员    修改单号       版本号                   修改说明
 * V6.0.0.1  20240604-01  chenxp   TXXXXXXXXXXXX    IFMS6.0VXXXXXXXXXXXXX   新增 
 *********************************************/
package org.mmfmilku.atom.agent.instrument.transformer;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * TestTransformer
 *
 * @author chenxp
 * @date 2024/6/4:15:00
 */
public class TestTransformer implements ClassFileTransformer {

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if ("com/hundsun/jres/impl/db/session/DBSession".equals(className)) {
            System.out.println("transform-3");
            try {
                // 从ClassPool获得CtClass对象
                final ClassPool classPool = ClassPool.getDefault();
                final CtClass clazz = classPool.get("com.hundsun.jres.impl.db.session.DBSession");
                clazz.instrument(new ExprEditor() {
                    @Override
                    public void edit(MethodCall m) throws CannotCompileException {
                        // MethodCall表示的是重写的方法里面，对其他方法的调用过程
                        if (m.getClassName().equals("com.hundsun.jres.impl.db.session.DBSession")
                                && m.getMethodName().equals("dbSessionDebugLog")) {
                            m.replace("System.out.println(java.util.Arrays.toString($args));");
//                            m.replace("" +
//                                    "if ($2.length >= 2 && $2[1].contains(\"tbfile\")) {" +
//                                    "/*System.out.println(\"get you tbfile\");*/" +
//                                    "} else {" +
//                                    "System.out.println(\"not you\");\n" +
//                                    "$_ = $proceed($$);" +
//                                    "}");
                        }
                    }
                });
//                    CtMethod convertToAbbr = clazz.getDeclaredMethod("dbSessionDebugLog");
//                    String methodBody = 
//                            "{" + 
//                                    "if ($2.length >= 2 && $2[1].contains(\"tbfile\")) {" +
//                                        "System.out.println(\"get you tbfile\");" +
//                                    "} else {" +
//                                        "System.out.println(\"not you\");" +
//                                    "} " +
//                                    "return;" + 
//                            "}";
//                    convertToAbbr.setBody(methodBody);
                // 返回字节码，并且detachCtClass对象
                byte[] byteCode = clazz.toBytecode();
                //detach的意思是将内存中曾经被javassist加载过的Date对象移除，如果下次有需要在内存中找不到会重新走javassist加载
                clazz.detach();
                return byteCode;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
} 