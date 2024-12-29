package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

public class SyncCodeBlock extends CodeBlock {

    private Expression syncObject;

    public SyncCodeBlock(CodeBlock codeBlock) {
        getStatements().addAll(codeBlock.getStatements());
        setModifier(codeBlock.getModifier());
    }

    public Object getSyncObject() {
        return syncObject;
    }

    public void setSyncObject(Expression syncObject) {
        this.syncObject = syncObject;
    }
}
