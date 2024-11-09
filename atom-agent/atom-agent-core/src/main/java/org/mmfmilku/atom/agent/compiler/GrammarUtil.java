package org.mmfmilku.atom.agent.compiler;

import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GrammarUtil {

    public static void notSupport() {
        throw new RuntimeException("not support");
    }

    /**
     * 运算符
     * */
    public static boolean isOperator(String value) {
        return "+".equals(value) ||
                "-".equals(value) ||
                "*".equals(value) ||
                "/".equals(value) ||
                "&".equals(value) ||
                "|".equals(value) ||
                "^".equals(value)
                ;
    }

    /**
     * 语句关键字
     * */
    public static boolean isCodeKeywords(String value) {
        // TODO  do {} while() , synchronized, return
        return "for".equals(value)
                || "while".equals(value)
                || "if".equals(value)
                || "new".equals(value)
                || "return".equals(value)
                ;
    }

    public static String surroundBlank(String code) {
        return " " + code + " ";
    }

    public static String toCallSourceCode(String calledMethod, List<Expression> passedParams) {
        return calledMethod + "(" +
                passedParams.stream()
                        .map(Node::getSourceCode)
                        .collect(Collectors.joining(", "))
                + ")";
    }

    public static String expToStatementSource(Expression expression) {
        return expression.getSourceCode() + ";";
    }

    public static String getLinesCode(Node ...nodes) {
        StringBuilder builder = new StringBuilder();
        for (Node node : nodes) {
            builder.append(node.getSourceCode()).append("\n");
        }
        return builder.toString();
    }

    public static String getLinesCode(List<? extends Node> nodes) {
        StringBuilder builder = new StringBuilder();
        for (Node node : nodes) {
            builder.append(node.getSourceCode()).append("\n");
        }
        return builder.toString();
    }

//    public static String getSentenceCode(Node ...nodes) {
//        StringBuilder builder = new StringBuilder();
//        for (Node node : nodes) {
//            builder.append(node.getSourceCode()).append(" ");
//        }
//        return builder.toString();
//    }

    /**
     * 语句拼接，通过空格分隔
     * */
    public static String getSentenceCode(String ...strings) {
        return Stream.of(strings)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(" "));
    }
}
