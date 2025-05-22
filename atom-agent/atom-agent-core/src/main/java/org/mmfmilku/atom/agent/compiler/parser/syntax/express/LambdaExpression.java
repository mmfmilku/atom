package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.Identifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.LeafExpression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;

import java.util.List;
import java.util.Map;

/**
 * lambda表达式
 * */
public class LambdaExpression implements NestedExpression {

    List<Identifier> inputs;

    CodeBlock codeBlock;

    public LambdaExpression(List<Identifier> inputs, CodeBlock codeBlock) {
        this.inputs = inputs;
        this.codeBlock = codeBlock;
    }

    public List<Identifier> getInputs() {
        return inputs;
    }

    public void setInputs(List<Identifier> inputs) {
        this.inputs = inputs;
    }

    public CodeBlock getCodeBlock() {
        return codeBlock;
    }

    public void setCodeBlock(CodeBlock codeBlock) {
        this.codeBlock = codeBlock;
    }

    @Override
    public List<Expression> getNested() {
        return null;
    }

    @Override
    public List<LeafExpression> getLeafExpression() {
        return null;
    }

    @Override
    public String getSourceCode() {
        return null;
    }

    @Override
    public void useImports(Map<String, String> importsMap) {

    }
}
