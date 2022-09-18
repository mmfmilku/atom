package org.mmfmilku.atom.test.atom;

import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.test.param.TestParam;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SumListAtom implements Atom<TestParam> {

    @Override
    public Boolean execute(TestParam param) {
        List<String> list = param.get("list");
        if (list != null && list.size() > 0) {
            AtomicInteger collect = list.stream().collect(() -> new AtomicInteger(0),
                    (result, each) -> result.addAndGet(Integer.parseInt(each)),
                    (a, b) -> a.addAndGet(b.get()));
            param.put("sumList", String.valueOf(collect.get()));
        }
        return true;
    }

}
