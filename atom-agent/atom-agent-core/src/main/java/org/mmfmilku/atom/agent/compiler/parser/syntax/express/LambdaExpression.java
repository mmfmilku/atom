package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.Identifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.LeafExpression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<Expression> nested = new ArrayList<>(inputs);
        // TODO codeBlock中的exp是否需要返回
        nested.addAll(codeBlock.getNestedExp());
        return nested;
    }

    @Override
    public List<LeafExpression> getLeafExpression() {
        List<LeafExpression> nested = new ArrayList<>(inputs);
        nested.addAll(codeBlock.getAllExpression()
                .stream()
                .map(Expression::getLeafExpression)
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
        return nested;
    }

    @Override
    public String getSourceCode() {
        return GrammarUtil.getSentenceCode("(" +
                inputs.stream()
                        .map(Identifier::getSourceCode)
                        .collect(Collectors.joining(", ")) +
                ")", "->", codeBlock.getSourceCode());
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        inputs.forEach(identifier -> identifier.useImports(importsMap));
        codeBlock.useImports(importsMap);
    }
}
