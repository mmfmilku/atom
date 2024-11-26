package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

import java.util.*;
import java.util.stream.Collectors;

/**
 * try 语句
 * try [(autoCloseDefines)]
 *      tryBody
 * catch (throwableDefines)
 *      catchBody
 * finally
 *      finallyBody
 * */
public class TryStatement implements SpecialStatement {

    /**
     * try with resource,AutoCloseable var define
     * */
    private List<VarDefineStatement> autoCloseDefines = Collections.emptyList();

    private CodeBlock tryBody;

    /**
     * catch块映射
     * k : 捕获的异常
     * v : 对应捕获异常的代码块
     * */
    private Map<ThrowableCatch, CodeBlock> throwableCatches = new LinkedHashMap<>();

    private CodeBlock finallyBody;

    public static class ThrowableCatch implements Statement {

        List<String> throwableTypes;

        String throwableName;

        public ThrowableCatch(List<String> throwableTypes, String throwableName) {
            this.throwableTypes = throwableTypes;
            this.throwableName = throwableName;
        }

        @Override
        public String getStatementSource() {
            return null;
        }

        @Override
        public List<Expression> getAllExpression() {
            return Collections.emptyList();
        }

        @Override
        public void useImports(Map<String, String> importsMap) {

        }

    }

    public List<VarDefineStatement> getAutoCloseDefines() {
        return autoCloseDefines;
    }

    public void setAutoCloseDefines(List<VarDefineStatement> autoCloseDefines) {
        this.autoCloseDefines = autoCloseDefines;
    }

    public CodeBlock getTryBody() {
        return tryBody;
    }

    public void setTryBody(CodeBlock tryBody) {
        this.tryBody = tryBody;
    }

    public Map<ThrowableCatch, CodeBlock> getThrowableCatches() {
        return throwableCatches;
    }

    public void setThrowableCatches(Map<ThrowableCatch, CodeBlock> throwableCatches) {
        this.throwableCatches = throwableCatches;
    }

    public CodeBlock getFinallyBody() {
        return finallyBody;
    }

    public void setFinallyBody(CodeBlock finallyBody) {
        this.finallyBody = finallyBody;
    }

    @Override
    public String getStatementSource() {
        return GrammarUtil.getSentenceCode(
                // try 部分
                "try",
                GrammarUtil.emptyWrap(
                        autoCloseDefines.isEmpty(),
                        () -> "(" + GrammarUtil.getLinesCode(autoCloseDefines) + ")"),
                tryBody.getSourceCode(),
                // catch 部分
                GrammarUtil.emptyWrap(
                        throwableCatches.isEmpty(),
                        () -> {
                            StringBuilder catchesBuilder = new StringBuilder();
                            throwableCatches.forEach((throwableCatch, codeBlock) -> {
                                catchesBuilder.append(
                                        GrammarUtil.getSentenceCode(
                                                " catch",
                                                "(" + String.join(" | ", throwableCatch.throwableTypes),
                                                throwableCatch.throwableName + ")",
                                                codeBlock.getSourceCode()
                                        )
                                );
                            });
                            // 移除第一个catch开头的空格，因为外部getSentenceCode会拼接空格
                            return catchesBuilder.substring(1);
                        }),
                // finally 部分
                GrammarUtil.emptyWrap(
                        finallyBody == null,
                        () -> GrammarUtil.getSentenceCode("finally", finallyBody.getSourceCode()))
        ) + "\n";
    }

    @Override
    public List<Expression> getAllExpression() {
        GrammarUtil.notSupport();
        return null;
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        GrammarUtil.notSupport();
    }
}
