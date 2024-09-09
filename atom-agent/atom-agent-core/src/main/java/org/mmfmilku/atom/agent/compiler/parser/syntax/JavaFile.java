package org.mmfmilku.atom.agent.compiler.parser.syntax;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;

import java.util.ArrayList;
import java.util.List;

public class JavaFile implements Node {

    private Package packageNode;

    private List<Import> imports = new ArrayList<>();

    private List<Class> classList = new ArrayList<>();

    public Package getPackageNode() {
        return packageNode;
    }

    public void setPackageNode(Package packageNode) {
        this.packageNode = packageNode;
    }

    public List<Import> getImports() {
        return imports;
    }

    public void setImports(List<Import> imports) {
        this.imports = imports;
    }

    public List<Class> getClassList() {
        return classList;
    }

    public void setClassList(List<Class> classList) {
        this.classList = classList;
    }

    @Override
    public String getSourceCode() {
        return GrammarUtil.getLinesCode(packageNode)
                + GrammarUtil.getLinesCode(imports)
                + GrammarUtil.getLinesCode(classList)
                ;
    }
}
