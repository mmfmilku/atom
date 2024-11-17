package org.mmfmilku.atom.agent.compiler.parser.syntax.handle;

import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.Parser;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.VarDefineStatement;

public class VarDefineAssignParser extends VarDefineParser {

    @Override
    public VarDefineStatement parse(Parser.ParserIterator iterator) {
        VarDefineStatement varDefine = super.parse(iterator);
        if (iterator.isNext(TokenType.Symbol, EQUAL)) {
            // 变量定义并且赋值
            // 指向等号后一位
            iterator.needNext();
            iterator.needNext();
            Expression expression = iterator.parseExpression();
            varDefine.setAssignExpression(expression);
        } else {
            // todo 多变量定义 int a,b;
        }
        return varDefine;
    }

}
