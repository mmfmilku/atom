package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Identifier;
import org.mmfmilku.atom.util.AssertUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 增强for循环
 *
 * for (varDefineStatement : loopIdentifier)
 *     loopBody
 *
 * */
public class EnhanceForStatement  extends LoopStatement {

    private VarDefineStatement loopItemVarDefine;

    private Identifier loopIdentifier;

    public VarDefineStatement getLoopItemVarDefine() {
        return loopItemVarDefine;
    }

    public void setLoopItemVarDefine(VarDefineStatement loopItemVarDefine) {
        this.loopItemVarDefine = loopItemVarDefine;
    }

    public Identifier getLoopIdentifier() {
        return loopIdentifier;
    }

    public void setLoopIdentifier(Identifier loopIdentifier) {
        this.loopIdentifier = loopIdentifier;
    }

    public EnhanceForStatement(VarDefineStatement loopItemVarDefine, Identifier loopIdentifier) {
        // 仅定义变量，不能赋值
        AssertUtil.isTrue(loopItemVarDefine.getAssignExpression() == null,
                "this expression should not assign value " + loopItemVarDefine.getSourceCode());
        this.loopItemVarDefine = loopItemVarDefine;
        this.loopIdentifier = loopIdentifier;
    }

    @Override
    public String getStatementSource() {
        return GrammarUtil.getSentenceCode("for",
                "(",
                loopItemVarDefine.getStatementSource(),
                ":",
                loopIdentifier.getSourceCode(),
                ")",
                loopBody.getSourceCode()
        );
    }

    @Override
    public List<Expression> getAllExpression() {
        List<Expression> expressions = new ArrayList<>();
        expressions.addAll(loopIdentifier.getAllExpression());
        expressions.addAll(loopBody.getAllExpression());
        return expressions;
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        loopItemVarDefine.useImports(importsMap);
        loopIdentifier.useImports(importsMap);
        loopBody.useImports(importsMap);
    }
}
