package org.mmfmilku.atom.parser;

import org.mmfmilku.atom.dispatcher.AtomChain;
import org.mmfmilku.atom.exeption.AtomException;
import org.mmfmilku.atom.param.Param;

public interface AtomChainParser<D, T extends Param> {

    <R extends AtomChain<T>> R parse(D d, Class<R> clazz) throws AtomException;

}
