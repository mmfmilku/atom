package dispatcher;

import atom.Atom;
import param.Param;
import util.AssertUtils;
import util.AtomConst;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class DefaultAtomChain<T extends Param> implements AtomChain<T> {

    private TryAtom currentAtom;

    private BaseAtomChain<T> atomChain;

    public DefaultAtomChain() {
        this.atomChain = new BaseAtomChain<>();
    }

    public DefaultAtomChain(BaseAtomChain<T> atomChain) {
        this.atomChain = atomChain;
    }

    public DefaultAtomChain(Atom<T> beforeAtom, Atom<T> afterAtom) {
        this.atomChain = new BaseAtomChain<>(beforeAtom, afterAtom);
    }

    @Override
    public DefaultAtomChain<T> add(Atom<T> atom) {
        currentAtom = null;
        this.atomChain.add(atom);
        return this;
    }

    @Override
    public void invoke(T param) {
        this.atomChain.invoke(param);
    }

    public DefaultAtomChain<T> tryProcess(Atom<T> atom) {
        TryAtom tryAtom = new TryAtom(atom);
        add(tryAtom);
        currentAtom = tryAtom;
        return this;
    }

    public DefaultAtomChain<T> catchProcess(Atom<T> catchProcess) {
        AssertUtils.notNull(catchProcess);
        currentAtom.catchProcess = (exception, param) -> catchProcess.execute(param);
        return this;
    }

    public DefaultAtomChain<T> catchProcess(BiConsumer<Exception, T> catchProcess) {
        AssertUtils.notNull(catchProcess);
        currentAtom.catchProcess = catchProcess;
        return this;
    }

    public DefaultAtomChain<T> finallyProcess(Atom<T> finallyProcess) {
        currentAtom.finallyProcess = finallyProcess;
        currentAtom = null;
        return this;
    }

    public ConditionAtom ifTrue(Predicate<T> predicate) {
        ConditionAtom multipleCondition =
                new ConditionAtom(predicate);
        add(multipleCondition);
        return multipleCondition;
    }

    /**
     * ?????????????????? TODO
     * */
    public <R> ForEachAtom forEach(Function<T, Iterable<R>> iterableGetter) {
        ForEachAtom<R> forEachHandle = new ForEachAtom<>(iterableGetter);
        add(forEachHandle);
        return forEachHandle;
    }

    public BiConsumer<DefaultAtomChain, Atom> operator(String operate) {
        switch (operate) {
            case "ADD":
                return DefaultAtomChain<T>::add;
            case "TRY":
                return DefaultAtomChain<T>::tryProcess;
            case "CATCH":
                return DefaultAtomChain<T>::catchProcess;
            case "FINALLY":
                return DefaultAtomChain<T>::finallyProcess;
            default:
                return AtomConst.NO_OPERATE;
        }
    }

    @Override
    public void execute(T param) {
        invoke(param);
    }

    abstract class AbstractAtom implements Atom<T> {
        public DefaultAtomChain<T> then() {
            return DefaultAtomChain.this;
        }
    }

    public class TryAtom extends AbstractAtom {

        private Atom<T> atom;
        private BiConsumer<Exception, T> catchProcess;
        private Atom<T> finallyProcess;

        private TryAtom(Atom<T> atom) {
            this.atom = atom;
        }

        @Override
        public void execute(T param) {
            try {
                atom.execute(param);
            } catch (Exception e) {
                if (catchProcess != null)
                    catchProcess.accept(e, param);
            } finally {
                if (finallyProcess != null)
                    finallyProcess.execute(param);
            }
        }

    }

    public class ConditionAtom extends AbstractAtom {

        private ConditionAtom(Predicate<T> predicate) {
            currentStatement = new ConditionalStatement(predicate);
        }

        public ConditionAtom ifTrue(Predicate<T> predicate) {
            AssertUtils.notNull(predicate);
            if (currentStatement == null) {
                // ??????????????????
                currentStatement = new ConditionalStatement(predicate);
            } else if (currentStatement.predicate != null)
                throw new IllegalStateException("condition already exist!");

            currentStatement.predicate = predicate;
            return this;
        }

        public ConditionAtom thenExecute(Atom<T> affirmation) {
            // ???????????????????????????????????????handle
            if (currentStatement == null || currentStatement.predicate == null)
                throw new IllegalStateException("empty condition!");
            currentStatement.affirmation = affirmation;
            // ??????????????????????????????list???
            if (statements == null) {
                statements = new ArrayList<>(2);
            }
            statements.add(currentStatement);
            currentStatement = null;
            return this;
        }

        public DefaultAtomChain<T> elseExecute(Atom<T> elseAtom) {
            if (currentStatement != null && !currentStatement.finish()) {
                // ???????????????????????????
                throw new IllegalStateException("empty execute handle!");
            }
            this.elseAtom = elseAtom;
            return then();
        }

        @Override
        public void execute(T param) {
            if (statements != null) {
                for (ConditionalStatement statement : statements) {
                    if (statement.executeStatement(param)) {
                        // ?????????true?????????
                        return;
                    }
                }
                if (elseAtom != null) {
                    elseAtom.execute(param);
                }
            }
        }

        private Atom<T> elseAtom;
        private ConditionalStatement currentStatement;
        private List<ConditionalStatement> statements;

        private class ConditionalStatement {
            private Predicate<T> predicate;
            private Atom<T> affirmation;

            private ConditionalStatement(Predicate<T> predicate) {
                this.predicate = predicate;
            }

            private boolean executeStatement(T param) {
                if (predicate.test(param)) {
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
        public void execute(T param) {
            Iterator<R> iterator = iterableGetter.apply(param).iterator();
            int index = 0;
            while (iterator.hasNext()) {
                R next = iterator.next();
                eachAtom.execute(param);
            }
        }
    }


}
