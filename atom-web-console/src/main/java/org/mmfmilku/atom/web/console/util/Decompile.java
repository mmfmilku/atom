package org.mmfmilku.atom.web.console.util;

import org.benf.cfr.reader.api.CfrDriver;
import org.benf.cfr.reader.util.getopt.OptionsImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Decompile
 *
 * @author mmfmilku
 * @date 2024/10/11:15:19
 */
public class Decompile {

    public static String decompile(String classFilePath, String targetPath) {
        List<String> files = new ArrayList<>();
        files.add(classFilePath);
        // target dir
        HashMap<String, String> outputMap = new HashMap<>();
        outputMap.put("outputdir", targetPath);

        OptionsImpl options = new OptionsImpl(outputMap);
        CfrDriver cfrDriver = new CfrDriver.Builder().withBuiltOptions(options).build();
        cfrDriver.analyse(files);
        return "";
    }
    
}
