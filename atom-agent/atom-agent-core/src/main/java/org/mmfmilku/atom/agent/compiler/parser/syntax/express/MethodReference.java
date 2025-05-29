package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.Identifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.LeafExpression;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MethodReference implements NestedExpression {

    private Identifier referenceObj;
    private String referenceName;

    public MethodReference(Identifier referenceObj, String referenceName) {
        this.referenceObj = referenceObj;
        this.referenceName = referenceName;
    }

    public Identifier getReferenceObj() {
        return referenceObj;
    }

    public void setReferenceObj(Identifier referenceObj) {
        this.referenceObj = referenceObj;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    @Override
    public String getSourceCode() {
        return referenceObj.getSourceCode() + "::" + referenceName;
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        referenceObj.useImports(importsMap);
    }

    @Override
    public List<Expression> getNested() {
        return Collections.singletonList(referenceObj);
    }

    @Override
    public List<LeafExpression> getLeafExpression() {
        return referenceObj.getLeafExpression();
    }
}
