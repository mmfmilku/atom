package org.mmfmilku.atom.agent.compiler.parser.syntax;

import java.util.List;

public class JavaFile implements Node {

    private Package packageNode;

    private List<Import> imports;

    private List<Class> classList;
}
