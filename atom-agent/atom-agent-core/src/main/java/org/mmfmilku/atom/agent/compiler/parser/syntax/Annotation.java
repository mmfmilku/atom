package org.mmfmilku.atom.agent.compiler.parser.syntax;

import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

import java.util.List;

public class Annotation implements Node {

    private String annotationName;
    private List<Expression> passedParams;

    public Annotation(String annotationName, List<Expression> passedParams) {
        this.annotationName = annotationName;
        this.passedParams = passedParams;
    }

    public String getAnnotationName() {
        return annotationName;
    }

    public void setAnnotationName(String annotationName) {
        this.annotationName = annotationName;
    }

    public List<Expression> getPassedParams() {
        return passedParams;
    }

    public void setPassedParams(List<Expression> passedParams) {
        this.passedParams = passedParams;
    }

}
