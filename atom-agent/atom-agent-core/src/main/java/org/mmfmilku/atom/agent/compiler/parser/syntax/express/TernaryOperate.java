package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 三目表达式
 * */
public class TernaryOperate implements Expression {


    @Override
    public String getSourceCode() {
        // TODO
        GrammarUtil.notSupport();
        return null;
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        GrammarUtil.notSupport();
    }

    @Override
    public List<Expression> getBaseExpression() {
        GrammarUtil.notSupport();
        return null;
    }
}
