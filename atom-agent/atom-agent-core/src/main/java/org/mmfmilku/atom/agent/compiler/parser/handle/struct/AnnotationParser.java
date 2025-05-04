package org.mmfmilku.atom.agent.compiler.parser.handle.struct;

import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.HandleScope;
import org.mmfmilku.atom.agent.compiler.parser.handle.StructParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Annotation;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

import java.util.Collections;
import java.util.List;

public class AnnotationParser implements StructParserHandle<Annotation> {
    @Override
    public boolean match(ParserIterator iterator) {
        Token curr = iterator.getCurr();
        return curr != null && curr.getValue().startsWith("@");
    }

    @Override
    public Annotation parse(ParserIterator iterator) {
        Token curr = iterator.getCurr();
        if (!match(iterator)) {
            iterator.throwIllegalToken(curr == null ? "" : curr.getValue());
        }
        String annotationName = curr.getValue();
        List<Expression> expressions = Collections.emptyList();
        if (iterator.isNext(TokenType.LParen)) {
            /*
              移动至param
              @annotation ( param )  ->  @annotation ( param )
              ^                                      ^
             * */
            iterator.needNext(TokenType.LParen);
            expressions = iterator.parameterPassing();
        }
        return new Annotation(annotationName, expressions);
    }

    @Override
    public int parseScope() {
        return HandleScope.assembly(
                HandleScope.OUT_CLASS,
                HandleScope.IN_CLASS,
                HandleScope.IN_METHOD);
    }
}
