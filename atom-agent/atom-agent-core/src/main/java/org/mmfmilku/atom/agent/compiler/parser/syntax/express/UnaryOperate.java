package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单目表达式
 * */
public class UnaryOperate implements Expression {

    private String operator;
    private Identifier identifier;
    private boolean preOperate;

    public UnaryOperate(String operator, Identifier identifier) {
        this.operator = operator;
        this.identifier = identifier;
        this.preOperate = true;
    }

    public UnaryOperate(String operator, Identifier identifier, boolean preOperate) {
        this.operator = operator;
        this.identifier = identifier;
        this.preOperate = preOperate;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public boolean isPreOperate() {
        return preOperate;
    }

    public void setPreOperate(boolean preOperate) {
        this.preOperate = preOperate;
    }

    @Override
    public String getSourceCode() {
        return preOperate ?
                operator + identifier.getSourceCode()
                : identifier.getSourceCode() + operator;
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        identifier.useImports(importsMap);
    }

    @Override
    public List<Expression> getBaseExpression() {
        return Collections.singletonList(identifier);
    }
}
