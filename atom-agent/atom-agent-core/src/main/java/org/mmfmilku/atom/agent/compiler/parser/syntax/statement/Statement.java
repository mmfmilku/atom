package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.parser.syntax.ImportUse;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;

public interface Statement extends Node, ImportUse {

    String getStatementSource();

    @Override
    default String getSourceCode() {
        return getStatementSource() + ";";
    }
}
