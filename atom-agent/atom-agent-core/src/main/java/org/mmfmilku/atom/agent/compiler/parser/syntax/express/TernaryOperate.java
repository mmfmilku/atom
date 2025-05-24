package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.LeafExpression;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 三目表达式
 * boolExp ? trueExp : falseExp
 * i == 1 ? i1.toString() : i2.toString()
 * */
public class TernaryOperate implements NestedExpression {

    private Expression[] children = new Expression[3];

    public TernaryOperate(Expression boolExp, Expression trueExp, Expression falseExp) {
        this.children[0] = boolExp;
        this.children[1] = trueExp;
        this.children[2] = falseExp;
    }

    public Expression getBoolExp() {
        return children[0];
    }

    public void setBoolExp(Expression boolExp) {
        this.children[0] = boolExp;
    }

    public Expression getTrueExp() {
        return children[1];
    }

    public void setTrueExp(Expression trueExp) {
        this.children[1] = trueExp;
    }

    public Expression getFalseExp() {
        return children[2];
    }

    public void setFalseExp(Expression falseExp) {
        this.children[2] = falseExp;
    }

    @Override
    public String getSourceCode() {
        return GrammarUtil.getSentenceCode(
                getBoolExp().getSourceCode(), "?",
                getTrueExp().getSourceCode(), ":", getFalseExp().getSourceCode());
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        GrammarUtil.notSupport();
    }

    @Override
    public List<LeafExpression> getLeafExpression() {
        return Stream.of(children)
                .map(Expression::getLeafExpression)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<Expression> getNested() {
        return Arrays.asList(children);
    }
}
