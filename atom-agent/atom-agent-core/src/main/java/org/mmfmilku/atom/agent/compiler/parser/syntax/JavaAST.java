package org.mmfmilku.atom.agent.compiler.parser.syntax;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        HashMap<String, String> importsMap = imports.stream().reduce(new HashMap<>(),
                (map, data) -> {
                    String value = data.getValue();
                    String substring = value.substring(value.lastIndexOf(".") + 1);
                    map.put(substring, value);
                    return map;
                },
                (a, b) -> {
                    a.putAll(b);
                    return a;
                });
        String packageName = getPackageNode().getValue();
        List<Class> classList = getClassList();
        for (Class clazz : classList) {
            clazz.setClassFullName(packageName + "." + clazz.getClassName());
            List<Method> methods = clazz.getMethods();
            for (Method method : methods) {
                method.useImports(importsMap);
            }
        }
    }
}
