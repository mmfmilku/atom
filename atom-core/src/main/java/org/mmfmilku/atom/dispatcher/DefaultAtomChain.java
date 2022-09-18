package org.mmfmilku.atom.dispatcher;

import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.decorator.TryAtom;
import org.mmfmilku.atom.exeption.AtomException;
import org.mmfmilku.atom.param.BaseParam;
import org.mmfmilku.atom.util.AssertUtils;
import org.mmfmilku.atom.util.AtomOperatesConst;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DefaultAtomChain<T extends BaseParam> implements AtomChain<T>, AtomOperator<T> {

    private TryAtom<T> currentAtom;

    private AtomChain<T> atomChain;

    public DefaultAtomChain() {
        this.atomChain = new LinkedAtomChain<>();
    }

    public DefaultAtomChain(AtomChain<T> atomChain) {
        this.atomChain = atomChain;
    }

    public DefaultAtomChain(Atom<T> beforeAtom, Atom<T> afterAtom) {
        this.atomChain = new LinkedAtomChain<>(beforeAtom, afterAtom);
    }

    @Override
    public DefaultAtomChain<T> add(Atom<T> atom) {
        currentAtom = null;
        this.atomChain.add(atom);
        return this;
    }

    @Override
    public Boolean invoke(T param) {
        return this.atomChain.invoke(param);
    }

    public DefaultAtomChain<T> tryProcess(Atom<T> atom) {
        TryAtom<T> tryAtom = new TryAtom<>(atom);
        add(tryAtom);
        currentAtom = tryAtom;
        return this;
    }

    public DefaultAtomChain<T> catchProcess(Atom<T> catchProcess) {
        currentAtom.catchProcess(catchProcess);
        return this;
    }

    public DefaultAtomChain<T> catchProcess(BiFunction<Exception, T, Boolean> catchProcess) {
        currentAtom.catchProcess(catchProcess);
        return this;
    }

    public DefaultAtomChain<T> finallyProcess(Atom<T> finallyProcess) {
        currentAtom.finallyProcess(finallyProcess);
        currentAtom = null;
        return this;
    }

    public ConditionAtom ifTrue(Atom<T> predicate) {
        ConditionAtom multipleCondition =
                new ConditionAtom(predicate);
        add(multipleCondition);
        return multipleCondition;
    }

    /**
     * 未完成的方法 TODO
     * */
    public <R> ForEachAtom forEach(Function<T, Iterable<R>> iterableGetter) {
        ForEachAtom<R> forEachHandle = new ForEachAtom<>(iterableGetter);
        add(forEachHandle);
        return forEachHandle;
    }

    @Override
    public void operate(String operate, Atom<T> atom) {
        // todo 添加if,else操作
        switch (operate) {
            case AtomOperatesConst.ADD:
                this.add(atom);
                break;
            case AtomOperatesConst.TRY:
                this.tryProcess(atom);
                break;
            case AtomOperatesConst.CATCH:
                this.catchProcess(atom);
                break;
            case AtomOperatesConst.FINALLY:
                this.finallyProcess(atom);
                break;
            default:
                throw new AtomException(operate + " not support in DefaultAtomChain");
        }
    }

    abstract class AbstractAtom implements Atom<T> {
        public DefaultAtomChain<T> then() {
            return DefaultAtomChain.this;
        }
    }

    public class ConditionAtom extends AbstractAtom {

        private ConditionAtom(Atom<T> predicate) {
            currentStatement = new ConditionalStatement(predicate);
        }

        public ConditionAtom ifTrue(Atom<T> predicate) {
            AssertUtils.notNull(predicate);
            if (currentStatement == null) {
                // 新建条件语句
                currentStatement = new ConditionalStatement(predicate);
            } else if (currentStatement.predicate != null)
                throw new IllegalStateException("condition already exist!");

            currentStatement.predicate = predicate;
            return this;
        }

        public ConditionAtom thenExecute(Atom<T> affirmation) {
            // 确保先添加条件，后添加执行handle
            if (currentStatement == null || currentStatement.predicate == null)
                throw new IllegalStateException("empty condition!");
            currentStatement.affirmation = affirmation;
            // 条件语句完成，加入到list中
            if (statements == null) {
                statements = new ArrayList<>(2);
            }
            statements.add(currentStatement);
            currentStatement = null;
            return this;
        }

        public DefaultAtomChain<T> elseExecute(Atom<T> elseAtom) {
            if (currentStatement != null && !currentStatement.finish()) {
                // 确保语句是完成状态
                throw new IllegalStateException("empty execute handle!");
            }
            this.elseAtom = elseAtom;
            return then();
        }

        @Override
        public Boolean execute(T param) {
            if (statements != null) {
                for (ConditionalStatement statement : statements) {
                    if (statement.predicate.execute(param)) {
                        // 执行为true则结束
                        return statement.affirmation.execute(param);
                    }
                }
                if (elseAtom != null) {
                    return elseAtom.execute(param);
                }
            }
            return true;
        }

        private Atom<T> elseAtom;
        private ConditionalStatement currentStatement;
        private List<ConditionalStatement> statements;

        private class ConditionalStatement {
            private Atom<T> predicate;
            private Atom<T> affirmation;

            private ConditionalStatement(Atom<T> predicate) {
                this.predicate = predicate;
            }

            private boolean executeStatement(T param) {
                if (predicate.execute(param)) {
                    affirmation.execute(param);
                    return true;
                } else
                    return false;
            }

            private boolean finish() {
                return predicate != null && affirmation != null;
            }
        }

    }

    public class ForEachAtom<R> extends AbstractAtom {

        private Atom<T> eachAtom;
        private Function<T, Iterable<R>> iterableGetter;

        private ForEachAtom(Function<T, Iterable<R>> iterableGetter) {
            AssertUtils.notNull(iterableGetter);
            this.iterableGetter = iterableGetter;
        }

        public ForEachAtom<R> eachExecute(Atom<T> eachAtom) {
            AssertUtils.notNull(eachAtom);
            this.eachAtom = eachAtom;
            return this;
        }

        @Override
        public Boolean execute(T param) {
            Iterator<R> iterator = iterableGetter.apply(param).iterator();
            int index = 0;
            Boolean success = true;
            while (iterator.hasNext()) {
                R next = iterator.next();
                success &= eachAtom.execute(param);
            }
            return success;
        }
    }


}
