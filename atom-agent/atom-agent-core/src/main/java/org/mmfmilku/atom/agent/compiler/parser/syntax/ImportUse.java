package org.mmfmilku.atom.agent.compiler.parser.syntax;

import java.util.HashMap;
import java.util.List;

public interface ImportUse {

    default void useImports(List<Import> imports) {
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
        useImports(importsMap);
    }
    void useImports(HashMap<String, String> importsMap);

}
