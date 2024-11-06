package org.mmfmilku.atom.agent.compiler.parser.syntax.deco;

/**
 * 对Attribute各项属性的访问
 * */
public interface AttributePeek {

    Attribute getAttribute();

    default Modifier getModifier() {
        return getAttribute().modifier;
    }

    default boolean staticDeco() {
        return getAttribute().staticDeco;
    }

    default boolean abstractDeco() {
        return getAttribute().abstractDeco;
    }

    default boolean finalDeco() {
        return getAttribute().finalDeco;
    }

    default boolean synchronizedDeco() {
        return getAttribute().synchronizedDeco;
    }

    default boolean transientDeco() {
        return getAttribute().transientDeco;
    }

    default boolean volatileDeco() {
        return getAttribute().volatileDeco;
    }

}
