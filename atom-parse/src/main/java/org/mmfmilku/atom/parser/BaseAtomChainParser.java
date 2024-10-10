package org.mmfmilku.atom.parser;


import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.decorator.AroundAtom;
import org.mmfmilku.atom.dispatcher.AtomChain;
import org.mmfmilku.atom.dispatcher.AtomOperator;
import org.mmfmilku.atom.exeption.AtomException;
import org.mmfmilku.atom.param.Param;
import org.mmfmilku.atom.util.AssertUtils;
import org.mmfmilku.atom.util.LambdaUtil;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class BaseAtomChainParser<T extends Param> implements AtomChainParser<BaseDefinition, T> {

    // atom实例工厂
    private Function<String, Atom<T>> handleFactory;

    private Parser<List<String>, Atom<T>> elParser;

    public BaseAtomChainParser(Function<String, Atom<T>> handleFactory) {
        this.handleFactory = handleFactory;
        this.elParser = new ELParser<>();
    }

    public BaseAtomChainParser(Function<String, Atom<T>> handleFactory, Parser<List<String>, Atom<T>> elParser) {
        this.handleFactory = handleFactory;
        this.elParser = elParser;
    }

    @Override
    public <R extends AtomChain<T>> R parse(BaseDefinition baseDefinition, Class<R> clazz) throws AtomException {
        AssertUtils.notNull(baseDefinition);
        AssertUtils.notNull(clazz);
        Class<?>[] clazzInterfaces = clazz.getInterfaces();
        boolean canOperate = Stream.of(clazzInterfaces)
                .anyMatch(clazzInterface -> clazzInterface == AtomOperator.class);
        if (!canOperate) {
            throw new AtomException(clazz.getName() + " did not implement AtomOperator interface");
        }

        R atomChain;
        try {
            atomChain = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new AtomException(clazz.getName() + " get instance failed", e);
        }
        AtomOperator<T> operator = (AtomOperator<T>) atomChain;
        List<BaseDefinition.Statement> statements = baseDefinition.getStatements();
        if (statements != null && statements.size() > 0) {
            statements.forEach(statement -> {
                Atom<T> preAtom = null;
                Atom<T> postAtom = null;
                List<String> preEL = statement.preEL;
                if (preEL != null && preEL.size() > 0) {
                    preAtom = elParser.parse(preEL);
                }
                List<String> postEL = statement.postEL;
                if (postEL != null && postEL.size() > 0) {
                    postAtom = elParser.parse(postEL);
                }
                Atom<T> atom = handleFactory.apply(statement.atom);
                if (preAtom != null || postAtom != null) {
                    atom = new AroundAtom<>(LambdaUtil.nonNull(preAtom), LambdaUtil.nonNull(postAtom), atom);
                }
                operator.operate(statement.operate, atom);
            });
        }
        // todo 返回带泛形参数的泛形对象 参考fastjson TypeReference
        return atomChain;
    }

}
