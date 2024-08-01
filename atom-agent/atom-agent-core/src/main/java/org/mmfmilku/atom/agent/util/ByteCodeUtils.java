/********************************************
 * 文件名称: ByteCodeUtils.java
 * 系统名称: 综合理财管理平台6.0
 * 模块名称:
 * 软件版权: 恒生电子股份有限公司
 * 功能说明:
 * 系统版本: 6.0.0.1
 * 开发人员: chenxp
 * 开发时间: 2024/6/19
 * 审核人员:
 * 相关文档:
 * 修改记录:   修改日期    修改人员    修改单号       版本号                   修改说明
 * V6.0.0.1  20240619-01  chenxp   TXXXXXXXXXXXX    IFMS6.0VXXXXXXXXXXXXX   新增 
 *********************************************/
package org.mmfmilku.atom.agent.util;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import org.mmfmilku.atom.agent.context.InstrumentationContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * ByteCodeUtils
 *
 * @author chenxp
 * @date 2024/6/19:13:54
 */
public class ByteCodeUtils {

    public static CtClass getCtClass(byte[] byteCode) throws IOException {
        //                final CtClass clazz = classPool.get(fullClassName);
        try (InputStream inputStream = new ByteArrayInputStream(byteCode)) {
            final ClassPool classPool = ClassPool.getDefault();
            return classPool.makeClass(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    public static void appendClassPath(ClassLoader classLoader) {
        ClassPool classPool = ClassPool.getDefault();
        classPool.appendClassPath(new LoaderClassPath(classLoader));
    }

}
