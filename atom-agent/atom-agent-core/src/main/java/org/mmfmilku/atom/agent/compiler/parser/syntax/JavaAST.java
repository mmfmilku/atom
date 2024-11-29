package org.mmfmilku.atom.agent.compiler.parser.syntax;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JavaAST implements Node {

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

    public void useImport() {
        if (imports == null || imports.isEmpty()) {
            return;
        }
        Map<String, String> importsMap = imports.stream()
                .filter(imports -> !imports.getValue().endsWith("*"))
                .collect(Collectors.toMap(data -> {
                    String value = data.getValue();
                    return value.substring(value.lastIndexOf(".") + 1);
                }, Import::getValue));
        List<Class> classList = getClassList();
        for (Class clazz : classList) {
            List<Method> methods = clazz.getMethods();
            for (Method method : methods) {
                method.useImports(importsMap);
            }
        }
    }
}
