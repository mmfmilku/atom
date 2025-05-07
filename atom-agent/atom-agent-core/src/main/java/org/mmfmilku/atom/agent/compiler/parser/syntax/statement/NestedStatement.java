package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

/**
 * 嵌套语句
 * 非单行类语句，如if、while语句，语句块等
 * 此类语句源码无需以;结尾
 * */
public interface NestedStatement extends Statement {

    @Override
    default String getSourceCode() {
        return getStatementSource();
    }

}
