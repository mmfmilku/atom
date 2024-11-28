package org.mmfmilku.atom.agent.compiler;

import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.parser.ParserDispatcher;
import org.mmfmilku.atom.agent.compiler.parser.syntax.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

public class CompilerUtil {

    public static JavaAST parseAST(String code) {
        Lexer lexer = new Lexer(code);
        lexer.execute();
        ParserDispatcher parser = new ParserDispatcher(lexer);
        return parser.execute();
    }

    public static Expression parseExpression(String text) {
        Lexer lexer = new Lexer(text);
        lexer.execute();
        return new ParserDispatcher(lexer).getExpression();
    }

}
