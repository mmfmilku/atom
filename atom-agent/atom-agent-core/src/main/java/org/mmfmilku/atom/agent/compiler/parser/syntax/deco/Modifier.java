package org.mmfmilku.atom.agent.compiler.parser.syntax.deco;

import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;

/**
 * 描述了一些关键字修饰：
 * 修饰符、同步、静态等
 * public static final synchronized
 * */
public class Modifier implements Node {

    public static final Modifier DEFAULT = new Modifier();

    private AccessPrivilege accessPrivilege = AccessPrivilege.DEFAULT;

    /**
     * static 关键字修饰
     * */
    private boolean staticDeco;

    /**
     * abstract 关键字修饰
     * */
    private boolean abstractDeco;

    /**
     * final 关键字修饰
     * */
    private boolean finalDeco;

    /**
     * synchronized 关键字修饰
     * */
    private boolean synchronizedDeco;

    /**
     * transient 关键字修饰
     * */
    private boolean transientDeco;

    /**
     * volatile 关键字修饰
     * */
    private boolean volatileDeco;

    public AccessPrivilege getAccessPrivilege() {
        return accessPrivilege;
    }

    public void setAccessPrivilege(AccessPrivilege accessPrivilege) {
        this.accessPrivilege = accessPrivilege;
    }

    public boolean isStaticDeco() {
        return staticDeco;
    }

    public void setStaticDeco(boolean staticDeco) {
        this.staticDeco = staticDeco;
    }

    public boolean isAbstractDeco() {
        return abstractDeco;
    }

    public void setAbstractDeco(boolean abstractDeco) {
        this.abstractDeco = abstractDeco;
    }

    public boolean isFinalDeco() {
        return finalDeco;
    }

    public void setFinalDeco(boolean finalDeco) {
        this.finalDeco = finalDeco;
    }

    public boolean isSynchronizedDeco() {
        return synchronizedDeco;
    }

    public void setSynchronizedDeco(boolean synchronizedDeco) {
        this.synchronizedDeco = synchronizedDeco;
    }

    public boolean isTransientDeco() {
        return transientDeco;
    }

    public void setTransientDeco(boolean transientDeco) {
        this.transientDeco = transientDeco;
    }

    public boolean isVolatileDeco() {
        return volatileDeco;
    }

    public void setVolatileDeco(boolean volatileDeco) {
        this.volatileDeco = volatileDeco;
    }

    @Override
    public String getSourceCode() {
        return accessPrivilege.getSourceCode();
    }
}
