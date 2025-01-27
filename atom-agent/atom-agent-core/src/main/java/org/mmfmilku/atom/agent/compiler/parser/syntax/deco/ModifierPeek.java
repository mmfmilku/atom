package org.mmfmilku.atom.agent.compiler.parser.syntax.deco;

/**
 * 对Attribute各项属性的访问
 * */
public interface ModifierPeek {

    Modifier getModifier();

    default AccessPrivilege getAccessPrivilege() {
        return this.getModifier().getAccessPrivilege();
    }

    default boolean isStatic() {
        return this.getModifier().isStaticDeco();
    }

    default boolean isAbstract() {
        return this.getModifier().isAbstractDeco();
    }

    default boolean isFinal() {
        return this.getModifier().isFinalDeco();
    }

    default boolean isSynchronized() {
        return this.getModifier().isSynchronizedDeco();
    }

    default boolean isTransient() {
        return this.getModifier().isTransientDeco();
    }

    default boolean isVolatile() {
        return this.getModifier().isVolatileDeco();
    }

}
