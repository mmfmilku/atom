package org.mmfmilku.atom.agent.compiler.parser.handle.struct;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.StructParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Generics;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Method;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.VarDefineStatement;

import java.util.List;

/**
 * 解析方法，不包含解析修饰符、方法泛形
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
        iterator.needNext();
        // TODO 返回类型的泛形
        Generics generics = iterator.parseGenericsAndNext();
        returnType += GrammarUtil.emptyWrap(generics);
        Token methodName = iterator.checkCurr(TokenType.Words);
        CodeBlock codeBlock = parseMethodParamAndBody(iterator, method);

        method.setMethodName(methodName.getValue());
        method.setReturnType(returnType);
        method.setCodeBlock(codeBlock);
        return method;
    }

    protected CodeBlock parseMethodParamAndBody(ParserIterator iterator, Method method) {
        iterator.needNext(TokenType.LParen);

        List<VarDefineStatement> varDefineStatements = iterator.parameterDefine();
        if (iterator.isNext(TokenType.Words, "throws")) {
            // 处理方法异常抛出
            method.setThrowList(iterator.parseThrowList());
        }
        method.setMethodParams(varDefineStatements);
        // TODO 抽象方法无代码体
        iterator.needNext(TokenType.LBrace);
        return iterator.parseCodeBlock();
    }

}
