package org.mmfmilku.atom.agent.compiler.parser.handle;

import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;

public interface StructParserHandle<T extends Node> extends ParserHandle<T> {

    @Override
    default int parseScope() {
        return HandleScope.assembly(HandleScope.IN_CLASS);
    }
}
