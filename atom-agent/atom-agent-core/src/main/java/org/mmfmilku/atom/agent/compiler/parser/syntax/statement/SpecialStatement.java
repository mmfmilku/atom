package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

/**
 * 非单行类语句，如if、while语句，语句块等
 * */
public interface SpecialStatement extends Statement {

    @Override
    default String getSourceCode() {
        return getStatementSource();
    }

}
