package org.mmfmilku.atom.agent.compiler;

import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.parser.ParserDispatcher;
import org.mmfmilku.atom.agent.compiler.parser.syntax.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Package;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.Modifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;

import java.lang.Class;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompilerUtil {

    /**
     * 解析源码为java语法树
     * */
    public static JavaAST parseAST(String code) {
        Lexer lexer = new Lexer(code);
        lexer.execute();
        ParserDispatcher parser = new ParserDispatcher(lexer);
        return parser.execute();
    }

    /**
     * 解析表达式
     * */
    public static Expression parseExpression(String text) {
        Lexer lexer = new Lexer(text);
        lexer.execute();
        return new ParserDispatcher(lexer).getExpression();
    }

    public static JavaAST newEmptyJavaAST(Class<?> nativeClass) {
        JavaAST javaAST = new JavaAST();
        javaAST.setImports(Collections.emptyList());

        Package aPackage = new Package();
        aPackage.setValue(nativeClass.getPackage().getName());
        javaAST.setPackageNode(aPackage);

        List<Method> methods = new ArrayList<>();
        for (java.lang.reflect.Method nativeClassMethod : nativeClass.getMethods()) {
            if (nativeClassMethod.getDeclaringClass() != nativeClass) {
                // 跳过父类方法
                continue;
            }
            Method method = new Method();
            method.setMethodName(nativeClassMethod.getName());
            // TODO 修饰符先写死
            method.setModifier(Modifier.DEFAULT);
            // TODO 注解先写死
            method.setAnnotations(Collections.emptyList());
            // TODO 方法参数先写死
            method.setMethodParams(Collections.emptyList());
            // TODO 返回类型先写死
            method.setReturnType(Object.class.getName());

            CodeBlock codeBlock = new CodeBlock();
            codeBlock.setStatements(Collections.emptyList());
            method.setCodeBlock(codeBlock);

            methods.add(method);
        }

        org.mmfmilku.atom.agent.compiler.parser.syntax.Class aClass = new org.mmfmilku.atom.agent.compiler.parser.syntax.Class(nativeClass.getSimpleName());
        aClass.setMethods(methods);
        aClass.setModifier(Modifier.DEFAULT);
        aClass.setAnnotations(Collections.emptyList());
        aClass.setMembers(Collections.emptyList());
        aClass.setConstructors(Collections.emptyList());

        javaAST.setClassList(Collections.singletonList(aClass));

        return javaAST;
    }

}
