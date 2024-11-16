package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

public class WhileStatement extends LoopStatement {

    public WhileStatement(Expression loopCondition, CodeBlock loopBody) {
        super(loopCondition, loopBody);
    }

    @Override
    public String getStatementSource() {
        return GrammarUtil.getSentenceCode("while",
                "(",
                loopCondition.getSourceCode(),
                ")",
                loopBody.getSourceCode()
        );
    }

}
