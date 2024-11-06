package org.mmfmilku.atom.agent.compiler.parser.syntax;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.Modifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.ModifierPeek;
import org.mmfmilku.atom.agent.compiler.parser.syntax.extend.ImportUse;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.VarDefineStatement;

import java.util.Map;

/**
 * 类成员变量
 * */
public class Member implements Node, ImportUse, ModifierPeek {

    private Modifier modifier;

    public Member(VarDefineStatement varDefineStatement) {
        this.modifier = Modifier.DEFAULT;
        this.varDefineStatement = varDefineStatement;
    }

    public Member(Modifier modifier, VarDefineStatement varDefineStatement) {
        this.modifier = modifier;
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
    public Modifier getModifier() {
        return modifier;
    }

}
