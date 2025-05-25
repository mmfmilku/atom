package org.mmfmilku.atom.agent.compiler.parser.handle.struct;

import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.StructParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Generics;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GenericsParser implements StructParserHandle<Generics> {
    @Override
    public boolean match(ParserIterator iterator) {
        return iterator.isCurr(TokenType.LAngle);
    }

    @Override
    public Generics parse(ParserIterator iterator) {
        iterator.checkCurr(TokenType.LAngle);
        List<String> stringList = new ArrayList<>();
        // 非泛形结束
        while (!iterator.isNext(TokenType.RAngle)) {
            iterator.needNext();
            if (iterator.isCurr(TokenType.LAngle)) {
                // 泛形中的泛形解析
                Generics generics = this.parse(iterator);
                // TODO 嵌套模型
                stringList.add(generics.getSourceCode());
            } else {
                // 判断没有<，则包括各种符号一股脑的添加
                stringList.add(iterator.getCurr().getValue());
            }
        }
        iterator.needNext(TokenType.RAngle);
        Generics generics = new Generics();
        generics.setContent(stringList.stream().collect(Collectors.joining(" ")));
        return generics;
    }
}
