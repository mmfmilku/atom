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
import org.mmfmilku.atom.agent.compiler.parser.Parser;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Class;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Method;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Identifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.VarDefineStatement;
import org.mmfmilku.atom.agent.context.InstrumentationContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static Map<String, Map<String, Method>> toJavassistCode(JavaAST ...javaASTArr) {
        Map<String, Map<String, Method>> classMap = new HashMap<>();
        Stream.of(javaASTArr)
                .map(javaAST -> {
                    javaAST.useImport();
                    return javaAST.getClassList();
                })
                .flatMap(Collection::stream)
                .forEach(clazz -> {
                    Map<String, Method> collect = clazz.getMethods()
                            .stream()
                            .peek(ByteCodeUtils::toJavassistCode)
                            .collect(Collectors.toMap(Method::getMethodName, v -> v));
                    classMap.put(clazz.getClassFullName(), collect);
                });
        return classMap;
    }

    /**
     * 方法参数调用替
     * this -> $0
     * var1 -> $1
     * var2 -> $2
     * ...
     */
    public static void toJavassistCode(Method method) {
        CodeBlock codeBlock = method.getCodeBlock();
        List<VarDefineStatement> methodParams = method.getMethodParams();
        if (methodParams == null || methodParams.size() == 0) {
            return;
        }
        Map<String, String> paramMap = new HashMap<>(methodParams.size() + 1);
        paramMap.put("this", "$0");
        for (int i = 0; i < methodParams.size(); i++) {
            VarDefineStatement defineStatement = methodParams.get(i);
            paramMap.put(defineStatement.getVarName(), "$" + (i + 1));
        }
        List<Statement> statements = codeBlock.getStatements();
        for (Statement statement : statements) {
            List<Expression> allExpression = statement.getAllExpression();
            allExpression.stream()
                    .map(Expression::getBaseExpression)
                    .flatMap(Collection::stream)
                    .forEach(expression -> {
                        if (expression instanceof Identifier) {
                            Identifier identifier = (Identifier) expression;
                            String value = identifier.getValue();
                            identifier.setValue(paramMap.getOrDefault(value, value));
                        }
                    });
        }
    }

}
