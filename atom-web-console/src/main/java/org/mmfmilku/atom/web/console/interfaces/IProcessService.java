package org.mmfmilku.atom.web.console.interfaces;

import org.mmfmilku.atom.web.console.domain.ProcessInfo;

/**
 * 进程服务
 **/
public interface IProcessService {

    ProcessInfo processInfo(String pid);

}
