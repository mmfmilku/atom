package org.mmfmilku.atom.agent.compiler.parser.syntax;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.Identifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.extend.LinkedNode;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.NestedStatement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
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

    /**
     * 遍历各节点
     * Consumer 接收，入参：各个节点
     * */
    public void traversalVisit(Consumer<Node> accepter) {
        traversalVisit((parent, child) -> accepter.accept(child));
    }

    /**
     * 遍历各节点
     * BiConsumer接收，入参1：父节点  入参2：子节点
     * */
    public void traversalVisit(BiConsumer<Node, Node> accepter) {
        List<Class> classList = getClassList();
        // 类
        for (Class clazz : classList) {
            accepter.accept(this, clazz);
            //  TODO 遍历成员
            // TODO 遍历静态代码块
            // 方法
            for (Method method : clazz.getMethods()) {
                accepter.accept(clazz, method);
                // TODO 方法注解
                // TODO 方法入参定义
                // 方法体
                CodeBlock codeBlock = method.getCodeBlock();
                accepter.accept(method, codeBlock);
                // 语句
                for (Statement statement : codeBlock.getStatements()) {
                    accepter.accept(codeBlock, statement);
                    traversalVisitLinkNode(statement, accepter::accept);
//                    if (statement instanceof NestedStatement) {
//                        NestedStatement nestedStatement = (NestedStatement) statement;
//                        for (Statement child : nestedStatement.getNested()) {
//                            accepter.accept(statement, child);
//                            traversalVisitStatement(child, accepter);
//                        }
//                    }
                }
            }
        }
    }

    private void traversalVisitLinkNode(LinkedNode parent,
                                         BiConsumer<LinkedNode, LinkedNode> accepter) {
        for (LinkedNode child : parent.getChildren()) {
            accepter.accept(parent, child);
            traversalVisitLinkNode(child, accepter);
        }
    }

//    private void traversalVisitStatement(Statement parent, BiConsumer<Node, Node> accepter) {
//        if (parent instanceof NestedStatement) {
//            NestedStatement nestedStatement = (NestedStatement) parent;
//            for (Statement child : nestedStatement.getNested()) {
//                accepter.accept(parent, child);
//                traversalVisitStatement(child, accepter);
//            }
//        }
//        for (Expression child : parent.getNestedExp()) {
//            accepter.accept(parent, child);
//            traversalVisitExpression(child, accepter);
//        }
//    }

//    private void traversalVisitExpression(Expression parent, BiConsumer<Node, Node> accepter) {
//        // TODO 表达式的处理
//    }

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

    /**
     * 构建节点见的关联引用
     * */
    public void buildLinkedNode() {
        traversalVisit((parent, child) -> {
            if (child instanceof Identifier) {
                Identifier identifier = (Identifier) child;
                identifier.setParent((LinkedNode) parent);
            }
        });
    }
}
