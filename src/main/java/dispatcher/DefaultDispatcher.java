package dispatcher;

import atom.Atom;
import param.Param;
import util.AssertUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class DefaultDispatcher<T extends Param> extends BaseDispatcher<T> {

    private TryAtom currentAtom;

    @Override
    public DefaultDispatcher<T> add(Atom<T> atom) {
        currentAtom = null;
        super.add(atom);
        return this;
    }

    public DefaultDispatcher<T> tryProcess(Atom<T> atom) {
        TryAtom tryAtom = new TryAtom(atom);
        add(tryAtom);
        currentAtom = tryAtom;
        return this;
    }

    public DefaultDispatcher<T> catchProcess(Atom<T> catchProcess) {
        AssertUtils.notNull(catchProcess);
        currentAtom.catchProcess = (exception, param) -> catchProcess.execute(param);
        return this;
    }

    public DefaultDispatcher<T> catchProcess(BiConsumer<Exception, T> catchProcess) {
        AssertUtils.notNull(catchProcess);
        currentAtom.catchProcess = catchProcess;
        return this;
    }

    public DefaultDispatcher<T> finallyProcess(Atom<T> finallyProcess) {
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

    public <R> ForEachAtom forEach(Function<T, Iterable<R>> iterableGetter) {
        ForEachAtom<R> forEachHandle = new ForEachAtom<>(iterableGetter);
        add(forEachHandle);
        return forEachHandle;
    }

    @Override
    public BiConsumer<DefaultDispatcher, Atom> operator(String operate) {
        BiConsumer<DefaultDispatcher, Atom> operator = super.operator(operate);
        if (NO_OPERATE == operator) {
            switch (operate) {
                case "TRY":
                    return DefaultDispatcher::tryProcess;
                case "CATCH":
                    return DefaultDispatcher::catchProcess;
                case "FINALLY":
                    return DefaultDispatcher::finallyProcess;
                default:
                    return NO_OPERATE;
            }
        }
        return operator;
    }

    abstract class AbstractAtom implements Atom<T> {
        public DefaultDispatcher<T> then() {
            return DefaultDispatcher.this;
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

        public DefaultDispatcher<T> elseExecute(Atom<T> elseAtom) {
            if (currentStatement != null && !currentStatement.finish()) {
                // 确保语句是完成状态
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
                        // 执行为true则结束
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
