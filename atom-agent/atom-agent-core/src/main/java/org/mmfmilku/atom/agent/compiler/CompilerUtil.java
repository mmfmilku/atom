package org.mmfmilku.atom.agent.compiler;

import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.parser.Parser;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Class;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Import;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Method;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

import java.util.HashMap;
import java.util.List;

public class CompilerUtil {

    public static JavaAST parseAST(String code) {
        Lexer lexer = new Lexer(code);
        lexer.execute();
        Parser parser = new Parser(lexer);
        return parser.execute();
    }

    public static Expression parseExpression(String text) {
        Lexer lexer = new Lexer(text);
        lexer.execute();
        return new Parser(lexer).getExpression();
    }

    public static void userImport(JavaAST ast) {
        List<Import> imports = ast.getImports();
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
        String packageName = ast.getPackageNode().getValue();
        List<Class> classList = ast.getClassList();
        for (Class clazz : classList) {
            clazz.setClassFullName(packageName + "." + clazz.getClassName());
            List<Method> methods = clazz.getMethods();
            for (Method method : methods) {
                method.setReturnType(importsMap.getOrDefault(method.getReturnType(), method.getReturnType()));
            }
        }
    }

}
