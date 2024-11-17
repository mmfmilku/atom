package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

import java.util.*;

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
    List<VarDefineStatement> autoCloseDefines = Collections.emptyList();

    CodeBlock tryBody;

    /**
     * catch块映射
     * k : 捕获的异常
     * v : 对应捕获异常的代码块
     * */
    Map<ThrowableCatch, CodeBlock> throwableCatches = new HashMap<>();

    CodeBlock finallyBody;

    public static class ThrowableCatch implements Statement {

        List<String> throwableTypes;

        public ThrowableCatch(List<String> throwableTypes, String varName) {
            this.throwableTypes = throwableTypes;
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
                "try",
                GrammarUtil.emptyWrap(
                        autoCloseDefines.isEmpty(),
                        () -> "(" + GrammarUtil.getLinesCode(autoCloseDefines) + ")"),
                tryBody.getSourceCode(),
                GrammarUtil.emptyWrap(
                        throwableCatches.isEmpty(),
                        // TODO
                        () -> GrammarUtil.getSentenceCode("catch", throwableCatches.toString())),
                GrammarUtil.emptyWrap(
                        finallyBody == null,
                        () -> GrammarUtil.getSentenceCode("finally", finallyBody.getSourceCode()))
        );
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
