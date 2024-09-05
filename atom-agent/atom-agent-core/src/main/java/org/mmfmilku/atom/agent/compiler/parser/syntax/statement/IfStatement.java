package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.parser.syntax.CodeBlock;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

import java.util.ArrayList;
import java.util.List;

public class IfStatement extends Statement {

    // TODO 如何体现 if,else if,else

    private List<Expression> conditions = new ArrayList<>();
    private List<CodeBlock> codeBlocks = new ArrayList<>();

    public List<Expression> getConditions() {
        return conditions;
    }

    public void setConditions(List<Expression> conditions) {
        this.conditions = conditions;
    }

    public List<CodeBlock> getCodeBlocks() {
        return codeBlocks;
    }

    public void setCodeBlocks(List<CodeBlock> codeBlocks) {
        this.codeBlocks = codeBlocks;
    }
}
