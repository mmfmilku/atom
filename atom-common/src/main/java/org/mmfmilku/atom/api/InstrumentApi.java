package org.mmfmilku.atom.api;

import java.util.List;

/**
 * InstrumentApi
 *
 * @author chenxp
 * @date 2024/10/10:10:26
 */
public interface InstrumentApi {
    
    List<String> listClassForPage(int offset, int size);
    
    List<String> searchClassForPage(int offset, int size, String classShortNameLike);
    
    byte[] getByteCode(String fullClassName);

    String writeByteCodeFile(String fullClassName, String targetDir);

    void retransformClass(String className);
    
}
