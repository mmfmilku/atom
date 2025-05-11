package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.LeafExpression;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 强制转换类型表达式，(Type) exp;
 * */
public class TypeCast implements Expression {

    private String type;

    private Expression expression;

    public TypeCast(String type, Expression expression) {
        this.type = type;
        this.expression = expression;
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        expression.useImports(importsMap);
    }

    @Override
    public String getSourceCode() {
        return "(" + type + ") " + expression.getSourceCode();
    }

    @Override
    public List<LeafExpression> getLeafExpression() {
        return expression.getLeafExpression();
    }
}
