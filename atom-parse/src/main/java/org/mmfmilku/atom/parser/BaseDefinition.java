package org.mmfmilku.atom.parser;

import java.io.Serializable;
import java.util.List;

public class BaseDefinition implements Serializable {

    private String name;

    private String paramType;

    private List<Statement> statements;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public String toString() {
        return "BaseDefinition{" +
                "name='" + name + '\'' +
                ", paramType='" + paramType + '\'' +
                ", statements=" + statements +
                '}';
    }

    public static class Statement implements Serializable {
        String operate;
        String atom;
        List<String> preEL;
        List<String> postEL;

        public List<String> getPreEL() {
            return preEL;
        }

        public void setPreEL(List<String> preEL) {
            this.preEL = preEL;
        }

        public List<String> getPostEL() {
            return postEL;
        }

        public void setPostEL(List<String> postEL) {
            this.postEL = postEL;
        }

        public String getOperate() {
            return operate;
        }

        public void setOperate(String operate) {
            this.operate = operate;
        }

        public String getAtom() {
            return atom;
        }

        public void setAtom(String atom) {
            this.atom = atom;
        }

        @Override
        public String toString() {
            return "Statement{" +
                    "operate='" + operate + '\'' +
                    ", atom='" + atom + '\'' +
                    ", preEL=" + preEL +
                    ", postEL=" + postEL +
                    '}';
        }
    }

}
