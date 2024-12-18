/********************************************
 * 文件名称: JSONDefineTransformer.java
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

import javassist.*;
import org.mmfmilku.atom.agent.config.ClassORDDefine;
import org.mmfmilku.atom.agent.config.Keywords;
import org.mmfmilku.atom.agent.config.MethodORDDefine;
import org.mmfmilku.atom.agent.config.OverrideBodyHolder;
import org.mmfmilku.atom.agent.util.ByteCodeUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Map;

/**
 * JSONDefineTransformer
 * 负责从定义的重写文件中重新定义转换class程序
 *
 * @author chenxp
 * @date 2024/6/4:15:07
 */
public class FileDefineTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null) {
            return classfileBuffer;
        }
        String fullClassName = className.replace("/", ".");
        ClassORDDefine classOrdDefine = OverrideBodyHolder.getORMethodMap(fullClassName);
        if (classOrdDefine != null) {
            System.out.println("do FileDefineTransformer class:" + className);
            return ByteCodeUtils.redefineClass(classfileBuffer, classOrdDefine);
        }
        return classfileBuffer;
    }
    
}
