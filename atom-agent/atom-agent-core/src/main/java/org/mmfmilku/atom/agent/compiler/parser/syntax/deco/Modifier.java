package org.mmfmilku.atom.agent.compiler.parser.syntax.deco;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;
import org.mmfmilku.atom.util.StringUtils;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 描述了一些关键字修饰：
 * 修饰符、同步、静态等
 * public static final synchronized
 * */
public class Modifier implements Node {

    public static final Modifier DEFAULT = new Modifier();

    private AccessPrivilege accessPrivilege = AccessPrivilege.DEFAULT;

    public enum ModifierEnum {

        STATIC("static", modifier -> modifier.setStaticDeco(true),
                modifier -> modifier.isStaticDeco() ? "static" : ""),
        ABSTRACT("abstract", modifier -> modifier.setAbstractDeco(true),
                modifier -> modifier.isAbstractDeco() ? "abstract" : ""),
        FINAL("final", modifier -> modifier.setFinalDeco(true),
                modifier -> modifier.isFinalDeco() ? "final" : ""),
        VOLATILE("volatile", modifier -> modifier.setVolatileDeco(true),
                modifier -> modifier.isVolatileDeco() ? "volatile" : ""),
        SYNCHRONIZED("synchronized", modifier -> modifier.setSynchronizedDeco(true),
                modifier -> modifier.isSynchronizedDeco() ? "synchronized" : ""),
        TRANSIENT("transient", modifier -> modifier.setTransientDeco(true),
                modifier -> modifier.isTransientDeco() ? "transient" : "");

        ModifierEnum(String keyword,
                     Consumer<Modifier> acceptHandle,
                     Function<Modifier, String> sourceHandle) {
            this.keyword = keyword;
            this.acceptHandle = acceptHandle;
            this.sourceHandle = sourceHandle;
        }

        private String keyword;
        private Consumer<Modifier> acceptHandle;
        private Function<Modifier, String> sourceHandle;

        public String getKeyword() {
            return keyword;
        }

        public Consumer<Modifier> getAcceptHandle() {
            return acceptHandle;
        }

        public Function<Modifier, String> getSourceHandle() {
            return sourceHandle;
        }

        public static ModifierEnum of(String keyword) {
            for (ModifierEnum modifierEnum : values()) {
                if (modifierEnum.getKeyword().equals(keyword)) {
                    return modifierEnum;
                }
            }
            return null;
        }
    }

    public boolean accept(String keyword) {
        ModifierEnum modifierEnum = ModifierEnum.of(keyword);
        if (modifierEnum != null) {
            modifierEnum.getAcceptHandle().accept(this);
            return true;
        }
        return false;
    }

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
        String modifierSource = Stream.of(ModifierEnum.values())
                .map(modifierEnum -> modifierEnum.getSourceHandle().apply(this))
                .filter(s -> !StringUtils.isEmpty(s))
                .collect(Collectors.joining(" "));
        return GrammarUtil.getSentenceCode(accessPrivilege.getSourceCode(), modifierSource);
    }
}
