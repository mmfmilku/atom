package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

/**
 * do while 循环语句
 *
 * do
 *      loopBody
 * while (loopCondition);
 *
 * */
public class DoWhileStatement extends LoopStatement {

    public DoWhileStatement(Expression loopCondition, CodeBlock loopBody) {
        super(loopCondition, loopBody);
    }

    @Override
    public String getStatementSource() {
        return GrammarUtil.getSentenceCode("do",
                loopBody.getSourceCode(),
                "while",
                "(",
                loopCondition.getSourceCode(),
                ");"
        );
    }
}
