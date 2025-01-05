package org.mmfmilku.atom.web.console.interfaces;

import java.util.List;
import java.util.Map;

/**
 * InstrumentService
 *
 * @author mmfmilku
 * @date 2024/10/11:14:47
 */
public interface IInstrumentService {

    List<String> listClassForPage(String appName, int offset, int size);

    List<String> listClassForPage(String appName, int offset, int size, String classShortNameLike);

    String decompile(String appName, String fullClassName);

    void retransformClass(String appName, String fullClassName);

    void loadOrdFile(String appName, String file);

    void stopClassOrd(String appName, String fullClassName);

    /**
     * 获取被重写的类
     * */
    Map<String, Object> getRunningOrdClass(String appName);

}
