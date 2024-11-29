package org.mmfmilku.atom.agent.compiler.parser.handle;

import org.mmfmilku.atom.agent.compiler.parser.ParserDispatcher;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;

public interface ParserHandle<T extends Node> {

    String SEMICOLONS = ";";

    String COLON = ":";

    String COMMA = ",";

    String POINT = ".";

    String EQUAL = "=";

    T parse(ParserDispatcher.ParserIterator iterator);

}
