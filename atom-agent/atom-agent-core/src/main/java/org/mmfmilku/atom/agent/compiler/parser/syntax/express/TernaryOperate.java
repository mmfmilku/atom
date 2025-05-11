package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.LeafExpression;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 三目表达式
 * */
public class TernaryOperate implements NestedExpression {


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
    public List<LeafExpression> getLeafExpression() {
        GrammarUtil.notSupport();
        return null;
    }

    @Override
    public List<Expression> getNested() {
        GrammarUtil.notSupport();
        return null;
    }
}
