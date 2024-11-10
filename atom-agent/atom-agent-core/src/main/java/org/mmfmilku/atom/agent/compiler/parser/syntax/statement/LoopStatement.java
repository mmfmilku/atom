package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class LoopStatement implements Statement {

    protected Expression loopCondition;

    protected CodeBlock loopBody;

    public LoopStatement(Expression loopCondition, CodeBlock loopBody) {
        this.loopCondition = loopCondition;
        this.loopBody = loopBody;
    }

    public Expression getLoopCondition() {
        return loopCondition;
    }

    public void setLoopCondition(Expression loopCondition) {
        this.loopCondition = loopCondition;
    }

    public CodeBlock getLoopBody() {
        return loopBody;
    }

    public void setLoopBody(CodeBlock loopBody) {
        this.loopBody = loopBody;
    }

    @Override
    public List<Expression> getAllExpression() {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(loopCondition);
        expressions.addAll(loopBody.getAllExpression());
        return expressions;
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        loopCondition.useImports(importsMap);
        loopBody.useImports(importsMap);
    }
}
