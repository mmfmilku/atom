package org.mmfmilku.atom.parser;

public interface Parser<T, R> {
    R parse(T t);
}
