package parser;


import atom.Atom;
import atom.decorator.PrePostAtom;
import dispatcher.LinkedAtomChain;
import param.Param;
import util.AssertUtils;
import util.LambdaUtil;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BaseParser<T extends Param> implements Parser<BaseDefinition, LinkedAtomChain<T>> {

    // atom实例工厂
    private Function<String, Atom<T>> handleFactory;

    private ELParser<T> elParser = new ELParser<>();

    public BaseParser(Function<String, Atom<T>> handleFactory) {
        this.handleFactory = handleFactory;
    }

    public BaseParser(Function<String, Atom<T>> handleFactory, ELParser<T> elParser) {
        this.handleFactory = handleFactory;
        this.elParser = elParser;
    }

    @Override
    public LinkedAtomChain<T> parse(BaseDefinition baseDefinition) {
        AssertUtils.notNull(baseDefinition);
        LinkedAtomChain<T> linkedAtomChain = new LinkedAtomChain<>();
        List<BaseDefinition.Statement> statements = baseDefinition.getStatements();
        if (statements != null && statements.size() > 0) {
            statements.forEach(statement -> {
                List<String> preEL = statement.preEL;
                List<String> postEL = statement.postEL;
                Atom<T> atom = handleFactory.apply(statement.atom);
                Atom<T> preAtom = null;
                Atom<T> postAtom = null;
                if (preEL != null && preEL.size() > 0) {
                    preAtom = elParser.parse(preEL);
                }
                if (postEL != null && postEL.size() > 0) {
                    postAtom = elParser.parse(postEL);
                }
                if (preAtom != null || postAtom != null) {
                    atom = new PrePostAtom<>(LambdaUtil.nonNull(preAtom), LambdaUtil.nonNull(postAtom), atom);
                }
                // 获取指令对应的操作
                BiConsumer<LinkedAtomChain, Atom> operator = linkedAtomChain.operator(statement.operate);
                operator.accept(linkedAtomChain, atom);
            });
        }
        return linkedAtomChain;
    }
}
