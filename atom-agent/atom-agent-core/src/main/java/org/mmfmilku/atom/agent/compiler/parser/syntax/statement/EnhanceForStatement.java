package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.Identifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.VarDefineStatement;
import org.mmfmilku.atom.util.AssertUtil;

import java.util.*;

/**
 * 增强for循环
 *
 * for (varDefineStatement : loopIdentifier)
 *     loopBody
 *
 * */
public class EnhanceForStatement  extends LoopStatement {

    private VarDefineStatement loopItemVarDefine;

    private Expression loopExp;

    public VarDefineStatement getLoopItemVarDefine() {
        return loopItemVarDefine;
    }

    public void setLoopItemVarDefine(VarDefineStatement loopItemVarDefine) {
        this.loopItemVarDefine = loopItemVarDefine;
    }

    public Expression getLoopExp() {
        return loopExp;
    }

    public void setLoopExp(Expression loopExp) {
        this.loopExp = loopExp;
    }

    public EnhanceForStatement(VarDefineStatement loopItemVarDefine, Expression loopExp) {
        // 仅定义变量，不能赋值
        AssertUtil.isTrue(loopItemVarDefine.getAssignExpression() == null,
                "this expression should not assign value " + loopItemVarDefine.getSourceCode());
        this.loopItemVarDefine = loopItemVarDefine;
        this.loopExp = loopExp;
    }

    @Override
    public String getStatementSource() {
        return GrammarUtil.getSentenceCode("for",
                "(",
                loopItemVarDefine.getStatementSource(),
                ":",
                loopExp.getSourceCode(),
                ")",
                loopBody.getSourceCode()
        );
    }

    @Override
    public List<Expression> getAllExpression() {
        List<Expression> expressions = new ArrayList<>();
        expressions.addAll(loopExp.getLeafExpression());
        expressions.addAll(loopBody.getAllExpression());
        return expressions;
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        loopItemVarDefine.useImports(importsMap);
        loopExp.useImports(importsMap);
        loopBody.useImports(importsMap);
    }

    @Override
    public List<Statement> getNested() {
        return Arrays.asList(loopItemVarDefine, loopBody);
    }

    @Override
    public List<Expression> getNestedExp() {
        return Collections.singletonList(loopExp);
    }
}
