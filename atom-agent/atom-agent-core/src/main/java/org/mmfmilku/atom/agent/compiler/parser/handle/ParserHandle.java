package org.mmfmilku.atom.agent.compiler.parser.handle;

import org.mmfmilku.atom.agent.compiler.parser.ParserDispatcher;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;

/**
 * 语法单元解析处理
 * */
public interface ParserHandle<T extends Node> {

    String SEMICOLONS = ";";

    String COLON = ":";

    String COMMA = ",";

    String POINT = ".";

    String EQUAL = "=";

    T parse(ParserDispatcher.ParserIterator iterator);

    /**
     * 支持的解析范围，类内部、方法内部、代码块内部
     * */
    int parseScope();

}
