package org.mmfmilku.atom.agent.compiler.parser.syntax;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.Attribute;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.AttributePeek;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.VarDefineStatement;

import java.util.Map;

/**
 * 类成员变量
 * */
public class Member implements Node, ImportUse, AttributePeek {

    private Attribute attribute;

    public Member(VarDefineStatement varDefineStatement) {
        this.attribute = Attribute.DEFAULT;
        this.varDefineStatement = varDefineStatement;
    }

    public Member(Attribute attribute, VarDefineStatement varDefineStatement) {
        this.attribute = attribute;
        this.varDefineStatement = varDefineStatement;
    }

    // TODO
    private VarDefineStatement varDefineStatement;

    @Override
    public String getSourceCode() {
        GrammarUtil.notSupport();
        return null;
    }

    @Override
    public void useImports(Map<String, String> importsMap) {

    }

    @Override
    public Attribute getAttribute() {
        return attribute;
    }

}
