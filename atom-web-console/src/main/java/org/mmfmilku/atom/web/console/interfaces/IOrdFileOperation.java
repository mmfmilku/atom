package org.mmfmilku.atom.web.console.interfaces;

import org.mmfmilku.atom.web.console.domain.OrdFile;

import java.util.List;

/**
 * IOrdFileOperation
 *
 * @author chenxp
 * @date 2024/7/31:13:13
 */
public interface IOrdFileOperation {
    
    String getOrdId(String appName);
    
    String getDir(String ordId);
    
    List<String> listDirs();
    
    List<String> listFiles(String ordId);
    
    String getText(OrdFile ordFile);
    
    void setText(OrdFile ordFile);
    
}
