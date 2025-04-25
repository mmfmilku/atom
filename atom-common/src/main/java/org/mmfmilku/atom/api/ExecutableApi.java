package org.mmfmilku.atom.api;

import org.mmfmilku.atom.api.dto.ExecuteResult;

/**
 * 可执行接口，用于即时执行代码块
 * */
public interface ExecutableApi {

    /**
     * 发起执行
     * @param toExecute 执行目标
     * @param args 执行参数
     * */
    ExecuteResult execute(String toExecute, Object... args);

}
