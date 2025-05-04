package org.mmfmilku.atom.agent.compiler.parser.handle.struct;

import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.StructParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Constructor;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.VarDefineStatement;

import java.util.List;

/**
 * 解析构造器，不包含解析修饰符
 * ClassName(...) {...}
 * */
public class ConstructorParser implements StructParserHandle<Constructor> {
    @Override
    public boolean match(ParserIterator iterator) {
        return false;
    }

    @Override
    public Constructor parse(ParserIterator iterator) {
        iterator.checkCurr(TokenType.Words);
        String returnType = iterator.getCurr().getValue();
        Constructor constructor = new Constructor(returnType);
        iterator.needNext(TokenType.LParen);

        List<VarDefineStatement> varDefineStatements = iterator.parameterDefine();
        if (iterator.isNext(TokenType.Words, "throws")) {
            // 处理方法异常抛出
            constructor.setThrowList(iterator.parseThrowList());
        }

        iterator.needNext(TokenType.LBrace);
        CodeBlock codeBlock = iterator.parseCodeBlock();

        constructor.setReturnType(returnType);
        constructor.setMethodParams(varDefineStatements);
        constructor.setCodeBlock(codeBlock);
        return constructor;
    }
}
