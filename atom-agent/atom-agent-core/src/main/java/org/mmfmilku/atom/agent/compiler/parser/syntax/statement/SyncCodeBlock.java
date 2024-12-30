package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

import java.util.List;
import java.util.Map;

public class SyncCodeBlock extends CodeBlock {

    private Expression syncObject;

    public SyncCodeBlock(CodeBlock codeBlock) {
        getStatements().addAll(codeBlock.getStatements());
        setModifier(codeBlock.getModifier());
    }

    @Override
    public String getStatementSource() {
        return GrammarUtil.getSentenceCode("synchronized (" + syncObject.getSourceCode() + ")"
                , super.getStatementSource());
    }

    @Override
    public List<Expression> getAllExpression() {
        List<Expression> allExpression = super.getAllExpression();
        allExpression.add(syncObject);
        return allExpression;
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        super.useImports(importsMap);
        syncObject.useImports(importsMap);
    }

    public Object getSyncObject() {
        return syncObject;
    }

    public void setSyncObject(Expression syncObject) {
        this.syncObject = syncObject;
    }
}
