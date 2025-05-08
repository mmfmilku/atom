package org.mmfmilku.atom.agent.compiler.parser.handle.struct;

import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.StructParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Method;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.VarDefineStatement;

import java.util.List;

/**
 * 解析方法，不包含解析修饰符
 * returnType methodName(...) {...}
 * */
public class MethodParser implements StructParserHandle<Method> {
    @Override
    public boolean match(ParserIterator iterator) {
        return false;
    }

    @Override
    public Method parse(ParserIterator iterator) {
        Method method = new Method();

        // 目前解析 public void getValue(...) {...}
        String returnType = iterator.parseWordsPoint();
        Token methodName = iterator.needNext(TokenType.Words);
        iterator.needNext(TokenType.LParen);

        List<VarDefineStatement> varDefineStatements = iterator.parameterDefine();
        if (iterator.isNext(TokenType.Words, "throws")) {
            // 处理方法异常抛出
            method.setThrowList(iterator.parseThrowList());
        }
        // TODO 抽象方法无代码体
        iterator.needNext(TokenType.LBrace);
        CodeBlock codeBlock = iterator.parseCodeBlock();

        method.setMethodName(methodName.getValue());
        method.setReturnType(returnType);
        method.setMethodParams(varDefineStatements);
        method.setCodeBlock(codeBlock);
        return method;
    }

}
