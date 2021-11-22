package atom;

import param.Param;

public interface ResultAtom<T extends Param, R> {

    R execute(T param);

}
